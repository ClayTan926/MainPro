package com.ztwifi.wallet;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netted.ba.ct.NetUtil;
import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.CtActEnvHelper;

import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CtUrlDataLoader;
import com.ztwifi.wallet.impl.LoginUtil;
import com.ztwifi.wallet.impl.WifiCallback;
import com.ztwifi.wallet.impl.ZtWifisLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.et_userName)
    EditText etUserName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private String username;
    private String password;

    LoginUtil loginUtil;
    WifiCallback wifiCallBack;
    CtActEnvHelper.OnCtViewUrlExecEvent urlEvt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        CtActEnvHelper.createCtTagUI(this, null, urlEvt);//初始化CT界面，可和获取CT Tag等功能
        Log.e("MainActivity", "onCreate: ");
        initView();
    }

    private void initView(){
        urlEvt=new CtActEnvHelper.OnCtViewUrlExecEvent() {
            @Override
            public boolean doExecUrl(Activity activity, View view, String s) {
                Log.e(TAG, "doExecUrl: 初始化" );

                return doExecUrlEx(activity,view,s);
            }
        };

    }

    private boolean doExecUrlEx(Activity activity,View view, String url) {
        Log.e(TAG, "doExecUrlEx: 获取到的执行URL为"+url );
        if (url.startsWith("cmd://doLogin/")) {

            Log.e(TAG, "do: 获取到的执行URL为"+url+" activity:"+activity+view );
            return true;
        }
        return false;
    }

    private void checkWxBindInfo(String code) {
        // 向后台查询微信绑定的用户信息
        CtUrlDataLoader ld = new CtUrlDataLoader();
        ld.init(this, 1);
        ld.custDataUrl = "/ctwx_access.nx?action=access_token&code="
                + NetUtil.urlEncode(code);
        ld.needVerifyCode = true;
        ld.cacheExpireTm = 0;
        CtDataLoader.OnCtDataEvent evt = new CtDataLoader.OnCtDataEvent() {
            @Override
            public void onDataCanceled() {
                UserApp.showToast("操作中止");
            }

            @Override
            public void onDataError(String msg) {
                UserApp.showToast("出现错误：" + msg);
            }

            @Override
            public void onDataLoaded(CtDataLoader cth) {

            }

            @Override
            public void afterFetchData() {
            }
        };
        ld.setCtDataEvt(evt);
        ld.loadingMessage = "正在检查登录信息...";
        ld.loadData();
    }

    @OnClick({R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                username = etUserName.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                ZtWifisLoader z=new ZtWifisLoader(this,username,"","");
                z.init(wifiCallBack);
                Toast.makeText(this, "username:" + username + "password:" + password, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy: " );
    }
}
