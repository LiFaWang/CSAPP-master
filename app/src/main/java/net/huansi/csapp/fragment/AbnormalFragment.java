package net.huansi.csapp.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.huansi.csapp.R;
import net.huansi.csapp.adapter.PopAreaAdapter;
import net.huansi.csapp.adapter.PopEquAdapter;
import net.huansi.csapp.adapter.PopFactoryAdapter;
import net.huansi.csapp.bean.CountryListBean;
import net.huansi.csapp.bean.EquipmentListBean;
import net.huansi.csapp.bean.FactoryListBean;
import net.huansi.csapp.databinding.FragmentAbnormalBinding;
import net.huansi.csapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;
import huansi.net.qianjingapp.view.LoadProgressDialog;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.HS_SERVICE;

/**
 * Created by Tony on 2017/8/5.
 * 16:50
 */

public class AbnormalFragment extends BaseFragment {

    private FragmentAbnormalBinding mFragmentAbnormalBinding;
    private LoadProgressDialog mDialog;
    private ArrayList<CountryListBean> countryData;//国家列表
    private PopAreaAdapter adapterArea;//pop地区列表的适配器
    private int[] mScreenSizeLength;
    private List<FactoryListBean> factoryDataMemory;//存储FactoryListBean的输出进行筛选

    private Map<String,String> mapCountry;//存储区域的id和名字
    private PopFactoryAdapter adapterFactory;//pop工厂列表的适配器
    private List<FactoryListBean> factoryData;//工厂列表
    private PopEquAdapter adapterEqu;//pop设备列表的适配器
    private List<EquipmentListBean> equData;//设备列表
    private String iCountryId="";//国家id

    @Override
    public int getLayout() {
        return R.layout.fragment_abnormal;
    }
    @Override
    public void init() {
        mFragmentAbnormalBinding = (FragmentAbnormalBinding) viewDataBinding;
        mDialog = new LoadProgressDialog(getActivity());
        mScreenSizeLength = MyUtils.getScreenSize(getActivity());
        setData();
    }

    /**
     * 设置数据
      */
    private void setData() {
//        OthersUtil.showLoadDialog(mDialog);
        countryData = new ArrayList<>();
        factoryData=new ArrayList<>();
        equData=new ArrayList<>();
        equData.clear();
        factoryData.clear();
        countryData.clear();
        //区域数据
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity) this.getActivity(), HS_SERVICE,
                "spappYunEquCountryList", "sMobileNo=" + mMobileNo,
                CountryListBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                        List<CountryListBean> data = new ArrayList<>();
                        for (int i = 0; i <listwsdata.size() ; i++) {
                            CountryListBean  countryListBean = (CountryListBean) listwsdata.get(i);
                            countryData.add(countryListBean);
                            data.add(countryListBean);
                            mapCountry.put(countryListBean.ICOUNTRYID,countryListBean.SCOUNTRYNAME);
                        }
                        iCountryId = countryData.get(0).ICOUNTRYID;

                        setFactory();
                    }
                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                    }
                });
        mFragmentAbnormalBinding.tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterArea = new PopAreaAdapter(countryData,getContext());
                showPop(view);
            }
        });
        mFragmentAbnormalBinding.tvFactory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterFactory=new PopFactoryAdapter(factoryData,getContext());
                showPop(view);
            }
        });
        mFragmentAbnormalBinding.tvEqu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterEqu=new PopEquAdapter(equData,getContext());
                showPop(view);
            }
        });
    }

    /**
     * 用PopupWindow显示区域、工厂、设备
     * @param view
     */
    List<FactoryListBean> listFactoryItem = new ArrayList<>();
    private void showPop(View view) {
        View contentView= LayoutInflater.from(getContext()).inflate( R.layout.pop_list, null);
        PopupWindow popupWindow=new PopupWindow(contentView,mScreenSizeLength[0]/3-5,mScreenSizeLength[1]/4,true);
        popupWindow.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(view);
         ListView popListView= (ListView) contentView.findViewById(R.id.lv_pop_clothlist);
        switch (view.getId()) {
            case R.id.tvArea:
                popListView.setAdapter(adapterArea);
                break;
            case R.id.tvFactory:
                popListView.setAdapter(adapterFactory);
                break;
            case R.id.tvEqu:
                popListView.setAdapter(adapterEqu);
                break;
        }

        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (view.getId()) {
                    case R.id.tvArea://区域
                        CountryListBean countryListBean = (CountryListBean) parent.getItemAtPosition(position);
                        mFragmentAbnormalBinding.tvArea.setText(countryListBean.SCOUNTRYNAME);
                        listFactoryItem.clear();

                        break;
                    case R.id.tvFactory://工厂
                        break;
                    case R.id.tvEqu://设备
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * 设置设备
     *
     */
    private void setEqu() {
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity) getActivity(), HS_SERVICE,
                "spappYunEquCountryList", "sMobileNo=" + mMobileNo,
                FactoryListBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                        equData.clear();
                        for (int i = 0; i <listwsdata.size() ; i++) {
                            EquipmentListBean equipmentListBean= (EquipmentListBean) listwsdata.get(i);
                            equData.add(equipmentListBean);
                        }
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                        OthersUtil.dismissLoadDialog(mDialog);
                    }
                });


    }

    /**
     * 设置工厂
     */
    private void setFactory() {
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity) getActivity(), HS_SERVICE,
                "spappYunEquCountryList", "sMobileNo=" + mMobileNo,
                FactoryListBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> listwsdata = hsWebInfo.wsData.LISTWSDATA;
                        factoryData.clear();
                        for (int i = 0; i < listwsdata.size(); i++) {
                            FactoryListBean factoryListBean = (FactoryListBean) listwsdata.get(i);
                            factoryData.add(factoryListBean);
//                            itemMapFactory.put(factory.IFACTORYID,factory.SFACTORYNAME);
//                            mapFactory.put(factory.ICOUNTRYID,factoryDataMemory);
                        }
                        setEqu();                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);

                    }
                });


    }


}
