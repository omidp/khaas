package com.omidbiz.khaas.manager;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class StringEntity extends org.apache.http.entity.StringEntity
{
    public StringEntity(String s, String charset) throws UnsupportedEncodingException
    {
        super(s, charset);
    }

    public StringEntity(String s) throws UnsupportedEncodingException
    {
        super(s);
    }


    @Override
    public Header getContentType()
    {
        Header header = new BasicHeader("Content-Type", "application/json");
        return header;
    }


}
