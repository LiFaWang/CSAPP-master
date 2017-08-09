package net.huansi.csapp.activity;

import android.content.Intent;
import android.view.View;

import net.huansi.csapp.R;
import net.huansi.csapp.databinding.ActivityConfirmPswBinding;

import huansi.net.qianjingapp.base.NotWebBaseActivity;

public class ConfirmPSWActivity extends NotWebBaseActivity {


    private ActivityConfirmPswBinding mConfirmPswBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm_psw;
    }

    @Override
    public void init() {
        mConfirmPswBinding = (ActivityConfirmPswBinding) viewDataBinding;
        mConfirmPswBinding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void commit(View v){
        Intent intent=new Intent(this,ResetPSWActivity.class);
        startActivity(intent);
        finish();
    }
}
