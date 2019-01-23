package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date
; importjava.util.List


; import java . util
.
    Locale ; importjava
    . util.TimeZone; publicclass
    UserInfo implementsSerializable{private StringuserName

    ; private List<
    String > availableRoles;
    private List <String
    > currentRoles ;private
    Date creationTime ;private
    Locale locale ;private

    TimeZone timeZone;private StringremoteAddr ;privateTimestampprevLoggedInTime ;private TimestamploggedInTime;public UserInfo(
    String
        userName,Collection < String>
        availableRoles,Collection < String >currentRoles){this.userName
        =userName; this . availableRoles=newArrayList<>(

        availableRoles); this . currentRoles=newArrayList
        <>( currentRoles );this.
    creationTime

    = new Date()
    ;
        this .locale
    =

    Locale . US;} publicString
    getUserName
        (){ return userName;
    }

    public void setUserName(String
    userName
        ) { this .userName=userName;}
        public LocalegetLocale(){Stringlang=locale
            . getLanguage(
        )
        ;
        if ( !"kz".equals (lang))returnlocale ;// fix for incorrect Kyrgyz language code in IE 6.0// should be 'ky' but IE sets it to 'kz'returnnewLocale(
    "ky"

    , locale .getCountry( ),
    locale
        .getVariant( ) );
    }

    public void setLocale(Locale
    locale
        ) {this
    .

    locale = locale;} publicTimeZone
    getTimeZone
        (){ return timeZone;
    }

    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone ; } public voidsetTimeZone(StringtimeZoneID) { this.
    timeZone

    = timeZoneID !=null?
    TimeZone
        . getTimeZone(
    timeZoneID

    ) : null;}
    public
        Timestamp getLoggedInTime(
    )

    { return loggedInTime;}
    public
        Timestamp getPrevLoggedInTime(
    )

    { return prevLoggedInTime;} publicString
    getRemoteAddr
        (){ return remoteAddr;
    }

    public void setRemoteAddr(String
    remoteAddr
        ) {this
    .

    remoteAddr = remoteAddr;} publicDate
    getCreationTime
        ( ){returncreationTime;}public
    boolean

    isUserInRole (Stringrole) {returncurrentRoles
    .
        contains (role
    )

    ; }publicList< String>getAvailableRoles
    (
        ) {return
    availableRoles

    ; } publicList<String>getCurrentRoles ()
    {
        returncurrentRoles; } publicvoid
    setAvailableRoles

    ( List <String>availableRoles){ this.
    availableRoles
        =availableRoles; } publicvoid
    setCurrentRoles

    ( List <String>
    currentRoles
        ) {this.currentRoles=currentRoles;}publicboolean isAdmin
               (){returngetCurrentRoles().contains(RoleType
    .

    ROLE_ADMINISTRATOR)
    || getCurrentRoles ().
    contains
        ( RoleType .
                ROLE_SYSTEM_DEVELOPER ) ; } @ Override
                public String toString (
                ) { return "UserInfo{"
                + "userName='" + userName
                + '\'' + ", availableRoles="
                + availableRoles + ", currentRoles="
                + currentRoles + ", creationTime=" + creationTime
                + ", locale=" + locale
                + ", timeZone=" + timeZone
                +", remoteAddr='"
    +

    remoteAddr + '\''+", prevLoggedInTime="
    +
        prevLoggedInTime +", loggedInTime="+loggedInTime+'}';}
    public
String
