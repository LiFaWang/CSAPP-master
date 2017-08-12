package net.huansi.csapp.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import net.huansi.csapp.R;
import net.huansi.csapp.adapter.HistoryCurveAdapter;
import net.huansi.csapp.bean.UnuaualBean;
import net.huansi.csapp.databinding.ActivityHistoryExcCurveBinding;
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
import huansi.net.qianjingapp.view.LoadProgressDialog;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static net.huansi.csapp.utils.Constants.CHANNEL_NAME;

/**
 * 历史异常曲线界面
 */
public class HistoryCurveActivity  extends NotWebBaseActivity {
    ActivityHistoryExcCurveBinding mHistoryExcCurveBinding;
    private LoadProgressDialog mDialog;
    private HistoryCurveAdapter adapter;
    private String mIChnnel="8";//通道
    private TimePickerView pvTime;//日历
    private String mTStartTime="1999-1-1";//开始日期
    private String mTEndTime="2020-1-1";//结束日期
    private LineChart lineChart;
    private ArrayList<String> xValues;//x轴数据
    private ArrayList<Entry> yValues;//y轴数据
    private ArrayList<LineDataSet> lineDataSets;
    private String mChannelname;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history_exc_curve;
    }

    @Override
    public void init() {
        initTimePickerView();
        mHistoryExcCurveBinding= (ActivityHistoryExcCurveBinding) viewDataBinding;
        mHistoryExcCurveBinding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lineChart = mHistoryExcCurveBinding.lineChart;
        mHistoryExcCurveBinding.lineChart.setNoDataText("没有数据");

        initLineChart();//初始化折线图

        mDialog=new LoadProgressDialog(this);
        Intent intent = getIntent();
        mChannelname = intent.getStringExtra(CHANNEL_NAME);

        String curDate = MyUtils.getCurDate("--");
        mHistoryExcCurveBinding.tvTitle.setText(mChannelname);
        mHistoryExcCurveBinding.tvStart.setText(curDate);
        mHistoryExcCurveBinding.tvEnd.setText(curDate);
        mHistoryExcCurveBinding.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mHistoryExcCurveBinding.ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mHistoryExcCurveBinding.tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mHistoryExcCurveBinding.ivEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mHistoryExcCurveBinding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHistoryExcCurveBinding.tvStart.getText().toString().isEmpty()||mHistoryExcCurveBinding.tvEnd.getText().toString().isEmpty()){
                    OthersUtil.ToastMsg(HistoryCurveActivity.this,"请选择日期");
                }else {
                    mTStartTime= mHistoryExcCurveBinding.tvStart.getText().toString();
                    mTEndTime = mHistoryExcCurveBinding.tvEnd.getText().toString();
                    setData(mTStartTime,mTEndTime);
                }
            }
        });
        setData(mTStartTime,mTEndTime);
    }
    private void setData(String mTStartTime,String mTEndTime) {
        OthersUtil.showLoadDialog(mDialog);
        //区域数据
        RxjavaWebUtils.requestByGetJsonData( this, CUS_SERVICE,
                "spappYunEquExpCurveInfo", "iChnnel="+mIChnnel+
                        ",tStartTime="+mTStartTime+",tEndTime="+mTEndTime,
                UnuaualBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        xValues.clear();
                        yValues.clear();
                        lineDataSets.clear();
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        List<UnuaualBean> data =  new ArrayList<>();
                        for (int i = 0; i < entities.size(); i++) {
                            UnuaualBean unuaualBean = (UnuaualBean) entities.get(i);
                            data.add(unuaualBean);
                            xValues.add(unuaualBean.TENDTIME);
                            yValues.add(new Entry(Float.valueOf(unuaualBean.SCURVALUE),i));
                        }
                        LineDataSet lineDataSet = new LineDataSet(yValues, mChannelname);
                        lineDataSets.add(lineDataSet);
                        LineData lineData = new LineData(xValues, lineDataSets);
                        lineChart.setData(lineData);
                        lineChart.setVisibleXRangeMaximum(15);
                        OthersUtil.dismissLoadDialog(mDialog);
                        lineData.setValueTextSize(4f);
                        lineData.notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                        lineChart.animateXY(3000,2000);

                        if(adapter==null){
                            adapter=new HistoryCurveAdapter(data,HistoryCurveActivity.this);
                        }
                        mHistoryExcCurveBinding.lvErrorInfo.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        OthersUtil.dismissLoadDialog(mDialog);

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                        mHistoryExcCurveBinding.lineChart.clear();
                        OthersUtil.dismissLoadDialog(mDialog);
                    }
                });
    }
    private void initLineChart() {
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        lineDataSets = new ArrayList<>();
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setTextSize(5f);
        lineChart.getAxisLeft().setTextSize(5f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setNoDataText("没有数据");
        lineChart.setDescription(mChannelname);
    }

    private void initTimePickerView() {
        //时间选择器
        //选中事件回调
        pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                switch (v.getId()){
                    case R.id.ivStart:
                        mHistoryExcCurveBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvStart:
                        mHistoryExcCurveBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.ivEnd:
                        mHistoryExcCurveBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvEnd:
                        mHistoryExcCurveBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .build();
    }
}
