package online.forgottenbit.attendance1.teacher;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import online.forgottenbit.attendance1.R;

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
                        ss.getInt(1),ss.getString(2),
                        ss.getString(3), ss.getInt(4)));
            }
        }else{
            Toast.makeText(this, "No Student found. Add student.", Toast.LENGTH_SHORT).show();
        }

        studentAttendanceListAdapter = new StudentAttendanceListAdapter(GenerateAttendanceSheet.this, list);
        studentAttendanceList.setAdapter(studentAttendanceListAdapter);
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
                        ss.getInt(1),ss.getString(2),
                        ss.getString(3), ss.getInt(4)));
            }
        }else{
            Toast.makeText(this, "No Student found. Add student.", Toast.LENGTH_SHORT).show();
        }

        studentAttendanceListAdapter = new StudentAttendanceListAdapter(GenerateAttendanceSheet.this, list);
        studentAttendanceList.deferNotifyDataSetChanged();

    }

    class StudentAttendanceData{
        String student_roll, time_stamp;
        int batch_id, subject_id, AorP;

        public StudentAttendanceData(int batch_id,
                                     int subject_id,
                                     String student_roll,
                                     String time_stamp,
                                     int AorP){
            this.batch_id = batch_id;
            this.subject_id = subject_id;
            this.student_roll = student_roll;
            this.time_stamp = time_stamp;
            this.AorP = AorP;
        }

        public String getStudent_roll() {
            return student_roll;
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
