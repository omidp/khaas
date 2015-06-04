package com.omidbiz.khaas.ui;

import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.omidbiz.khaas.KhaasConstant;

import java.lang.reflect.Field;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class AndroidUtilities
{

    public static int statusBarHeight = 0;

    public static int getViewInset(View view)
    {
        if (view == null || Build.VERSION.SDK_INT < 21)
        {
            return 0;
        }
        try
        {
            Field mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
            mAttachInfoField.setAccessible(true);
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null)
            {
                Field mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                mStableInsetsField.setAccessible(true);
                Rect insets = (Rect) mStableInsetsField.get(mAttachInfo);
                return insets.bottom;
            }
        }
        catch (Exception e)
        {
            //FileLog.e("tmessages", e);
            Log.i(KhaasConstant.LOG_TAG, e.getMessage());
        }
        return 0;
    }
}
