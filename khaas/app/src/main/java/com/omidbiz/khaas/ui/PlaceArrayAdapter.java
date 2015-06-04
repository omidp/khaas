package com.omidbiz.khaas.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omidbiz.khaas.R;
import com.omidbiz.khaas.handlers.KhaasUtil;

import java.util.List;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class PlaceArrayAdapter extends BaseAdapter
{


    private List<GooglePlace> places;
    private Context ctx;


    public PlaceArrayAdapter(List<GooglePlace> places, Context ctx)
    {
        this.places = places;
        this.ctx = ctx;
    }

    @Override
    public int getCount()
    {
        return places == null ? 0 : places.size();
    }

    @Override
    public Object getItem(int position)
    {
        if (places == null || places.size() == 0)
            return null;
        return places.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            row = layoutInflater.inflate(R.layout.place_item, parent, false);
            row.setTag(new ViewHolder((TextView) row.findViewById(R.id.place_name), (TextView) row.findViewById(R.id.place_address)));
        }
        ViewHolder vh = (ViewHolder) row.getTag();
        TextView tv = vh.placeTv;
        TextView tv2 = vh.addrTv;

        if (places != null && places.size() > 0)
        {
            GooglePlace item = places.get(position);
            tv.setText(item.getName());
            tv2.setText(item.getVicinity());
        }

        if (row != null)
        {
            if (position % 2 == 0)
            {
                row.setBackgroundColor(KhaasUtil.getRowColor(0));
            }
            else
            {
                row.setBackgroundColor(KhaasUtil.getRowColor(1));
            }
        }

        return row;
    }

    private static class ViewHolder
    {
        private TextView placeTv;
        private TextView addrTv;

        private ViewHolder(TextView placeTv, TextView addrTv)
        {
            this.placeTv = placeTv;
            this.addrTv = addrTv;
        }
    }
}
