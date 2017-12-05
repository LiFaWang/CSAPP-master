package net.huansi.csapp.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterView;

import net.huansi.csapp.R;
import net.huansi.csapp.activity.LoginActivity;
import net.huansi.csapp.activity.SetLanguageActivity;
import net.huansi.csapp.adapter.MineFragmentAdapter;
import net.huansi.csapp.bean.MineBean;
import net.huansi.csapp.databinding.FragmentMineBinding;
import net.huansi.csapp.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import huansi.net.qianjingapp.fragment.BaseFragment;
import huansi.net.qianjingapp.utils.SPUtils;

import static net.huansi.csapp.utils.Constants.IS_LOGIN;
import static net.huansi.csapp.utils.Constants.USER_COMPANY;
import static net.huansi.csapp.utils.Constants.USER_NAME;
import static net.huansi.csapp.utils.Constants.USER_PWD;
import static net.huansi.csapp.utils.Constants.USER_SECTION;


public class MineFragment extends BaseFragment {

    private List<MineBean> data;

    @Override
    public int getLayout() {
        return R.layout.fragment_mine;
    }


    @Override
    public void init() {
        String mobileNo = SPUtils.getMobileNo(getActivity());
        String userName = SPUtils.getSpData(getActivity(), USER_NAME, "张思");
        String userSection = SPUtils.getSpData(getActivity(), USER_SECTION, "运营部");
        String userCompany = SPUtils.getSpData(getActivity(), USER_COMPANY, "环思智慧");
        String versionNum = MyUtils.getVersionName(getActivity());
        int ima[] = {R.drawable.account,R.drawable.user,R.drawable.state,R.drawable.company,
        R.drawable.section,R.drawable.multi_languge,R.drawable.updata,R.drawable.end};
        Resources resources = getContext().getResources();
        String account_number = resources.getString(R.string.account_number);
        String user_name = resources.getString(R.string.user_name);
        String status = resources.getString(R.string.status);
        String company = resources.getString(R.string.company);
        String department = resources.getString(R.string.department);
        final String language_setting = resources.getString(R.string.language_setting);
        String version_updating = resources.getString(R.string.version_updating);
        final String logout = resources.getString(R.string.logout);
        String valid = resources.getString(R.string.valid);
        String title[]={account_number,user_name,status,company,department,language_setting,version_updating,logout};
        String content[]={mobileNo,userName,valid,userCompany,userSection,"","V"+versionNum,""};
        data = new ArrayList<>();
        for (int i = 0; i <ima.length; i++) {
            data.add(new MineBean(ima[i],title[i],content[i]));
        }
        FragmentMineBinding fragmentMineBinding = (FragmentMineBinding) viewDataBinding;
        MineFragmentAdapter adapter  = new MineFragmentAdapter(data,getContext());
        fragmentMineBinding.lvMine.setAdapter(adapter);
        fragmentMineBinding.lvMine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              MineBean mine = (MineBean) adapterView.getItemAtPosition(i);
               if(mine.name.equals(logout)){
                   SPUtils.saveSpData(getActivity(),IS_LOGIN,"false");
                   SPUtils.saveMobileNo(getActivity(),"");
                   SPUtils.saveSpData(getActivity(),USER_PWD,"");
                   SPUtils.saveSpData(getActivity(), USER_NAME, "");
                   SPUtils.saveSpData(getActivity(), USER_SECTION, "");
                   SPUtils.saveSpData(getActivity(), USER_COMPANY, "");

                   startActivity(new Intent(getActivity(), LoginActivity.class));
                   getActivity().finish();
               }
               if (i==5){
                   startActivity(new Intent(getActivity(), SetLanguageActivity.class));
//                   getActivity().finish();
               }

           }
       });
    }
}
