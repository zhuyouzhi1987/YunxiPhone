package com.yunxi.phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yunxi.phone.R;
import com.yunxi.phone.adapter.CloudCenterAdapter;
import com.yunxi.phone.adapter.NewsKindAdapter;
import com.yunxi.phone.bean.NewsBean;
import com.yunxi.phone.bean.NewsContentBean;
import com.yunxi.phone.utils.ACache;
import com.yunxi.phone.utils.AddressApi;
import com.yunxi.phone.utils.AutoLoadListView;
import com.yunxi.phone.utils.L;
import com.yunxi.phone.utils.MySwipeRefreshLayout;
import com.yunxi.phone.utils.XUtil;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class NewsItemFragment extends Fragment {


	private int page=1;

	private boolean isRefresh;

	private AutoLoadListView news_kind_listview;
	private MySwipeRefreshLayout swipeRefreshLayout;

	private NewsKindAdapter mAdapter;

	private ArrayList<NewsContentBean> newsList;

	private int title;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=this.getArguments();

		if(bundle != null){
			title = bundle.getInt("arg");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contextView = inflater.inflate(R.layout.news_fragment_item, container, false);

		init(contextView);


		return contextView;
	}



	private void init(View view) {


		news_kind_listview=(AutoLoadListView)view.findViewById(R.id.news_kind_listview);

		swipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);


		mAdapter=new NewsKindAdapter(getActivity());
		news_kind_listview.setAdapter(mAdapter);
		news_kind_listview.setHasMoreItems(false);

//        //下拉刷新
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				isRefresh=true;
				page=1;

				loadNewsData(1);

			}
		});



		//加载更多

		news_kind_listview.setPagingableListener(new AutoLoadListView.Pagingable() {
			@Override
			public void onLoadMoreItems() {

				loadNewsData(page);

			}
		});


		news_kind_listview.setBackTopListener(new AutoLoadListView.BackTop(){


			@Override
			public void onBackTop(int state) {

			}
		});


		loadNewsData(page);

	}

	private void loadNewsData(int pager){

		Map<String, Object> map = new HashMap<>();

		ACache mCache = ACache.get(getActivity());

		if("".equals(mCache.getAsString("user_id")) || mCache.getAsString("user_id")==null){
			map.put("user_id","0");
		}else{
			map.put("user_id",mCache.getAsString("user_id"));
		}

		if("".equals(mCache.getAsString("token")) || mCache.getAsString("token")==null){
			map.put("userkey","0");
		}else{
			map.put("userkey",mCache.getAsString("token"));
		}

		map.put("category",title);

		map.put("page", pager);


		XUtil.Post(getActivity(), AddressApi.NEWS_LIST, map, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {

				L.d("=======新请求的结果======="+result.toString());

				boolean hasMoreData = true;

				NewsBean bean = JSON.parseObject(result,
						NewsBean.class);



				if ("200".equals(bean.getResult()) ){

					newsList=bean.getData();

					if (newsList.size() > 0) {

						if (isRefresh) {
							mAdapter.clear();
							mAdapter.notifyDataSetChanged();
							isRefresh = false;
						}


						mAdapter.addAll(newsList);
						mAdapter.notifyDataSetChanged();

						swipeRefreshLayout.setRefreshing(false);
						news_kind_listview.setIsLoading(false);

						news_kind_listview.setHasMoreItems(hasMoreData);

						page = page + 1;



					} else {
						hasMoreData = false;
						swipeRefreshLayout.setRefreshing(false);
						news_kind_listview.setHasMoreItems(hasMoreData);
						news_kind_listview.setIsLoading(false);

					}


				} else if ("500".equals(bean.getResult())) {
					Toast.makeText(getActivity(), bean.getMsg(), Toast.LENGTH_SHORT).show();

					swipeRefreshLayout.setRefreshing(false);
					news_kind_listview.setHasMoreItems(false);
					news_kind_listview.setIsLoading(false);
				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {

				L.d("异常"+ex.toString());


				Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();

				swipeRefreshLayout.setRefreshing(false);
				news_kind_listview.setHasMoreItems(false);
				news_kind_listview.setIsLoading(false);
			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}
		});

	}





}