package net.huansi.csapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huansi.csapp.R;
import net.huansi.csapp.bean.ProductionFragmentBean;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;

/**
 *
 * Created by Tony on 2017/11/7.
 */

public class ProductionFragmentAdapter extends HsBaseAdapter<ProductionFragmentBean> {
    public void setOnOrderNumberClickListener(OnOrderNumberClickListener onOrderNumberClickListener) {
        mOnOrderNumberClickListener = onOrderNumberClickListener;
    }

    private OnOrderNumberClickListener mOnOrderNumberClickListener;
    public ProductionFragmentAdapter(List<ProductionFragmentBean> list, Context context) {
        super(list, context);
    }
    public interface OnOrderNumberClickListener {
        void onOrderNumberClick(ProductionFragmentBean bean);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_production_fragment,null);
        }
        TextView tvOrder = ViewHolder.get(convertView, R.id.tvOrder);
        TextView tvGuest = ViewHolder.get(convertView, R.id.tvGuest);
        TextView tvProduction = ViewHolder.get(convertView, R.id.tvProduction);
        TextView tvProductCount = ViewHolder.get(convertView, R.id.tvProductCount);
        TextView tvCabinet = ViewHolder.get(convertView, R.id.tvCabinet);
        tvOrder.setText(mList.get(position).SORDERNO);
        tvGuest.setText(mList.get(position).SCUSTNAME);
        tvProduction.setText(mList.get(position).SPRODUCTNAME);
        tvProductCount.setText(mList.get(position).NQTY);
        tvCabinet.setText(mList.get(position).STERMINALNAME);
        tvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnOrderNumberClickListener != null) {
                    mOnOrderNumberClickListener.onOrderNumberClick(mList.get(position));
                }

            }
        });


        return convertView;
    }
}
