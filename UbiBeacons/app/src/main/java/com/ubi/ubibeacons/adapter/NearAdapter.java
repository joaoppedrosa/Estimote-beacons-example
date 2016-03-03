package com.ubi.ubibeacons.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;
import com.ubi.ubibeacons.R;
import com.ubi.ubibeacons.beacons.Constants;
import com.ubi.ubibeacons.beacons.EstimoteBeaconsManager;

import java.util.List;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */
public class NearAdapter extends ArrayAdapter<Beacon> {
    private int layoutResourceId;
    private Context context;
    private List<Beacon> data;
    private EstimoteBeaconsManager estimoteBeaconsManager;

    public NearAdapter(Context context, int layoutResourceId, List<Beacon> data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.estimoteBeaconsManager = new EstimoteBeaconsManager(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Beacon getItem(int position) {
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
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.distance = (TextView) row.findViewById(R.id.distance);
            holder.proximity = (TextView) row.findViewById(R.id.proximity);
            row.setTag(holder);
        } else{
            holder = (Holder) row.getTag();
        }

        Beacon item = data.get(position);
        holder.distance.setText(String.format("Distance: (%.2fm)", Utils.computeAccuracy(item)));
        holder.proximity.setText("Proximity: " + Utils.computeProximity(item));
        holder.name.setText(estimoteBeaconsManager.placesNearBeacon(item).get(0));
        String value = Constants.getBeaconsType(item);
        switch (value){
            case Constants.BLUBERRY:
                holder.image.setImageResource(R.drawable.blueberry_beacons);
                break;
            case Constants.ICE:
                holder.image.setImageResource(R.drawable.ice_beacons);
                break;
            case Constants.MINT:
                holder.image.setImageResource(R.drawable.mint_beacons);
                break;
        }

        return row;
    }

    static class Holder {
        ImageView image;
        TextView name;
        TextView distance;
        TextView proximity;
    }
}
