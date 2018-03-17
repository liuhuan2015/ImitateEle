package com.liuh.learn.imitateele;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.liuh.learn.imitateele.adapter.MyDeviceSiteChooseAdapter;
import com.liuh.learn.imitateele.base.BaseActivity;
import com.liuh.learn.imitateele.fragment.BaseFragment;
import com.liuh.learn.imitateele.fragment.MyDeviceAddSiteChooseCityListFragment;
import com.liuh.learn.imitateele.fragment.MyDeviceAddSiteChooseListFragment;
import com.liuh.learn.imitateele.fragment.MyDeviceAddSiteChooseSearchListFragment;
import com.liuh.learn.imitateele.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyAddressChooseLocListActivity extends BaseActivity {
    @BindView(R.id.tl_site_search)
    TabLayout tlSiteSearch;
    @BindView(R.id.vp_site_search)
    ViewPager vpSiteSearch;

    @BindView(R.id.et_site_search_input)
    EditText etSiteSearchInput;

    @BindView(R.id.fl_site_search_list)
    FrameLayout flSiteSearchList;

    @BindView(R.id.tv_site_choose_city_name)
    TextView tvSiteCityName;

    @BindView(R.id.iv_site_choose_city_arrow)
    ImageView ivSiteCityArrow;

    List<String> tabTitles = new ArrayList<String>();

    MyDeviceSiteChooseAdapter mAdapter;

    List<BaseFragment> fragmentList = new ArrayList<BaseFragment>();

    private LatLng curLatlng;

    boolean isSearchListFraShow = false;//搜索结果列表的Fragment是否处于显示状态

    boolean isSearchCityFraShow = false;//城市选择列表的Fragment是否处于显示状态

    MyDeviceAddSiteChooseSearchListFragment searchListFragment;

    MyDeviceAddSiteChooseCityListFragment searchCityFragment;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_my_address_choose_loc_list;
    }


    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent != null) {
            curLatlng = intent.getParcelableExtra("curLatlng");
        }

        tabTitles.add("全部");
        tabTitles.add("写字楼");
        tabTitles.add("小区");
        tabTitles.add("学校");

        for (int i = 0; i < tabTitles.size(); i++) {

            MyDeviceAddSiteChooseListFragment fragment = new MyDeviceAddSiteChooseListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("searchTypeStr", tabTitles.get(i));
            bundle.putParcelable("curLatlng", curLatlng);
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();

        mAdapter = new MyDeviceSiteChooseAdapter(getSupportFragmentManager(), fragmentList, tabTitles);
        vpSiteSearch.setAdapter(mAdapter);
//        vpSiteSearch.setOffscreenPageLimit(tabTitles.size());
        tlSiteSearch.setupWithViewPager(vpSiteSearch);

        etSiteSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Log.e("--------", " v.getText().toString().trim() : " + v.getText().toString().trim());

                        String searchText = v.getText().toString().trim();

                        if (TextUtils.isEmpty(searchText)) {
                            UIUtils.showToast("请输入搜索关键字");
                        } else {
                            UIUtils.hideSoftInput(etSiteSearchInput);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("curLatlng", curLatlng);
                            bundle.putString("searchKeyword", searchText);
                            EventBus.getDefault().post(new MessageEvent(MessageEvent.ACTION_SITE_SEARCH_KEYWORD, bundle));
                        }
                        break;
                }
                return true;
            }
        });

    }

    @OnClick({R.id.et_site_search_input, R.id.tv_site_choose_list_cancel, R.id.ll_site_choose_city})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_site_search_input:
                if (!isSearchListFraShow) {
                    //地点Fragment没有显示,此时分城市Fragment没有显示和显示两种状态
                    //只有在地点Fragment没有显示时,点击才会有反应
                    etSiteSearchInput.setCursorVisible(true);
                    if (flSiteSearchList.getVisibility() != View.VISIBLE) {
                        flSiteSearchList.setVisibility(View.VISIBLE);
                    }
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();

                    if (isSearchCityFraShow) {
                        //城市Fragment处于显示状态
                        if (searchListFragment == null) {
                            searchListFragment = new MyDeviceAddSiteChooseSearchListFragment();
                            transaction.hide(searchCityFragment);
                            transaction.add(R.id.fl_site_search_list, searchListFragment);
                        } else {
                            transaction.hide(searchCityFragment);
                            transaction.show(searchListFragment);
                        }

                        isSearchCityFraShow = false;
                        ivSiteCityArrow.setBackgroundResource(R.drawable.cart_icon_down_arrow);
                    } else {
                        //城市列表处于未显示状态
                        if (searchListFragment == null) {
                            searchListFragment = new MyDeviceAddSiteChooseSearchListFragment();

                            if (searchCityFragment == null) {
                                transaction.replace(R.id.fl_site_search_list, searchListFragment);
                            } else {
                                transaction.add(R.id.fl_site_search_list, searchListFragment);
                            }
                        } else {
                            transaction.show(searchListFragment);
                        }
                    }
                    transaction.commit();
                    isSearchListFraShow = true;
                }

                break;
            case R.id.ll_site_choose_city:
                if (flSiteSearchList.getVisibility() != View.VISIBLE) {
                    flSiteSearchList.setVisibility(View.VISIBLE);
                }
                FragmentManager fmCity = getSupportFragmentManager();
                FragmentTransaction transactionCity = fmCity.beginTransaction();
                if (!isSearchCityFraShow) {
                    //城市Fragment没有显示,此时分地点Fragment没有显示和在显示两种状态
                    if (isSearchListFraShow) {
                        //地点列表处于显示状态
                        if (searchCityFragment == null) {
                            searchCityFragment = new MyDeviceAddSiteChooseCityListFragment();
                            transactionCity.hide(searchListFragment);
                            transactionCity.add(R.id.fl_site_search_list, searchCityFragment);
                        } else {
                            transactionCity.hide(searchListFragment);
                            transactionCity.show(searchCityFragment);
                        }
                        isSearchListFraShow = false;
                    } else {
                        //地点列表处于未显示状态
                        //地点列表处于未显示状态时,此时因为城市Fragment也没有显示,所以地点Fragment一定为null
                        if (searchCityFragment == null) {
                            searchCityFragment = new MyDeviceAddSiteChooseCityListFragment();

                            if (searchListFragment == null) {
                                transactionCity.replace(R.id.fl_site_search_list, searchCityFragment);
                            } else {
                                transactionCity.add(R.id.fl_site_search_list, searchCityFragment);
                            }
                        } else {
                            transactionCity.show(searchCityFragment);
                        }
                    }

                    transactionCity.commit();
                    etSiteSearchInput.setCursorVisible(false);
                    ivSiteCityArrow.setBackgroundResource(R.drawable.cart_icon_up_arrow);
                    isSearchCityFraShow = true;
                    UIUtils.hideSoftInput(etSiteSearchInput);
                } else {
                    //城市Fragment处于显示状态,此时分地点Fragment==null和地点Fragment!=null两种情况
                    transactionCity.hide(searchCityFragment);
                    transactionCity.commit();
                    ivSiteCityArrow.setBackgroundResource(R.drawable.cart_icon_down_arrow);
                    isSearchCityFraShow = false;
                }

                if (!isSearchCityFraShow && !isSearchListFraShow) {
                    flSiteSearchList.setVisibility(View.GONE);
                }

                break;
            case R.id.tv_site_choose_list_cancel:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (searchListFragment != null && isSearchListFraShow) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            transaction.hide(searchListFragment);
            transaction.commit();
            isSearchListFraShow = false;
            flSiteSearchList.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getAction() == MessageEvent.ACTION_SITE_CHOOSED) {
            //如果收到了地址被选择的消息，则关闭当前Activity
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registEventBus(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistEventBus(this);
    }
}
