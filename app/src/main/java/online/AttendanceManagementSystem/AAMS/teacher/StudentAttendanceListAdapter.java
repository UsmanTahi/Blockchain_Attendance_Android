package online.AttendanceManagementSystem.AAMS.teacher;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import online.AttendanceManagementSystem.AAMS.R;

public class StudentAttendanceListAdapter extends BaseAdapter {


    private final Activity context;
    private final ArrayList<GenerateAttendanceSheet.StudentAttendanceData> id;


    public StudentAttendanceListAdapter(Activity context, ArrayList<GenerateAttendanceSheet.StudentAttendanceData> id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public int getCount() {
        return id.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.student_list_items, null, true);

        TextView textView = rowView.findViewById(R.id.studentName);
        TextView textViewCode = rowView.findViewById(R.id.studentEmail);
        TextView textViewTimestamp = rowView.findViewById(R.id.timestamp);

        textView.setText(Html.fromHtml("<b>Roll : </b>"+id.get(position).getStudent_roll()));
//        textView.append("     "+Html.fromHtml("<b>Roll : </b>"+id.get(position).getStudent_roll()));

        int aOrP = id.get(position).getAorP();
        String attendance = (aOrP == 1 )? "Present" : "Absent";
        textViewCode.setText(Html.fromHtml("<b>Attendance : </b>"+attendance));

        textViewTimestamp.setText(Html.fromHtml("<b>Time : </b>"+id.get(position).getTime_stamp()));

//        textViewCode.append("     "+Html.fromHtml("<b>Mob : </b>"+id.get(position).getMob()));
        return rowView;
    }
}