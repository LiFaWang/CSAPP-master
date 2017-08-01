package net.huansi.csapp.fragment;


import android.content.Context;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.huansi.csapp.R;
import net.huansi.csapp.adapter.HomeFragmentAdapter;
import net.huansi.csapp.bean.HomeInfoBean;
import net.huansi.csapp.databinding.FragmentHomeBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.entity.HsWebInfo;
import huansi.net.qianjingapp.entity.WsEntity;
import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.imp.SimpleHsWeb;
import huansi.net.qianjingapp.utils.OthersUtil;
import huansi.net.qianjingapp.utils.RxjavaWebUtils;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

import static huansi.net.qianjingapp.utils.WebServices.WebServiceType.HS_SERVICE;

public class HomeFragment extends BaseFragment implements View.OnClickListener {


    private FragmentHomeBinding fragmentHomeBinding;
    List<HomeInfoBean> data;

    private HomeFragmentAdapter adapter;

    private String CURRENTADTAID;//当前选择的国家ID
    private static final String CNID = "8";//中国
    private static final String TGID = "10";//泰国
    private static final String TWID = "9";//台湾
    private static final String MLID = "12";//马来西亚
    private static final String YNID = "13";//越南
    private List<HomeInfoBean> mFliterData;


    @Override
    public int getLayout() {
        return R.layout.fragment_home;

    }

    @Override
    public void init() {
        EventBus.getDefault().register(getContext());
        fragmentHomeBinding = (FragmentHomeBinding) viewDataBinding;

        fragmentHomeBinding.homeListView.setAdapter(adapter);
        OthersUtil.initRefresh(fragmentHomeBinding.prtHome, getActivity());
        fragmentHomeBinding.prtHome.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                setData();
            }
        });
//        fragmentHomeBinding.ml2.setRotation(-30);
        fragmentHomeBinding.cn.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.ml1.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.ml2.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.tg.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.tw.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.yn.setOnClickListener(HomeFragment.this);
        setData();

    }


    public void setData() {

        //首页数据
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity) this.getActivity(), HS_SERVICE,
                "spappYunEquDistributionMap", "sMobileNo=" + mMobileNo,
                HomeInfoBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        data = new ArrayList<>();
                        for (int i = 0; i < entities.size(); i++) {
                            HomeInfoBean homeInfoBean = (HomeInfoBean) entities.get(i);
                            data.add(homeInfoBean);
                        }
                        mFliterData = getFliterData(CURRENTADTAID);
                        if (adapter == null)
                            adapter = new HomeFragmentAdapter(mFliterData, getContext());
                        fragmentHomeBinding.homeListView.setAdapter(adapter);
                        fragmentHomeBinding.prtHome.refreshComplete();

                    }

                    @Override
                    public void error(HsWebInfo hsWebInfo, Context context) {
                        super.error(hsWebInfo, context);
                        fragmentHomeBinding.prtHome.refreshComplete();
                    }
                });


    }

    private List<HomeInfoBean> getFliterData(String currentadtaId) {
        if (currentadtaId == null) return data;
        List<HomeInfoBean> fliterData = new ArrayList<>();
        for (HomeInfoBean bean : data) {

            if (bean.ICOUNTRYID.equals(currentadtaId)) {
                fliterData.add(bean);
            }

        }
        return fliterData;


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(getContext())) {
            EventBus.getDefault().unregister(getContext());
        }
        ;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cn:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.cn);
                CURRENTADTAID = CNID;
                break;
            case R.id.ml1:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.malaixiya);
                CURRENTADTAID = MLID;
                break;
            case R.id.ml2:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.malaixiya);
                CURRENTADTAID = MLID;
                break;
            case R.id.tg:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.taiguo);
                CURRENTADTAID = TGID;
                break;
            case R.id.tw:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.taiwan);
                CURRENTADTAID = TWID;
                break;
            case R.id.yn:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.yuenan);
                CURRENTADTAID = YNID;
                break;

            default:
//                fragmentHomeBinding.ivAr.setImageResource(R.drawable.zongtu);
//                CURRENTADTAID = CNID;
                break;
        }
        mFliterData = getFliterData(CURRENTADTAID);
        adapter.setList(mFliterData);
        adapter.notifyDataSetChanged();
    }
}
