package online.AttendanceManagementSystem.AAMS.teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import online.AttendanceManagementSystem.AAMS.LanConnection.CloseHardwares;
import online.AttendanceManagementSystem.AAMS.R;

public class ReceiveStudentData extends AppCompatActivity {


    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    ConnectionsClient connectionsClient;

    String[] sDetails;
    String subject_code;
    TeacherDB tDB;
    int BATCH_ID, SUBJECT_ID;
    AlertDialog dd;
    String studentID, studentDetails, sName;
    Button start, stop;
    TextView textStatus;

    private CloseHardwares closeHardwares;
    /**
     * Callbacks for receiving payloads
     */
    private PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            Log.e("ReceivedFrom: ", endpointId);
            studentDetails = new String(payload.asBytes());
            textStatus.setText("Receiving data from " + sName);
            Log.e("StudentDetails : ", studentDetails);
            try {
                sDetails = parseReceivedData(studentDetails);
                showPopDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

        }
    };
    /**
     * Callback for connection to students devices
     */
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            Log.e("TeacherServer", "onConnectionInitiated: accepting connection");
            textStatus.setText("Connection Initiated with " + connectionInfo.getEndpointName());
            sName = connectionInfo.getEndpointName();
            connectionsClient.acceptConnection(endpointId, payloadCallback);
            Log.e("ConnectionSenderName: ", connectionInfo.getEndpointName());
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {

            if (connectionResolution.getStatus().isSuccess()) {
                Log.e("ConnectionLifeCycle", "onConnectionResult: connection successful with : " + sName);

                textStatus.setText("connection successful with : " + endpointId);
                studentID = endpointId;
            } else {
                Log.i("ConnectionLifeCycle", "onConnectionResult: connection failed");

                textStatus.setText("connection failed with : " + sName);
            }

        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            Log.i("ConnectionLifeCycle", "onDisconnected: disconnected from the student");

            textStatus.setText("connection successful with : " + sName);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_student_data);

        tDB = new TeacherDB(ReceiveStudentData.this);

        BATCH_ID = getIntent().getIntExtra("batch_id", 0);
        SUBJECT_ID = getIntent().getIntExtra("subject_id", 0);
        subject_code = getIntent().getStringExtra("subject_code");

        connectionsClient = Nearby.getConnectionsClient(this);

        closeHardwares = new CloseHardwares(getApplicationContext());

        stop = findViewById(R.id.stopAdvertising);
        start = findViewById(R.id.startAdvertising);
        textStatus = findViewById(R.id.textStatus);


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(true);
                stop.setEnabled(false);
                disconnect();

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setEnabled(false);
                stop.setEnabled(true);
                startAdvertising();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }

    /**
     * Broadcasts our presence using Nearby Connections so other players can find us.
     */
    private void startAdvertising() {

        connectionsClient.startAdvertising("teacher", getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("StartAdvertise", "We are Advertising");
                        textStatus.setText("Started Advertising");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("StartAdvertise", "We are unable to Advertise");
                textStatus.setText("unable to Advertise, Reopen the app");
            }
        });
    }

    private void showPopDialog() {
        new AlertDialog.Builder(ReceiveStudentData.this)
                .setCancelable(false)
                .setTitle("Verify student")
                .setMessage(Html.fromHtml("<b>Name : </b>" + sDetails[0] + "<br><b>Roll : </b>" + sDetails[1] + "<br><b>Sem : </b>" + sDetails[7]))
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendMessage("Rejected", studentID);
                        connectionsClient.disconnectFromEndpoint(studentID);
                        dialog.cancel();

                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long a = tDB.insertStudentDetails(sDetails[0], sDetails[1], sDetails[2],
                                sDetails[3], sDetails[4], sDetails[5], sDetails[6], sDetails[7],
                                sDetails[8], BATCH_ID, SUBJECT_ID, subject_code);
                        Log.e("isInsertedInDB", a + " ");
                        String s_pk = computeSha3(String.format("%s#%s", sDetails[1], sDetails[8]));

                        Cursor ss = tDB.getSubjectwiseUniqueStudentsForBlockchain();
                        int studentExistsInSubjectFlag = 0;
                        if(ss!=null && ss.getCount() > 0){
                            while(ss.moveToNext()){
                                String roll = ss.getString(2).toLowerCase().trim();
                                String imei = ss.getString(4);
                                int sub_id = ss.getInt(6);
                                if((sDetails[1].toLowerCase().trim().equals(roll) && sDetails[8].toLowerCase().trim().equals(imei) && SUBJECT_ID == sub_id)){
                                    studentExistsInSubjectFlag = 1;
                                }
                            }
                        }


                        Cursor sT  = tDB.getAllUniqueStudents();
                        int studentExistsFlag = 0;
                        if(sT!=null && sT.getCount() > 0){
                            while(sT.moveToNext()){
                                if(sT.getString(0).equals(s_pk)){
                                    studentExistsFlag = 1;
                                }
                            }
                        }
                        if(studentExistsInSubjectFlag == 0) {
                            tDB.insertSubjectStudentForBlockchain(s_pk, sDetails[0], sDetails[1], sDetails[3], sDetails[8], subject_code, SUBJECT_ID);
                        }

                        if(studentExistsFlag == 0){
                            long b = tDB.insertStudent(s_pk, sDetails[0], sDetails[1], sDetails[3], sDetails[8]);
                            long c = tDB.insertForBlockchain(s_pk, sDetails[0], sDetails[1], sDetails[3], sDetails[8]);
                            Log.e("b", "isInsertedInBlockchain"+b);
                        }


                        sendMessage("teacherConfirm8185", studentID);
                        connectionsClient.disconnectFromEndpoint(studentID);
                        sDetails = null;

                        //TODO : have to add condition if student data is already in teacher device and student is re-registering himself
                        dialog.cancel();
                    }
                }).show();
    }

    public String computeSha3(String input) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest224();
        byte[] digest = digestSHA3.digest(input.getBytes());
        return Hex.toHexString(digest);
    }

    /**
     * send message to given student id, Can only used by teacher side
     */
    private void sendMessage(String message, String studentID) {
        Payload payload = Payload.fromBytes(message.getBytes());
        connectionsClient.sendPayload(studentID, payload);
    }

    /**
     * Disconnects from the opponent and reset the UI.
     */
    public void disconnect() {
        Log.e("ConnectionClientServer", "Stopping all functionality");
        if (textStatus != null) {
            textStatus.setText("Stop registering students");
        }
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

}
