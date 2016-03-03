package com.ubi.ubibeacons.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ubi.ubibeacons.R;
import com.ubi.ubibeacons.model.Notification;

import java.util.List;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class LogsAdapter extends ArrayAdapter<Notification> {
    private int layoutResourceId;
    private Context context;
    private List<Notification> data;

    public LogsAdapter(Context context, int layoutResourceId, List<Notification> data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Notification getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new Holder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.message = (TextView) row.findViewById(R.id.message);
            row.setTag(holder);
        } else{
            holder = (Holder) row.getTag();
        }

        Notification item = data.get(position);
        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());

        return row;
    }

    static class Holder {
        TextView title;
        TextView message;
    }
}
