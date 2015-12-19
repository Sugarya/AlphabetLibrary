package com.sugarya.example.alphabetdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sugarya.example.alphabetdemo.event.AlphabetValueEvent;
import com.sugarya.example.alphabetdemo.utils.LogUtils;

import de.greenrobot.event.EventBus;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "LoginActivity";

    private TextView mTxtCountry,mTxtCode;
    private RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);

        mTxtCountry = (TextView)findViewById(R.id.common_country_name_txt);
        mTxtCode = (TextView)findViewById(R.id.activity_login_country_code_txt);
        mLayout = (RelativeLayout)findViewById(R.id.common_country_code_relative);

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,CountryCodeListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        LogUtils.e(TAG,"onStart");
        super.onStart();
    }

    public void onEventMainThread(AlphabetValueEvent value){
        if(value != null){
            String telephoneInfo = value.itemValue;
            LogUtils.e(TAG, "telephoneInfo=" + telephoneInfo);

            String[] strings = telephoneInfo.split("\\+");
            if(strings != null){
                if(strings.length>=2){
                    mTxtCountry.setText(strings[0]);
                    mTxtCode.setText("+"+strings[1]);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        LogUtils.e(TAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
