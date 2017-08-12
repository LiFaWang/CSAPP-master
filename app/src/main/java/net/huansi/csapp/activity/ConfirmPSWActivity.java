package net.huansi.csapp.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.huansi.csapp.R;
import net.huansi.csapp.bean.AuthCodeBean;
import net.huansi.csapp.databinding.ActivityConfirmPswBinding;
import net.huansi.csapp.utils.CountDownTimerUtils;

import java.util.ArrayList;
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
    private Map<String, String> authCodeMap=new HashMap<>();
    private String mPhoneNub;
    private LoadProgressDialog mDialog;
    private String mSsmcheckno1;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_psw;
    }



    @Override
    public void init() {
        mDialog=new LoadProgressDialog(this);
        mConfirmPswBinding = (ActivityConfirmPswBinding) viewDataBinding;
        mPhoneNub = mConfirmPswBinding.etPhoneNum.getText().toString();
        if (mPhoneNub.isEmpty()){
            OthersUtil.ToastMsg(ConfirmPSWActivity.this,"手机号不能为空");

        }else {
            authCodeMap.put("sDestNo", mPhoneNub);
            authCodeMap.put("sSourceIP", DeviceUtil.getPhoneDrivceNo(getApplicationContext()));
        }



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

                getAuthCode(authCodeMap);
                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(mConfirmPswBinding.tvAuthCode, 60000, 1000);
                mCountDownTimerUtils.start();
            }
        });

    }


    private void getAuthCode(Map<String,String> phoneNub) {
        String sDestNo = phoneNub.get("sDestNo");
        System.out.println(sDestNo);
        OthersUtil.showLoadDialog(mDialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, phoneNub)
                .map(new Func1<Map<String, String>, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(Map<String, String> stringStringMap) {
                        return NewRxjavaWebUtils.getNormalFunction(getApplicationContext(), HS_SERVICE_LATER, "SendShortMessage", stringStringMap,
                                AuthCodeBean.class.getName(), true, "输入有错误");
                    }
                }), getApplicationContext(), mDialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> entities=hsWebInfo.wsData.LISTWSDATA;
                List<AuthCodeBean> datas=new ArrayList<>();
                for (int i = 0; i < entities.size(); i++) {
                    AuthCodeBean authCodeBean = (AuthCodeBean) entities.get(i);
                    mSsmcheckno1 = authCodeBean.SSMCHECKNO1;
                    datas.add(authCodeBean);
                }
                OthersUtil.dismissLoadDialog(mDialog);
                OthersUtil.ToastMsg(ConfirmPSWActivity.this,mSsmcheckno1);
                System.out.println(hsWebInfo.json);


            }

            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                super.error(hsWebInfo, context);
                OthersUtil.ToastMsg(ConfirmPSWActivity.this,hsWebInfo.json);
                System.out.println(hsWebInfo.json);
            }
        });

//        List<WsEntity> entities = webInfo.wsData.LISTWSDATA;
//        List<WsData>datas=new ArrayList<>();
//        for (int i = 0; i < entities.size(); i++) {
//            WsData wsData = (WsData) entities.get(i);
//            datas.add(wsData);
//            mConfirmPswBinding.etAuthCode.setText(wsData.SMESSAGE);
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
        OthersUtil.ToastMsg(ConfirmPSWActivity.this,authCode+"="+mSsmcheckno1);
        if (authCode.equals(mSsmcheckno1)){
            OthersUtil.ToastMsg(ConfirmPSWActivity.this,"成功");
            Intent intent=new Intent(this,ResetPSWActivity.class);
            startActivity(intent);
            finish();
        }
        Intent intent=new Intent(this,ResetPSWActivity.class);
        startActivity(intent);
        finish();

    }
}
