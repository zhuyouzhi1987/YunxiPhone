package com.yunxi.phone.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by bond on 2016/11/9.
 */
public class LockView3 extends RelativeLayout {
    Context mContext;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    OnDownListener onDownListener = new OnDownListener() {
        @Override
        public void onDown(float x, float y) {
        }

        @Override
        public void onUp(float x, float y) {

        }
    };

    public OnDownListener getOnDownListener() {
        return onDownListener;
    }

    public void setOnDownListener(OnDownListener onDownListener) {
        this.onDownListener = onDownListener;
    }

    public LockView3(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    public LockView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LockView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 = ev.getY();
                onDownListener.onDown(x1, y1);
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = ev.getX();
                y2 = ev.getY();
                if (Math.abs(y2 - y1) > 100) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                onDownListener.onUp(x1, y1);
                break;
        }
        return false;
    }

    public interface OnDownListener {
        public void onDown(float x, float y);

        public void onUp(float x, float y);
    }
}
