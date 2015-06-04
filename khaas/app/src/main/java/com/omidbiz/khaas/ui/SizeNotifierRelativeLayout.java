package com.omidbiz.khaas.ui;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.omidbiz.khaas.KhaasConstant;


public class SizeNotifierRelativeLayout extends RelativeLayout
{

    private Rect rect = new Rect();
    private Drawable backgroundDrawable;
    public SizeNotifierRelativeLayoutDelegate delegate;

    public abstract interface SizeNotifierRelativeLayoutDelegate
    {
        public abstract void onSizeChanged(int keyboardHeight);
    }

    public SizeNotifierRelativeLayout(Context context)
    {
        super(context);
    }

    public SizeNotifierRelativeLayout(android.content.Context context, android.util.AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SizeNotifierRelativeLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void setBackgroundImage(int resourceId)
    {
        try
        {
            backgroundDrawable = getResources().getDrawable(resourceId);
        }
        catch (Throwable e)
        {
            // FileLog.e("tmessages", e);
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
    }

    public void setBackgroundImage(Drawable bitmap)
    {
        backgroundDrawable = bitmap;
    }

    public Drawable getBackgroundImage()
    {
        return backgroundDrawable;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (delegate != null)
        {
            View rootView = this.getRootView();
            int usableViewHeight = rootView.getHeight() - AndroidUtilities.statusBarHeight - AndroidUtilities.getViewInset(rootView);
            this.getWindowVisibleDisplayFrame(rect);
            int keyboardHeight = usableViewHeight - (rect.bottom - rect.top);
            delegate.onSizeChanged(keyboardHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (backgroundDrawable != null)
        {
            float scaleX = (float) getMeasuredWidth() / (float) backgroundDrawable.getIntrinsicWidth();
            float scaleY = (float) getMeasuredHeight() / (float) backgroundDrawable.getIntrinsicHeight();
            float scale = scaleX < scaleY ? scaleY : scaleX;
            int width = (int) Math.ceil(backgroundDrawable.getIntrinsicWidth() * scale);
            int height = (int) Math.ceil(backgroundDrawable.getIntrinsicHeight() * scale);
            int x = (getMeasuredWidth() - width) / 2;
            int y = (getMeasuredHeight() - height) / 2;
            backgroundDrawable.setBounds(x, y, x + width, y + height);
            backgroundDrawable.draw(canvas);
        }
        else
        {
            super.onDraw(canvas);
        }
    }
}
