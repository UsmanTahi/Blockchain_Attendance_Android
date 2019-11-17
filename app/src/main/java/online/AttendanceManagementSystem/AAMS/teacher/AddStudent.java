package online.AttendanceManagementSystem.AAMS.teacher;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import online.AttendanceManagementSystem.AAMS.R;
import online.AttendanceManagementSystem.AAMS.contract.Attendees;
import online.AttendanceManagementSystem.AAMS.util.Web3jConstants;
import online.AttendanceManagementSystem.AAMS.util.Web3jUtils;

public class AddStudent extends AppCompatActivity {

    int batch_id, subject_id;

    ListView studentList;
    ArrayList<StudentData> list;
    String subject_code;

    FloatingActionButton addNewStudent;
    SwipeRefreshLayout swipeRefreshLayout;

    TeacherDB tDb;

    StudentListAdapter studentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        batch_id = getIntent().getIntExtra("batch_id",0);
        subject_id = getIntent().getIntExtra("subject_id", 0);
        subject_code = getIntent().getStringExtra("subject_code");
        studentList = findViewById(R.id.studentListAdd);

        addNewStudent = findViewById(R.id.addNewStudent);
        addNewStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddStudent.this, ReceiveStudentData.class).
                        putExtra("batch_id",batch_id).
                        putExtra("subject_code", subject_code).
                        putExtra("subject_id", subject_id));
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        tDb = new TeacherDB(AddStudent.this);

        list = new ArrayList<>();

        Cursor ss = tDb.getStudentByBatchAndSubject(batch_id, subject_id);
        if(ss!=null && ss.getCount()>0){
            if(list.size()>0){
                list.clear();
            }
            while (ss.moveToNext()){
                list.add(new StudentData(ss.getString(0),ss.getString(1),ss.getString(2),ss.getString(3)
                        ,ss.getString(4),ss.getString(5),ss.getString(6),ss.getString(7),ss.getString(8)
                        ,ss.getString(9)));
            }
        }else{
            Toast.makeText(this, "No Student found. Add student.", Toast.LENGTH_SHORT).show();
        }



        studentListAdapter = new StudentListAdapter(AddStudent.this, list);
        studentList.setAdapter(studentListAdapter);
    }

    //transfer ether to an account and wait for receipt
    public void createSignAndSendTransaction(BigInteger amountWei) throws Exception {
        String from = Web3jUtils.getWalletAddress();
        System.out.println("Wallet address from : " + from);
        Credentials credentials = Web3jUtils.getCredentials();
        BigInteger nonce = Web3jUtils.getNonce(from);
        Log.e("b","nonce generated");
        //create raw transaction
        BigInteger txFees = Web3jConstants.GAS_LIMIT_ETHER_TX.multiply(Web3jConstants.GAS_PRICE);
        RawTransaction txRaw = RawTransaction
                .createEtherTransaction(
                        nonce,
                        Web3jConstants.GAS_PRICE,
                        Web3jConstants.GAS_LIMIT_ETHER_TX,
                        Web3jConstants.CONTRACT_ADDRESS,
                        amountWei);
        Log.e("b","transaction generated");

        //sign transaction
        byte[] txSignedBytes = TransactionEncoder.signMessage(txRaw, credentials);
        String txSigned = Numeric.toHexString(txSignedBytes);
        Log.e("b","transaction signed");

        // send the signed transaction to the ethereum client
        EthSendTransaction ethSendTx = TeacherDashboard.web3j
                .ethSendRawTransaction(txSigned)
                .sendAsync()
                .get();
        Log.e("b","transaction sent");

        String txHash = ethSendTx.getTransactionHash();
        Log.e("b","transaction hash");

        Web3jUtils.waitForReceipt(txHash);
        Log.e("b","transaction hash" + txHash);

    }


    private void refreshLayout(){
        list = new ArrayList<>();

        Cursor ss = tDb.getStudentByBatch(batch_id);
        if(ss!=null && ss.getCount()>0){
            if(list.size()>0){
                list.clear();
            }
            while (ss.moveToNext()){
                list.add(new StudentData(ss.getString(0),ss.getString(1),ss.getString(2),ss.getString(3)
                        ,ss.getString(4),ss.getString(5),ss.getString(6),ss.getString(7),ss.getString(8)
                        ,ss.getString(9)));
            }
        }else{
            Toast.makeText(this, "No Student found. Add student.", Toast.LENGTH_SHORT).show();
        }

        studentListAdapter = new StudentListAdapter(AddStudent.this, list);
        studentListAdapter.notifyDataSetChanged();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                    addStudentToBlockchain();
                    addStudentToSubjectInBlockchain();
                }
        });

    }

    public void addStudentToBlockchain(){
        try {
            Attendees addStudentContract = null;
            addStudentContract = Attendees.load(Web3jConstants.CONTRACT_ADDRESS,
                    TeacherDashboard.web3j, Web3jUtils.getCredentials(), new DefaultGasProvider());
            Log.i("b", "Add Student contract loaded successfully");

            Cursor sT = tDb.getForBlockchain();
            if(sT.getCount() == 0){
                Log.i("b", "No student found to be added in the blockchain");
            }
            if (sT != null && sT.getCount() > 0) {
                Log.e("b", " teacher data");
                while (sT.moveToNext()) {

                    //send the add student request to the contract
                    TransactionReceipt result = null;
                    try {
                        String public_key = sT.getString(0).substring(0, 10);
                        String name = sT.getString(1);
                        String roll = sT.getString(2);
                        String email = sT.getString(3);
                        BigInteger imei = new BigInteger(sT.getString(4));
                        BigInteger status = new BigInteger("0");

                        result = addStudentContract.addAttendee(name, roll, email, imei, public_key, status).send();
                        System.out.println("result " + result);
                    } catch (InterruptedException e) {
                        Log.e("b", "Error in add student transaction: " + e.getMessage());
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Log.e("b", "Error in add student transaction: " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e("b", "Error in add student transaction: " + e.getMessage());
                        e.printStackTrace();
                    }
                    System.out.println("Balance from the address: " + result.getTransactionHash());
                    tDb.deleteStudentEntryForBlockchain(sT.getString(4), sT.getString(2));
                }
            }
        }catch (Exception e) {
            Log.e("b", "Add Student contract could not be loaded");
            toastAsync("Contract not loaded\nConnect to blockchain");
            e.printStackTrace();
        }
    }

    public void addStudentToSubjectInBlockchain(){
        try {
            Attendees addStudentContract = null;
            addStudentContract = Attendees.load(Web3jConstants.CONTRACT_ADDRESS,
                    TeacherDashboard.web3j, Web3jUtils.getCredentials(), new DefaultGasProvider());
            Log.i("b", "Add Student contract loaded successfully");

            Cursor sT = tDb.getSubjectwiseUniqueStudentsForBlockchain();
            if(sT.getCount() == 0){
                Log.i("b", "No student found to be enrolled to subject in the blockchain");
            }
            if (sT != null && sT.getCount() > 0) {
                while (sT.moveToNext()) {
                    //send the add student request to the contract
                    TransactionReceipt result = null;
                    try {
                        String public_key = sT.getString(0).substring(0, 10);
                        String subject = sT.getString(5);
                        result = addStudentContract.addStudentEnrolled(Web3jUtils.getWalletAddress(), subject, public_key).send();
                        Log.i("b", "transaction for enrolling student to subject successfull");
                        System.out.println("result " + result);
                    } catch (InterruptedException e) {
                        Log.e("b", "Error in add student to subject transaction: " + e.getMessage());
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Log.e("b", "Error in add student to subject transaction: " + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e("b", "Error in add student to subject transaction: " + e.getMessage());
                        e.printStackTrace();
                    }
                    System.out.println("Balance from the address: " + result.getTransactionHash());
                    tDb.deleteStudentEntrySubjectwiseForBlockchain(sT.getString(4), sT.getString(2), sT.getInt(6));
                }
            }
        }catch (Exception e) {
            Log.e("b", "Add Student contract could not be loaded");
            toastAsync("Contract not loaded\nConnect to blockchain");
            e.printStackTrace();
        }
    }

    public void toastAsync(final String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }


    class StudentData{
        String name,roll,sap,email,course,batchSec,mob,sem,imei,batch_id;

        public StudentData(String name, String roll, String sap, String email, String course, String batchSec, String mob, String sem, String imei, String batch_id) {
            this.name = name;
            this.roll = roll;
            this.sap = sap;
            this.email = email;
            this.course = course;
            this.batchSec = batchSec;
            this.mob = mob;
            this.sem = sem;
            this.imei = imei;
            this.batch_id = batch_id;
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

        public String getSap() {
            return sap;
        }

        public void setSap(String sap) {
            this.sap = sap;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        public String getBatchSec() {
            return batchSec;
        }

        public void setBatchSec(String batchSec) {
            this.batchSec = batchSec;
        }

        public String getMob() {
            return mob;
        }

        public void setMob(String mob) {
            this.mob = mob;
        }

        public String getSem() {
            return sem;
        }

        public void setSem(String sem) {
            this.sem = sem;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getBatch_id() {
            return batch_id;
        }

        public void setBatch_id(String batch_id) {
            this.batch_id = batch_id;
        }
    }



}
