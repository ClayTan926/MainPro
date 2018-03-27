package com.ztwifi.wallet.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;
import com.ztwifi.wallet.R;
import com.ztwifi.wallet.impl.GetInfo;
import com.ztwifi.wallet.impl.InfoCallBack;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    CtActEnvHelper.OnCtViewUrlExecEvent urlEvt;
    private String uid;
    private GetInfo getInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uid= UserApp.curApp().getGParamValue("CUR_UID");
        Log.e(TAG, "onCreate: " +uid);
        urlEvt=new CtActEnvHelper.OnCtViewUrlExecEvent() {
            @Override
            public boolean doExecUrl(Activity activity, View view, String s) {

                return doExecUrlEx(activity,view,s);
            }
        };

        CtActEnvHelper.createCtTagUI(this, null, urlEvt);//初始化CT界面，可和获取CT Tag等功能

    }

    private boolean doExecUrlEx(Activity activity, View view, String s) {
        InfoCallBack infoCallback=new InfoCallBack(activity);
        infoCallback.registerLoginCallback(uid);

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsLogin();

    }

    private void checkIsLogin(){
        //获取当前登录用户信息
        getInfo=new GetInfo(this);
        getInfo.getLoginInfo(uid);
    }
}
