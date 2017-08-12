package net.huansi.csapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huansi.csapp.R;
import net.huansi.csapp.bean.UnuaualBean;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;

/**
 * Created by Tony on 2017/8/11.
 * 9:37
 * 历史异常曲线
 */

public class HistoryCurveAdapter extends HsBaseAdapter<UnuaualBean> {

    public HistoryCurveAdapter(List<UnuaualBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_historycurve_adapter,parent,false);
        }
        TextView unusalPoint = ViewHolder.get(convertView, R.id.unusalPoint);
        TextView listStartTime = ViewHolder.get(convertView, R.id.listStartTime);
        TextView listEndTime = ViewHolder.get(convertView, R.id.listEndTime);
        TextView unusalValue = ViewHolder.get(convertView, R.id.unusalValue);
        TextView unusalType = ViewHolder.get(convertView, R.id.unusalType);
        UnuaualBean unuaualBean = mList.get(position);
        listStartTime.setText(unuaualBean.TSTARTTIME);
        listEndTime.setText(unuaualBean.TENDTIME);
        unusalValue.setText(unuaualBean.SCURVALUE);
        unusalType.setText(unuaualBean.STYPE);
        return convertView;
    }
}
