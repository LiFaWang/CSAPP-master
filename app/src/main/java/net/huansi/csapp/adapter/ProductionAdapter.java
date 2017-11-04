package net.huansi.csapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huansi.csapp.R;
import net.huansi.csapp.bean.ProductionBean;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;

/**
 *
 * Created by Tony on 2017/11/3.
 */

public class ProductionAdapter extends HsBaseAdapter<ProductionBean> {
    public void setOnCabinetNameClickListener(OnCabinetNameClickListener onCabinetNameClickListener) {
        mOnCabinetNameClickListener = onCabinetNameClickListener;
    }

    private OnCabinetNameClickListener mOnCabinetNameClickListener;

    public ProductionAdapter(List<ProductionBean> list, Context context) {
        super(list, context);
    }
    public interface OnCabinetNameClickListener{
        void onCabinetNameClick(ProductionBean bean);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_production,null);
        }
        final TextView tvCabinet =  ViewHolder.get(convertView,R.id.tvCabinet);//机台
        TextView tvProduction = ViewHolder.get(convertView,R.id.tvProduction);
        TextView tvWorker = ViewHolder.get(convertView,R.id.tvWorker);
        TextView tvStartTime = ViewHolder.get(convertView,R.id.tvStartTime);
        TextView tvStopTime = ViewHolder.get(convertView,R.id.tvStopTime);
        TextView tvPlanDate = ViewHolder.get(convertView,R.id.tvPlanDate);
        tvCabinet.setText(mList.get(position).STERMINALNAME);
        tvProduction.setText(mList.get(position).NQTY);
        tvWorker.setText(mList.get(position).SOPTION);
        tvStartTime.setText(mList.get(position).TSTARTTIME);
        tvStopTime.setText(mList.get(position).TENDTIME);
        tvPlanDate.setText(mList.get(position).TPLANDATE);
        tvCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnCabinetNameClickListener != null) {
                    mOnCabinetNameClickListener.onCabinetNameClick(mList.get(position));
                }

            }
        });
        return convertView;
    }
}
