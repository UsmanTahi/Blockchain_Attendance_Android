package online.AttendanceManagementSystem.AAMS.teacher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import online.AttendanceManagementSystem.AAMS.MainActivity;
import online.AttendanceManagementSystem.AAMS.R;
import online.AttendanceManagementSystem.AAMS.util.Web3jConstants;
import online.AttendanceManagementSystem.AAMS.util.Web3jUtils;

public class TeacherDashboard extends AppCompatActivity {

    TeacherDB tDB;

    ListView batchList;
    BatchListAdapter adapter;
    ArrayList<BatchData> list;

    static public Web3j web3j = null;
    static String clientUrl = null;
    static String[] accounts = new String[15];
    public static String wallet_address  = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        wallet_address = Web3jUtils.getWalletAddress();
        String TAG = "Return";
        // show client details

        tDB = new TeacherDB(TeacherDashboard.this);

        batchList = findViewById(R.id.batch_list);

        list = new ArrayList<>();

        Cursor ss = tDB.getAllBatch();
        if(ss!=null && ss.getCount()>0){

            while (ss.moveToNext()){
                list.add(new BatchData(ss.getInt(0),ss.getString(1)));
            }
        }else{
            Toast.makeText(this, "No batch found. Add batch.", Toast.LENGTH_SHORT).show();
        }

        adapter = new BatchListAdapter(TeacherDashboard.this, list);

        batchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(TeacherDashboard.this, BatchActivity.class);
                i.putExtra("name",list.get(position).getName());
                i.putExtra("id",list.get(position).getId());
                startActivity(i);
            }
        });
        batchList.setAdapter(adapter);




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_dashboard_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.teacher_logout:

                new AlertDialog.Builder(TeacherDashboard.this)
                        .setCancelable(false)
                        .setTitle("Are you sure you want to logout?")
//                        .setMessage("Once you log out you will lose all app data...")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO : option to clear databse
//                                tDB.clearTable();
                                startActivity(new Intent(TeacherDashboard.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                break;

            case R.id.add_batch:
                addBatch();
                break;
            case R.id.connectEth:
                Start_Connect();
                break;

            case R.id.checkWallet:
                checkWalletBalance();
                break;
            case R.id.checkContract:
                if(Web3jConstants.CONTRACT_ADDRESS == null){
                    try {
                        TeacherRegistration.deployContract();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("b", e.getMessage());
                        toastAsync("Error deploying contract");
                    }
                } else {
                    toastAsync("Contract Present\n Contract Address :" + Web3jConstants.CONTRACT_ADDRESS);
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    class BatchData{
        private int id;
        private  String name;

        public BatchData(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public void addBatch(){

        LayoutInflater inflater = TeacherDashboard.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.batch_add_custom_dialog,null);

        final Dialog dialogAddBatch = new Dialog(TeacherDashboard.this);
        dialogAddBatch.setContentView(view);

        final EditText name = view.findViewById(R.id.batch_name);
        Button cancel = view.findViewById(R.id.cancel);
        Button add = view.findViewById(R.id.add);

        dialogAddBatch.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddBatch.cancel();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().trim()!=null && !name.getText().toString().trim().isEmpty()){
                    tDB.insertBatch(name.getText().toString().trim());

                    //notify data set changes in adapter

                    Cursor ss = tDB.getAllBatch();
                    if(ss!=null && ss.getCount()>0){
                        if(list!=null && list.size()>0){
                            list.clear();
                        }
                        while (ss.moveToNext()){
                            list.add(new BatchData(ss.getInt(0),ss.getString(1)));
                        }
                    }else{
                        Toast.makeText(TeacherDashboard.this, "No batch found. Add batch.", Toast.LENGTH_SHORT).show();
                    }

                    adapter = new BatchListAdapter(TeacherDashboard.this, list);
                    adapter.notifyDataSetChanged();

                    dialogAddBatch.cancel();
                }
            }
        });
    }

    public void Start_Connect() {
        clientUrl = argsToUrl();
        web3j = Web3j.build(new HttpService(clientUrl));


        Web3ClientVersion client = null;
        try {
            client = web3j
                    .web3ClientVersion()
                    .sendAsync()
                    .get();
            if (!client.hasError()) {
                toastAsync("Connected");
            } else {
                toastAsync("Not connected");
                toastAsync(client.getError().getMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            toastAsync("Error : Check Internet Connection");
            e.printStackTrace();
        } catch (Exception e){
            toastAsync("Error : Check Internet Connection");
        }
//        Log.d("Connected to " + String.valueOf(client.getWeb3ClientVersion()) + "\n");

    }

    public void toastAsync(final String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    //connection port and ip
    public String argsToUrl() {
//        String ip = Web3jConstants.CLIENT_IP;
//        String port = Web3jConstants.CLIENT_PORT;
//
        return "https://rinkeby.infura.io/v3/32ab89cf19494f4abf3ab9e9b1e6ef89";

    }

    private void checkWalletBalance() {

        try {
            String wallet_address = Web3jUtils.getWalletAddress();
            System.out.println("Address is : " +wallet_address);

            EthGetBalance ethGetBalance=
                    web3j.ethGetBalance(wallet_address,
                            DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger wei = ethGetBalance.getBalance();
            java.math.BigDecimal tokenValue = Convert.fromWei(String.valueOf(wei), Convert.Unit.ETHER);
            String strTokenAmount = String.valueOf(tokenValue);

            toastAsync("Address is : " + wallet_address + "and Balance is :" +
                    strTokenAmount);

            System.out.println("Address is : " +wallet_address + "and Balance is :" +
                    strTokenAmount);


        } catch (Exception e) {
            Log.e("Error ", e.getMessage());
            toastAsync("Error: " + e.getMessage());
        }
    }
}
