package com.liuh.learn.imitateele.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liuh.learn.imitateele.MessageEvent;
import com.liuh.learn.imitateele.R;
import com.liuh.learn.imitateele.adapter.MyDeviceAddSiteSearchListAdapter;
import com.liuh.learn.imitateele.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2018/3/7 16:52
 * Description:我的设备的添加的位置选择的搜索出来的列表界面
 */

public class MyDeviceAddSiteChooseSearchListFragment extends BaseFragment {

    @BindView(R.id.rv_sitelist)
    RecyclerView rvSiteList;

    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;

    private PoiSearch mPoiSearch;


    private List<PoiInfo> poiInfos = new ArrayList<PoiInfo>();

    MyDeviceAddSiteSearchListAdapter mAdapter;

    private String searchKeyWord;

    private LatLng currentLatLng;//坐标点位置

    @Override
    protected void loadData() {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_mydeviceadd_site_choose_searchlist;
    }


    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mAdapter = new MyDeviceAddSiteSearchListAdapter(R.layout.item_mydevice_add_sitelist, poiInfos);
        rvSiteList.setAdapter(mAdapter);
        rvSiteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.bindToRecyclerView(rvSiteList);
        mAdapter.setEmptyView(R.layout.view_load_empty);
        mPoiSearch = PoiSearch.newInstance();

        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
    }

    @Override
    protected void initListener() {
        super.initListener();

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.ACTION_SITE_CHOOSED, poiInfos.get(position)));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getAction() == MessageEvent.ACTION_SITE_SEARCH_KEYWORD) {

            loadingLayout.setVisibility(View.VISIBLE);
            Bundle bundle = (Bundle) messageEvent.getMessage();
            if (bundle != null) {
                currentLatLng = bundle.getParcelable("curLatlng");
                searchKeyWord = bundle.getString("searchKeyword");
                Log.e("-----------------", "searchKeyWord : " + searchKeyWord);


                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                        .keyword(searchKeyWord)
                        .sortType(PoiSortType.distance_from_near_to_far)
                        .location(currentLatLng)
                        .radius(1000)
                        .pageNum(20));
            } else {
                UIUtils.showToast("发生参数错误");
                loadingLayout.setVisibility(View.GONE);
                mAdapter.setNewData(null);
            }
        }
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果
            loadingLayout.setVisibility(View.GONE);
            if (result != null && result.getAllPoi() != null) {

                poiInfos = result.getAllPoi();
                if (poiInfos != null && poiInfos.size() > 0) {
                    mAdapter.setNewData(poiInfos);
                } else {
                    Log.e("--------------", " poiInfos == null || poiInfos.size() ==0");
                    mAdapter.setNewData(null);
                }
            } else {
                Log.e("--------------", "result==null||result.getAllPoi()==null");
                mAdapter.setNewData(null);
            }
        }

        public void onGetPoiDetailResult(PoiDetailResult result) {
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerEventBus(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEventBus(this);
        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }
    }
}
