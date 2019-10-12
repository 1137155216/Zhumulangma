package com.gykj.zhumulangma.home.fragment;


import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gykj.zhumulangma.common.mvvm.view.BaseMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.SearchHistoryAdapter;
import com.gykj.zhumulangma.home.adapter.SearchHotAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.SearchViewModel;

import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:搜索下历史页
 */
public class SearchHistoryFragment extends BaseMvvmFragment<SearchViewModel> implements View.OnClickListener {

    private SearchHistoryAdapter mHistoryAdapter;
    private SearchHotAdapter mHotAdapter;
    private onSearchListener mSearchListener;

    public SearchHistoryFragment() {
    }

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_search_history;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView(View view) {
        RecyclerView rvHistory = fd(R.id.rv_history);
        TextView tvClear = fd(R.id.tv_clear);
        tvClear.setOnClickListener(this);
        RecyclerView rvHot = fd(R.id.rv_hot);

        rvHistory.setLayoutManager(new com.library.flowlayout.FlowLayoutManager());
        rvHistory.setHasFixedSize(true);

        mHistoryAdapter = new SearchHistoryAdapter(R.layout.common_item_tag);
        mHistoryAdapter.bindToRecyclerView(rvHistory);
        mHistoryAdapter.setOnItemClickListener((adapter, view1, position) -> {
            if (mSearchListener != null)
                mSearchListener.onSearch(mHistoryAdapter.getItem(position).getKeyword());
        });

        rvHot.setLayoutManager(new com.library.flowlayout.FlowLayoutManager());
        rvHot.setHasFixedSize(true);

        mHotAdapter = new SearchHotAdapter(R.layout.common_item_tag);
        mHotAdapter.bindToRecyclerView(rvHot);
        mHotAdapter.setOnItemClickListener((adapter, view12, position) -> {
            if (mSearchListener != null)
                mSearchListener.onSearch(mHotAdapter.getItem(position).getSearchword());
        });
    }


    public void refreshHistory() {
        mViewModel.refreshHistory();
    }

    @Override
    public void initData() {
        mViewModel.getHistory();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.tv_clear == id) {
            mViewModel.clearHistory();
            mHistoryAdapter.setNewData(null);
        }
    }

    public void setSearchListener(onSearchListener searchListener) {
        mSearchListener = searchListener;
    }

    @Override
    public Class<SearchViewModel> onBindViewModel() {
        return SearchViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    public interface onSearchListener {

        void onSearch(String keyword);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getHotWordsEvent().observe(this, hotWords -> mHotAdapter.setNewData(hotWords));
        mViewModel.getHistorySingleLiveEvent().observe(this, historyBeanList ->
                mHistoryAdapter.setNewData(historyBeanList));
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy() called");
    }
}
