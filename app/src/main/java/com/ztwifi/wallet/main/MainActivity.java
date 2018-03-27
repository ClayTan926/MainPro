package com.ztwifi.wallet.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netted.ba.ct.UserApp;
import com.netted.ba.ctact.AppUrlManager;
import com.netted.ba.ctact.CtActEnvHelper;

import com.netted.ba.ctact.CtDataLoader;
import com.netted.ba.ctact.CtUrlDataLoader;
import com.netted.ba.login.CtLogoutHelper;
import com.ztwifi.wallet.R;
import com.ztwifi.wallet.impl.LoginUtil;
import com.ztwifi.wallet.impl.UserLogin;
import com.ztwifi.wallet.impl.WifiCallback;
import com.ztwifi.wallet.impl.ZtWifisLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int isLogin=1;

    @BindView(R.id.et_userName)
    EditText etUserName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.web_view)
    WebView webView;

    private String username;
    private String password;
    private String nick="";
    private String iconUrl="";
    private String sign="";
    UserLogin userLogin;
    LoginUtil loginUtil;
    WifiCallback wifiCallBack;
    CtActEnvHelper.OnCtViewUrlExecEvent urlEvt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        username="sa";
        //sdk初始化
        ZtWifisLoader z=new ZtWifisLoader(this,username,"","");
        z.init(wifiCallBack);
        Log.e("MainActivity", "onCreate: ");
        initWebView();
        urlEvt=new CtActEnvHelper.OnCtViewUrlExecEvent() {
            @Override
            public boolean doExecUrl(Activity activity, View view, String s) {
                Log.e(TAG, "doExecUrl: 初始化" );
                return doExecUrlEx(activity,view,s);
            }
        };
        CtActEnvHelper.createCtTagUI(MainActivity.this, null, urlEvt);//初始化CT界面，可和获取CT Tag等功能
    }

    private void initWebView(){
        webView.loadUrl("file:///android_asset/game.html");

    }

    private boolean doExecUrlEx(Activity activity,View view, String url) {

            Log.e(TAG, "doExecUrlEx: url:"+url );
        if (url.startsWith("cmd://doLogin/")) {
            username = etUserName.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            UserApp.curApp().setGParamValue("CUR_UID",username);
            UserApp.curApp().setGParamValue("CUR_ISLOGIN","1");
            Log.e(TAG, "doExecUrlEx: "+UserApp.curApp().getGParamValue("CUR_UID") );
            checkInfo(username,password,sign);
            getUserLoginInfo(activity,1);

            Toast.makeText(this, "username:" + username + "password:" + password, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (url.startsWith("cmd://logout/")){
            getUserLoginInfo(activity,2);
            UserApp.curApp().setSharePrefParamValue("CUR_ISLOGIN","2");

            Toast.makeText(this, "当前用户已退出", Toast.LENGTH_SHORT).show();
            return true;

        }
        return false;
    }

    /**
     * 用户登录状态变更时，获取当前登录用户信息
     * @param context
     */
    private void getUserLoginInfo(Context context,int isLogin){

        userLogin=new UserLogin(context);
        userLogin.userLogin(isLogin,username,nick,iconUrl);
    }


    /**
     * 检查登陆信息
     * @param username 用户名
     * @param password 密码
     * @param sign  登陆密令
     */
    private void checkInfo(String username,String password,String sign) {

        CtUrlDataLoader ld = new CtUrlDataLoader();
        ld.init(this, 1);
        ld.custDataUrl = "ztwifi://userStatus?action=login&userName="+username+"&password="+password+"&sign="+sign;
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

    /**
     * @param sign 登陆密令
     */
    private void logoutInfo(String sign) {

        CtUrlDataLoader ld = new CtUrlDataLoader();
        ld.init(this, 1);
        ld.custDataUrl = "ztwifi://userStatus?action=logout&sign="+sign;
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
        ld.loadingMessage = "注销中...";
        ld.loadData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy: " );
    }
}
