package cf.paradoxie.dizzypassword.activity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import cf.paradoxie.dizzypassword.R;
import cf.paradoxie.dizzypassword.base.BaseActivity;
import cf.paradoxie.dizzypassword.databinding.ActivityChoiceBinding;
import cf.paradoxie.dizzypassword.utils.ThemeUtils;

public class ChoiceActivity extends BaseActivity {
    private ActivityChoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinding(R.layout.activity_choice);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("身份选择");
        setSupportActionBar(toolbar);
//
        ThemeUtils.initStatusBarColor(ChoiceActivity.this, ThemeUtils.getPrimaryDarkColor(ChoiceActivity.this));
        initViewWithData();

    }

    private void initViewWithData() {
        binding.tvBmob.setOnClickListener(v -> {
            gotoActivity(SignActivity.class);
        });
        binding.tvWebdav.setOnClickListener(v -> {
            gotoActivity(JianGuoSignActivity.class);
        });
        binding.tvNew.setOnClickListener(v -> {
            gotoActivity(JianGuoSignActivity.class);
        });

    }


}
