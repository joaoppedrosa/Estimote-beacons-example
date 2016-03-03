package com.ubi.ubibeacons.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.connection.MotionState;
import com.ubi.ubibeacons.MainActivity;
import com.ubi.ubibeacons.R;
import com.ubi.ubibeacons.adapter.LogsAdapter;
import com.ubi.ubibeacons.beacons.Constants;
import com.ubi.ubibeacons.beacons.EstimoteBeaconsManager;
import com.ubi.ubibeacons.beacons.OnBeaconsAction;
import com.ubi.ubibeacons.beacons.State;
import com.ubi.ubibeacons.model.Notification;
import com.ubi.ubibeacons.utils.DateUtils;
import com.ubi.ubibeacons.utils.NotificationsBuilder;
import com.ubi.ubibeacons.utils.OnRegist;
import com.ubi.ubibeacons.utils.PreferencesManager;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;


public class LogFragment extends Fragment implements OnBeaconsAction, OnRegist {

    @Bind(R.id.listView)
    ListView listView;

    private PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();
    private LogsAdapter mAdapter;
    private WeakReference<Context> mContext;
    private List<Notification> mList;
    private Realm realm;
    private EstimoteBeaconsManager estimoteBeaconsManager;
    private Region mainRegion;
    private MainActivity mainActivity;

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);
        ButterKnife.bind(this,view);
        this.mContext = new WeakReference<Context>(getActivity());
        this.realm = Realm.getDefaultInstance();
        this.mList = realm.where(Notification.class).findAll();
        this.mAdapter = new LogsAdapter(mContext.get(),R.layout.row_logs, mList);
        this.listView.setAdapter(mAdapter);
        this.mainActivity = (MainActivity) getActivity();
        this.mainActivity.setListener(this);
        this.estimoteBeaconsManager = new EstimoteBeaconsManager(mContext.get(),this);
        this.mainRegion = estimoteBeaconsManager.createRegionForAllBeacons(Constants.UUID_BEACONS);

        return view;
    }

    private void onUpdate(){
        this.realm = Realm.getDefaultInstance();
        this.mList = realm.where(Notification.class).findAll();
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        estimoteBeaconsManager.startMonitoringBeacons(mainRegion);
    }

    @Override
    public void onPause() {
        super.onPause();
        estimoteBeaconsManager.stopMonitoringBeacons(mainRegion);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDiscoveryBeacon(List<Beacon> beacons) {

    }

    @Override
    public void showBeaconNotificationEnter(final String title, final String message) {
        final int id = DateUtils.getCurrentMiliseconds();
        String date = DateUtils.getCurrentDateTime();
        Intent notifyIntent = new Intent(mContext.get(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.putExtra("notification", true);
        notifyIntent.putExtra("id", id);
        notifyIntent.putExtra("title", title);
        notifyIntent.putExtra("message", message);
        notifyIntent.putExtra("date", date);

        if(preferencesManager.getPrefsState().equals("")){
            NotificationsBuilder.showNotificationBeacon(mContext.get(),notifyIntent,id,title,message);
            mainActivity.showCheckInDialog(String.valueOf(id),title,message,date);
        }else{
            if(preferencesManager.getPrefsState().equals(State.CHECK_OUT.toString())){
                NotificationsBuilder.showNotificationBeacon(mContext.get(),notifyIntent,id,title,message);
                mainActivity.showCheckInDialog(String.valueOf(id),title,message,date);
            }
        }
    }

    @Override
    public void showBeaconNotificationExit(final String title, final String message) {
        final int id = DateUtils.getCurrentMiliseconds();
        String date = DateUtils.getCurrentDateTime();
        Intent notifyIntent = new Intent(mContext.get(), MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.putExtra("notification", true);
        notifyIntent.putExtra("id", id);
        notifyIntent.putExtra("title", title);
        notifyIntent.putExtra("message", message);
        notifyIntent.putExtra("date", date);

        if(!preferencesManager.getPrefsState().equals("")){
            if(preferencesManager.getPrefsState().equals(State.CHECK_IN.toString())){
                NotificationsBuilder.showNotificationBeacon(mContext.get(),notifyIntent,id,title,message);
                mainActivity.showCheckOutDialog(String.valueOf(id),title,message,date);
            }
        }
    }

    @Override
    public void onBeaconTemperature(float temperature) {

    }

    @Override
    public void onBeaconMotionListener(MotionState motionState) {

    }

    @Override
    public void onCheckIn(final String id, final String title, final String message, final String date) {
        this.realm = Realm.getDefaultInstance();
        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String message = "You regist check-in at :\n" + date;
                Notification notification = realm.where(Notification.class).equalTo("id",id).findFirst();
                if(notification!=null){
                    notification.setMessage(message);
                    notification.setTitle(title);
                    Toast.makeText(mContext.get(), "Already insert...", Toast.LENGTH_SHORT).show();
                }else{
                    Notification n = new Notification(id,title,message);
                    realm.copyToRealm(n);
                }
                onUpdate();
            }
        });
    }

    @Override
    public void onCheckOut(final String id, final String title, final String message, final String date) {
        this.realm = Realm.getDefaultInstance();
        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                String message = "You regist check-out at :\n" + DateUtils.getCurrentDateTime();
                Notification notification = realm.where(Notification.class).equalTo("id",id).findFirst();
                if(notification!=null){
                    notification.setMessage(message);
                    notification.setTitle(title);
                    Toast.makeText(mContext.get(), "Already insert...", Toast.LENGTH_SHORT).show();
                }else{
                    Notification n = new Notification(id,title,message);
                    realm.copyToRealm(n);
                }
                onUpdate();
            }
        });
    }
}
