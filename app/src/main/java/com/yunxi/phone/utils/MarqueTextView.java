package com.yunxi.phone.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yunxi on 2017/3/28.
 */

public class MarqueTextView extends TextView {

    public MarqueTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MarqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueTextView(Context context) {
        super(context);
    }

    @Override

    public boolean isFocused() {
        return true;
    }
}
