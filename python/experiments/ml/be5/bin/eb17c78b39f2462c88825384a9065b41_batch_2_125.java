package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;importjava.util
.Date ;importjava.util.List;importjava.util
.Locale ;importjava.util.TimeZone;publicclassUserInfoimplements
Serializable{ privateStringuserName;privateList<String
> availableRoles;privateList<String


> currentRoles ; private Date
creationTime
    ; private Localelocale
    ; privateTimeZonetimeZone; privateString
    remoteAddr ;privateTimestampprevLoggedInTime ;private

    Timestamp loggedInTime ;public
    UserInfo ( StringuserName
    , Collection <String
    > availableRoles ,Collection
    < String >currentRoles
    ) { this.

    userName =userName; this. availableRoles=newArrayList <> (availableRoles); this.
    currentRoles
        =newArrayList < >(
        currentRoles); this . creationTime=newDate();
        this.locale = Locale .US;}publicStringgetUserName

        (){ return userName ;}publicvoid
        setUserName(String userName ){this.
    userName

    = userName ;}public
    Locale
        getLocale ()
    {

    String lang =locale. getLanguage(
    )
        ;if( ! "kz".
    equals

    ( lang ))return
    locale
        ; // fix for incorrect Kyrgyz language code in IE 6.0 // should be 'ky' but IE sets it to 'kz' returnnewLocale("ky",
        locale .getCountry(),locale.getVariant(
            ) );
        }
        public
        void setLocale (Localelocale) {this.locale=locale ;}publicTimeZonegetTimeZone()
    {

    return timeZone ;}public voidsetTimeZone
    (
        TimeZonetimeZone) { this.
    timeZone

    = timeZone ;}public
    void
        setTimeZone (String
    timeZoneID

    ) { this.timeZone =timeZoneID
    !=
        null?TimeZone . getTimeZone(
    timeZoneID

    ) : null;} publicTimestamp
    getLoggedInTime
        (){ return loggedInTime ; } public TimestampgetPrevLoggedInTime(){return prevLoggedInTime ;}
    public

    String getRemoteAddr (){
    return
        remoteAddr ;}
    public

    void setRemoteAddr (StringremoteAddr
    )
        { this.
    remoteAddr

    = remoteAddr ;}public
    Date
        getCreationTime ()
    {

    return creationTime ;}public booleanisUserInRole
    (
        Stringrole) { returncurrentRoles
    .

    contains ( role);
    }
        public List<
    String

    > getAvailableRoles (){ returnavailableRoles
    ;
        } publicList<String>getCurrentRoles(
    )

    { returncurrentRoles;} publicvoidsetAvailableRoles
    (
        List <String
    >

    availableRoles ){this. availableRoles=availableRoles
    ;
        } publicvoid
    setCurrentRoles

    ( List <String>currentRoles){ this.
    currentRoles
        =currentRoles; } publicboolean
    isAdmin

    ( ) {returngetCurrentRoles(). contains(
    RoleType
        .ROLE_ADMINISTRATOR) || getCurrentRoles(
    )

    . contains (RoleType.
    ROLE_SYSTEM_DEVELOPER
        ) ;}@OverridepublicStringtoString(){ return
               "UserInfo{"+"userName='"+userName+'\''+", availableRoles="+availableRoles
    +

    ", currentRoles="+
    currentRoles + ", creationTime="+creationTime
    +
        ", locale=" + locale
                + ", timeZone=" + timeZone + ", remoteAddr='"
                + remoteAddr + '\''
                + ", prevLoggedInTime=" + prevLoggedInTime
                + ", loggedInTime=" + loggedInTime
                + '}' ; }
                public String getLanguage (
                ) { return getLocale ( )
                . getLanguage ( )
                ; } } 