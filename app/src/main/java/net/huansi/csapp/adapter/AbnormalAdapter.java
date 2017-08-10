package net.huansi.csapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huansi.csapp.R;
import net.huansi.csapp.bean.AbnormalBean;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;

/**
 * Created by Tony on 2017/8/10.
 * 9:31
 */

public class AbnormalAdapter extends HsBaseAdapter<AbnormalBean> {
    public AbnormalAdapter(List<AbnormalBean> list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.item_abnormal_adapter,parent,false);
        }
        TextView abnormalPoint = ViewHolder.get(convertView, R.id.abnormalPoint);
        TextView abnormalCount = ViewHolder.get(convertView, R.id.abnormalCount);

        AbnormalBean abnormalBean = mList.get(position);
        abnormalPoint.setText(abnormalBean.SCHANNELNAME);
        abnormalCount.setText(abnormalBean.EXPCOUNT);
        return convertView;
    }
}
