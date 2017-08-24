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

import com.bigkoo.pickerview.TimePickerView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.huansi.csapp.R;
import net.huansi.csapp.activity.HistoryCurveActivity;
import net.huansi.csapp.adapter.AbnormalAdapter;
import net.huansi.csapp.adapter.PopAreaAdapter;
import net.huansi.csapp.adapter.PopEquAdapter;
import net.huansi.csapp.adapter.PopFactoryAdapter;
import net.huansi.csapp.bean.AbnormalBean;
import net.huansi.csapp.bean.CountryListBean;
import net.huansi.csapp.bean.EquipmentListBean;
import net.huansi.csapp.bean.FactoryListBean;
import net.huansi.csapp.bean.HistoryDataMapBean;
import net.huansi.csapp.databinding.FragmentAbnormalBinding;
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
import huansi.net.qianjingapp.view.LoadProgressDialog;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Func1;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.CUS_SERVICE;
import static net.huansi.csapp.utils.Constants.CHANNEL_NAME;
import static net.huansi.csapp.utils.Constants.ENDTIME;
import static net.huansi.csapp.utils.Constants.STARTTIME;


public class AbnormalFragment extends BaseFragment implements AbsListView.OnScrollListener {

    private FragmentAbnormalBinding mFragmentAbnormalBinding;
    private int[] length;//屏幕长宽;
    private List<CountryListBean> countryData;//区域数据
    private List<FactoryListBean> factoryData;//工厂数据
    private List<FactoryListBean> factoryDataMemory;//工厂数据
    private List<EquipmentListBean> equipmentData;//设备数据
    private  List<EquipmentListBean> equipmentDataMemory;
//    private HistoryFragmentAdapter adapter;
//    private List<HistoryListBean> data;//历史数据
    private PopAreaAdapter areaAdapter;
    private PopFactoryAdapter factoryAdapter;
    private PopEquAdapter equAdapter;
    private LoadProgressDialog dialog;
    private AbnormalAdapter mAbnormalAdapter;
//    private String iTerminalId="1";
//    private String pageIndex="1";
//    private String pageSize="10";
    private List<FactoryListBean> listFactoryItem;//筛选工厂的数据
    private List<EquipmentListBean> listEquipmentItem;//筛选设备的数据
    private List<HistoryDataMapBean> listDataMap;
    private String mtStartTime="";//开始日期
    private String mtEndTime="";//结束日期
    private TimePickerView pvTime;//日历
    private String mIYunTerminalId="1510";
    private List<AbnormalBean> data;

    @Override
    public int getLayout() {
        return R.layout.fragment_abnormal;
    }

    @Override
    public void init() {
        initTimePickerView();
        dialog=new LoadProgressDialog(getActivity());
        data = new ArrayList<>();
        factoryData = new ArrayList<>();
        factoryDataMemory = new ArrayList<>();
        equipmentData = new ArrayList<>();
        countryData = new ArrayList<>();
        equipmentDataMemory  = new ArrayList<>();
        listFactoryItem = new ArrayList<>();
        listEquipmentItem = new ArrayList<>();
        length = MyUtils.getScreenSize(getActivity());
        mFragmentAbnormalBinding = (FragmentAbnormalBinding) viewDataBinding;
        String curDate = MyUtils.getCurDate("--");
        mFragmentAbnormalBinding.tvStart.setText(curDate);
        mFragmentAbnormalBinding.tvEnd.setText(curDate);
        mAbnormalAdapter=new AbnormalAdapter(data,getContext());
        mFragmentAbnormalBinding.errorListView.setAdapter(mAbnormalAdapter);
        mFragmentAbnormalBinding.tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mFragmentAbnormalBinding.tvEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvTime.show(v);
            }
        });
        mFragmentAbnormalBinding.ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);
            }
        });
        mFragmentAbnormalBinding.ivEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvTime.show(view);
            }
        });
        mFragmentAbnormalBinding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFragmentAbnormalBinding.tvStart.getText().toString().isEmpty() ||
                        mFragmentAbnormalBinding.tvEnd.getText().toString().isEmpty()) {
                    OthersUtil.ToastMsg(getContext(), "请选择日期");
                } else {
                    mtStartTime = mFragmentAbnormalBinding.tvStart.getText().toString();
                    mtEndTime = mFragmentAbnormalBinding.tvEnd.getText().toString();
                    getAbnormalData();
                }
            }});
        OthersUtil.initRefresh(mFragmentAbnormalBinding.prtError,getContext());
        setData();
        mFragmentAbnormalBinding.prtError.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getAbnormalData();
                mAbnormalAdapter.notifyDataSetChanged();
            }
        });

        mFragmentAbnormalBinding.errorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AbnormalBean abnormalBean=data.get(position);
                Intent intent=new Intent(getActivity(),HistoryCurveActivity.class);
                intent.putExtra(CHANNEL_NAME,abnormalBean.SCHANNELNAME);
                intent.putExtra(STARTTIME,mtStartTime);
                intent.putExtra(ENDTIME,mtEndTime);
                startActivity(intent);
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
                        mFragmentAbnormalBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvStart:
                        mFragmentAbnormalBinding.tvStart.setText(MyUtils.getTime(date));
                        break;
                    case R.id.ivEnd:
                        mFragmentAbnormalBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                    case R.id.tvEnd:
                        mFragmentAbnormalBinding.tvEnd.setText(MyUtils.getTime(date));
                        break;
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .build();
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


        mFragmentAbnormalBinding.tvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areaAdapter = new PopAreaAdapter(countryData,getContext());
                showPop(view);
            }
        });
        mFragmentAbnormalBinding.tvFactory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFragmentAbnormalBinding.tvArea.getText().toString().equals("(区域)")){
                    OthersUtil.ToastMsg(getContext(),"请选择区域");
                }else{
                    factoryAdapter = new PopFactoryAdapter(factoryData,getContext());
                    showPop(view);
                }

            }
        });
        mFragmentAbnormalBinding.tvEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFragmentAbnormalBinding.tvArea.getText().toString().equals("(区域)")){
                    OthersUtil.ToastMsg(getContext(),"请选择区域");
                }else if(mFragmentAbnormalBinding.tvFactory.getText().toString().equals("(工厂)")){
                    OthersUtil.ToastMsg(getContext(),"请选择工厂");
                }else if(mFragmentAbnormalBinding.tvFactory.getText().toString().equals("无")){
                    OthersUtil.ToastMsg(getContext(),"没有对应的工厂");
                }else{
                    equAdapter = new PopEquAdapter(equipmentData,getContext());
                    showPop(view);
                }
            }
        });
    }
//获取模块对应的异常数据
    private void getAbnormalData() {
        OthersUtil.showLoadDialog(dialog);
        data.clear();
        NewRxjavaWebUtils.getUIThread(NewRxjavaWebUtils.getObservable(this, "")
                .map(new Func1<String, HsWebInfo>() {
                    @Override
                    public HsWebInfo call(String s) {
                        return NewRxjavaWebUtils.getJsonData(getContext(),CUS_SERVICE,
                                "spappYunEquExpHistoryList"
                                , "iYunTerminalId=" + mIYunTerminalId + ",tStartTime=" +mtStartTime + ",tEndTime=" + mtEndTime,
                                AbnormalBean.class.getName(), true, "查询失败！！！");
                    }
                }), getContext(), dialog, new SimpleHsWeb() {
            @Override
            public void success(HsWebInfo hsWebInfo) {
                List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                for (int i = 0; i <entities.size() ; i++) {
                    AbnormalBean abnormalBean = (AbnormalBean) entities.get(i);
                    data.add(abnormalBean);
                }
                mAbnormalAdapter.notifyDataSetChanged();
                mFragmentAbnormalBinding.prtError.refreshComplete();
            }



            @Override
            public void error(HsWebInfo hsWebInfo, Context context) {
                super.error(hsWebInfo, context);
                mAbnormalAdapter.notifyDataSetChanged();
                mFragmentAbnormalBinding.prtError.refreshComplete();
                OthersUtil.dismissLoadDialog(dialog);
            }
        });
    }



    /**
     * popWindow
     * @param view
     */
    private void showPop(final View view) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_list, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, length[0]/4-10, length[1]/4, true);
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
                        mFragmentAbnormalBinding.tvArea.setText(itemCountry.SCOUNTRYNAME);
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
                            mFragmentAbnormalBinding.tvFactory.setText("无");
                            mFragmentAbnormalBinding.tvEquipment.setText("无");
                            factoryData.clear();
                            equipmentData.clear();
                        }else{
                            factoryData.clear();
                            factoryData.addAll(listFactoryItem);
                            mFragmentAbnormalBinding.tvFactory.setText(listFactoryItem.get(0).SFACTORYNAME);
                            String factoryId = listFactoryItem.get(0).IFACTORYID;
                            //工厂对应的设备
                            for (int i = 0; i < equipmentDataMemory.size(); i++) {
                                EquipmentListBean equipmentListBean = equipmentDataMemory.get(i);
                                if(factoryId.equals(equipmentListBean.IFACTORYID)){
                                    listEquipmentItem.add(equipmentListBean);
                                }
                            }
                            if(listEquipmentItem.isEmpty()){
                                mFragmentAbnormalBinding.tvEquipment.setText("无");
                                equipmentData.clear();
                            }else{
                                equipmentData.clear();
                                equipmentData.addAll(listEquipmentItem);
                                mFragmentAbnormalBinding.tvEquipment.setText(listEquipmentItem.get(0).STERMINALNAME);
//                                iTerminalId = listEquipmentItem.get(0).ITERMINALID;
                            }

                        }

                        break;
                    case R.id.tvFactory:
                        FactoryListBean itemFactory= (FactoryListBean) parent.getItemAtPosition(position);
                        mFragmentAbnormalBinding.tvFactory.setText(itemFactory.SFACTORYNAME);
                        listEquipmentItem.clear();
                        //工厂对应的设备
                        for (int i = 0; i < equipmentDataMemory.size(); i++) {
                            EquipmentListBean equipmentListBean = equipmentDataMemory.get(i);
                            if(itemFactory.IFACTORYID.equals(equipmentListBean.IFACTORYID)){
                                     listEquipmentItem.add(equipmentListBean);
                                 }
                        }
                        if(listEquipmentItem.isEmpty()){
                            mFragmentAbnormalBinding.tvEquipment.setText("无");
                            equipmentData.clear();
                        }else{
                            equipmentData.clear();
                            equipmentData.addAll(listEquipmentItem);
                            mFragmentAbnormalBinding.tvEquipment.setText(listEquipmentItem.get(0).STERMINALNAME);
//                            iTerminalId = listEquipmentItem.get(0).ITERMINALID;
                        }

                        break;
                    case R.id.tvEquipment:
                        EquipmentListBean itemEquipment= (EquipmentListBean) parent.getItemAtPosition(position);
                        mFragmentAbnormalBinding.tvEquipment.setText(itemEquipment.STERMINALNAME);
//                        iTerminalId = itemEquipment.ITERMINALID;
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
}
