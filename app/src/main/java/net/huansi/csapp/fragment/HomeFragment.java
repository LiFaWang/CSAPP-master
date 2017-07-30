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

public class HomeFragment extends BaseFragment implements View.OnClickListener{


    private FragmentHomeBinding fragmentHomeBinding;
    private static final String CN = "8";
    private static final String TAIWAN = "9";
    private static final String TAIGUO = "10";
    List<HomeInfoBean> data;

    private HomeFragmentAdapter adapter;
    private String mCurrentSelectedId;
    private List<HomeInfoBean> mFilterData;


    @Override
    public int getLayout() {
        return R.layout.fragment_home;

    }

    @Override
    public void init() {
        EventBus.getDefault().register(getContext());
        fragmentHomeBinding = (FragmentHomeBinding) viewDataBinding;

        fragmentHomeBinding.homeListView.setAdapter(adapter);
        OthersUtil.initRefresh(fragmentHomeBinding.prtHome,getActivity());
        fragmentHomeBinding.prtHome.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                setData();
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.zongtu);
            }
        });
        fragmentHomeBinding.cn.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.ml.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.tg.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.tw.setOnClickListener(HomeFragment.this);
        fragmentHomeBinding.yn.setOnClickListener(HomeFragment.this);
        setData();

    }

    private List<HomeInfoBean> getFilterData(String countryId) {
        if (countryId == null) return data;
        List<HomeInfoBean> result = new ArrayList<>();
        for (HomeInfoBean bean: data) {
            if (bean.ICOUNTRYID.equals(countryId)) {
                result.add(bean);
            }

        }
        return  result;
    }

    public void setData(){

        //首页数据
        RxjavaWebUtils.requestByGetJsonData((RxAppCompatActivity)this.getActivity(), HS_SERVICE,
                "spappYunEquDistributionMap", "sMobileNo="+mMobileNo,
                HomeInfoBean.class.getName(), true, "", new SimpleHsWeb() {
                    @Override
                    public void success(HsWebInfo hsWebInfo) {
                        List<WsEntity> entities = hsWebInfo.wsData.LISTWSDATA;
                        data = new ArrayList<>();
                        for (int i = 0; i < entities.size(); i++) {
                            HomeInfoBean homeInfoBean = (HomeInfoBean) entities.get(i);
                            data.add(homeInfoBean);
                        }
                        mFilterData = getFilterData(mCurrentSelectedId);
                        if (adapter == null) adapter= new HomeFragmentAdapter(mFilterData,getContext());
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



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(getContext())){
            EventBus.getDefault().unregister(getContext());
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cn:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.cn);
                mCurrentSelectedId = CN;
                break;
            case R.id.ml:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.malaixiya);
                break;
            case R.id.tg:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.taiguo);
                mCurrentSelectedId = TAIGUO;
                break;
            case R.id.tw:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.taiwan);
                mCurrentSelectedId = TAIWAN;
                break;
            case R.id.yn:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.yuenan);
                break;

            default:
                fragmentHomeBinding.ivAr.setImageResource(R.drawable.zongtu);
                break;
        }

        mFilterData = getFilterData(mCurrentSelectedId);
        adapter.setList(mFilterData);
        adapter.notifyDataSetChanged();

    }
}
