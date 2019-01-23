package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;public


classUserInfo implementsSerializable {private StringuserName ;private
List<
    String> availableRoles; privateList<
    String>currentRoles;private DatecreationTime;private
    Localelocale ;privateTimeZonetimeZone;private StringremoteAddr

    ; private TimestampprevLoggedInTime
    ; private TimestamploggedInTime
    ; public UserInfo(
    String userName ,Collection
    < String >availableRoles
    , Collection <String

    > currentRoles){ this. userName=userName; this. availableRoles=newArrayList <>
    (
        availableRoles); this .currentRoles
        =newArrayList < > (currentRoles);this.creationTime
        =newDate ( ) ;this.locale=Locale.

        US;} public String getUserName(){
        returnuserName; } publicvoidsetUserName(
    String

    userName ) {this.
    userName
        = userName;
    }

    public Locale getLocale() {String
    lang
        =locale. getLanguage ()
    ;

    if ( !"kz".
    equals
        ( lang ) )returnlocale;// fix for incorrect Kyrgyz language code in IE 6.0// should be 'ky' but IE sets it to 'kz'
        return newLocale("ky",locale.getCountry(
            ) ,locale
        .
        getVariant
        ( ) );}public voidsetLocale(Localelocale) {this.locale=locale;
    }

    public TimeZone getTimeZone() {return
    timeZone
        ;}public void setTimeZone(
    TimeZone

    timeZone ) {this.
    timeZone
        = timeZone;
    }

    public void setTimeZone(String timeZoneID)
    {
        this.timeZone = timeZoneID!=
    null

    ? TimeZone .getTimeZone( timeZoneID)
    :
        null;} public Timestamp getLoggedInTime ( ) {returnloggedInTime;}public Timestamp getPrevLoggedInTime(
    )

    { return prevLoggedInTime;}
    public
        String getRemoteAddr(
    )

    { return remoteAddr;}
    public
        void setRemoteAddr(
    String

    remoteAddr ) {this.
    remoteAddr
        = remoteAddr;
    }

    public Date getCreationTime() {return
    creationTime
        ;}public boolean isUserInRole(
    String

    role ) {returncurrentRoles
    .
        contains (role
    )

    ; } publicList< String>
    getAvailableRoles
        ( ){returnavailableRoles;}public
    List

    < String>getCurrentRoles( ){return
    currentRoles
        ; }public
    void

    setAvailableRoles (List<String >availableRoles)
    {
        this .availableRoles
    =

    availableRoles ; }publicvoidsetCurrentRoles(List <String
    >
        currentRoles){ this .currentRoles
    =

    currentRoles ; }publicbooleanisAdmin() {return
    getCurrentRoles
        (). contains (RoleType
    .

    ROLE_ADMINISTRATOR ) ||getCurrentRoles(
    )
        . contains(RoleType.ROLE_SYSTEM_DEVELOPER);}@Override public
               StringtoString(){return"UserInfo{"+"userName='"+userName
    +

    '\''+
    ", availableRoles=" + availableRoles+", currentRoles="
    +
        currentRoles + ", creationTime="
                + creationTime + ", locale=" + locale
                + ", timeZone=" + timeZone
                + ", remoteAddr='" + remoteAddr
                + '\'' + ", prevLoggedInTime="
                + prevLoggedInTime + ", loggedInTime="
                + loggedInTime + '}'
                ; } public String getLanguage (
                ) { return getLocale
                ( ) . getLanguage
                ()
    ;

    } } 