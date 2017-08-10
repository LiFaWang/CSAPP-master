package net.huansi.csapp.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;

import net.huansi.csapp.R;
import net.huansi.csapp.adapter.UnusalActivityAdapter;
import net.huansi.csapp.bean.UnuaualBean;
import net.huansi.csapp.databinding.ActivityUnusualBinding;
import net.huansi.csapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.HS_SERVICE;
import static net.huansi.csapp.utils.Constants.FACTORY_NAME;
import static net.huansi.csapp.utils.Constants.ITEM_EQU_NAME;

public class UnusualActivity extends NotWebBaseActivity {
    ActivityUnusualBinding activityUnusualBinding;
    private String mIChnnel="8";//通道
    private String mTStartTime="";//开始日期
    private String mTEndTime="";//结束日期
    private TimePickerView pvTime;//日历
    private UnusalActivityAdapter adapter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_unusual;
    }

    @Override
    public void init() {
        initTimePickerView();
        activityUnusualBinding = (ActivityUnusualBinding) viewDataBinding;
        Intent intent = getIntent();
        String equName = intent.getStringExtra(ITEM_EQU_NAME);
        String factoryName = intent.getStringExtra(FACTORY_NAME);
        String curDate = MyUtils.getCurDate("--");
        activityUnusualBinding.tvStart.setText(curDate);
        activityUnusualBinding.tvEnd.setText(curDate);
        activityUnusualBinding.tvTitle.setText(equName+"("+factoryName+")");
        activityUnusualBinding.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        activityUnusualBinding.tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        activityUnusualBinding.ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);
            }
        });
        activityUnusualBinding.ivEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);
            }
        });
        activityUnusualBinding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityUnusualBinding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activityUnusualBinding.tvStart.getText().toString().isEmpty()||
                        activityUnusualBinding.tvEnd.getText().toString().isEmpty()){
                    OthersUtil.ToastMsg(UnusualActivity.this,"请选择日期");
                }else{
                    mTStartTime = activityUnusualBinding.tvStart.getText().toString();
                    mTEndTime = activityUnusualBinding.tvEnd.getText().toString();
                    setData(mTStartTime,mTEndTime);
                }

            }
        });

    }
    private void initTimePickerView() {
        //时间选择器
        //选中事件回调
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                switch (v.getId()){
                    case R.id.ivStart:
                        activityUnusualBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvStart:
                        activityUnusualBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.ivEnd:
                        activityUnusualBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvEnd:
                        activityUnusualBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .build();
    }

    private void setData(String mTStartTime,String mTEndTime) {
//        OthersUtil.showLoadDialog(dialog);
        //区域数据
        RxjavaWebUtils.requestByGetJsonData( this, HS_SERVICE,
                "spappYunEquExpCurveInfo", "iChnnel="+mIChnnel+
                ",tStartTime="+mTStartTime+",tEndTime="+mTEndTime,
                UnuaualBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        List<UnuaualBean> data =  new ArrayList<>();
                        for (int i = 0; i < entities.size(); i++) {
                            UnuaualBean unuaualBean = (UnuaualBean) entities.get(i);
                            data.add(unuaualBean);
                        }

                        if(adapter==null){
                            adapter=new UnusalActivityAdapter(data,UnusualActivity.this);
                        }
                        activityUnusualBinding.lvErrorInfo.setAdapter(adapter);
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                    }
                });
    }
}
