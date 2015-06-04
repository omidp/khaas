package com.omidbiz.khaas.ui;

import java.io.Serializable;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [dot] com
 */
public class GooglePlace implements Serializable
{

    public GooglePlace()
    {
    }

    private String referenceId;
    private String phone, internationalPhone;
    private String googleUrl, website;
    private String name;
    private String addr;
    private String vicinity;
    private double latitude;
    private double longitude;

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public String getVicinity()
    {
        return vicinity;
    }

    public void setVicinity(String vicinity)
    {
        this.vicinity = vicinity;
    }

    public String getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(String referenceId)
    {
        this.referenceId = referenceId;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getInternationalPhone()
    {
        return internationalPhone;
    }

    public void setInternationalPhone(String internationalPhone)
    {
        this.internationalPhone = internationalPhone;
    }

    public String getGoogleUrl()
    {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl)
    {
        this.googleUrl = googleUrl;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddr()
    {
        return addr;
    }

    public void setAddr(String addr)
    {
        this.addr = addr;
    }

}
