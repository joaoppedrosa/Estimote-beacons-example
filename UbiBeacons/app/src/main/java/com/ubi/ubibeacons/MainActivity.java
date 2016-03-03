package com.ubi.ubibeacons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.SystemRequirementsHelper;
import com.ubi.ubibeacons.beacons.State;
import com.ubi.ubibeacons.fragments.LogFragment;
import com.ubi.ubibeacons.fragments.NearFragment;
import com.ubi.ubibeacons.utils.OnRegist;
import com.ubi.ubibeacons.utils.PreferencesManager;
import com.ubi.ubibeacons.utils.SmartFragmentStatePagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = "MainActivity";
    private PreferencesManager preferencesManager = PreferencesManager.getPreferencesManager();
    private static final int LOGS = 0;
    private static final int NEAR = 1;
    private OnRegist onRegist;

    @Bind(R.id.pager)
    ViewPager viewPager;
    @Bind(R.id.buttonLog)
    Button buttonLog;
    @Bind(R.id.buttonNear)
    Button buttonNear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(viewPagerAdapter);
        this.viewPager.setOnPageChangeListener(this);

        if(preferencesManager.getPrefsState().equals("")){
            Toast.makeText(this,"To use the application turn on bluetooth and your location!",Toast.LENGTH_LONG).show();
        }
    }

    public void setListener(OnRegist onRegist){
        this.onRegist = onRegist;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null){
            if(extras.containsKey("notification")){
                String id = String.valueOf(extras.getInt("id"));
                String title = extras.getString("title");
                String message = extras.getString("message");
                String date = extras.getString("date");

                if(preferencesManager.getPrefsState().equals("")){
                    showCheckInDialog(id,title,message,date);
                }else{
                    if(preferencesManager.getPrefsState().equals(State.CHECK_IN.toString())){
                        showCheckInDialog(id,title,message,date);
                    }else{
                        showCheckOutDialog(id,title,message,date);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsHelper.checkAllPermissions(this);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == LOGS){
            buttonLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            buttonNear.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }else{
            buttonLog.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            buttonNear.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onLogsClick(View view) {
        buttonLog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        buttonNear.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        viewPager.setCurrentItem(LOGS);
    }

    public void onNearClick(View view) {
        buttonLog.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        buttonNear.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        viewPager.setCurrentItem(NEAR);
    }

    public class ViewPagerAdapter extends SmartFragmentStatePagerAdapter {
        private int NUM_ITEMS = 2;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case LOGS:
                    return LogFragment.newInstance();
                case NEAR:
                    return NearFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    public void showCheckInDialog(final String id, final String title, final String message, final String date){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_check_in, null);
        ImageView checkInButton = (ImageView) view.findViewById(R.id.checkin);

        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();

        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegist.onCheckIn(id,title,message,date);
                preferencesManager.setPrefsState(State.CHECK_IN.toString());
                materialDialog.dismiss();
            }
        });


    }

    public void showCheckOutDialog(final String id, final String title, final String message, final String date){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_check_out, null);
        ImageView checkInButton = (ImageView) view.findViewById(R.id.checkout);

        final MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();


        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegist.onCheckOut(id,title,message,date);
                preferencesManager.setPrefsState(State.CHECK_OUT.toString());
                materialDialog.dismiss();
            }
        });


    }
}
