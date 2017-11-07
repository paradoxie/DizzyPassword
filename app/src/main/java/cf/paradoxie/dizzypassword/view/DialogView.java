package cf.paradoxie.dizzypassword.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cf.paradoxie.dizzypassword.R;

/**
 * Created by xiehehe on 2017/11/7.
 */

public class DialogView extends Dialog implements View.OnClickListener {

    private View mView;
    private Context mContext;

    private LinearLayout mLinearLayout;
    private TextView mTitleTv,mTextView;
    private EditText mEditText;
    private Button mButton;
    private Button mPosBtn;


    public DialogView(Context context) {
        this(context, 0, null);
    }

    public DialogView(Context context, int theme, View contentView) {
        super(context, theme == 0 ? R.style.MyDialogStyle : theme);

        this.mView = contentView;
        this.mContext = context;

        if (mView == null) {
            mView = View.inflate(mContext, R.layout.view_enter_edit, null);
        }

        init();
        initView();
        initData();
        initListener();

    }

    private void init() {
        this.setContentView(mView);
    }

    private void initView() {
        mLinearLayout = (LinearLayout) mView.findViewById(R.id.lLayout_bg);
        mTitleTv = (TextView) mView.findViewById(R.id.txt_title);
        mTextView = (TextView) mView.findViewById(R.id.txt_title_account);
        mEditText = (EditText) mView.findViewById(R.id.et_msg);
        mButton = (Button) mView.findViewById(R.id.btn_neg);
        mPosBtn = (Button) mView.findViewById(R.id.btn_pos);
    }

    private void initData() {
        mLinearLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (getMobileWidth(mContext) * 0.85), FrameLayout.LayoutParams.WRAP_CONTENT));
    }

    private void initListener() {
        mButton.setOnClickListener(this);
        mPosBtn.setOnClickListener(this);
    }

    public void setAccount(String s ){
        mTextView.setText(s);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_neg:
                if (onPosNegClickListener != null) {
                    String mEtValue = mEditText.toString().trim();
                    if (!mEtValue.isEmpty()) {
                        onPosNegClickListener.negCliclListener(mEtValue);
                    }
                }
                this.dismiss();
                break;

            case R.id.btn_pos:    //确认
                if (onPosNegClickListener != null) {
                    String mEtValue = mEditText.getText().toString().trim();
                    if (!mEtValue.isEmpty()) {
                        onPosNegClickListener.posClickListener(mEtValue);
                    }
                }
                this.dismiss();
                break;
        }
    }

    private OnPosNegClickListener onPosNegClickListener;

    public void setOnPosNegClickListener(OnPosNegClickListener onPosNegClickListener) {
        this.onPosNegClickListener = onPosNegClickListener;
    }

    public interface OnPosNegClickListener {
        void posClickListener(String value);

        void negCliclListener(String value);
    }


    /**
     * 工具类
     *
     * @param context
     * @return
     */
    public static int getMobileWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels; // 得到宽度
        return width;
    }
}
