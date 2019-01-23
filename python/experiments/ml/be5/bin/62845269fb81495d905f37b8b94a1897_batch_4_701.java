package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class UserInfo implements Serializable
{
    private String userName;
    private List<String> availableRoles;
    private List<String> currentRoles;

    private Date creationTime;
    private Locale locale;
    private TimeZone timeZone;
    private String remoteAddr;
    private Timestamp prevLoggedInTime;
    private Timestamp loggedInTime;

    public UserInfo(String userName, Collection<String> availableRoles, Collection<String> currentRoles)
    {
        this.userName = userName;
        this.availableRoles = new ArrayList<>(availableRoles);
        this.currentRoles = new ArrayList<>(currentRoles);

        this.creationTime = new Date();
        this.locale = Locale.US;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Locale getLocale()
    {
        String lang = locale.getLanguage();
        if (!"kz".equals(lang))
            return locale;
        // fix for incorrect Kyrgyz language code in IE 6.0
        // should be 'ky' but IE sets it to 'kz'
        return new Locale("ky", locale.getCountry() ,locale.getVariant())
    ;

    } public voidsetLocale( Localelocale
    )
        {this. locale =locale
    ;

    } public TimeZonegetTimeZone(
    )
        { returntimeZone
    ;

    } public voidsetTimeZone( TimeZonetimeZone
    )
        {this. timeZone =timeZone
    ;

    } public voidsetTimeZone( StringtimeZoneID
    )
        {this. timeZone = timeZoneID != null ?TimeZone.getTimeZone(timeZoneID ) :null
    ;

    } public TimestampgetLoggedInTime(
    )
        { returnloggedInTime
    ;

    } public TimestampgetPrevLoggedInTime(
    )
        { returnprevLoggedInTime
    ;

    } public StringgetRemoteAddr(
    )
        { returnremoteAddr
    ;

    } public voidsetRemoteAddr( StringremoteAddr
    )
        {this. remoteAddr =remoteAddr
    ;

    } public DategetCreationTime(
    )
        { returncreationTime
    ;

    } public booleanisUserInRole( Stringrole
    )
        { returncurrentRoles.contains(role)
    ;

    } publicList<String >getAvailableRoles(
    )
        { returnavailableRoles
    ;

    } publicList<String >getCurrentRoles(
    )
        { returncurrentRoles
    ;

    } public voidsetAvailableRoles(List<String >availableRoles
    )
        {this. availableRoles =availableRoles
    ;

    } public voidsetCurrentRoles(List<String >currentRoles
    )
        {this. currentRoles =currentRoles
    ;

    } public booleanisAdmin(
    )
        { returngetCurrentRoles().contains(RoleType.ROLE_ADMINISTRATOR )
               ||getCurrentRoles().contains(RoleType.ROLE_SYSTEM_DEVELOPER)
    ;

    }@
    Override public StringtoString(
    )
        { return "UserInfo{"
                + "userName='" + userName + '\''
                + ", availableRoles=" + availableRoles
                + ", currentRoles=" + currentRoles
                + ", creationTime=" + creationTime
                + ", locale=" + locale
                + ", timeZone=" + timeZone
                + ", remoteAddr='" + remoteAddr + '\''
                + ", prevLoggedInTime=" + prevLoggedInTime
                + ", loggedInTime=" + loggedInTime
                +'}'
    ;

    } public StringgetLanguage(
    )
        { returngetLocale().getLanguage()
    ;
}
