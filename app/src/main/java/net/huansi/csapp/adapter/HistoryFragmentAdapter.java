package net.huansi.csapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import net.huansi.csapp.R;
import net.huansi.csapp.activity.HistoryDetailActivity;
import net.huansi.csapp.bean.HistoryDataMapBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;

import static net.huansi.csapp.utils.Constants.CHANNEL_ID;
import static net.huansi.csapp.utils.Constants.CHANNEL_NAME;


/**
 * Created by Administrator on 2017/7/7 0007.
 */

public class HistoryFragmentAdapter extends HsBaseAdapter<List<HistoryDataMapBean>> {



    public HistoryFragmentAdapter(List<List<HistoryDataMapBean>> list, Context context) {
        super(list, context);


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = mInflater.inflate(R.layout.item_history_adapter, viewGroup, false);
        }
        LineChart chart = ViewHolder.get(view, R.id.lineChart);
        chart.setNoDataText("没有数据！");
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        chart.setDescription("");
        chart.setPinchZoom(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setEnabled(true);//横坐标显示值
        chart.getAxisRight().setEnabled(false); //横坐标右边没有值
        chart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat df = new DecimalFormat("0.0");
                return df.format(value);
            }
        });
        List<HistoryDataMapBean> subList= mList.get(i);
        if(subList==null) subList=new ArrayList<>();
        String channelName="";
//        List<HistoryDataMapBean> historyDataMapBeen = new ArrayList<>();
//        Set<String> strings = map.keySet();
//        String channelName = "";
//        for (String string : strings) {
//            historyDataMapBeen.addAll(map.get(string));
//            channelName = string;
//        }
        //没有数据情况下，直接显示10个0
        if (subList.isEmpty()) {
            for (int k = 0; k < 10; k++) {
                xValues.add("");
                yValues.add(new Entry(0, k));
            }
        } else {
            channelName=subList.get(0).showName;
            for (int j = 0; j < subList.size(); j++) {

                xValues.add(subList.get(j).COLLECTTIME);//设置横坐标的值（测量时间）
                String value = subList.get(j).NCHANNELVALUE;
                float v=0;
                try {
                    v = Float.parseFloat(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                yValues.add(new Entry(v, j));
            }
        }
        LineDataSet lineDataSet = new LineDataSet(yValues, channelName);
//        lineDataSet.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                return "";
//            }
//        });
        lineDataSets.add(lineDataSet); // add the datasets
        LineData lineData = new LineData(xValues, lineDataSets);
        chart.setData(lineData);
        chart.setVisibleXRangeMaximum(6);
        chart.animateX(2000);
        lineData.notifyDataChanged();
        chart.invalidate();
//        }

        final List<HistoryDataMapBean> finalSubList = subList;
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalSubList.isEmpty()) return;
                HistoryDataMapBean bean= finalSubList.get(0);
                String name = bean.showName;
                String id = bean.IUSERMODULECHANNELID;
//                for (String s : strings1) {
//                    name = s;
//                    List<HistoryDataMapBean> historyDataMapBeen1 = stringListMap.get(s);
//                    historyDataMapBeen1.size();
//                    id = historyDataMapBeen1.get(0).IUSERMODULECHANNELID;
//                }
                Intent intent = new Intent(mContext, HistoryDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(CHANNEL_NAME, name);
                intent.putExtra(CHANNEL_ID, id);
                mContext.startActivity(intent);
            }
        });
        return view;
    }
}
