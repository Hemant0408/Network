package com.network.networklib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.network.networklibrary.CentralTendency;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CentralTendency.arithmeticMean(10);
    }
}
