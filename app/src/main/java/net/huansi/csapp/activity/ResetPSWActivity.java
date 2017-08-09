package net.huansi.csapp.activity;

import android.view.View;

import net.huansi.csapp.R;
import net.huansi.csapp.databinding.ActivityResetPswBinding;

import huansi.net.qianjingapp.base.NotWebBaseActivity;

public class ResetPSWActivity extends NotWebBaseActivity {


    private ActivityResetPswBinding mResetPswBinding;

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
        finish();

    }
}
