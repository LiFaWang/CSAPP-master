package net.huansi.csapp.factory;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import net.huansi.csapp.fragment.AbnormalFragment;
import net.huansi.csapp.fragment.HistoryFragment;
import net.huansi.csapp.fragment.HomeFragment;
import net.huansi.csapp.fragment.MineFragment;
import net.huansi.csapp.fragment.ProductionFragment;
import net.huansi.csapp.fragment.RealFragment;

import huansi.net.qianjingapp.fragment.BaseFragment;

/**
 * Created by Tony on 2017/9/29.
 * 21:21
 */

public class FragmentFactory {

    private static SparseArray<BaseFragment> sFragmentSparseArray=new SparseArray<>();
    public static Fragment createFragment(int pos){
        BaseFragment baseFragment = sFragmentSparseArray.get(pos);
        if (baseFragment==null){
            switch (pos) {
                case 0:
                    baseFragment=new HomeFragment();
                    break;
                case 1:
                    baseFragment=new RealFragment();
                    break;
                case 2:
                    baseFragment=new HistoryFragment();
                    break;
                case 3:
                    baseFragment=new AbnormalFragment();
                    break;
                case 4:
                    baseFragment=new ProductionFragment();
                    break;
                case 5:
                    baseFragment=new MineFragment();
                    break;

                default:
                    break;
            }
            sFragmentSparseArray.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
