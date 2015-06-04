package com.omidbiz.khaas.music;

import java.io.Serializable;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class MusicJam implements Serializable
{

    private String albumName;
    private String artistName;
    private String streamUrl;
    private String id;

    public String getAlbumName()
    {
        return albumName;
    }

    public void setAlbumName(String albumName)
    {
        this.albumName = albumName;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public void setArtistName(String artistName)
    {
        this.artistName = artistName;
    }

    public String getStreamUrl()
    {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl)
    {
        this.streamUrl = streamUrl;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}

