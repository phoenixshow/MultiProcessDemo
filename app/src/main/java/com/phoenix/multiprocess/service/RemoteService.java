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
import com.phoenix.multiprocess.R;

/**
 * Created by flashing on 2017/3/19.
 */

public class RemoteService extends Service {
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
         * 连接本地服务
         *
         * 参数Context.BIND_IMPORTANT指定绑定标示符，可提高服务的优先级别
         * Android 4.0(API 14) 引入了一些新的标示符：
         * 1.BIND_ADJUST_WITH_ACTIVITY  Service的优先级将相对于其绑定的Activity，Activity到前台，则Service优先级相对提升，Activity到后台，则Servcie优先级相对降低。
         * 2.BIND_ABOVE_CLIENT和BIND_IMPORTANT  当你的客户端在前台，这个标示符下的Service也变得重要性相当于前台的Activity，优先级迅速提升。若是BIND_ABOVE_CLIENT，则优先级已经超过了Activity，也就是说Activity要比Service先死，当资源不够的时候。
         * 3.BIND_NOT_FOREGROUND 你所绑定的Service优先级永远高不过前台Activity。
         * 4.BIND_WAIVE_PRIORITY 绑定的服务不可调整自身的优先级。
         */
        this.bindService(new Intent(this, LocalService.class), conn, Context.BIND_IMPORTANT);

        //提高服务优先级，避免过多被杀掉，采用360的方式
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(RemoteService.this);
        builder.setSmallIcon(R.mipmap.ic_launcher); //设置图标
        builder.setTicker("安全服务启动中");
        builder.setContentTitle("菲尼克斯安全服务"); //设置标题
        builder.setContentText("防火防盗防菲尼克斯"); //消息内容
        builder.setWhen(System.currentTimeMillis()); //发送时间
        builder.setContentIntent(pIntent);
        Notification notification = builder.build();//build()方法要求API版本大于等于16
        manager.notify(125, notification); // 通过通知管理器发送通知

        //不要返回super.onStartCommand(……)，也是为了提高优先级，避免被清理
        return START_STICKY;
    }

    class MyBinder extends IMyAidlInterface.Stub{
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    class MyServiceConnection implements ServiceConnection {
        //连接成功
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //复活本地服务
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
            //连接本地服务
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), conn, Context.BIND_IMPORTANT);
        }
    }
}
