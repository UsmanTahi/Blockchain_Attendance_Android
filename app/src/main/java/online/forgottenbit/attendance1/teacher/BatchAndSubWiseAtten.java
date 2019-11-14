package online.forgottenbit.attendance1.teacher;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import online.forgottenbit.attendance1.LanConnection.CloseHardwares;
import online.forgottenbit.attendance1.MainActivity;
import online.forgottenbit.attendance1.R;

public class BatchAndSubWiseAtten extends AppCompatActivity {


    Button start,stop;
    ListView presentStudentList;

    SwipeRefreshLayout swipeRefreshLayout;

    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    ConnectionsClient connectionsClient;

    String[] sDetails;
    TeacherDB tDB;
    int BATCH_ID,SUB_ID;
    String studentID,studentDetails,sName;

    ArrayList<AttendanceData> list;

    private CloseHardwares closeHardwares;
    AttendanceAdapter adapter;

    DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
    String date = df.format(Calendar.getInstance().getTime());


    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {

        if(list!=null && list.size()>0){
            list.clear();
        }
        connectionsClient.startAdvertising("teacher", getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("StartAdvertise","We are Advertising");
                        Toast.makeText(BatchAndSubWiseAtten.this, "Started taking attendance", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("StartAdvertise","We are unable to Advertise");
            }
        });
    }



    /** Callback for connection to students devices */
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            Log.e("TeacherServer", "onConnectionInitiated: accepting connection");
            sName = connectionInfo.getEndpointName();
            connectionsClient.acceptConnection(endpointId,payloadCallback);
            Log.e("ConnectionSenderName: ",connectionInfo.getEndpointName());
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {

            if(connectionResolution.getStatus().isSuccess()){
                Log.e("ConnectionLifeCycle", "onConnectionResult: connection successful with : "+sName);

                studentID = endpointId;
            }else {
                Log.e("ConnectionLifeCycle", "onConnectionResult: connection failed");

            }

        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            Log.e("ConnectionLifeCycle", "onDisconnected: disconnected from the student");

        }
    };


    /** Callbacks for receiving payloads */
    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull final String endpointId, @NonNull Payload payload) {
            Log.e("ReceivedFrom: ",endpointId);
            studentDetails = new String(payload.asBytes());
            Log.e("StudentDetails : ",studentDetails);

            try {
                sDetails = parseReceivedData(studentDetails);
                tDB.insertAttendance(BATCH_ID,SUB_ID,sDetails[1],date,1);
                list.add(new AttendanceData(sDetails[0],sDetails[1]));
                Log.e("dblength updated: ", String.valueOf(list.size()));
                adapter = new AttendanceAdapter(BatchAndSubWiseAtten.this, list);
                //Log.e("checkpoint 1", studentDetails);
                adapter.notifyDataSetChanged();
                //Log.e("checkpoint 2", studentDetails);

                //TODO : have to extract time stamp from system
                connectionsClient.sendPayload(endpointId,Payload.fromBytes("teacherConfirm8185".getBytes()));
                Log.e("endpoint", endpointId);
                connectionsClient.disconnectFromEndpoint(endpointId);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };


    /** Disconnects from the opponent and reset the UI. */
    public void disconnect() {
        Log.e("ConnectionClientServer", "Stopping all functionality");

        connectionsClient.stopAllEndpoints();
        connectionsClient.stopAdvertising();

        if(closeHardwares.isBluetoothEnabled()){
            closeHardwares.closeBluetooth();
        }

        if(closeHardwares.isWifiEnabled()){
            closeHardwares.closeWifi();
        }
    }


    private String[] parseReceivedData(String receivedData) {
        String[] val = receivedData.split("#");
        return val;
    }


    class AttendanceData{
        String name,roll;

        public AttendanceData(String name, String roll) {
            this.name = name;
            this.roll = roll;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRoll() {
            return roll;
        }

        public void setRoll(String roll) {
            this.roll = roll;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_and_sub_wise_atten);

        BATCH_ID = getIntent().getIntExtra("batch_id",0);
        SUB_ID = getIntent().getIntExtra("sub_id",0);

        tDB = new TeacherDB(BatchAndSubWiseAtten.this);

        connectionsClient = Nearby.getConnectionsClient(BatchAndSubWiseAtten.this);
        closeHardwares = new CloseHardwares(getApplicationContext());

        start = findViewById(R.id.startAdvertising);
        stop= findViewById(R.id.stopAdvertising);

        presentStudentList = findViewById(R.id.presentStudentList);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                stop.setEnabled(true);
                startAdvertising();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                start.setEnabled(true);
                disconnect();
            }
        });


        this.list = new ArrayList<>();
        adapter = new AttendanceAdapter(BatchAndSubWiseAtten.this, this.list);
        presentStudentList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.batch_and_subjectwise_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.generateAttendanceSheet:
                startActivity(new Intent(BatchAndSubWiseAtten.this, GenerateAttendanceSheet.class)
                        .putExtra("batch_id",BATCH_ID).putExtra("subject_id", SUB_ID));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
