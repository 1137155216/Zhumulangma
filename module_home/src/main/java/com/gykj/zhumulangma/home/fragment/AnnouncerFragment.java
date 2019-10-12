package com.gykj.zhumulangma.home.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gykj.zhumulangma.common.AppConstants;
import com.gykj.zhumulangma.common.bean.NavigateBean;
import com.gykj.zhumulangma.common.event.EventCode;
import com.gykj.zhumulangma.common.event.KeyCode;
import com.gykj.zhumulangma.common.event.ActivityEvent;
import com.gykj.zhumulangma.common.event.FragmentEvent;
import com.gykj.zhumulangma.common.extra.GlideImageLoader;
import com.gykj.zhumulangma.common.mvvm.view.BaseRefreshMvvmFragment;
import com.gykj.zhumulangma.home.R;
import com.gykj.zhumulangma.home.adapter.AnnouncerAdapter;
import com.gykj.zhumulangma.home.mvvm.ViewModelFactory;
import com.gykj.zhumulangma.home.mvvm.viewmodel.AnnouncerViewModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.banner.BannerV2;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * Author: Thomas.
 * <br/>Date: 2019/9/10 8:23
 * <br/>Email: 1071931588@qq.com
 * <br/>Description:主播
 */
public class AnnouncerFragment extends BaseRefreshMvvmFragment<AnnouncerViewModel, Announcer> implements OnBannerListener,
        BaseQuickAdapter.OnItemClickListener {

    private static final String TAG = "AnnouncerFragment";
    private AnnouncerAdapter mAnnouncerAdapter;

    private Banner banner;
    private SmartRefreshLayout refreshLayout;

    @Override
    protected int onBindLayout() {
        return R.layout.home_fragment_announcer;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView.setBackground(null);
        setSwipeBackEnable(false);
    }

    protected void initView(View view) {
        RecyclerView rvAnnouncer = fd(R.id.rv_announcer);
        refreshLayout = fd(R.id.refreshLayout);
        banner = fd(R.id.banner);
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        banner.setDelayTime(3000);
        mAnnouncerAdapter = new AnnouncerAdapter(R.layout.home_item_announcer);
        rvAnnouncer.setLayoutManager(new LinearLayoutManager(mActivity));
        mAnnouncerAdapter.bindToRecyclerView(rvAnnouncer);
    }

    @Override
    public void initListener() {
        super.initListener();
        banner.setOnBannerListener(this);
        mAnnouncerAdapter.setOnItemClickListener(this);
        ((NestedScrollView) fd(R.id.nsv)).setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (nestedScrollView, i, i1, i2, i3) -> {
                    fd(R.id.fl_title_top).setVisibility(i1 > fd(R.id.ll_title).getTop() ? View.VISIBLE : View.GONE);
                });
    }

    @NonNull
    @Override
    protected WrapRefresh onBindWrapRefresh() {
        return new WrapRefresh(refreshLayout, mAnnouncerAdapter);
    }

    @Override
    public void initData() {
        mViewModel.init();
    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (banner != null)
            banner.startAutoPlay();
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        banner.stopAutoPlay();
    }

    @Override
    public Class<AnnouncerViewModel> onBindViewModel() {
        return AnnouncerViewModel.class;
    }

    @Override
    protected boolean enableSimplebar() {
        return false;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mApplication);
    }

    @Override
    public void initViewObservable() {
        mViewModel.getBannerV2Event().observe(this, bannerV2s -> {
            List<String> images = new ArrayList<>();
            for (BannerV2 bannerV2 : bannerV2s) {
                images.add(bannerV2.getBannerUrl());
            }
            banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
        });
        mViewModel.getInitAnnouncerEvent().observe(this, announcers -> mAnnouncerAdapter.setNewData(announcers));
    }

    @Override
    public void OnBannerClick(int position) {
        BannerV2 bannerV2 = mViewModel.getBannerV2Event().getValue().get(position);
        switch (bannerV2.getBannerContentType()) {
            case 2:
                Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ALBUM_DETAIL)
                        .withLong(KeyCode.Home.ALBUMID, bannerV2.getAlbumId())
                        .navigation();
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                        new NavigateBean(AppConstants.Router.Home.F_ALBUM_DETAIL, (ISupportFragment) navigation)));
                break;
            case 3:
                mViewModel.play(bannerV2.getTrackId());
                break;
            case 1:
                Object navigation1 = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                        .withLong(KeyCode.Home.ANNOUNCER_ID, bannerV2.getBannerUid())
                        .navigation();
                EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                        new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation1)));

                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Object navigation = ARouter.getInstance().build(AppConstants.Router.Home.F_ANNOUNCER_DETAIL)
                .withLong(KeyCode.Home.ANNOUNCER_ID, mAnnouncerAdapter.getData().get(position).getAnnouncerId())
                .withString(KeyCode.Home.ANNOUNCER_NAME, mAnnouncerAdapter.getData().get(position).getNickname())
                .navigation();
        EventBus.getDefault().post(new ActivityEvent(EventCode.Main.NAVIGATE,
                new NavigateBean(AppConstants.Router.Home.F_ANNOUNCER_DETAIL, (ISupportFragment) navigation)));
    }
    @Override
    public  void onEvent(FragmentEvent event) {
        super.onEvent(event);
        switch (event.getCode()){
            case EventCode.Home.TAB_REFRESH:
                if(isSupportVisible()&&mBaseLoadService.getCurrentCallback()!=getInitCallBack().getClass()){
                    fd(R.id.nsv).scrollTo(0,0);
                    ((SmartRefreshLayout)fd(R.id.refreshLayout)).autoRefresh();
                }
                break;
        }
    }
}
