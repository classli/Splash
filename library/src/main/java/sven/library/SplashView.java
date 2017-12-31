package sven.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by sven on 17/1/5.
 */
public class SplashView extends View {

    private Paint paint;
    private Path towerPath;
    private PathMeasure towerPathMeasure;
    private float length;
    private Path towerDst;
    private Path[] couldPaths;
    // from the svg file
    private int towerHeight = 600;
    private int towerWidth = 440;

    private float animatorValue;

    private int couldCount = 4;

    private float couldX[] = {0f, 100f, 350f, 400f};
    private float couldY[] = {-5000f, -5000f, -5000f, -5000f};
    private float couldFinalY[] = {100f, 80f, 200f, 300f};

    private long durationTime = 3000;
    private boolean isAnimationEnd = true;
    private int alpha;
    private float titleY;
    private int screenWidth;
    private String title = "WEIXIAO";

    private static float SCALE = 2f;
    public static float translateX;
    public static float translateY;

    private float finalTitleY = towerHeight + 100;
    private float scaleW;
    private float scaleH;

    private OnEndListener listener;

    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setDrawingCacheEnabled(true);
        if (Build.VERSION.SDK_INT < 21) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        paint = new Paint();
        towerPath = new SvgPathParser().parsePath(getResources().getString(R.string.path_00));
        towerPathMeasure = new PathMeasure(towerPath, true);
        length = towerPathMeasure.getLength();

        towerDst = new Path();
        couldPaths = new Path[couldCount];
        for (int i = 0; i < couldPaths.length; i++) {
            couldPaths[i] = new Path();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        scaleW = w / (float) towerWidth;
        scaleH = h / (float) towerHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //这里SVG过小  就临时这样适配一下。
        canvas.scale(SCALE, SCALE);
        translateX = (screenWidth - towerWidth * SCALE) / 2 - 80;
        translateY = 30;
        canvas.translate(30, 30);

        towerDst.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);

        float stop = length*animatorValue;
        towerPathMeasure.getSegment(0, stop, towerDst, true);
        drawTower(canvas);
        paint.setAlpha(255);
        drawCould(canvas);
        drawTitle(canvas);
    }

    private void drawTitle(Canvas canvas) {
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
        int length = (int) paint.measureText(title);
        int x=(towerWidth-length)/2;
        canvas.drawText(title, x, titleY, paint);
    }

    private void  drawCould(Canvas canvas) {
        for (int i=0; i< couldPaths.length; i++) {
            setupCouldPath(couldPaths[i], i);
            canvas.drawPath(couldPaths[i], paint);
        }
    }

    private void setupCouldPath(Path path, int pos) {
        path.reset();
        path.moveTo(couldX[pos], couldY[pos]);
        path.lineTo(couldX[pos] + 30, couldY[pos]);
        path.quadTo(couldX[pos] + 30 + 30, couldY[pos] - 50, couldX[pos] + 30 + 60, couldY[pos]);
        path.lineTo(couldX[pos] + 30 + 60 + 30, couldY[pos]);
    }

    private void drawTower(Canvas canvas) {
        canvas.drawPath(towerDst, paint);

        if (isAnimationEnd) {
            paint.setAlpha(alpha);
            canvas.drawPath(towerPath, paint);
        }
    }

    public void startAnimate() {
        reset();
        getTowerValueAnimator().start();
        for (int i=0; i < couldPaths.length; i++) {
            final ValueAnimator animator = getCouldValueAnimator(i);
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animator.start();
                }
            }, durationTime/2);
        }
        getTitleAnimate().start();
    }

    private void reset() {
        for (int i = 0; i < couldCount; i++) {
            couldY[i] = 0;
        }
    }

    private ValueAnimator getTitleAnimate() {
        final ValueAnimator va = ValueAnimator.ofFloat(0, finalTitleY);
        va.setDuration(durationTime/2);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                titleY = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        return va;
    }

    private ValueAnimator getCouldValueAnimator(final int pos) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, couldFinalY[pos]);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                couldY[pos] = (float) valueAnimator.getAnimatedValue();
                postInvalidateDelayed(10);
            }
        });
        valueAnimator.setDuration(1500);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        return valueAnimator;
    }

    private ValueAnimator getTowerValueAnimator() {
        final ValueAnimator towerAnimator = ValueAnimator.ofFloat(0, 1f);
        towerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animatorValue = (float) valueAnimator.getAnimatedValue();
                postInvalidateDelayed(10);
            }
        });
        towerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationEnd = true;
                invalidate();
                getAlphaAnimator().start();
                towerAnimator.removeAllUpdateListeners();

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimationEnd = false;
            }
        });
        towerAnimator.setDuration(durationTime);
        towerAnimator.setInterpolator(new DecelerateInterpolator());
        return towerAnimator;
    }

    private ValueAnimator getAlphaAnimator() {
        final ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 255);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                alpha = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                alphaAnimator.removeAllUpdateListeners();
                if (listener != null) {
                    listener.onEnd(SplashView.this);
                }
            }
        });
        alphaAnimator.setDuration(500);
        return alphaAnimator;
    }

    public interface OnEndListener {
        void onEnd(SplashView view);
    }

    public void setOnEndListener(OnEndListener listener) {
        this.listener = listener;
    }
}

