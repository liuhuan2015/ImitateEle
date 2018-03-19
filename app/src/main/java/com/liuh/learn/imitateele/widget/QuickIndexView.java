package com.liuh.learn.imitateele.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by huan on 2018/3/11.
 * 快速索引控件
 */

public class QuickIndexView extends View {

    private static final String[] LETTERS = {"A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X",
            "Y", "Z"};

    private Paint mPaint;

    int mWidth, mHeight;
    float mCellHeight;

    private OnLetterChangeListener onLetterChangeListener;

    public interface OnLetterChangeListener {
        void onLetterChanged(String letter);
    }

    public void setOnLetterChangeListener(OnLetterChangeListener onLetterChangeListener) {
        this.onLetterChangeListener = onLetterChangeListener;
    }

    public QuickIndexView(Context context) {
        this(context, null);
    }

    public QuickIndexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));
//        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < LETTERS.length; i++) {

            Rect rect = new Rect();
            mPaint.getTextBounds(LETTERS[i], 0, 1, rect);

            int cellX = (int) (mWidth * 0.5f - rect.width() * 0.5f);
            int cellY = (int) (mCellHeight * 0.5f + rect.height() * 0.5f + mCellHeight * i);

            canvas.drawText(LETTERS[i], cellX, cellY, mPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mCellHeight = mHeight * 1.0f / LETTERS.length;
    }


    int currentIndex = -1;
    String curLetter = "";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentIndex = (int) (event.getY() / mCellHeight);

                String curLetterDownLast = LETTERS[currentIndex];

                if (!curLetterDownLast.equals(curLetter)) {
                    curLetter = curLetterDownLast;

                    if (onLetterChangeListener != null) {
                        onLetterChangeListener.onLetterChanged(curLetter);
                    }
                }


                break;
            case MotionEvent.ACTION_MOVE:
                currentIndex = (int) (event.getY() / mCellHeight);

                String curLetterMoveLast = LETTERS[currentIndex];
                if (!curLetterMoveLast.equals(curLetter)) {
                    curLetter = curLetterMoveLast;

                    if (onLetterChangeListener != null) {
                        onLetterChangeListener.onLetterChanged(curLetter);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //up的时候重置一下letter的值
                curLetter = "";
                break;
        }


        return true;
    }
}
