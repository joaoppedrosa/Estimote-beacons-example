package com.ubi.ubibeacons.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.connection.MotionState;
import com.ubi.ubibeacons.R;
import com.ubi.ubibeacons.adapter.NearAdapter;
import com.ubi.ubibeacons.beacons.Constants;
import com.ubi.ubibeacons.beacons.EstimoteBeaconsManager;
import com.ubi.ubibeacons.beacons.OnBeaconsAction;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author João Pedro Pedrosa, UbiBeacons on 29/02/2016.
 */

public class NearFragment extends Fragment implements OnBeaconsAction {

    @Bind(R.id.listView)
    ListView listView;

    private static final String TAG = "NearFragment";
    private WeakReference<Context> mContext;
    private EstimoteBeaconsManager estimoteBeaconsManager;
    private Region mainRegion;

    public static NearFragment newInstance() {
        return new NearFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near, container, false);
        ButterKnife.bind(this,view);
        this.mContext = new WeakReference<Context>(getActivity());

        this.estimoteBeaconsManager = new EstimoteBeaconsManager(mContext.get(),this);
        this.mainRegion = estimoteBeaconsManager.createRegionForAllBeacons(Constants.UUID_BEACONS);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        estimoteBeaconsManager.startRangingBeacons(mainRegion);
    }

    @Override
    public void onPause() {
        super.onPause();
        estimoteBeaconsManager.stopRangingBeacons(mainRegion);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDiscoveryBeacon(final List<Beacon> beacons) {
        if (!beacons.isEmpty()) {
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   NearAdapter nearAdapter = new NearAdapter(getActivity(), R.layout.row_near, beacons);
                   listView.setAdapter(nearAdapter);
                   nearAdapter.notifyDataSetChanged();
               }
           });
        }
    }

    @Override
    public void showBeaconNotificationEnter(String title, String message) {

    }

    @Override
    public void showBeaconNotificationExit(String title, String message) {

    }

    @Override
    public void onBeaconTemperature(float temperature) {

    }

    @Override
    public void onBeaconMotionListener(MotionState motionState) {

    }
}
