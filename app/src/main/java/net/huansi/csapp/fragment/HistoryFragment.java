package net.huansi.csapp.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.huansi.csapp.R;
import net.huansi.csapp.activity.HistoryDetailActivity;
import net.huansi.csapp.adapter.HistoryFragmentAdapter;
import net.huansi.csapp.adapter.PopAreaAdapter;
import net.huansi.csapp.adapter.PopEquAdapter;
import net.huansi.csapp.adapter.PopFactoryAdapter;
import net.huansi.csapp.bean.CountryListBean;
import net.huansi.csapp.bean.EquipmentListBean;
import net.huansi.csapp.bean.FactoryListBean;
import net.huansi.csapp.bean.HistoryDataMapBean;
import net.huansi.csapp.bean.HistoryListBean;
import net.huansi.csapp.databinding.FragmentHistoryBinding;
import net.huansi.csapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.NewRxjavaWebUtils;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;
import huansi.net.qianjingapp.view.LoadProgressDialog;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;


public class HistoryFragment extends BaseFragment implements AbsListView.OnScrollListener {


    public FragmentHistoryBinding fragmentHistoryBinding;
    private int[] length;//屏幕长宽;
    private List<CountryListBean> countryData;//区域数据
    private List<FactoryListBean> factoryData;//工厂数据
    private List<FactoryListBean> factoryDataMemory;//工厂数据
    private List<EquipmentListBean> equipmentData;//设备数据
    private  List<EquipmentListBean> equipmentDataMemory;
    private HistoryFragmentAdapter adapter;
    private PopAreaAdapter areaAdapter;
    private PopFactoryAdapter factoryAdapter;
    private PopEquAdapter equAdapter;
    private LoadProgressDialog dialog;

    public void setiTerminalId(String iTerminalId) {
        this.iTerminalId = iTerminalId;
    }

    private String iTerminalId="1";

    public void setMtStartTime(String mtStartTime) {
        this.mtStartTime = mtStartTime;
    }

    private String mtStartTime="";

    public void setMtEndTime(String mtEndTime) {
        this.mtEndTime = mtEndTime;
    }

    private String mtEndTime="";
    private List<FactoryListBean> listFactoryItem;//筛选工厂的数据
    private List<EquipmentListBean> listEquipmentItem;//筛选设备的数据
    //折线图三层数据集合
    private List<List<HistoryDataMapBean>> data;//折线图


    @Override
    public int getLayout() {
        return R.layout.fragment_history;
    }

    @Override
    public void init() {
        dialog=new LoadProgressDialog(getActivity());
        factoryData = new ArrayList<>();
        factoryDataMemory = new ArrayList<>();
        equipmentData = new ArrayList<>();
        countryData = new ArrayList<>();
        data = new ArrayList<>();
        equipmentDataMemory  = new ArrayList<>();
        listFactoryItem = new ArrayList<>();
        listEquipmentItem = new ArrayList<>();
        length = MyUtils.getScreenSize(getActivity());
        fragmentHistoryBinding = (FragmentHistoryBinding) viewDataBinding;
        OthersUtil.initRefresh(fragmentHistoryBinding.prtHistory,getActivity());

        setData();
        adapter = new HistoryFragmentAdapter(data,getContext());
        fragmentHistoryBinding.gvChart.setAdapter(adapter);
        fragmentHistoryBinding.gvChart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), HistoryDetailActivity.class);
                getActivity().startActivity(intent);
            }
        });
        fragmentHistoryBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String factory = fragmentHistoryBinding.tvFactory.getText().toString();
                String country = fragmentHistoryBinding.tvArea.getText().toString();
                String equ = fragmentHistoryBinding.tvEquipment.getText().toString();
                if(factory.equals("无")||equ.equals("无")){
                    OthersUtil.ToastMsg(getContext(),"该区域无数据");
                }else if(country.equals("(区域)")){
                    OthersUtil.ToastMsg(getContext(),"请选择区域");
                }else if(factory.equals("(工厂)")){
                    OthersUtil.ToastMsg(getContext(),"请选择工厂");
                }else if(equ.equals("(设备)")){
                    OthersUtil.ToastMsg(getContext(),"请选择设备");
                }else{
                    getEquData(true);
                }
            }
        });
        fragmentHistoryBinding.prtHistory.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                getEquData(false);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getEquData(true);
            }
        });
        fragmentHistoryBinding.gvChart.setEmptyView(View.inflate(getContext(),R.layout.empty_view,null));
    }

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
        //设备数据
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity)this.getActivity(), CUS_SERVICE,
                "spappYunEquTerminalList", "sMobileNo="+mMobileNo,
                EquipmentListBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        equipmentDataMemory.clear();
                        for (int i = 0; i < entities.size(); i++) {
                            EquipmentListBean equipment = (EquipmentListBean) entities.get(i);
                            equipmentDataMemory.add(equipment);
                        }
                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                    }
                });


        fragmentHistoryBinding.tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaAdapter = new PopAreaAdapter(countryData,getContext());
                showPop(view);
            }
        });
        fragmentHistoryBinding.tvFactory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentHistoryBinding.tvArea.getText().toString().equals("(区域)")){
                    OthersUtil.ToastMsg(getContext(),"请选择区域");
                }else{
                    factoryAdapter = new PopFactoryAdapter(factoryData,getContext());
                    showPop(view);
                }

            }
        });
        fragmentHistoryBinding.tvEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fragmentHistoryBinding.tvArea.getText().toString().equals("(区域)")){
                    OthersUtil.ToastMsg(getContext(),"请选择区域");
                }else if(fragmentHistoryBinding.tvFactory.getText().toString().equals("(工厂)")){
                    OthersUtil.ToastMsg(getContext(),"请选择工厂");
                }else if(fragmentHistoryBinding.tvFactory.getText().toString().equals("无")){
                    OthersUtil.ToastMsg(getContext(),"没有对应的工厂");
                }else{
                    equAdapter = new PopEquAdapter(equipmentData,getContext());
                    showPop(view);
                }
            }
        });

    }


    //获取模块下数据和对应折线图
   public void getEquData(boolean isRefresh){
       if(isRefresh) data.clear();
       OthersUtil.showLoadDialog(dialog);

       NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                       //列表数据
                       .map(new Func1<String, HsWebInfo>() {
                           @Override
                           public HsWebInfo call(String string) {
                               return NewRxjavaWebUtils.getJsonData(getContext(),CUS_SERVICE,
                                       "spappYunEquChannelList",
                                       "sMobileNo=" +mMobileNo+
                                               ",iTerminalId="+iTerminalId+
                                               ",iPageIndex="+getPage()+
                                               ",iPageSize=6",
                                       HistoryListBean.class.getName(),
                                       true,"");
                           }
                       })
                       //每个Item的折线图数据
                       .map(new Func1<HsWebInfo, HsWebInfo>() {
                           @Override
                           public HsWebInfo call(HsWebInfo hsWebInfo) {
                               if(!hsWebInfo.success) return  hsWebInfo;
                               List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                               hsWebInfo=NewRxjavaWebUtils.getJsonData(getContext(),CUS_SERVICE,
                                       "spappYunEquHistoryDataMap",
                                       "sMobileNo=" +mMobileNo+
                                               ",iTerminalId="+iTerminalId+
                                               ",pageindex="+getPage()+
                                               ",pagesize=6"+",tStartTime="+mtStartTime+
                                       ",tEndTime="+mtEndTime,
                                       HistoryDataMapBean.class.getName(),
                                       true,"");
                               if(!hsWebInfo.success) return hsWebInfo;
                               hsWebInfo.object=entities;
                               return hsWebInfo;
                           }
                       })
               , getContext(), dialog, new SimpleHsWeb() {
                   @Override
                   @SuppressWarnings("unchecked")
                   public void success(HsWebInfo hsWebInfo) {
                       List<WsEntity> dataMapEntities = hsWebInfo.wsData.LISTWSDATA;//每个Item的折线图数据
                       List<WsEntity> listEntities= (List<WsEntity>) hsWebInfo.object;//列表数据
                       Map<String,List<HistoryDataMapBean>> newDataMap = new HashMap<>();//防止获取和之前同样内容
                       for (int i = 0; i < dataMapEntities.size(); i++) {
                           HistoryDataMapBean historyDataMapBean = (HistoryDataMapBean) dataMapEntities.get(i);
                           List<HistoryDataMapBean> list=newDataMap.get(historyDataMapBean.IUSERMODULECHANNELID);
                           if(list==null)list=new ArrayList<>();
                           list.add(historyDataMapBean);
                           newDataMap.put(historyDataMapBean.IUSERMODULECHANNELID,list);
                       }
                       for (int i = 0; i < listEntities.size(); i++) {
                           HistoryListBean historyListBean = (HistoryListBean) listEntities.get(i);
                           List<HistoryDataMapBean> subList=newDataMap.get(historyListBean.IUSERMODULECHANNELID);
                           if(subList==null||subList.isEmpty())continue;
                           for(HistoryDataMapBean bean:subList){
                               bean.showName=historyListBean.SCHANNELNAME;
                           }
                           data.add(subList);
                       }
                       adapter.notifyDataSetChanged();
                       fragmentHistoryBinding.prtHistory.refreshComplete();
                       OthersUtil.dismissLoadDialog(dialog);
                   }

                   @Override
                   public void error(HsWebInfo hsWebInfo, Context context) {
                       super.error(hsWebInfo, context);
                       adapter.notifyDataSetChanged();
                       fragmentHistoryBinding.prtHistory.refreshComplete();
                       OthersUtil.dismissLoadDialog(dialog);
                   }
               });

   }

    /**
     * popWindow
     * @param view
     */
    private void showPop(final View view) {
        View contentView = LayoutInflater.from(getContext()).inflate(
                R.layout.pop_list, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                length[0]/4-10, length[1]/4, true);
        final ListView popListView = (ListView) contentView.findViewById(R.id.lv_pop_clothlist);
        switch (view.getId()){
            case R.id.tvArea:
                popListView.setAdapter(areaAdapter);
                break;
            case R.id.tvFactory:
                popListView.setAdapter(factoryAdapter);
                break;
            case R.id.tvEquipment:
                popListView.setAdapter(equAdapter);
                break;
        }
        popupWindow.setTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(view);
        //三级联动
        popListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {

                switch (view.getId()){
                    case R.id.tvArea:
                        CountryListBean  itemCountry = (CountryListBean) parent.getItemAtPosition(position);
                        fragmentHistoryBinding.tvArea.setText(itemCountry.SCOUNTRYNAME);
                        listFactoryItem.clear();
                        listEquipmentItem.clear();
                        //国家对应的工厂
                        for (int i = 0; i < factoryDataMemory.size(); i++) {
                            FactoryListBean factoryListBean = factoryDataMemory.get(i);
                            if(itemCountry.ICOUNTRYID.equals(factoryListBean.ICOUNTRYID)){
                                listFactoryItem.add(factoryListBean);
                            }
                        }
                        if(listFactoryItem.isEmpty()){
                            fragmentHistoryBinding.tvFactory.setText("无");
                            fragmentHistoryBinding.tvEquipment.setText("无");
                            factoryData.clear();
                            equipmentData.clear();
                        }else{
                            factoryData.clear();
                            factoryData.addAll(listFactoryItem);
                            fragmentHistoryBinding.tvFactory.setText(listFactoryItem.get(0).SFACTORYNAME);
                            String factoryId = listFactoryItem.get(0).IFACTORYID;
                            //工厂对应的设备
                            for (int i = 0; i < equipmentDataMemory.size(); i++) {
                                EquipmentListBean equipmentListBean = equipmentDataMemory.get(i);
                                if(factoryId.equals(equipmentListBean.IFACTORYID)){
                                    listEquipmentItem.add(equipmentListBean);
                                }
                            }
                            if(listEquipmentItem.isEmpty()){
                                fragmentHistoryBinding.tvEquipment.setText("无");
                                equipmentData.clear();
                            }else{
                                equipmentData.clear();
                                equipmentData.addAll(listEquipmentItem);
                                fragmentHistoryBinding.tvEquipment.setText(listEquipmentItem.get(0).STERMINALNAME);
                                iTerminalId = listEquipmentItem.get(0).ITERMINALID;
                            }

                        }

                        break;
                    case R.id.tvFactory:
                        FactoryListBean itemFactory= (FactoryListBean) parent.getItemAtPosition(position);
                        fragmentHistoryBinding.tvFactory.setText(itemFactory.SFACTORYNAME);
                        listEquipmentItem.clear();
                        //工厂对应的设备
                        for (int i = 0; i < equipmentDataMemory.size(); i++) {
                            EquipmentListBean equipmentListBean = equipmentDataMemory.get(i);
                            if(itemFactory.IFACTORYID.equals(equipmentListBean.IFACTORYID)){
                                     listEquipmentItem.add(equipmentListBean);
                                 }
                        }
                        if(listEquipmentItem.isEmpty()){
                            fragmentHistoryBinding.tvEquipment.setText("无");
                            equipmentData.clear();
                        }else{
                            equipmentData.clear();
                            equipmentData.addAll(listEquipmentItem);
                            fragmentHistoryBinding.tvEquipment.setText(listEquipmentItem.get(0).STERMINALNAME);
                            iTerminalId = listEquipmentItem.get(0).ITERMINALID;
                        }

                        break;
                    case R.id.tvEquipment:
                        EquipmentListBean itemEquipment= (EquipmentListBean) parent.getItemAtPosition(position);
                        fragmentHistoryBinding.tvEquipment.setText(itemEquipment.STERMINALNAME);
                        iTerminalId = itemEquipment.ITERMINALID;
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }

    /**
     *分页功能
     * @param absListView
     * @param i
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    /**
     * 获取页数
     * @return
     */
    private int getPage(){

        return (data.size()%6==0?(data.size()/6):(data.size()/6+1))+1;
    }


}
