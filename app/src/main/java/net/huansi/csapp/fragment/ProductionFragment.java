package net.huansi.csapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.bigkoo.pickerview.TimePickerView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.huansi.csapp.MainActivity;
import net.huansi.csapp.R;
import net.huansi.csapp.activity.ProductionActivity;
import net.huansi.csapp.adapter.PopAreaAdapter;
import net.huansi.csapp.adapter.PopFactoryAdapter;
import net.huansi.csapp.adapter.ProductionFragmentAdapter;
import net.huansi.csapp.bean.CountryListBean;
import net.huansi.csapp.bean.FactoryListBean;
import net.huansi.csapp.bean.ProductionFragmentBean;
import net.huansi.csapp.databinding.FragmentProductionBinding;
import net.huansi.csapp.factory.FragmentFactory;
import net.huansi.csapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;
import huansi.net.qianjingapp.utils.SPUtils;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static net.huansi.csapp.utils.Constants.RESULT_PRODUCTION;

/**
 *
 * Created by Tony on 2017/11/2.
 */

public class ProductionFragment extends BaseFragment {

    private FragmentProductionBinding mFragmentProductionBinding;
    private List<CountryListBean> countryData;//区域数据
    private List<FactoryListBean> factoryDataMemory;//工厂数据
    private List<FactoryListBean> factoryData;//工厂数据
    private PopAreaAdapter areaAdapter;
    private PopFactoryAdapter factoryAdapter;
    private List<FactoryListBean> listFactoryItem;//筛选工厂的数据
    private int[] length;//屏幕长宽;
    private String mFactoryId;//工厂的id
    private LoadProgressDialog dialog;
    private TimePickerView pvTime;//日历
    private List<ProductionFragmentBean> mProductionFragmentBeanList;
    private ProductionFragmentAdapter mProductionFragmentAdapter;
    private String mTStartTime;
    private String mTEndTime;


    @Override
    public int getLayout() {
        return R.layout.fragment_production;
    }

    @Override
    public void init() {
        mFragmentProductionBinding = (FragmentProductionBinding) viewDataBinding;
        initTimePickerView();
        dialog=new LoadProgressDialog(getActivity());
        countryData = new ArrayList<>();
        factoryDataMemory = new ArrayList<>();
        factoryData = new ArrayList<>();
        listFactoryItem = new ArrayList<>();
        length = MyUtils.getScreenSize(getActivity());
        mProductionFragmentBeanList = new ArrayList<>();
        String curDate = MyUtils.getCurDate("--");
        String preDate = MyUtils.getPreDate("--");
        mFragmentProductionBinding.tvStart.setText(preDate);
        mFragmentProductionBinding.tvEnd.setText(curDate);
        OthersUtil.initRefresh(mFragmentProductionBinding.prtProduction,getContext());
        setData();
        mFragmentProductionBinding.prtProduction.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                obtainProductionList(mFactoryId,mTStartTime,mTEndTime, "");
                mProductionFragmentAdapter.notifyDataSetChanged();
            }
        });
        mFragmentProductionBinding.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mFragmentProductionBinding.tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mFragmentProductionBinding.ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);
            }
        });
        mFragmentProductionBinding.ivEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);
            }
        });
        mFragmentProductionBinding.btnSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mFragmentProductionBinding.btnSelector.getText().toString().equals("按日期")){
                mFragmentProductionBinding.rlOrder.setVisibility(View.GONE);
                mFragmentProductionBinding.llDateSearch.setVisibility(View.VISIBLE);
                    mFragmentProductionBinding.btnSelector.setText("按单");
                }else if (mFragmentProductionBinding.btnSelector.getText().toString().equals("按单")){
                    mFragmentProductionBinding.rlOrder.setVisibility(View.VISIBLE);
                    mFragmentProductionBinding.llDateSearch.setVisibility(View.GONE);
                    mFragmentProductionBinding.btnSelector.setText("按日期");
                }
            }
        });
        mFragmentProductionBinding.tvDateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentProductionBinding.tvArea.getText().toString().equals("(区域)")|| mFragmentProductionBinding.tvFactory.getText().toString().equals("(工厂)")){

                    OthersUtil.ToastMsg(getContext(), "区域或工厂不能为空");

                } else if (mFragmentProductionBinding.tvStart.getText().toString().isEmpty() ||
                        mFragmentProductionBinding.tvEnd.getText().toString().isEmpty()) {
                    OthersUtil.ToastMsg(getContext(), "请选择日期");
                } else {
                    mTStartTime = mFragmentProductionBinding.tvStart.getText().toString();
                    mTEndTime = mFragmentProductionBinding.tvEnd.getText().toString();
                    obtainProductionList(mFactoryId, mTStartTime, mTEndTime,"");
                }
            }
        });
        mFragmentProductionBinding.tvOrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentProductionBinding.tvArea.getText().toString().equals("(区域)")|| mFragmentProductionBinding.tvFactory.getText().toString().equals("(工厂)")){

                    OthersUtil.ToastMsg(getContext(), "区域或工厂不能为空");

                } else if (mFragmentProductionBinding.etOrderNo.getText().toString().isEmpty()){
                    OthersUtil.ToastMsg(getContext(), "请先填写订单号");

                }else {

                    String etOrderNo = mFragmentProductionBinding.etOrderNo.getText().toString();
                    obtainProductionList(mFactoryId, "", "", etOrderNo);
                }

            }
        });
        mProductionFragmentAdapter = new ProductionFragmentAdapter(mProductionFragmentBeanList,getContext());
        mFragmentProductionBinding.productListView.setAdapter(mProductionFragmentAdapter);
        mProductionFragmentAdapter.setOnOrderNumberClickListener(new ProductionFragmentAdapter.OnOrderNumberClickListener() {
            @Override
            public void onOrderNumberClick(ProductionFragmentBean bean) {

                Intent intent=new Intent(getContext(),ProductionActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("SORDERNO",bean.SORDERNO);
                bundle.putString("SORDERNO",bean.SORDERNO);
                bundle.putString("mFactoryId",mFactoryId);
                bundle.putString("mTStartTime",mTStartTime);
                bundle.putString("mTEndTime",mTEndTime);
                SPUtils.saveSpData(getContext(),"mTStartTime",mTStartTime);
                SPUtils.saveSpData(getContext(),"mTEndTime",mTEndTime);
                intent.putExtras(bundle);
               startActivityForResult(intent,1);

            }
        });
    }
    private void initTimePickerView() {
        //时间选择器
        //选中事件回调
        pvTime = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                switch (v.getId()){
                    case R.id.ivStart:
                        mFragmentProductionBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvStart:
                        mFragmentProductionBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.ivEnd:
                        mFragmentProductionBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvEnd:
                        mFragmentProductionBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .build();
    }
    //获取产量的列表数据
    private void obtainProductionList(final String factoryId, final String tStartTime, final String tEndTime, final String etOrderNo) {
        OthersUtil.showLoadDialog(dialog);
        mProductionFragmentBeanList.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getContext(),CUS_SERVICE,
                                "spappYunGetProductSum"
                                , "iFactoryId=" + factoryId + ",tStartTime=" +tStartTime + ",tEndTime=" + tEndTime
                                +",sOrderNo="+etOrderNo,
                                ProductionFragmentBean.class.getName(), true, "没有发现异常信息！");
                    }
                }), getContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                for (int i = 0; i <entities.size() ; i++) {
                    ProductionFragmentBean productionFragmentBean = (ProductionFragmentBean) entities.get(i);
                    mProductionFragmentBeanList.add(productionFragmentBean);
                }
                if (TextUtils.isEmpty(mTStartTime)&&TextUtils.isEmpty(mTEndTime)){
                    mTStartTime = mProductionFragmentBeanList.get(0).TSTARTTIME;
                    mTEndTime = mProductionFragmentBeanList.get(0).TENDTIME;
                }

                mFragmentProductionBinding.prtProduction.refreshComplete();
                mProductionFragmentAdapter.notifyDataSetChanged();

            }



            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                super.error(hsWebInfo, context);
               mProductionFragmentAdapter.notifyDataSetChanged();
                mFragmentProductionBinding.prtProduction.refreshComplete();
                OthersUtil.dismissLoadDialog(dialog);
            }
        });
    }



    /**
     * 设置选择菜单数据
     */
    private void setData() {
        //区域数据
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity)this.getActivity(), CUS_SERVICE,
                "spappYunEquCountryList", "sMobileNo="+mMobileNo,
                CountryListBean.class.getName(), true, "", new SimpleHsWeb() {

                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        countryData.clear();
                        for (int i = 0; i < entities.size(); i++) {
                            CountryListBean countryListBean = (CountryListBean) entities.get(i);
                            countryData.add(countryListBean);
                        }

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);

                    }
                });
        //工厂数据
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity)this.getActivity(), CUS_SERVICE,
                "spappYunEquFactoryList", "sMobileNo="+mMobileNo,
                FactoryListBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        factoryDataMemory.clear();
                        for (int i = 0; i < entities.size(); i++) {
                            FactoryListBean factory = (FactoryListBean) entities.get(i);
                            factoryDataMemory.add(factory);
                        }
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);


                    }
                });
        mFragmentProductionBinding.tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaAdapter = new PopAreaAdapter(countryData,getContext());
                showPop(view);
            }
        });
        mFragmentProductionBinding.tvFactory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFragmentProductionBinding.tvArea.getText().toString().equals("(区域)")){
                    OthersUtil.ToastMsg(getContext(),"请选择区域");
                }else{
                    factoryAdapter = new PopFactoryAdapter(factoryData,getContext());
                    showPop(view);
                }

            }
        });
    }
    /**
     * popWindow
     * @param view
     */
    private void showPop(final View view) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_list, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, view.getMeasuredWidth(), length[1]/4, true);
        final ListView popListView = (ListView) contentView.findViewById(R.id.lv_pop_clothlist);
        switch (view.getId()){
            case R.id.tvArea:
                popListView.setAdapter(areaAdapter);
                break;
            case R.id.tvFactory:
                popListView.setAdapter(factoryAdapter);
                break;
        }
        popupWindow.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(view);
        //两级联动
        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

                switch (view.getId()){
                    case R.id.tvArea:
                        CountryListBean  itemCountry = (CountryListBean) parent.getItemAtPosition(position);
                        mFragmentProductionBinding.tvArea.setText(itemCountry.SCOUNTRYNAME);
                        listFactoryItem.clear();
                        //国家对应的工厂
                        for (int i = 0; i < factoryDataMemory.size(); i++) {
                            FactoryListBean factoryListBean = factoryDataMemory.get(i);
                            if(itemCountry.ICOUNTRYID.equals(factoryListBean.ICOUNTRYID)){
                                listFactoryItem.add(factoryListBean);
                            }
                        }
                        if(listFactoryItem.isEmpty()){
                            mFragmentProductionBinding.tvFactory.setText("无");
                            factoryData.clear();
                        }else{
                            factoryData.clear();
                            factoryData.addAll(listFactoryItem);
                            mFragmentProductionBinding.tvFactory.setText(listFactoryItem.get(0).SFACTORYNAME);
                        }
                        break;
                    case R.id.tvFactory:
                        FactoryListBean itemFactory= (FactoryListBean) parent.getItemAtPosition(position);
                        mFragmentProductionBinding.tvFactory.setText(itemFactory.SFACTORYNAME);
                        mFactoryId = itemFactory.IFACTORYID;


                }
                popupWindow.dismiss();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_PRODUCTION:
                Bundle b=data.getExtras();  //data为B中回传的Intent
                String sorderno = b.getString("SORDERNO");
                String mTStartTime = b.getString("mTStartTime");
                String mTEndTime = b.getString("mTEndTime");
                String ITERMINAL = b.getString("ITERMINAL");
                String sterminalname = b.getString("STERMINALNAME");

                MainActivity activity = (MainActivity) getActivity();
                activity.activityMainBinding.mainHistory.setChecked(true);
                HistoryFragment historyFragment = (HistoryFragment) FragmentFactory.createFragment(2);
                historyFragment.fragmentHistoryBinding.tvArea.setText( mFragmentProductionBinding.tvArea.getText());
                historyFragment.fragmentHistoryBinding.tvFactory.setText(mFragmentProductionBinding.tvFactory.getText());
                historyFragment.fragmentHistoryBinding.tvEquipment.setText(sterminalname);
                historyFragment.setiTerminalId(ITERMINAL);
                historyFragment.setMtStartTime(mTStartTime);
                historyFragment.setMtEndTime(mTEndTime);
                historyFragment.getEquData(true);
                break;
        }
    }
}
