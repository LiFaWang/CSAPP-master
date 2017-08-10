package net.huansi.csapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huansi.csapp.R;
import net.huansi.csapp.activity.HistoryCurveActivity;
import net.huansi.csapp.bean.AbnormalBean;

import java.util.List;

import huansi.net.qianjingapp.adapter.HsBaseAdapter;
import huansi.net.qianjingapp.utils.ViewHolder;

import static net.huansi.csapp.utils.Constants.CHANNEL_NAME;

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

        final AbnormalBean abnormalBean = mList.get(position);
        abnormalPoint.setText(abnormalBean.SCHANNELNAME);
        abnormalCount.setText(abnormalBean.EXPCOUNT);
        //点击跳转到历史异常曲线界面（单击）
        abnormalPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,HistoryCurveActivity.class);
                intent.putExtra(CHANNEL_NAME,abnormalBean.SCHANNELNAME);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
