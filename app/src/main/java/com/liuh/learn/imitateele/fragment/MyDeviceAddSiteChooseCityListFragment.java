package com.liuh.learn.imitateele.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.liuh.learn.imitateele.R;
import com.liuh.learn.imitateele.adapter.CityListAdapter;
import com.liuh.learn.imitateele.model.ATestData;
import com.liuh.learn.imitateele.model.ATestGoodMan;
import com.liuh.learn.imitateele.utils.UIUtils;
import com.liuh.learn.imitateele.widget.QuickIndexView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2018/3/8 14:53
 * Description:城市选择列表
 */

public class MyDeviceAddSiteChooseCityListFragment extends BaseFragment {

    @BindView(R.id.rv_city)
    RecyclerView rvCity;

    @BindView(R.id.id_quickIndexView)
    QuickIndexView mQuickIndexView;

    private CityListAdapter cityAdapter;

    private List<ATestGoodMan> datas = new ArrayList<ATestGoodMan>();

    LinearLayoutManager layoutManager;

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_mydeviceadd_site_choose_citylist;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        for (String city : ATestData.names) {
            datas.add(new ATestGoodMan(city));
        }

        Collections.sort(datas);

        cityAdapter = new CityListAdapter(datas);
        rvCity.setAdapter(cityAdapter);
        layoutManager = new LinearLayoutManager(getActivity());

        rvCity.setLayoutManager(layoutManager);


        mQuickIndexView.setOnLetterChangeListener(new QuickIndexView.OnLetterChangeListener() {
            @Override
            public void onLetterChanged(String letter) {
                Log.e("-----------", "letter : " + letter);

                UIUtils.showToast(letter);
                for (int i = 0; i < datas.size(); i++) {

                    if (letter.equals(datas.get(i).getPinyin().charAt(0) + "")) {
                        rvCity.scrollToPosition(i);
                        layoutManager.scrollToPositionWithOffset(i, 0);
                        break;
                    }

                }


            }
        });
    }
}
