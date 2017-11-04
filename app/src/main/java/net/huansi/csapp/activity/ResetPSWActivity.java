package net.huansi.csapp.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import net.huansi.csapp.MainActivity;
import net.huansi.csapp.R;
import net.huansi.csapp.bean.ResetPSWBean;
import net.huansi.csapp.databinding.ActivityResetPswBinding;

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
import static net.huansi.csapp.utils.Constants.USER_PWD;

public class ResetPSWActivity extends NotWebBaseActivity {


    private ActivityResetPswBinding mResetPswBinding;
    private String mPhoneNum;
    private String mPassWord;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_psw;
    }

    @Override
    public void init() {
        mResetPswBinding = (ActivityResetPswBinding) viewDataBinding;

        mResetPswBinding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 重置密码提交
     */
    public void reset(View v ){
        mPhoneNum = mResetPswBinding.etPhoneNum.getText().toString();
        mPassWord = mResetPswBinding.etPassword.getText().toString();
        getLoginMes(mPhoneNum,mPassWord);
    }


    private void getLoginMes(final String mPhoneNum, final String mPassWord) {
        RxjavaWebUtils.requestByGetJsonData(this, CUS_SERVICE,
                "spappYunEquUpdatePwd"
                , "sMobileNo="+mPhoneNum+",sPassword="+mPassWord,
                ResetPSWBean.class.getName(), true, "", new SimpleHsWeb() {

                    private String mSmessage;

                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        for (int i = 0; i < entities.size(); i++) {
                            ResetPSWBean  resetPSWBean = (ResetPSWBean) entities.get(i);
                            mSmessage = resetPSWBean.SMESSAGE;

                        }

                        if(mSmessage!=null&&mSmessage.equals("OK")){

                            SPUtils.saveSpData(ResetPSWActivity.this,USER_PWD,mPassWord);
                            SPUtils.saveMobileNo(ResetPSWActivity.this,mPhoneNum);
                           SPUtils.saveSpData(ResetPSWActivity.this, IS_LOGIN, "true");
                            OthersUtil.ToastMsg(ResetPSWActivity.this,mSmessage);
                            Intent intent = new Intent(ResetPSWActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            OthersUtil.ToastMsg(ResetPSWActivity.this,"账号或者密码不正确！");
                        }

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                    }
                });

    }

    }


