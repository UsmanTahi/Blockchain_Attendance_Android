package online.AttendanceManagementSystem.AAMS.teacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import online.AttendanceManagementSystem.AAMS.R;
import online.AttendanceManagementSystem.AAMS.contract.Attendees;
import online.AttendanceManagementSystem.AAMS.util.Web3jConstants;
import online.AttendanceManagementSystem.AAMS.util.Web3jUtils;

public class GenerateAttendanceSheet  extends AppCompatActivity {

    int batch_id, subject_id;

    ListView studentAttendanceList;
    ArrayList<StudentAttendanceData> list;

    SwipeRefreshLayout swipeRefreshLayout;

    TeacherDB tDb;

    StudentAttendanceListAdapter studentAttendanceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        batch_id = getIntent().getIntExtra("batch_id",0);
        subject_id = getIntent().getIntExtra("subject_id",0);


        studentAttendanceList = findViewById(R.id.studentAttendanceList);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        tDb = new TeacherDB(GenerateAttendanceSheet.this);

        list = new ArrayList<>();

        Cursor ss = tDb.getSubjectWiseAttendance(batch_id, subject_id);
        if(ss!=null && ss.getCount()>0){
            if(list.size()>0){
                list.clear();
            }
            while (ss.moveToNext()){
                list.add(new StudentAttendanceData(ss.getInt(0),
                        ss.getInt(1),ss.getString(2), ss.getString(3),
                        ss.getString(4), ss.getString(5), ss.getInt(6)));
            }
        }else{
            Toast.makeText(this, "No Student found. Add student.", Toast.LENGTH_SHORT).show();
        }

        studentAttendanceListAdapter = new StudentAttendanceListAdapter(GenerateAttendanceSheet.this, list);
        studentAttendanceList.setAdapter(studentAttendanceListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.generate_attendance_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.syncSubjectAttendance:
                showDialog("Sync Attendance","The app might not respond when in sync. Confirm?");

        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void showDialog(String title, String message){
        new AlertDialog.Builder(GenerateAttendanceSheet.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addStudentToBlockchain();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private void refreshLayout(){
        list = new ArrayList<>();

        Cursor ss = tDb.getSubjectWiseAttendance(batch_id, subject_id);
        if(ss!=null && ss.getCount()>0){
            if(list.size()>0){
                list.clear();
            }
            while (ss.moveToNext()){
                list.add(new StudentAttendanceData(ss.getInt(0),
                        ss.getInt(1),ss.getString(2), ss.getString(3),
                        ss.getString(4), ss.getString(5), ss.getInt(6)));
            }
        }else{
            Toast.makeText(this, "No Student found. Add student.", Toast.LENGTH_SHORT).show();
        }

        studentAttendanceListAdapter = new StudentAttendanceListAdapter(GenerateAttendanceSheet.this, list);
        studentAttendanceList.deferNotifyDataSetChanged();

    }

    public void addStudentToBlockchain(){
        try {
            Attendees attendanceContract = null;
            attendanceContract = Attendees.load(Web3jConstants.CONTRACT_ADDRESS,
                    TeacherDashboard.web3j, Web3jUtils.getCredentials(), new DefaultGasProvider());
            Log.i("b", "Add Student Attendance contract loaded successfully");
            Cursor ss = tDb.getSubjectWiseAttendanceForBlockchain(batch_id, subject_id);
            if(ss.getCount() == 0){
                toastAsync("No more student attendances to sync");
            }
            if(ss!=null && ss.getCount()>0){

                while (ss.moveToNext()){
                    TransactionReceipt result = null;
                    try{
                        int batch_id = ss.getInt(0);
                        int subject_id = ss.getInt(1);
                        String subject_code = ss.getString(2);
                        String roll = ss.getString(3);
                        String student_pk = ss.getString(4).substring(0, 10);
                        String timestamp = ss.getString(5);
                        int aOrP = ss.getInt(6);
                        result = attendanceContract.markAttendanceBySubject(subject_code, student_pk).send();
                    } catch (InterruptedException e) {
                        Log.e("b", "Error in add attendance for student transaction: " + e.getMessage());
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        Log.e("b", "Error in add attendance for student transaction:" + e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e("b", "Error in add attendance for student transaction:" + e.getMessage());
                        e.printStackTrace();
                    }
                    System.out.println("Balance from the address: " + result.getTransactionHash());
                    tDb.deleteAttendanceSubjectwiseForBlockchain(batch_id, subject_id, ss.getString(3), ss.getString(4));
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


    class StudentAttendanceData{
        String student_roll, time_stamp, student_pk, subject_code;
        int batch_id, subject_id, AorP;

        public StudentAttendanceData(int batch_id,
                                     int subject_id,
                                     String subject_code,
                                     String student_roll,
                                     String student_pk,
                                     String time_stamp,
                                     int AorP){
            this.batch_id = batch_id;
            this.subject_id = subject_id;
            this.subject_code = subject_code;
            this.student_roll = student_roll;
            this.student_pk = student_pk;
            this.time_stamp = time_stamp;
            this.AorP = AorP;
        }

        public String getStudent_roll() {
            return student_roll;
        }

        public String getSubject_code() {
            return subject_code;
        }

        public String getStudent_pk() {
            return student_pk;
        }

        public void setStudent_roll(String student_roll) {
            this.student_roll = student_roll;
        }

        public String getTime_stamp() {
            return time_stamp;
        }

        public void setTime_stamp(String time_stamp) {
            this.time_stamp = time_stamp;
        }

        public Integer getBatch_id() {
            return batch_id;
        }

        public void setBatch_id(Integer batch_id) {
            this.batch_id = batch_id;
        }

        public Integer getSubject_id() {
            return subject_id;
        }

        public void setSubject_id(Integer subject_id) {
            this.subject_id = subject_id;
        }

        public Integer getAorP() {
            return AorP;
        }

        public void setAorP(Integer aorP) {
            AorP = aorP;
        }
    }


}
