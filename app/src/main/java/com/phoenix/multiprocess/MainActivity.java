package com.phoenix.multiprocess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.phoenix.multiprocess.service.JobHandleService;
import com.phoenix.multiprocess.service.LocalService;
import com.phoenix.multiprocess.service.RemoteService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.startService(new Intent(this, LocalService.class));
        this.startService(new Intent(this, RemoteService.class));
        this.startService(new Intent(this, JobHandleService.class));
    }
}
