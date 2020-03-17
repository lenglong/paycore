package com.yyc.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.yyc.paycore.PayCallback;
import com.yyc.paycore.PayCore;
import com.yyc.paycore.PayResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PayCore.getInstance().wxPay(this, "", new PayCallback() {
            @Override
            public void onPayBegin() {

            }

            @Override
            public void onPayCallback(PayResult result, String msg) {
                if(PayResult.SUCCESS.equals(result)){
                    //支付成功
                }
            }
        });
    }
}
