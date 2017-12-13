package com.example.administrator.ofounlock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 仿ofo解锁的View
 * Created by lishaojie on 2017/12/12.
 */


public class OfoUnlockView extends View {
    //每个圆圈的周期
    public static final int LIFE_DURATION = 2000;
    //默认宽高
    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT =600;

    private final int STEP = 500;
    private
    @ColorInt
    int mCircleColor = Color.parseColor("#ffF5C70D");
    @ColorInt
    int mTextColor = Color.parseColor("#ff212121");
    int mTextSize = DisplayUtil.sp2px(24);
    private Paint mCirclePaint = new Paint();
    private Paint mTextPaint = new TextPaint();
    private List<Circle> mCircles;
    private int mMaxRadius;
    private int mWidth;
    private int mHeight;
    private int mProgress;
    //是否结束绘制
    private boolean mIsRun = true;
    private ValueAnimator mValueAnimator = ValueAnimator.ofInt(0, 100);
    private float mBaseLine;


    public OfoUnlockView(Context context) {
        this(context, null);
    }

    public OfoUnlockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfoUnlockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specWidthMode == MeasureSpec.AT_MOST) {
            //宽为wrap_content
            mWidth = DEFAULT_WIDTH;
        } else {
            mWidth = specWidthSize;
        }
        if (specHeightMode == MeasureSpec.AT_MOST) {
            //高为wrap_content
            mHeight = DEFAULT_HEIGHT;
        } else {
            mHeight = specHeightSize;
        }
        setMeasuredDimension(mWidth, mHeight);
        initCircles();

    }

    private void initView() {
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mBaseLine = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;

        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (int) animation.getAnimatedValue();
                if (mProgress >= 100) {
                    mIsRun = false;
                }
            }

        });
        mValueAnimator.setDuration(5000);

    }

    private void initCircles() {
        if (mCircles != null) return;
        if (mMaxRadius <= 0) {
            mMaxRadius = Math.min(mWidth - getPaddingLeft() - getPaddingRight(), mHeight - getPaddingTop() - getPaddingBottom()) / 2;
        }
        int count = LIFE_DURATION / STEP;
        mCircles = new ArrayList<>();
        long currentTime = System.currentTimeMillis() - LIFE_DURATION;
        for (int i = 0; i < count; i++) {
            mCircles.add(new Circle(currentTime + i * STEP));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Circle cirCle : mCircles) {
            drawCircle(canvas, cirCle);
        }
        drawText(canvas);
        if (mIsRun){
            invalidate();
        }
    }


    private void drawText(Canvas canvas) {
        String text = mIsRun ? mProgress + "%":"开锁成功";
        if (mIsRun) {
            canvas.drawText(text, mWidth / 2, mHeight / 2 + mBaseLine, mTextPaint);
            if (!mValueAnimator.isRunning()) {
                mValueAnimator.start();
            }
        } else {
            canvas.drawText(text, mWidth / 2, mHeight / 2 + mBaseLine, mTextPaint);
        }

    }

    private void drawCircle(Canvas canvas, Circle circle) {
        int radius = circle.getCurrentRadius();
        mCirclePaint.setColor(circle.getColor());
        canvas.drawCircle(mWidth / 2, mHeight / 2, radius, mCirclePaint);
    }

    class Circle {
        long mCreateTime;
        public @ColorInt
        int mColor;

        //最大半径

        public Circle(long circleTime) {
            mCreateTime = circleTime;
        }

        public int getCurrentRadius() {
            //通过取模运算实现半径以及颜色的变化
            float percent = (System.currentTimeMillis() - mCreateTime) % LIFE_DURATION * 1.0f / LIFE_DURATION;
            mColor = ColorUtils.setAlphaComponent(mCircleColor, (int) ((1 - percent) * 255));
            return (int) (percent * mMaxRadius);
        }

        public @ColorInt
        int getColor() {
            return mColor;
        }

    }
}
