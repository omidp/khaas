package com.omidbiz.khaas;

import com.omidbiz.khaas.handlers.KhaasUtil;

import java.io.Serializable;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class BotResponse implements Serializable
{

    public enum ACTION
    {
        CALL, SMS, NOTHING, CANCEL, LOCATE, LOOKUP, CLEAR, TELL_JOKE, UPDATE_NAME, SEARCH, ALARM, IDENTIFY, PLAY_MUSIC, TODAY, TIME
    }

    private String msg;

    private ACTION action;

    private int code;

    public BotResponse()
    {
    }

    public BotResponse(String msg, ACTION action, int code)
    {
        this.msg = msg;
        this.action = action;
        this.code = code;
    }

    public String getMsg()
    {
        if (KhaasUtil.isEmpty(this.msg))
            return msg;
        msg = msg.replaceAll("\t", "");
        msg = msg.replaceAll("\n", "");
        msg = msg.replaceAll("\r", "");
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public ACTION getAction()
    {
        return action;
    }

    public void setAction(ACTION action)
    {
        this.action = action;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

}
