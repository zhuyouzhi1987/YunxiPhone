package com.yunxi.phone.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yunxi.phone.R;

/**
 * Created by bond on 2016/12/30.
 */

public class LoadingView extends LinearLayout {

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.load_more_view, this);
    }


}
