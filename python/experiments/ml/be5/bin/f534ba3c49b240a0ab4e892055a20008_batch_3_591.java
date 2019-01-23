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

    private DatecreationTime ;privateLocalelocale
    ;private TimeZonetimeZone ;privateStringremoteAddr
    ;private TimestampprevLoggedInTime ;privateTimestamploggedInTime
    ;public UserInfo( StringuserName,Collection
    <String >availableRoles ,Collection<String
    >currentRoles ){ this.userName=
    userName; this. availableRoles=

    new ArrayList<> (availableRoles );this. currentRoles= newArrayList<> (currentRoles
    )
        ;this. creationTime =new
        Date() ; this .locale=Locale.US;
        }publicString getUserName ( ){returnuserName;}public

        voidsetUserName( String userName ){this.
        userName=userName ; }publicLocalegetLocale
    (

    ) { Stringlang=
    locale
        . getLanguage(
    )

    ; if (!"kz" .equals
    (
        lang)) return locale;
    // fix for incorrect Kyrgyz language code in IE 6.0

    // should be 'ky' but IE sets it to 'kz' return newLocale(
    "ky"
        , locale . getCountry(),locale.
        getVariant ());}publicvoidsetLocale(
            Locale locale)
        {
        this
        . locale =locale;} publicTimeZonegetTimeZone(){ returntimeZone;}publicvoidsetTimeZone
    (

    TimeZone timeZone ){this .timeZone
    =
        timeZone;} public voidsetTimeZone
    (

    String timeZoneID ){this
    .
        timeZone =timeZoneID
    !=

    null ? TimeZone.getTimeZone (timeZoneID
    )
        :null; } publicTimestamp
    getLoggedInTime

    ( ) {returnloggedInTime ;}
    public
        TimestampgetPrevLoggedInTime( ) { return prevLoggedInTime ; }publicStringgetRemoteAddr() { returnremoteAddr
    ;

    } public voidsetRemoteAddr(
    String
        remoteAddr ){
    this

    . remoteAddr =remoteAddr;
    }
        public DategetCreationTime
    (

    ) { returncreationTime;
    }
        public booleanisUserInRole
    (

    String role ){return currentRoles.
    contains
        (role) ; }public
    List

    < String >getAvailableRoles(
    )
        { returnavailableRoles
    ;

    } public List<String >getCurrentRoles
    (
        ) {returncurrentRoles;}publicvoid
    setAvailableRoles

    ( List<String> availableRoles){
    this
        . availableRoles=
    availableRoles

    ; }publicvoidsetCurrentRoles (List<
    String
        > currentRoles)
    {

    this . currentRoles=currentRoles;}public booleanisAdmin
    (
        ){return getCurrentRoles ()
    .

    contains ( RoleType.ROLE_ADMINISTRATOR)||getCurrentRoles ()
    .
        contains(RoleType . ROLE_SYSTEM_DEVELOPER)
    ;

    } @ OverridepublicString
    toString
        ( ){return"UserInfo{"+"userName='"+userName+'\'' +
               ", availableRoles="+availableRoles+", currentRoles="+currentRoles+", creationTime="+creationTime
    +

    ", locale="+
    locale + ", timeZone="+timeZone
    +
        ", remoteAddr='" + remoteAddr
                + '\'' + ", prevLoggedInTime=" + prevLoggedInTime
                + ", loggedInTime=" + loggedInTime
                + '}' ; }
                public String getLanguage (
                ) { return getLocale
                ( ) . getLanguage
                ( ) ; } } 