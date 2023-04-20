package cf.paradoxie.dizzypassword.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cf.paradoxie.dizzypassword.R;

/**
 * Created by xiehehe on 2017/11/12.
 */

public class ClearableEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {
    private Drawable clearTextIcon;
    private View.OnFocusChangeListener mOnFocusChangeListener;
    private View.OnTouchListener mOnTouchListener;
    private boolean canClear = false;

    public ClearableEditText(final Context context) {
        super(context);
        init(context);
    }

    public ClearableEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClearableEditText(final Context context, final AttributeSet attrs,
                             final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void setOnFocusChangeListener(final View.OnFocusChangeListener
                                                 onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final View.OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private void init(final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_clear_20);
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        clearTextIcon = wrappedDrawable;
        clearTextIcon.setBounds(0, 0, clearTextIcon.getIntrinsicWidth(),
                clearTextIcon.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
            setCanClear(true);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (x > getWidth() - getPaddingRight() - clearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (clearTextIcon.isVisible()) {
                    setError(null);
                    setText("");
                } else if (isCanClear()) {
                    setCanClear(false);
                    setError(null);
                    setText("");
                }
            }
            return true;
        } else {
            return mOnTouchListener != null && mOnTouchListener.onTouch(view,
                    motionEvent);
        }
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final
    int before, final int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void setClearIconVisible(final boolean visible) {
        clearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], visible ?
                clearTextIcon :
                null, compoundDrawables[3]);
    }

    public synchronized boolean isCanClear() {
        return canClear;
    }

    public synchronized void setCanClear(boolean canClear) {
        this.canClear = canClear;
    }
}
