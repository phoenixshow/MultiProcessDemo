package com.phoenix.multiprocess.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.phoenix.multiprocess.IMyAidlInterface;
import com.phoenix.multiprocess.MainActivity;
import com.phoenix.multiprocess.R;

/**
 * Created by flashing on 2017/3/19.
 */

public class LocalService extends Service {
    private MyBinder binder;
    private MyServiceConnection conn;
    private PendingIntent pIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(binder == null){
            binder = new MyBinder();
        }
        conn = new MyServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * 连接远程服务
         */
        this.bindService(new Intent(this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        //提高服务优先级，避免过多被杀掉，采用360的方式
//        Notification notification = new Notification(R.mipmap.ic_launcher, "安全服务启动中", System.currentTimeMillis());
//        pIntent = PendingIntent.getService(this, 0, intent, 0);
//        notification.setLatestEventInfo(this, "菲尼克斯安全服务", "防火防盗防菲尼克斯", pIntent);
//        startForeground(startId, notification);

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(LocalService.this);
        builder.setSmallIcon(R.mipmap.ic_launcher); //设置图标
        builder.setTicker("安全服务启动中");
        builder.setContentTitle("菲尼克斯安全服务"); //设置标题
        builder.setContentText("防火防盗防菲尼克斯"); //消息内容
        builder.setWhen(System.currentTimeMillis()); //发送时间
//        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
//        builder.setAutoCancel(true);//打开程序后图标消失
        builder.setContentIntent(pIntent);
        Notification notification = builder.build();//build()方法要求API版本大于等于16
        manager.notify(124, notification); // 通过通知管理器发送通知

        //不要返回super.onStartCommand(……)，也是为了提高优先级，避免被清理
        return START_STICKY;
    }

    class MyBinder extends IMyAidlInterface.Stub{
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    class MyServiceConnection implements ServiceConnection{
        //连接成功
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //说明远程服务挂了
            LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
            //连接远程服务
            LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), conn, Context.BIND_IMPORTANT);
        }
    }
}
