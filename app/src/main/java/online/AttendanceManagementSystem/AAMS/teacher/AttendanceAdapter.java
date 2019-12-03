package online.AttendanceManagementSystem.AAMS.teacher;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import online.AttendanceManagementSystem.AAMS.R;

public class AttendanceAdapter extends BaseAdapter {
    private final Activity context;
    private final ArrayList<BatchAndSubWiseAtten.AttendanceData> id;


    public AttendanceAdapter(Activity context, ArrayList<BatchAndSubWiseAtten.AttendanceData> id) {
        this.context = context;
        this.id = id;
    }

    @Override
    public int getCount() {
        return id.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("checkpoint 4", id.get(position).getName());
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.subject_list_item, null, true);
        Log.e("checkpoint 5", id.get(position).getName());

        TextView textView = rowView.findViewById(R.id.s_name);
        TextView textViewCode = rowView.findViewById(R.id.s_code);
        Log.e("checkpoint 6", id.get(position).getName());

        textView.setText(Html.fromHtml("<b>Student Name : </b>"+id.get(position).getName()));
        textViewCode.setText(Html.fromHtml("<b>Student Roll : </b>"+id.get(position).getRoll()));
        Log.e("checkpoint ", id.get(position).getName());

        return rowView;

    }
}
