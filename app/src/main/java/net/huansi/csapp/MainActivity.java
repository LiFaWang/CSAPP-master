package net.huansi.csapp;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import net.huansi.csapp.bean.ProductionBean;
import net.huansi.csapp.databinding.ActivityMainBinding;
import net.huansi.csapp.event.HomeToRealEvent;
import net.huansi.csapp.factory.FragmentFactory;
import net.huansi.csapp.fragment.AbnormalFragment;
import net.huansi.csapp.fragment.HistoryFragment;
import net.huansi.csapp.fragment.HomeFragment;
import net.huansi.csapp.fragment.MineFragment;
import net.huansi.csapp.fragment.ProductionFragment;
import net.huansi.csapp.fragment.RealFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import huansi.net.qianjingapp.base.NotWebBaseActivity;

public class MainActivity extends NotWebBaseActivity implements RadioGroup.OnCheckedChangeListener{

    public ActivityMainBinding activityMainBinding;
    private HomeFragment homeFragment;
    private RealFragment realFragment;
    private HistoryFragment historyFragment;
    private AbnormalFragment mAbnormalFragment;
    private MineFragment mineFragment;
    private ProductionFragment mProductionFragment;

    public ProductionBean getProductionBean() {
        return mProductionBean;
    }

    public void setProductionBean(ProductionBean productionBean) {
        mProductionBean = productionBean;
    }

    private ProductionBean mProductionBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {

        activityMainBinding = (ActivityMainBinding) viewDataBinding;
        activityMainBinding.mainRG.setOnCheckedChangeListener(this);

        initFragments();
    }


    /**
     * 初始化fragment
     */
    private void initFragments() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        homeFragment = (HomeFragment) FragmentFactory.createFragment(0);
        realFragment = ( RealFragment)FragmentFactory.createFragment(1);
        historyFragment = (HistoryFragment)FragmentFactory.createFragment(2);
        mAbnormalFragment= (AbnormalFragment)FragmentFactory.createFragment(3);
        mProductionFragment= (ProductionFragment)FragmentFactory.createFragment(4);
        mineFragment =  (MineFragment)FragmentFactory.createFragment(5);
        ft.add(R.id.mainFrameLayout, homeFragment);
        ft.add(R.id.mainFrameLayout, realFragment);
        ft.add(R.id.mainFrameLayout, historyFragment);
        ft.add(R.id.mainFrameLayout,mAbnormalFragment);
        ft.add(R.id.mainFrameLayout, mProductionFragment);
        ft.add(R.id.mainFrameLayout, mineFragment);
        ft.commitAllowingStateLoss();
        hideFragments(ft);
        ft.show(homeFragment);
    }


    /**
     * 隐藏fragment
     */
    private void hideFragments(FragmentTransaction ft) {
        ft.hide(homeFragment);
        ft.hide(realFragment);
        ft.hide(historyFragment);
        ft.hide(mAbnormalFragment);
        ft.hide(mineFragment);
        ft.hide(mProductionFragment);
    }
    public void gotoHistoryFragment() {

        FragmentManager fm =  getSupportFragmentManager();
        //注意v4包的配套使用
        Fragment fragment = new HistoryFragment();
        fm.beginTransaction().replace(R.id.mainFrameLayout,fragment).commit();
        activityMainBinding.mainHistory.setChecked(true);
    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        changeFragment(i);
    }

    private void changeFragment(@IdRes int i) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragments(ft);
        switch (i){
            case R.id.mainHome:
                ft.show(homeFragment);
                break;
            case R.id.mainReal:
                ft.show(realFragment);
                break;
            case R.id.mainHistory:
                ft.show(historyFragment);
                break;
            case R.id.mainErro:
                ft.show(mAbnormalFragment);
                break;
            case R.id.mainMy:
                ft.show(mineFragment);
                break;
            case R.id.mainProduction:
                ft.show(mProductionFragment);
                break;
        }
        ft.commitAllowingStateLoss();
    }

    /**
     * 点击工厂按钮跳转实时监控界面
     */
     @Subscribe(threadMode = ThreadMode.MAIN)
     public void jumpRealFragment(HomeToRealEvent event){
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         hideFragments(ft);
         ft.show(realFragment);
         activityMainBinding.mainReal.setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                int idFt = data.getIntExtra("id_ft", R.id.mainHome);
                activityMainBinding.mainHome.setChecked(idFt == R.id.mainHome);
                activityMainBinding.mainReal.setChecked(idFt == R.id.mainReal);
                activityMainBinding.mainHistory.setChecked(idFt == R.id.mainHistory);
                activityMainBinding.mainErro.setChecked(idFt == R.id.mainErro);
                activityMainBinding.mainProduction.setChecked(idFt == R.id.mainProduction);
                activityMainBinding.mainMy.setChecked(idFt == R.id.mainMy);
                break;
            default:
                break;
        }
    }
}
