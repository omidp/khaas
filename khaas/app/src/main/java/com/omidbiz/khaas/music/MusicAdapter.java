package com.omidbiz.khaas.music;

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
public class MusicAdapter extends BaseAdapter
{

    private List<MusicJam> musics;
    private Context ctx;


    public MusicAdapter(List<MusicJam> musics, Context ctx)
    {
        this.musics = musics;
        this.ctx = ctx;
    }

    @Override
    public int getCount()
    {
        return musics == null ? 0 : musics.size();
    }

    @Override
    public Object getItem(int position)
    {
        if (musics == null || musics.size() == 0)
            return null;
        return musics.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        if (musics != null)
        {
            return Long.valueOf(musics.get(0).getId());
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if (row == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(ctx);
            row = layoutInflater.inflate(R.layout.music_item, parent, false);
            row.setTag(new ViewHolder((TextView) row.findViewById(R.id.albumName), (TextView) row.findViewById(R.id.artistName)));
        }
        ViewHolder vh = (ViewHolder) row.getTag();
        TextView tv = vh.albumeNameTv;
        TextView tv2 = vh.artistNameTv;

        if (musics != null && musics.size() > 0)
        {
            MusicJam item = musics.get(position);
            tv.setText(item.getAlbumName());
            tv2.setText(item.getArtistName());
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
        private TextView albumeNameTv;
        private TextView artistNameTv;

        private ViewHolder(TextView albumeNameTv, TextView artistNameTv)
        {
            this.albumeNameTv = albumeNameTv;
            this.artistNameTv = artistNameTv;
        }
    }

}

