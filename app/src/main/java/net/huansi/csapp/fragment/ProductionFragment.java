package net.huansi.csapp.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.huansi.csapp.MainActivity;
import net.huansi.csapp.R;
import net.huansi.csapp.adapter.PopAreaAdapter;
import net.huansi.csapp.adapter.PopFactoryAdapter;
import net.huansi.csapp.adapter.ProductionAdapter;
import net.huansi.csapp.bean.CountryListBean;
import net.huansi.csapp.bean.FactoryListBean;
import net.huansi.csapp.bean.ProductionBean;
import net.huansi.csapp.databinding.FragmentProductionBinding;
import net.huansi.csapp.factory.FragmentFactory;
import net.huansi.csapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;

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
    private List<ProductionBean> mProductionBeanList;
    private ProductionAdapter adapter;


    @Override
    public int getLayout() {
        return R.layout.fragment_production;
    }

    @Override
    public void init() {
        mFragmentProductionBinding = (FragmentProductionBinding) viewDataBinding;
        dialog=new LoadProgressDialog(getActivity());
        countryData = new ArrayList<>();
        factoryDataMemory = new ArrayList<>();
        factoryData = new ArrayList<>();
        listFactoryItem = new ArrayList<>();
        length = MyUtils.getScreenSize(getActivity());
        mProductionBeanList = new ArrayList<>();
        setData();
        mFragmentProductionBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderNo = mFragmentProductionBinding.etOrder.getText().toString();
                String cardNo = mFragmentProductionBinding.etCard.getText().toString();
                if (TextUtils.isEmpty(orderNo)&&TextUtils.isEmpty(cardNo)){
                    OthersUtil.ToastMsg(getContext(),"订单号和卡号不能都为空");
                    return;
                }
                obtainProductionData(mFactoryId,orderNo,cardNo);
            }
        });
    }

    /**
     * 获取产量数据
     * @param factoryId
     * @param orderNo
     * @param cardNo
     */
    private void obtainProductionData(final String factoryId, final String orderNo, final String cardNo) {
        OthersUtil.showLoadDialog(dialog);
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getContext(), CUS_SERVICE, "spappYunGetProductInfo",
                                "iFactoryId="+factoryId+",sOrderNo="+orderNo+
                                ",sCardNo="+cardNo,
                                ProductionBean.class.getName(), true, "");
                    }
                }), getContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                for (int i = 0; i < listwsdata.size(); i++) {
                    ProductionBean productionBean = (ProductionBean) listwsdata.get(i);
                    mProductionBeanList.add(productionBean);
                }
                //设置展示数据
                showProductionData(mProductionBeanList);
            }
        });

    }

    /**
     * 展示产量数据
     * @param productionBeanList
     */
    private void showProductionData(final List<ProductionBean> productionBeanList) {
        adapter=new ProductionAdapter(productionBeanList,getActivity());
        View view = View.inflate(getContext(), R.layout.production_item_title, null);
        ListView lvProduction = (ListView) view.findViewById(R.id.lvProduction);
        lvProduction.setAdapter(adapter);
        adapter.setOnCabinetNameClickListener(new ProductionAdapter.OnCabinetNameClickListener() {
            @Override
            public void onCabinetNameClick(ProductionBean bean) {
                MainActivity activity = (MainActivity) getActivity();
                activity.activityMainBinding.mainHistory.setChecked(true);


                HistoryFragment historyFragment = (HistoryFragment) FragmentFactory.createFragment(2);
                historyFragment.setiTerminalId(bean.ITERMINAL);
                historyFragment.setMtStartTime(bean.TSTARTTIME);
                historyFragment.setMtEndTime(bean.TENDTIME);
                historyFragment.getEquData(true);

            }
        });
        mFragmentProductionBinding.hsProduction.addView(view);
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
        final PopupWindow popupWindow = new PopupWindow(contentView, length[0]/2-10, length[1]/4, true);
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
}
