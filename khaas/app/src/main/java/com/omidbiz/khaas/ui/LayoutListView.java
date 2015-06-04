package com.omidbiz.khaas.ui;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.omidbiz.khaas.KhaasConstant;

public class LayoutListView extends ListView
{

    public static interface OnInterceptTouchEventListener
    {
        public abstract boolean onInterceptTouchEvent(MotionEvent event);
    }

    private OnInterceptTouchEventListener onInterceptTouchEventListener;
    private int height = -1;

    public LayoutListView(Context context)
    {
        super(context);
    }

    public LayoutListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LayoutListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener listener)
    {
        onInterceptTouchEventListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (onInterceptTouchEventListener != null)
        {
            return onInterceptTouchEventListener.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
//        super.onLayout(changed, left, top, right, bottom);
        View v = getChildAt(getChildCount() - 1);
        if (v != null && height > 0 && changed && ((bottom - top) < height))
        {
            int b = height - v.getTop();
            final int scrollTo = getLastVisiblePosition();
            super.onLayout(changed, left, top, right, bottom);
            final int offset = (bottom - top) - b;
            post(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        //FIXME : what should I do in API Level < 21
                        //setSelectionFromTop(scrollTo, offset - getPaddingTop());
                    }
                    catch (Exception e)
                    {
                        Log.i(KhaasConstant.LOG_TAG, e.getMessage());
                    }
                }
            });
        }
        else
        {
            try
            {
                super.onLayout(changed, left, top, right, bottom);
            }
            catch (Exception e)
            {
                Log.i(KhaasConstant.LOG_TAG, e.getMessage());
            }
        }
        height = (bottom - top);
    }
}

