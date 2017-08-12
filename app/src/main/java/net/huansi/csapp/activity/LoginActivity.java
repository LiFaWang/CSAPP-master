package net.huansi.csapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import net.huansi.csapp.MainActivity;
import net.huansi.csapp.R;
import net.huansi.csapp.bean.LoginBean;
import net.huansi.csapp.databinding.ActivityLoginBinding;
import net.huansi.csapp.utils.SpUtils;

import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static net.huansi.csapp.utils.Constants.IS_LOGIN;
import static net.huansi.csapp.utils.Constants.USER_COMPANY;
import static net.huansi.csapp.utils.Constants.USER_NAME;
import static net.huansi.csapp.utils.Constants.USER_PWD;
import static net.huansi.csapp.utils.Constants.USER_SECTION;

public class LoginActivity extends NotWebBaseActivity {

     private String phoneNum="";//电话号码
     private String sPassword="";//密码
    private ActivityLoginBinding activityLoginBinding;
    private int REQUECT_READ_PHONE_STATE=2;

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

                Intent intent=new Intent(LoginActivity.this,ConfirmPSWActivity.class);
                startActivity(intent);
            }
        });

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
        Toast.makeText(this,"读取手机状态权限已经拒绝！",Toast.LENGTH_SHORT).show();
    }

    private void readPhoneState() {
        Toast.makeText(this,"读取手机状态成功！",Toast.LENGTH_SHORT).show();
    }


    /**
     * 登陆按钮监听
     * @param view
     */
    public void login(View view){
        String isLogin = SpUtils.getSpData(LoginActivity.this, IS_LOGIN, "false");
        String password = activityLoginBinding.etPassword.getText().toString();
        String phoneNum = activityLoginBinding.etPhoneNum.getText().toString();
        //测试
         if(phoneNum.equals("18063396908")&& password.equals(SpUtils.getSpData(this,USER_PWD,"8"))){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else if(isLogin.equals("false")){
             getLoginMes();

         }else{
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void getLoginMes() {
        RxjavaWebUtils.requestByGetJsonData(this, CUS_SERVICE,
                "spappYunEquUserLogin"
                , "sMobileNo="+phoneNum+",sPassword="+sPassword,
                LoginBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        LoginBean loginBean=null;
                        for (int i = 0; i < entities.size(); i++) {
                             loginBean = (LoginBean) entities.get(i);
                        }
                        if(loginBean!=null&&!loginBean.SMOBILENO.isEmpty()){
                            SpUtils.saveMobileNo(LoginActivity.this,loginBean.SMOBILENO);
                            SpUtils.saveSpData(LoginActivity.this,USER_NAME,loginBean.SUSERNAME);
                            SpUtils.saveSpData(LoginActivity.this,USER_SECTION,loginBean.SDEPTNAME);
                            SpUtils.saveSpData(LoginActivity.this,USER_COMPANY,loginBean.SCOMPANYNAME);
                            SpUtils.saveSpData(LoginActivity.this,IS_LOGIN,"true");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            OthersUtil.ToastMsg(LoginActivity.this,"账号或者密码不正确！");
                        }

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                    }
                });

    }


}
