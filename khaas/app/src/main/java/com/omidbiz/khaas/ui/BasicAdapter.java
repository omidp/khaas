package com.omidbiz.khaas.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.omidbiz.khaas.handlers.KhaasUtil;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class BasicAdapter extends ArrayAdapter<TextView>
{

    private final int layoutId;
    LayoutInflater inflater;


    public BasicAdapter(Context context, int resource)
    {
        super(context, resource);
        this.inflater = LayoutInflater.from(context);
        this.layoutId = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = inflater.inflate(layoutId, parent, false);
        return getItem(position);
    }


    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }
}
