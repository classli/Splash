package sven.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sven on 17/1/5.
 */
public class MistView extends View {
    private Bitmap dstBitmap;
    private Bitmap splashBitmap;
    private Paint paint;
    private float scale;
    private long duration = 2000;
    private float alpha;
    private PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    public MistView(Context context) {
        this(context, null);
    }

    public MistView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MistView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        dstBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wow_splash_shade);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (splashBitmap != null) {
            canvas.drawBitmap(splashBitmap, 0, 0, null);
            canvas.scale(scale, scale, dstBitmap.getWidth() / 2, dstBitmap.getHeight() / 2);
            paint.setXfermode(mode);
            canvas.drawBitmap(dstBitmap, 0, 0, paint);
        }
        setAlpha(alpha);
    }

    public void startAnimate(Bitmap bitmap) {
        setSplashBitmap(bitmap);
        getAlphaValueAnimator().start();
        getScaleValueAnimator().start();
    }

    public void setSplashBitmap(Bitmap bitmap) {
        splashBitmap = bitmap;
        invalidate();
    }

    private ValueAnimator getScaleValueAnimator() {
        ValueAnimator scaleVa = ValueAnimator.ofFloat(0, 6);
        scaleVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scale = (float) valueAnimator.getAnimatedValue();
            }
        });
        scaleVa.setDuration(duration);
        return scaleVa;
    }

    private ValueAnimator getAlphaValueAnimator() {
        ValueAnimator alphaVa = ValueAnimator.ofFloat(1, 0f);
        alphaVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                alpha = (float) valueAnimator.getAnimatedValue();
                postInvalidateDelayed(16);
            }
        });
        alphaVa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(GONE);
            }
        });
        alphaVa.setDuration(duration);
        return alphaVa;
    }
}
