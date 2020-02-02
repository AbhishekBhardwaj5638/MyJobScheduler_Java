package com.bhardwaj.abhishek.myjobschedulerjava;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.BufferUnderflowException;

public class MainActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    private JobScheduler jobScheduler;
    private static int JOB_ID = 0;
    private Switch mDeviceIdle,mDeviceCharging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView seekBarProgress = (TextView)findViewById(R.id.seekBarLabel);

        mSeekBar =(SeekBar)findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i>0){
                    seekBarProgress.setText(i + " s");
                }
                else{
                    seekBarProgress.setText(R.string.notset);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDeviceIdle = (Switch)findViewById(R.id.idleSwitch);
        mDeviceCharging = (Switch)findViewById(R.id.chargingSwitch);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
        }
        Button bSchedule = (Button)findViewById(R.id.scheduleButton);
        final Button bCancelJobs = (Button)findViewById(R.id.cancelJobButton);
        bSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleJob();
            }
        });

        bCancelJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelJobs();

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob(){

        RadioGroup networkOptions = findViewById(R.id.networkOptions);

        int selectedNetworkId = networkOptions.getCheckedRadioButtonId();
        int selectedOption = JobInfo.NETWORK_TYPE_NONE;

        int seekBarInteger = mSeekBar.getProgress();
        boolean seekBarSet = seekBarInteger >0 ;





        switch (selectedNetworkId){
            case R.id.noNetwork:
                selectedOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkId = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        ComponentName serviceName = new ComponentName(getPackageName(),
                MyNotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID,serviceName);
        builder.setRequiredNetworkType(selectedOption);
        builder.setRequiresDeviceIdle(mDeviceIdle.isChecked());
        builder.setRequiresCharging(mDeviceCharging.isChecked());
        if (seekBarSet){
            builder.setOverrideDeadline(seekBarInteger*1000);
        }

        boolean constraintSet = (selectedOption != JobInfo.NETWORK_TYPE_NONE)
                ||mDeviceCharging.isChecked()||mDeviceIdle.isChecked()||seekBarSet;


        if (constraintSet){
            JobInfo jobInfo=builder.build();
            jobScheduler.schedule(jobInfo);

            Toast.makeText(this, "Job Scheduled, job will run"+"the constraint are met", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please set at least one constraint", Toast.LENGTH_SHORT).show();
        }
       }

    public void cancelJobs(){
        if (jobScheduler!=null){
            jobScheduler.cancelAll();
            jobScheduler=null;
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }


}
