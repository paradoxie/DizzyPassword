package cf.paradoxie.dizzypassword.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


/**
 * 动画工具类
 */
public class AnimationUtil {
    private static AnimationUtil animationUtil = new AnimationUtil();
    private long mCurrentPlayTime;
    private ObjectAnimator animator;
    private AnimationUtil() {
    }


    public static AnimationUtil getInstance() {
        return animationUtil;
    }

    public void startAnim(ImageView refresh, long time) {
        animator = ObjectAnimator.ofFloat(refresh, "rotation", 0f, 360.0f);
        animator.setDuration(time);
        animator.setInterpolator(new LinearInterpolator());//不停顿
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        startAnimation();
    }

    public void stopAnim() {
        mCurrentPlayTime = animator.getCurrentPlayTime();
        animator.cancel();
    }

    public void startAnimation() {
        animator.start();
        animator.setCurrentPlayTime(mCurrentPlayTime);
    }
}
