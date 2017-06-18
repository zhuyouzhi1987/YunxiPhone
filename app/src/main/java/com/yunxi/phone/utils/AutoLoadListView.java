package com.yunxi.phone.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yunxi.phone.R;

/**
 * Created by bond on 2016/12/30.
 */

public class AutoLoadListView extends ListView {
    private boolean isLoading;
    private LoadingView loadingView;
    public boolean hasMoreItems;
    private Pagingable pagingableListener;
    private BackTop backTopListener;


    public AutoLoadListView(Context context) {
        this(context, null);
    }

    public AutoLoadListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public boolean isLoading() {
        return this.isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void setPagingableListener(Pagingable pagingableListener) {
        this.pagingableListener = pagingableListener;
    }

    public void setBackTopListener(BackTop backTopListener){
        this.backTopListener=backTopListener;
    }


    //是否还有更多数据加载,在没有的时候remove叼footerview
    public void setHasMoreItems(boolean hasMoreItems) {
        this.hasMoreItems = hasMoreItems;
        if (!this.hasMoreItems) {
            removeFooterView(loadingView);
        } else if (findViewById(R.id.footer_container) == null) {
            addFooterView(loadingView);
            ListAdapter adapter = ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter();
            setAdapter(adapter);
        }
    }

    public boolean hasMoreItems() {
        return this.hasMoreItems;
    }

    private void init() {
        isLoading = false;
        loadingView = new LoadingView(getContext());
        //设置footerview
        addFooterView(loadingView);
        //添加滑动监听
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {


                backTopListener.onBackTop(i);


            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //获取当前底下的条目位置,其值为第一个可见的条目加上所有可见的条目,
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                //为了防止重复加载更多,我们需要一个boolean变量表示已经在刷新,防止重复刷新,
                //同样,在没有更多数据加载的时候,我们也不需要去加载更多
                //当可见的最后一个条目为最后的一个条目,说明就滑动到了底部,
                //在都满足了之后,我们就去回调加载更多的方法
                if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
                    //判断加载回调更多是否不为null,
                    if (pagingableListener != null) {
                        isLoading = true;
                        pagingableListener.onLoadMoreItems();
                    }

                }



            }
        });
    }
    public interface Pagingable {
        void onLoadMoreItems();
    }


    public interface BackTop{
        void onBackTop(int state);
    }


}
