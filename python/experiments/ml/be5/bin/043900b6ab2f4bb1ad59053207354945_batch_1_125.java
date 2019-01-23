package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;importjava.util
.Date ;importjava.util.List;importjava.util.Locale;import
java. util.TimeZone;publicclassUserInfoimplementsSerializable{privateString
userName; privateList<String>availableRoles;private
List <String>currentRoles;private


Date creationTime ; private Locale
locale
    ; private TimeZonetimeZone
    ; privateStringremoteAddr; privateTimestamp
    prevLoggedInTime ;privateTimestamploggedInTime ;public

    UserInfo ( StringuserName
    , Collection <String
    > availableRoles ,Collection
    < String >currentRoles
    ) { this.
    userName = userName;

    this .availableRoles= newArrayList <>(availableRoles ); this.currentRoles= newArrayList
    <
        >(currentRoles ) ;this
        .creationTime= new Date ();this.locale=
        Locale.US ; } publicStringgetUserName(){return

        userName;} public void setUserName(StringuserName
        ){this . userName=userName;
    }

    public Locale getLocale()
    {
        String lang=
    locale

    . getLanguage (); if(
    !
        "kz".equals ( lang)
    )

    return locale ;// fix for incorrect Kyrgyz language code in IE 6.0// should be 'ky' but IE sets it to 'kz'
    return
        new Locale ( "ky",locale.getCountry(
        ) ,locale.getVariant());}
            public voidsetLocale
        (
        Locale
        locale ) {this.locale =locale;}publicTimeZone getTimeZone(){returntimeZone;
    }

    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone;
    }

    public void setTimeZone(String
    timeZoneID
        ) {this
    .

    timeZone = timeZoneID!=null ?TimeZone
    .
        getTimeZone(timeZoneID ) :null
    ;

    } public TimestampgetLoggedInTime( ){
    return
        loggedInTime;} public Timestamp getPrevLoggedInTime ( ) {returnprevLoggedInTime;}public String getRemoteAddr(
    )

    { return remoteAddr;}
    public
        void setRemoteAddr(
    String

    remoteAddr ) {this.
    remoteAddr
        = remoteAddr;
    }

    public Date getCreationTime()
    {
        return creationTime;
    }

    public boolean isUserInRole(String role)
    {
        returncurrentRoles. contains (role
    )

    ; } publicList<
    String
        > getAvailableRoles(
    )

    { return availableRoles;} publicList
    <
        String >getCurrentRoles(){returncurrentRoles
    ;

    } publicvoidsetAvailableRoles( List<String
    >
        availableRoles ){
    this

    . availableRoles=availableRoles; }publicvoid
    setCurrentRoles
        ( List<
    String

    > currentRoles ){this.currentRoles= currentRoles;
    }
        publicbooleanisAdmin ( ){
    return

    getCurrentRoles ( ).contains(RoleType. ROLE_ADMINISTRATOR)
    ||
        getCurrentRoles() . contains(
    RoleType

    . ROLE_SYSTEM_DEVELOPER );}
    @
        Override publicStringtoString(){return"UserInfo{"+"userName='" +
               userName+'\''+", availableRoles="+availableRoles+", currentRoles="+currentRoles
    +

    ", creationTime="+
    creationTime + ", locale="+locale
    +
        ", timeZone=" + timeZone
                + ", remoteAddr='" + remoteAddr + '\''
                + ", prevLoggedInTime=" + prevLoggedInTime
                + ", loggedInTime=" + loggedInTime
                + '}' ; }
                public String getLanguage (
                ) { return getLocale
                ( ) . getLanguage ( )
                ; } } 