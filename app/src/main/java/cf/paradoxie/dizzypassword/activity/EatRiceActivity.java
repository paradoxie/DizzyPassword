package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.google.android.gms.common.util.CollectionUtils;
import com.junmeng.lib.sharemodel.viewmodel.ShareViewModelProvider;
import com.zhpan.bannerview.constants.IndicatorGravity;
import com.zhpan.bannerview.constants.PageStyle;
import com.zhpan.indicator.enums.IndicatorSlideMode;

import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.BannerAdapter;
import cf.paradoxie.dizzypassword.adapter.GridMineAdapter;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.databinding.ActivityEatriceBinding;
import cf.paradoxie.dizzypassword.http.HttpListener;
import cf.paradoxie.dizzypassword.http.HttpUtils;
import cf.paradoxie.dizzypassword.bean.CommonEntity;
import cf.paradoxie.dizzypassword.room.CommonViewModel;
import cf.paradoxie.dizzypassword.utils.JumpUtil;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class EatRiceActivity extends BaseActivity {
    private ActivityEatriceBinding binding;
    private GridMineAdapter gridAdapter;
    private CommonViewModel commonViewModel;
    private List<CommonEntity> commonEntities = new ArrayList<>();
    private List<CommonEntity> bannerEntities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding(R.layout.activity_eatrice);
        commonViewModel = ShareViewModelProvider.get(this, CommonViewModel.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("好东西😏");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> finish());
        init();
        ThemeUtils.initStatusBarColor(EatRiceActivity.this, ThemeUtils.getPrimaryDarkColor(EatRiceActivity.this));
    }

    private void init() {

        binding.bannerView.registerLifecycleObserver(getLifecycle())
                .setAdapter(new BannerAdapter(bean->{
                    JumpUtil.jump(EatRiceActivity.this, bean);
                }))
                .setScrollDuration(500)
                .setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                .setIndicatorGravity(IndicatorGravity.END)
                .setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
                .setRoundCorner(30)
                .create();

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
            //每排3个，禁止竖向滑动 RecyclerView 为垂直状态（VERTICAL）
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        binding.rvMine.setLayoutManager(manager);
        gridAdapter = new GridMineAdapter(this,bean->{
            JumpUtil.jump(EatRiceActivity.this, bean);
        });
        binding.rvMine.setAdapter(gridAdapter);

        getAds();

        commonViewModel.getAll().observe(this, entities -> {
            if (entities != null) {
                commonEntities.clear();
                bannerEntities.clear();
                commonEntities.addAll(entities);
                for(CommonEntity commonEntity:commonEntities){
                    if(commonEntity.isBanner()){
                        bannerEntities.add(commonEntity);
                    }
                }
                binding.bannerView.refreshData(bannerEntities);
                gridAdapter.setLists(commonEntities);
            }
        });

    }

    private void getAds() {
        HttpUtils.getInstance().getAds(new HttpListener<List<CommonEntity>>() {

            @Override
            public void success(List<CommonEntity> adBeans) {
                if (!CollectionUtils.isEmpty(adBeans)) {
                    commonViewModel.deleteAll();
//                    Log.e("推广信息", adBeans.toString());
                    for (CommonEntity ad : adBeans) {
                        commonViewModel.insert(ad);
                    }
                }
            }

            @Override
            public void failed() {

            }
        });
    }

}
