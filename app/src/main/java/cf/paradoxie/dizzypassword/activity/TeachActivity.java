package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.bean.CommonEntity;
import cf.paradoxie.dizzypassword.databinding.ActivityTeachBinding;
import cf.paradoxie.dizzypassword.utils.JumpUtil;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;
import cf.paradoxie.dizzypassword.utils.Utils;

/**
 * Created by xiehehe on 2017/10/28.
 */

public class TeachActivity extends BaseActivity {

    private ActivityTeachBinding binding;
    private CommonEntity bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding(R.layout.activity_teach);
        ThemeUtils.initStatusBarColor(TeachActivity.this, ThemeUtils.getPrimaryDarkColor(TeachActivity.this));

        Toolbar toolbar = findViewById(R.id.toolbar);

        bean = (CommonEntity) getIntent().getSerializableExtra("bean");

        if (bean != null) {
            toolbar.setTitle(bean.getName());

            toolbar.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_open) {
                    MyApplication.showToast("已复制地址");
                    JumpUtil.jumpToDefaultBrowser(TeachActivity.this, bean.getJumpUrl());
                }
                return false;
            });

        } else {
            MyApplication.showToast("参数异常");
            finish();
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> finish());


        init();

    }

    private void init() {
        Utils.loadUri(binding.web, 0, bean.getJumpUrl(), binding.progressBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_open, menu);
        return true;
    }
}
