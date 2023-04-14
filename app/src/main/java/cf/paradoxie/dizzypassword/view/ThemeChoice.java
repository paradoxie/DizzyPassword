package cf.paradoxie.dizzypassword.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cf.paradoxie.dizzypassword.base.MyApplication;
import cf.paradoxie.dizzypassword.R;


public class ThemeChoice extends DialogPreference implements RadioGroup.OnCheckedChangeListener {


    private RadioGroup mGroup1;
    private RadioGroup mGroup2;
    private RadioGroup mGroup3;
    private RadioGroup mGroup4;

    //    已保存的主题
    private int mTheme;

    private SharedPreferences mSp;
    //    style文件中的所有theme
    private int[] mThemes;
    //    所有的radioButton
    private int[] mRdoBtns;


    private boolean changeGroup = false;
    //    现在的value
    private int mCurrentValue;
    //    新的value
    private int mNewValue;

    private static final int DEFAULT_VALUE = R.style.Theme7;

    public ThemeChoice(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mSp = PreferenceManager.getDefaultSharedPreferences(context);
        mTheme = mSp.getInt("theme_change", R.style.Theme7);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.dialog_theme_choice, null);
        mGroup1 = view.findViewById(R.id.group1);
        mGroup2 = view.findViewById(R.id.group2);
        mGroup3 = view.findViewById(R.id.group3);
        mGroup4 = view.findViewById(R.id.group4);
        mThemes = new int[]{R.style.Theme1, R.style.Theme2, R.style.Theme3
                , R.style.Theme4, R.style.Theme5, R.style.Theme6
                , R.style.Theme7, R.style.Theme8, R.style.Theme9
                , R.style.Theme10, R.style.Theme11,
                //                R.style.Theme12
                //                , R.style.Theme13,R.style.Theme14,
                R.style.Theme15
                , R.style.Theme16, R.style.Theme17
                //                , R.style.Theme18
                , R.style.Theme19};
        mRdoBtns = new int[]{R.id.rdobtn_1, R.id.rdobtn_2, R.id.rdobtn_3, R.id.rdobtn_4, R.id.rdobtn_5
                , R.id.rdobtn_6, R.id.rdobtn_7, R.id.rdobtn_8, R.id.rdobtn_9, R.id.rdobtn_10
                , R.id.rdobtn_11,
                //                R.id.rdobtn_12, R.id.rdobtn_13,R.id.rdobtn_14,
                R.id.rdobtn_15
                , R.id.rdobtn_16, R.id.rdobtn_17
                //                , R.id.rdobtn_18
                , R.id.rdobtn_19};
        for (int i = 0; i < mThemes.length; i++) {
            if (mTheme == mThemes[i]) {
                RadioButton radioButton = view.findViewById(mRdoBtns[i]);
                radioButton.setChecked(true);
                break;
            }
        }

        mGroup1.setOnCheckedChangeListener(this);
        mGroup2.setOnCheckedChangeListener(this);
        mGroup3.setOnCheckedChangeListener(this);
        mGroup4.setOnCheckedChangeListener(this);
        builder.setView(view)
                .setTitle("主题更换")
                .setNegativeButton("", (dialog, which) -> {
                })
                .setPositiveButton("", (dialog, which) -> {
                });
    }


    @Override
    protected View onCreateView(ViewGroup parent) {

        super.onCreateView(parent);
        View mContentView = LayoutInflater.from(getContext()).inflate(
                R.layout.preference_theme_change, parent, false);

        return mContentView;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
        mNewValue = mCurrentValue;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_VALUE);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        Log.d("currentValue", mCurrentValue + "");
        Log.d("newValue", mNewValue + "");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group != null && checkedId != -1 && changeGroup == false) {
            for (int i = 0; i < mRdoBtns.length; i++) {
                if (checkedId == mRdoBtns[i]) {
                    mNewValue = mThemes[i];
                    group.check(mRdoBtns[i]);
                    persistInt(mNewValue);
                    getDialog().cancel();
                }
            }
        }
    }
}
