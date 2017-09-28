package net.huansi.csapp.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.huansi.csapp.R;
import net.huansi.csapp.bean.AuthCodeBean;
import net.huansi.csapp.databinding.ActivityConfirmPswBinding;
import net.huansi.csapp.utils.CountDownTimerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.DeviceUtil;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.HS_SERVICE_LATER;

public class ConfirmPSWActivity extends NotWebBaseActivity {


    private ActivityConfirmPswBinding mConfirmPswBinding;
//    private Map<String, String> authCodeMap=new HashMap<>();
//    private String mPhoneNub;
    private LoadProgressDialog mDialog;
//    private String mSsmcheckno1;
    private AuthCodeBean authCodeBean;
    private long mAuthCodeTimeMillis;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_psw;
    }



    @Override
    public void init() {
        mDialog=new LoadProgressDialog(this);
        mConfirmPswBinding = (ActivityConfirmPswBinding) viewDataBinding;
//        if (mPhoneNub.isEmpty()){
//            OthersUtil.ToastMsg(ConfirmPSWActivity.this,"手机号不能为空");
//        }else {
//            authCodeMap.put("sDestNo", mPhoneNub);
//            authCodeMap.put("sSourceIP", DeviceUtil.getPhoneDrivceNo(getApplicationContext()));
//        }



        mConfirmPswBinding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //获取验证码
        mConfirmPswBinding.tvAuthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getAuthCode();
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mConfirmPswBinding.tvAuthCode, 60000, 1000);
                mCountDownTimerUtils.start();

            }
        });

    }


    private void getAuthCode() {
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        String phoneNub=mConfirmPswBinding.etPhoneNum.getText().toString();
                        Map<String,String>map=new HashMap<>();
                        map.put("sDestNo", phoneNub);
                        map.put("sSourceIP", DeviceUtil.getPhoneDrivceNo(getApplicationContext()));
                        return NewRxjavaWebUtils.getNormalFunction(getApplicationContext(), HS_SERVICE_LATER, "SendShortMessage", map,
                                AuthCodeBean.class.getName(), true, "输入有错误");
                    }
                }), getApplicationContext(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> entities=hsWebInfo.wsData.LISTWSDATA;
                authCodeBean= (AuthCodeBean) entities.get(0);
                mAuthCodeTimeMillis = System.currentTimeMillis();
            }

            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                super.error(hsWebInfo, context);
                OthersUtil.dismissLoadDialog(mDialog);
            }
        });

        }


//        RxjavaWebUtils.requestByNormalFunction(this, HS_SERVICE, phoneNub, "SendShortMessage", "", WsData.class.getName(), true, "输入有错误", new SimpleHsWeb() {
//            @Override
//            public void success(HsWebInfo hsWebInfo) {
//
//            }
//
//            @Override
//            public void error(HsWebInfo hsWebInfo, Context context) {
//                super.error(hsWebInfo, context);
//
//            }
//        });

    public void commit(View v){
        String authCode = mConfirmPswBinding.etAuthCode.getText().toString();
        if(authCodeBean==null){
            OthersUtil.ToastMsg(getApplicationContext(),"请发送验证码！");
            return;
        }
        if(!authCode.equals(authCodeBean.SSMCHECKNO1)){
            OthersUtil.ToastMsg(getApplicationContext(),"验证码错误！");
            return;
        }

        //TODO 判断时间是否过时
        long commitTimeMillis = System.currentTimeMillis();
        if (commitTimeMillis-mAuthCodeTimeMillis>3*60*1000){
            OthersUtil.ToastMsg(getApplicationContext(),"验证码已超时，请重新获取！");
            return;
        }
        Intent intent=new Intent(this,ResetPSWActivity.class);
        startActivity(intent);
        finish();

    }
}
