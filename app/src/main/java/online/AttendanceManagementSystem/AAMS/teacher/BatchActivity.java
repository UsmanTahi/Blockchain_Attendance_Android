package online.AttendanceManagementSystem.AAMS.teacher;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import online.AttendanceManagementSystem.AAMS.R;
import online.AttendanceManagementSystem.AAMS.contract.Attendees;
import online.AttendanceManagementSystem.AAMS.util.Web3jConstants;
import online.AttendanceManagementSystem.AAMS.util.Web3jUtils;

public class BatchActivity extends AppCompatActivity {


    TeacherDB tDB = null;

    int batch_id = 0;
    EditText code,name;


    ListView subjectList;
    SubjectAdapter adapter;
    ArrayList<SubjectData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);

        Intent i = getIntent();
        String title = i.getStringExtra("name");
        batch_id = i.getIntExtra("id",0);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setElevation(0);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tDB = new TeacherDB(BatchActivity.this);

        subjectList = findViewById(R.id.subject_list);

        list = new ArrayList<>();

        Cursor ss = tDB.getSubjectByBatch(batch_id);
        if(ss!=null && ss.getCount()>0){

            while (ss.moveToNext()){
                list.add(new SubjectData(ss.getInt(0),ss.getString(2),ss.getString(3)));
            }
        }else{
            Toast.makeText(this, "No Subject found. Add Subject.", Toast.LENGTH_SHORT).show();
        }

        adapter = new SubjectAdapter(BatchActivity.this, list);

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(BatchActivity.this, BatchAndSubWiseAtten.class);
                i.putExtra("name",list.get(position).getName());
                i.putExtra("sub_id",list.get(position).getId());
                i.putExtra("code",list.get(position).getCode());
                i.putExtra("batch_id",batch_id);
                startActivity(i);
            }
        });
        subjectList.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.batch_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.addSubjects:
                addBatch();
                break;

            case R.id.syncSubjectsWithBlockchain:
                showDialog("Sync subjects", "Do you want to start the sync ?");
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void syncSubjectsWithBlockchain() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Attendees addSubjectContract = null;
        addSubjectContract = Attendees.load(Web3jConstants.CONTRACT_ADDRESS, TeacherDashboard.web3j,
                Web3jUtils.getCredentials(), new DefaultGasProvider());

        Log.i("b", "Add Subject loaded successfully");

        Cursor ss = tDB.getSubjectsForBlockchain();
        if(ss!=null && ss.getCount()>0){
            while (ss.moveToNext()){
                TransactionReceipt result = null;
                try {
                    String subject_code = ss.getString(1);

                    result = addSubjectContract.addNewSubject(Web3jUtils.getWalletAddress(),
                            subject_code, getIntent().getStringExtra("name")).send();
                    System.out.println("result " + result);
                } catch (InterruptedException e) {
                    Log.e("b", "Error in add subject transaction: " + e.getMessage());
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    Log.e("b", "Error in add subject transaction: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("b", "Error in add subject transaction: " + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("Transaction Hash from the address: " + result.getTransactionHash());
                tDB.deleteSubjectsPushedToBlockchain(ss.getString(1));
            }
        }
    }

    public void showDialog(String title, String message){
        new AlertDialog.Builder(BatchActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        syncSubjectsWithBlockchain();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
            }).show();
    }

    public void addBatch() {

        LayoutInflater inflater = BatchActivity.this.getLayoutInflater();

        View view = inflater.inflate(R.layout.batch_add_custom_dialog, null);

        final Dialog dialogAddBatch = new Dialog(BatchActivity.this);
        dialogAddBatch.setContentView(view);

        name = view.findViewById(R.id.batch_name);
        name.setHint("Enter Subject name");

        TextView heading = view.findViewById(R.id.txt);
        heading.setText("Add new Subject here");


        code = view.findViewById(R.id.sub_code);
        code.setVisibility(View.VISIBLE);

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
                if (name.getText().toString().trim() != null && !name.getText().toString().trim().isEmpty() && code.getText().toString().trim() != null && !code.getText().toString().trim().isEmpty()) {

                    //notify data set changes in adapter
                    code.setVisibility(View.GONE);
                    Cursor ss = tDB.getSubjectByBatch(batch_id);
                    int newSubjectFlag = 0;
                    while (ss.moveToNext()) {
                        if (ss.getString(3).toLowerCase().trim().equals(code.getText().toString().toLowerCase().trim())) {
                            newSubjectFlag = 1;
                        }
                    }
                    if (newSubjectFlag == 0) {
                        tDB.insertSubjectForBlockchain(code.getText().toString().trim());
                    }
                    tDB.insertSubject(batch_id, name.getText().toString().trim(), code.getText().toString().trim());

                    ss = tDB.getSubjectByBatch(batch_id);
                    if (ss != null && ss.getCount() > 0) {
                        if (list != null && list.size() > 0) {
                            list.clear();
                        }
                        while (ss.moveToNext()) {
                            list.add(new SubjectData(ss.getInt(0), ss.getString(2), ss.getString(3)));
                        }
                    } else {
                        Toast.makeText(BatchActivity.this, "No Subject found. Add Subject.", Toast.LENGTH_SHORT).show();
                    }

                    adapter = new SubjectAdapter(BatchActivity.this, list);
                    adapter.notifyDataSetChanged();

                    dialogAddBatch.cancel();
                }
            }
        });
    }

    class SubjectData{
        private int id;
        private  String name,code;

        public SubjectData(int id, String name,String code) {
            this.id = id;
            this.name = name;
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
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

}
