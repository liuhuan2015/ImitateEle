package com.liuh.learn.imitateele.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
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
import com.liuh.learn.imitateele.adapter.MyDeviceAddSiteListAdapter;
import com.liuh.learn.imitateele.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2018/3/6 15:09
 * Description:我的设备的添加的选择位置的列表的Fragment
 */

public class MyDeviceAddSiteChooseListFragment extends BaseFragment {

    @BindView(R.id.rv_sitelist)
    RecyclerView rvSiteList;

    private String searchTypeStr;//全部;写字楼;小区;学校

    private LatLng currentLatLng;//坐标点位置

    private GeoCoder mSearch;//逆地理编码（即坐标转地址）

    private PoiSearch mPoiSearch;

    private List<PoiInfo> poiInfos = new ArrayList<PoiInfo>();

    MyDeviceAddSiteListAdapter mAdapter;

    @Override
    protected void loadData() {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_mydeviceadd_site_chooselist;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        mAdapter = new MyDeviceAddSiteListAdapter(R.layout.item_mydevice_add_sitelist, poiInfos);
        rvSiteList.setAdapter(mAdapter);
        rvSiteList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.bindToRecyclerView(rvSiteList);
//        mAdapter.setEmptyView(R.layout.view_load_empty);
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            searchTypeStr = (String) bundle.get("searchTypeStr");
            currentLatLng = bundle.getParcelable("curLatlng");
//            Log.e("--------------", "searchTypeStr : " + searchTypeStr + ",currentLatLng : " + currentLatLng.latitude + "," + currentLatLng.longitude);
        } else {
            Log.e("--------------", "bundle值为null");
        }

        if (currentLatLng != null) {
            if ("全部".equals(searchTypeStr)) {
                //进行地理位置反查(坐标转位置描述)
                mSearch = GeoCoder.newInstance();
                mSearch.setOnGetGeoCodeResultListener(listener);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(currentLatLng));
            } else if ("写字楼".equals(searchTypeStr) || "小区".equals(searchTypeStr) || "学校".equals(searchTypeStr)) {

                mPoiSearch = PoiSearch.newInstance();
                mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                        .keyword(searchTypeStr)
                        .sortType(PoiSortType.distance_from_near_to_far)
                        .location(currentLatLng)
                        .radius(1000)
                        .pageNum(20));
            }
        } else {
            UIUtils.showToast("发生错误:坐标值为空");
        }
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

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            }

            //获取地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
                Log.e("----------", "没有找到检索结果");
            }
            //获取反向地理编码结果
            poiInfos = result.getPoiList();
            if (poiInfos != null && poiInfos.size() > 0) {
                mAdapter.setNewData(poiInfos);
            } else {
                Log.e("--------------", " poiInfos == null || poiInfos.size() ==0");
            }

        }
    };

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {

        public void onGetPoiResult(PoiResult result) {
            //获取POI检索结果

            if (result != null && result.getAllPoi() != null) {

                poiInfos = result.getAllPoi();
                if (poiInfos != null && poiInfos.size() > 0) {
                    mAdapter.setNewData(poiInfos);
                } else {
                    Log.e("--------------", " poiInfos == null || poiInfos.size() ==0");
                }
            } else {
                Log.e("--------------", "result==null||result.getAllPoi()==null");
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
    public void onDestroy() {
        super.onDestroy();
        if (mSearch != null) {
            mSearch.destroy();
        }

        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }

    }
}
