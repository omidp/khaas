package com.omidbiz.khaas.manager;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.omidbiz.khaas.KhaasConstant;
import com.omidbiz.khaas.R;
import com.omidbiz.khaas.handlers.KhaasUtil;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class HandlerManager
{

    private Runnable runnable;
    private Handler handler;
    private Context ctx;

    public HandlerManager(Runnable runnable, Context context)
    {
        this.runnable = runnable;
        this.handler = new Handler();
        this.ctx = context;
    }


    public void run()
    {
        KhaasUtil.createToast(ctx, ctx.getString(R.string.action_delay), Toast.LENGTH_LONG);
        handler.postDelayed(runnable, KhaasConstant.CALL_DELAY);
    }

    public void cancel()
    {
        handler.removeCallbacks(runnable);
    }

}
