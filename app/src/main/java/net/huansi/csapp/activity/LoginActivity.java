package net.huansi.csapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import net.huansi.csapp.MainActivity;
import net.huansi.csapp.R;
import net.huansi.csapp.bean.LoginBean;
import net.huansi.csapp.databinding.ActivityLoginBinding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;
import huansi.net.qianjingapp.utils.SPUtils;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static net.huansi.csapp.utils.Constants.IS_LOGIN;
import static net.huansi.csapp.utils.Constants.USER_COMPANY;
import static net.huansi.csapp.utils.Constants.USER_NAME;
import static net.huansi.csapp.utils.Constants.USER_PWD;
import static net.huansi.csapp.utils.Constants.USER_SECTION;

public class LoginActivity extends NotWebBaseActivity {

    private ActivityLoginBinding activityLoginBinding;
    private int REQUECT_READ_PHONE_STATE = 2;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        doNext(requestCode, grantResults);

    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == REQUECT_READ_PHONE_STATE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意权限,做跳转逻辑
                readPhoneState();
            } else {
                // 权限拒绝，提示用户开启权限
                denyPermission();
            }
        }
    }

    @Override
    public void init() {
        checkPhonePermission();
        activityLoginBinding = (ActivityLoginBinding) viewDataBinding;
        activityLoginBinding.tvFindPSW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ConfirmPSWActivity.class);
                startActivity(intent);
            }
        });
        String mobileNo = SPUtils.getMobileNo(this);
        String psw = SPUtils.getSpData(this, USER_PWD, "8");

        activityLoginBinding.etPhoneNum.setText(mobileNo);
        activityLoginBinding.etPassword.setText(psw);

    }

    /*
     校验手机状态权限
    */
    private void checkPhonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有读取手机状态权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUECT_READ_PHONE_STATE);
            } else {
                //具有权限
                readPhoneState();
            }
        } else {
            //系统不高于6.0直接执行
            readPhoneState();
        }
    }

    private void denyPermission() {
        Toast.makeText(this, "读取手机状态权限已经拒绝！", Toast.LENGTH_SHORT).show();
    }

    private void readPhoneState() {
        Toast.makeText(this, "读取手机状态成功！", Toast.LENGTH_SHORT).show();
    }


    /**
     * 登陆按钮监听
     *
     * @param view
     */
    public void login(View view) {
        String isLogin = SPUtils.getSpData(LoginActivity.this, IS_LOGIN, "false");
        String phoneNum = activityLoginBinding.etPhoneNum.getText().toString();
        String password = activityLoginBinding.etPassword.getText().toString();
        String mobileNo = SPUtils.getMobileNo(this);
        String psw = SPUtils.getSpData(this, USER_PWD, "8");
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNum)) {
            OthersUtil.ToastMsg(LoginActivity.this, "账号或者密码不能为空！");
        } else if ( phoneNum.equals(mobileNo)&&psw.equals(password)&&isLogin.equals("true")) {
            getLogin(phoneNum,md5(password));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (isLogin.equals("false")) {
            getLogin(phoneNum,md5(password));
        } else {
            OthersUtil.ToastMsg(LoginActivity.this, "账号或者密码不正确！");
        }
        //测试
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    /**
     * md5加密
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void getLogin(final String phoneNum, final String password){

    RxjavaWebUtils.requestByGetJsonData(this, CUS_SERVICE,
            "spappYunEquUserLogin"
            , "sMobileNo=" + phoneNum + ",sPassword=" + password,
            LoginBean.class.getName(), true, "", new SimpleHsWeb() {
                @Override
                public void success(HsWebInfo hsWebInfo) {
                    List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                    LoginBean loginBean = null;
                    for (int i = 0; i < entities.size(); i++) {
                        loginBean = (LoginBean) entities.get(i);
                    }
                    if (loginBean != null && !loginBean.SMOBILENO.isEmpty()) {
                        SPUtils.saveMobileNo(LoginActivity.this,phoneNum);
                        SPUtils.saveSpData(LoginActivity.this,USER_PWD,password);
                        SPUtils.saveSpData(LoginActivity.this, USER_NAME, loginBean.SUSERNAME);
                        SPUtils.saveSpData(LoginActivity.this, USER_SECTION, loginBean.SDEPTNAME);
                        SPUtils.saveSpData(LoginActivity.this, USER_COMPANY, loginBean.SCOMPANYNAME);
                        SPUtils.saveSpData(LoginActivity.this, IS_LOGIN, "true");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        OthersUtil.ToastMsg(LoginActivity.this, "账号或者密码不正确！");
                    }

                }

                @Override
                public void error(HsWebInfo hsWebInfo, Context context) {
                    super.error(hsWebInfo, context);
                }
            });


}



}
