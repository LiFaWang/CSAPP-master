package net.huansi.csapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import net.huansi.csapp.R;
import net.huansi.csapp.adapter.ProductionActivityAdapter;
import net.huansi.csapp.bean.ProductionActivityBean;
import net.huansi.csapp.databinding.ActivityProductionBinding;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.base.NotWebBaseActivity;
import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static net.huansi.csapp.utils.Constants.RESULT_PRODUCTION;

public class ProductionActivity extends NotWebBaseActivity {



    private ActivityProductionBinding mActivityProductionBinding;
    private LoadProgressDialog dialog;
    private List<ProductionActivityBean> mProductionActivityBeanList;
    private ProductionActivityAdapter adapter;

    @Override
    protected int getLayoutId() {
        return (R.layout.activity_production);
    }

    @Override
    public void init() {
        mActivityProductionBinding = (ActivityProductionBinding) viewDataBinding;
        dialog=new LoadProgressDialog(this);
        mProductionActivityBeanList = new ArrayList<>();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String sorderno = bundle.getString("SORDERNO");
        String mTStartTime = bundle.getString("mTStartTime");
        String mTEndTime = bundle.getString("mTEndTime");
        String mFactoryId = bundle.getString("mFactoryId");
        mActivityProductionBinding.tvOrder.setText(sorderno);
        obtainProductionData(mFactoryId,sorderno,"",mTStartTime,mTEndTime);
        mActivityProductionBinding.tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 获取产量数据
     * @param factoryId
     * @param orderNo
     * @param orderNo
     * @param mTStartTime
     * @param mTEndTime
     *
     */
    private void obtainProductionData(final String factoryId, final String orderNo, final String cardNo, final String mTStartTime, final String mTEndTime) {
        OthersUtil.showLoadDialog(dialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getApplicationContext(), CUS_SERVICE, "spappYunGetProductInfo",
                                "iFactoryId="+factoryId+",sOrderNo="+orderNo
                                        +",sCardNo="+cardNo+",tStartTime="+mTStartTime+
                                        ",tEndTime="+mTEndTime
                                       ,
                                ProductionActivityBean.class.getName(), true, "");
                    }
                }), getApplicationContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                for (int i = 0; i < listwsdata.size(); i++) {
                    ProductionActivityBean productionActivityBean = (ProductionActivityBean) listwsdata.get(i);
                    mProductionActivityBeanList.add(productionActivityBean);
                }
                //设置展示数据
                 showProductionData(mProductionActivityBeanList);
            }
        });

    }

    /**
     * 展示产量数据
     * @param productionActivityBeanList
     */
    private void showProductionData(final List<ProductionActivityBean> productionActivityBeanList) {
        adapter=new ProductionActivityAdapter(productionActivityBeanList,this);
        View view = View.inflate(this, R.layout.production_item_title, null);
        ListView lvProduction = (ListView) view.findViewById(R.id.lvProduction);
        lvProduction.setAdapter(adapter);
        adapter.setOnCabinetNameClickListener(new ProductionActivityAdapter.OnCabinetNameClickListener() {
            @Override
            public void onCabinetNameClick(ProductionActivityBean bean) {
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                bundle.putString("ITERMINAL",bean.ITERMINAL);
                bundle.putString("STERMINALNAME",bean.STERMINALNAME);
                bundle.putString("mTStartTime",bean.TSTARTTIME);
                bundle.putString("mTEndTime",bean.TENDTIME);
                intent.putExtras(bundle);
                setResult(RESULT_PRODUCTION,intent);  //结果码和请求码互不影响，设值任意
                finish();

            }
        });
        mActivityProductionBinding.hsProduction.addView(view);
    }

}
