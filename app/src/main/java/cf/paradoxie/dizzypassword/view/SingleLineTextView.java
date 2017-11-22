package cf.paradoxie.dizzypassword.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by xiehehe on 2017/11/21.
 */

public class SingleLineTextView extends android.support.v7.widget.AppCompatTextView {
    private Paint mPaint;
    private float mTextSize;

    public SingleLineTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public SingleLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    public SingleLineTextView(Context context, AttributeSet attrs,
                                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }
    /**
     * getTextSize 返回值是以像素(px)为单位的，而 setTextSize() 默认是 sp 为单位
     * 因此我们要传入像素单位 setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
     */
    private void refitText(String text, int textWidth) {
        if (textWidth > 0) {
            mTextSize = this.getTextSize();//这个返回的单位为px
            mPaint = new Paint();
            mPaint.set(this.getPaint());
            int drawWid = 0;//drawableLeft，Right，Top，Buttom 所有图片的宽
            Drawable[] draws = getCompoundDrawables();
            for (int i = 0; i < draws.length; i++) {
                if(draws[i]!= null){
                    drawWid += draws[i].getBounds().width();
                }
            }
            //获得当前TextView的有效宽度
            int availableWidth = textWidth - this.getPaddingLeft()
                    - this.getPaddingRight()- getCompoundDrawablePadding()- drawWid;
            //所有字符所占像素宽度
            float textWidths = getTextLength(mTextSize, text);
            while(textWidths > availableWidth){
                mPaint.setTextSize(--mTextSize);//这里传入的单位是 px
                textWidths = getTextLength(mTextSize, text);
            }
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);//这里设置单位为 px
        }
    }

    /**
     * @param textSize
     * @param text
     * @return 字符串所占像素宽度
     */
    private float getTextLength(float textSize,String text){
        mPaint.setTextSize(textSize);
        return mPaint.measureText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        refitText(getText().toString(), this.getWidth());
    }

}

