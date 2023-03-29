package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import java.util.ArrayList;
import java.util.List;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.adapter.GridMineAdapter;
import cf.paradoxie.dizzypassword.adapter.RecyclerItemClickListener;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.bean.MineNavBean;
import cf.paradoxie.dizzypassword.databinding.ActivityEatriceBinding;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class EatRiceActivity extends BaseActivity {
    private ActivityEatriceBinding binding;
    private GridMineAdapter gridAdapter;
    private List<MineNavBean> mineNavBeans = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding(R.layout.activity_eatrice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("好东西😏");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> finish());
        init();
        ThemeUtils.initStatusBarColor(EatRiceActivity.this, ThemeUtils.getPrimaryDarkColor(EatRiceActivity.this));
    }

    private void init() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL) {
            //每排3个，禁止竖向滑动 RecyclerView 为垂直状态（VERTICAL）
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mineNavBeans.add(new MineNavBean(R.mipmap.ic_ele, "饿了么红包"));
        mineNavBeans.add(new MineNavBean(R.mipmap.ic_mei, "美团红包"));
        mineNavBeans.add(new MineNavBean(R.mipmap.ic_tool, "全能工具箱"));
//        mineNavBeans.add(new MineNavBean(R.mipmap.ic_welcome, "开屏配置"));

        binding.rvMine.setLayoutManager(manager);
        gridAdapter = new GridMineAdapter(this);
        binding.rvMine.setAdapter(gridAdapter);
        gridAdapter.setLists(mineNavBeans);
        binding.rvMine.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
//                        CommonUtil.INSTANCE.startActivityWithAnimate(getActivity(), ManageBannerActivity.class);
                        break;
                    case 1:
//                        CommonUtil.INSTANCE.startActivityWithAnimate(getActivity(), ManageNavActivity.class);
                        break;
                    case 2:
//                        CommonUtil.INSTANCE.startActivityWithAnimate(getActivity(), ManagePicActivity.class);
                        break;
                    case 3:
//                        CommonUtil.INSTANCE.startActivityWithAnimate(getActivity(), WelfareAddActivity.class);
                        break;
                    case 4:
//                        CommonUtil.INSTANCE.startActivityWithAnimate(getActivity(), ManageWelcomeActivity.class);
                        break;

                }

            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        }));
    }

}
