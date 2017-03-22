package com.phoenix.multiprocess.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by flashing on 2017/3/20.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobHandleService extends JobService {
    private int kJobId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "jobService create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG", "jobService start");
        //通过这种方式来启动我们的服务
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("TAG", "job start");
        boolean isLocalServiceWork = isServiceWork(this, "com.phoenix.multiprocess.service.LocalService");
        boolean isRemoteServiceWork = isServiceWork(this, "com.phoenix.multiprocess.service.RemoteService");
        if (!isLocalServiceWork || !isRemoteServiceWork){
            this.startService(new Intent(this, LocalService.class));
            this.startService(new Intent(this, RemoteService.class));
            Toast.makeText(this, "process start", Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("TAG", "job stop");
        scheduleJob(getJobInfo());
        return true;
    }

    public void scheduleJob(JobInfo t){
        Log.e("TAG", "Scheduling job");
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo(){
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, JobHandleService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        builder.setPeriodic(10);
        return builder.build();
    }

    /**
     * 判断某个服务是否正在运行的方法
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName){
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0){
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)){
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
