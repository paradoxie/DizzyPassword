package km.lmy.searchview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义搜索框
 * Created by limuyang on 2017/7/29.
 */

public class SearchView extends LinearLayout {
    public static final int CLOSE = 0;
    public static final int OPEN = 1;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CLOSE, OPEN})
    public @interface State {
    }

    private Context context;

    private String hintText = "";

    private int backIcon = R.drawable.ic_arrow_back_black_24dp;

    private int cleanIcon = R.drawable.ic_clean_input;

    private int historyIcon = R.drawable.ic_history_black_24dp;

    private int defaultState;

    private int historyTextColor = android.R.color.darker_gray;

    private boolean isOnKeyCleanVisible = true;

    private int searchInputViewHeight;


    ImageView ivSearchBack;

    EditText etSearch;

    ImageView clearSearch;

    RecyclerView recyclerView;

    CardView cardViewSearch;

    TextView cleanHistory, close;

    LinearLayout searchLinearLayout;

    LayoutInflater mInflater;

    FlowLayout mFlowLayout, mFlowLayout_name;
    TextView tv;

    /*整体根布局view*/
    private View mView;

    private SearchRecyclerViewAdapter adapter;

    private OnHistoryItemClickListener onHistoryItemClickListener;

    private OnSearchActionListener onSearchActionListener;

    private OnInputTextChangeListener inputTextChangeListener;

    private OnSearchBackIconClickListener onSearchBackIconClickListener;

    private OnCleanHistoryClickListener onCleanHistoryClickListener;

    private List<String> historyList = new ArrayList<>();

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        findView(context);
        initView(context);
        getCustomStyle(attrs);

    }

    /***
     * 捕获控件
     * @param context
     */
    private void findView(Context context) {
        mView = View.inflate(context, R.layout.search_view, this);
        ivSearchBack = (ImageView) mView.findViewById(R.id.iv_search_back);
        etSearch = (EditText) mView.findViewById(R.id.et_search);
        clearSearch = (ImageView) mView.findViewById(R.id.clearSearch);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        cardViewSearch = (CardView) mView.findViewById(R.id.cardView_search);
        cleanHistory = (TextView) mView.findViewById(R.id.clearHistory);
        close = (TextView) mView.findViewById(R.id.close);
        searchLinearLayout = (LinearLayout) mView.findViewById(R.id.search_linearLayout);
    }

    private void initView(final Context context) {
        ivSearchBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSearchBackIconClickListener != null) {
                    KeyboardUtils.hideSoftInput(etSearch, context);
                    onSearchBackIconClickListener.onClick(view);
                } else
                    close();
            }
        });

        cleanHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //                adapter.getData().clear();
                adapter.notifyDataSetChanged();
                switchCleanHistoryDisplay();
                if (onCleanHistoryClickListener != null)
                    onCleanHistoryClickListener.onClick();
            }
        });

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        adapter = new SearchRecyclerViewAdapter(historyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (onHistoryItemClickListener != null)
                    onHistoryItemClickListener.onClick(adapter.getData().get(position).toString(), position);
            }
        });

        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (inputTextChangeListener != null)
                    inputTextChangeListener.beforeTextChanged(charSequence);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                switchOneKeyCleanIconDisplay(charSequence);
                if (inputTextChangeListener != null)
                    inputTextChangeListener.onTextChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputTextChangeListener != null)
                    inputTextChangeListener.afterTextChanged(editable);
            }
        });
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH && onSearchActionListener != null) {
                    onSearchActionListener.onSearchAction(textView.getText().toString());
                    KeyboardUtils.hideSoftInput(etSearch, context);
                    return true;
                }
                return false;
            }
        });
    }

    /***
     * 设置“清除历史记录”的显示隐藏
     */
    private void switchCleanHistoryDisplay() {
        if (adapter.getData() == null || adapter.getData().size() == 0) {
            cleanHistory.setVisibility(GONE);
        } else {
            cleanHistory.setVisibility(VISIBLE);
        }
    }

    /***
     * 设置“一键清除输入”图标的显示隐藏
     */
    private void switchOneKeyCleanIconDisplay(CharSequence charSequence) {
        if (isOnKeyCleanVisible && !TextUtils.isEmpty(charSequence)) {
            clearSearch.setVisibility(VISIBLE);
        } else {
            clearSearch.setVisibility(GONE);
        }
    }

    /**
     * 初始化自定义属性
     *
     * @param attrs
     */
    @SuppressLint("ResourceAsColor")
    public void getCustomStyle(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchView);

        hintText = a.getString(R.styleable.SearchView_hintText);
        backIcon = a.getResourceId(R.styleable.SearchView_backIcon, backIcon);
        defaultState = a.getInt(R.styleable.SearchView_defaultState, CLOSE);
        cleanIcon = a.getResourceId(R.styleable.SearchView_oneKeyCleanIcon, cleanIcon);
        historyIcon = a.getResourceId(R.styleable.SearchView_historyIcon, historyIcon);
        historyTextColor = a.getColor(R.styleable.SearchView_historyTextColor, ContextCompat.getColor(context, historyTextColor));
        searchInputViewHeight = a.getDimensionPixelSize(R.styleable.SearchView_inputView_height, -10);

        setHintText(hintText);
        setBackIcon(backIcon);
        defaultState(defaultState == CLOSE ? CLOSE : OPEN);
        setOneKeyCleanIcon(cleanIcon);
        setHistoryIcon(historyIcon);
        setHistoryTextColor(historyTextColor);
        if (searchInputViewHeight != -10)
            setSearchInputViewHeight(searchInputViewHeight);

        a.recycle();
    }

    public void initFlowView(String[] mVals) {
        mInflater = LayoutInflater.from(context);
        mFlowLayout = (FlowLayout) findViewById(R.id.flowlayout);
        initData(mVals);
    }

    private void initData(String[] mVals) {

        for (int i = 0; i < mVals.length; i++) {

            tv = (TextView) mInflater.inflate(
                    R.layout.search_label_tv, mFlowLayout, false);
            tv.setTextColor(getResources().getColor(R.color.cardview_dark_background));
            tv.setText(mVals[i]);
            tv.setClickable(true);
            tv.setFocusable(true);
            mFlowLayout.addView(tv);
            final String str = tv.getText().toString().trim();
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
//                    String string = etSearch.getText().toString().trim();
//                    etSearch.setText(string + " " + str);
                    etSearch.setText(str);
                }
            });

        }
    }

    public void initFlowViewByName(String[] mVals) {
        mInflater = LayoutInflater.from(context);
        mFlowLayout_name = (FlowLayout) findViewById(R.id.flowlayout_name);
        initDataByName(mVals);
    }

    private void initDataByName(String[] mVals) {

        for (int i = 0; i < mVals.length; i++) {

            tv = (TextView) mInflater.inflate(
                    R.layout.search_label_tv, mFlowLayout_name, false);
            tv.setTextColor(getResources().getColor(R.color.cardview_dark_background));
            tv.setText(mVals[i]);
            tv.setClickable(true);
            tv.setFocusable(true);
            mFlowLayout_name.addView(tv);
            final String str = tv.getText().toString().trim();
            tv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
//                    String string = etSearch.getText().toString().trim();
//                    etSearch.setText(string + " " + str);
                    etSearch.setText(str);
                }
            });

        }
    }

    /**
     * 设置一键清除是否显示
     *
     * @param visible
     */
    public void setOneKeyCleanIsVisible(boolean visible) {
        isOnKeyCleanVisible = visible;
    }

    /***
     * 设置搜索框提示文本
     * @param hintText
     */
    public void setHintText(String hintText) {
        etSearch.setHint(hintText);
    }

    /**
     * 设置搜索框初始文本
     *
     * @param text
     */
    public void setSearchEditText(String text) {
        etSearch.setText(text);
        etSearch.setSelection(text.length());
    }

    /**
     * @return 获取搜索输入框
     */
    public EditText getEditTextView() {
        return etSearch;
    }

    /**
     * @return 获取返回按钮
     */
    public ImageView getBackIV() {
        return ivSearchBack;
    }

    /**
     * @return 获取关闭按钮
     */
    public TextView getCloseTv() {
        return close;
    }

    /**
     * @return 获取单个标签
     */
    public TextView getTv() {
        return tv;
    }

    /**
     * 设置输入框打开或者关闭状态
     *
     * @param enabled
     */
    public void setSearchEditTextEnabled(boolean enabled) {
        etSearch.setEnabled(enabled);
    }

    /**
     * 数值搜索输入框高度
     *
     * @param height
     */
    public void setSearchInputViewHeight(int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) searchLinearLayout.getLayoutParams();
        params.height = height;
        searchLinearLayout.setLayoutParams(params);
    }

    /***
     * 设置返回按钮图标
     * @param icon
     */
    public void setBackIcon(@DrawableRes int icon) {
        ivSearchBack.setImageResource(icon);
    }

    /***
     * 设置清空按钮图标
     * @param icon
     */
    public void setOneKeyCleanIcon(@DrawableRes int icon) {
        clearSearch.setImageResource(icon);
    }

    public void setHistoryIcon(@DrawableRes int icon) {
        adapter.setHistoryIcon(icon);
    }

    /***
     * 设置搜索历史文本颜色
     * @param color
     */
    public void setHistoryTextColor(@ColorInt int color) {
        adapter.setHistoryTextColor(color);
    }

    /***
     * 显示搜索框
     */
    public void open() {
        SearchViewUtils.open(context, cardViewSearch, etSearch);
        switchCleanHistoryDisplay();
        switchOneKeyCleanIconDisplay("");
    }

    /***
     * 关闭搜索框
     */
    public void close() {
        SearchViewUtils.close(context, cardViewSearch, etSearch);
    }

    /***
     * 打开或关闭搜索框
     */
    public void autoOpenOrClose() {
        SearchViewUtils.autoOpenOrClose(context, cardViewSearch, etSearch);
    }

    /***
     * 默认状态：显示或隐藏
     * @param state
     */
    public void defaultState(@State int state) {
        switch (state) {
            case CLOSE:
                cardViewSearch.setVisibility(INVISIBLE);
                break;
            case OPEN:
                cardViewSearch.setVisibility(VISIBLE);
                break;
        }
        switchCleanHistoryDisplay();
        switchOneKeyCleanIconDisplay("");
    }

    /***
     * 添加一条历史纪录
     * @param historyStr
     */
    public void addOneHistory(String historyStr) {
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        adapter.addData(historyStr);
        switchCleanHistoryDisplay();
    }

    /***
     * 添加历史纪录列表
     * @param list
     */
    public void addHistoryList(List<String> list) {
        adapter.addData(list);
        switchCleanHistoryDisplay();
    }

    /***
     * 设置全新的历史记录列表
     * @param list 历史纪录列表
     */
    public void setNewHistoryList(List<String> list) {
        adapter.setNewData(list);
        switchCleanHistoryDisplay();
    }

    /***
     * 搜索框是否打开
     * @return
     */
    public boolean isOpen() {
        return cardViewSearch.getVisibility() == VISIBLE;
    }

    /***
     * 设置历史纪录点击事件
     * @param onHistoryItemClickListener
     */
    public void setHistoryItemClickListener(OnHistoryItemClickListener onHistoryItemClickListener) {
        this.onHistoryItemClickListener = onHistoryItemClickListener;
    }

    /***
     * 设置软键盘搜索按钮点击事件
     * @param onSearchActionListener
     */
    public void setOnSearchActionListener(OnSearchActionListener onSearchActionListener) {
        this.onSearchActionListener = onSearchActionListener;
    }

    /***
     * 设置输入文本监听事件
     * @param onInputTextChangeListener
     */
    public void setOnInputTextChangeListener(OnInputTextChangeListener onInputTextChangeListener) {
        this.inputTextChangeListener = onInputTextChangeListener;
    }

    public void setOnSearchBackIconClickListener(OnSearchBackIconClickListener onSearchBackIconClickListener) {
        this.onSearchBackIconClickListener = onSearchBackIconClickListener;
    }

    /**
     * 设置清除历史点击监听
     *
     * @param onCleanHistoryClickListener
     */
    public void setOnCleanHistoryClickListener(OnCleanHistoryClickListener onCleanHistoryClickListener) {
        this.onCleanHistoryClickListener = onCleanHistoryClickListener;
    }

    public interface OnHistoryItemClickListener {
        void onClick(String historyStr, int position);
    }

    public interface OnSearchActionListener {
        void onSearchAction(String searchText);
    }

    public interface OnInputTextChangeListener {
        void onTextChanged(CharSequence charSequence);

        void beforeTextChanged(CharSequence charSequence);

        void afterTextChanged(Editable editable);
    }

    public interface OnSearchBackIconClickListener {
        void onClick(View view);
    }

    public interface OnCleanHistoryClickListener {
        void onClick();
    }
}
