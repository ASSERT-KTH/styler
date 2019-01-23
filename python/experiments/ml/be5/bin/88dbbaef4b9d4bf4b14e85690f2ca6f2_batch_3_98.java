package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;publicclassUserInfoimplementsSerializable


{private StringuserName ;private List< String>
availableRoles;
    privateList <String >currentRoles;
    privateDatecreationTime

    ;private Localelocale;privateTimeZonetimeZone;private StringremoteAddr;
    private TimestampprevLoggedInTime;private TimestamploggedInTime

    ; public UserInfo(
    String userName ,Collection
    < String >availableRoles
    , Collection <String
    > currentRoles ){
    this . userName=

    userName ;this. availableRoles= newArrayList<> (availableRoles );this. currentRoles=
    new
        ArrayList<> ( currentRoles)
        ;this. creationTime = newDate();this.
        locale=Locale . US ;}publicStringgetUserName()

        {returnuserName ; } publicvoidsetUserName(
        StringuserName) { this.userName=
    userName

    ; } publicLocalegetLocale
    (
        ) {String
    lang

    = locale .getLanguage( );
    if
        (!"kz" . equals(
    lang

    ) ) returnlocale;
    // fix for incorrect Kyrgyz language code in IE 6.0
        // should be 'ky' but IE sets it to 'kz' return new Locale("ky",locale.
        getCountry (),locale.getVariant())
            ; }public
        void
        setLocale
        ( Locale locale){this .locale=locale;} publicTimeZonegetTimeZone(){return
    timeZone

    ; } publicvoidsetTimeZone (TimeZone
    timeZone
        ){this . timeZone=
    timeZone

    ; } publicvoidsetTimeZone
    (
        String timeZoneID)
    {

    this . timeZone=timeZoneID !=null
    ?
        TimeZone.getTimeZone ( timeZoneID)
    :

    null ; }publicTimestamp getLoggedInTime(
    )
        {returnloggedInTime ; } public Timestamp getPrevLoggedInTime (){returnprevLoggedInTime; } publicString
    getRemoteAddr

    ( ) {returnremoteAddr
    ;
        } publicvoid
    setRemoteAddr

    ( String remoteAddr){
    this
        . remoteAddr=
    remoteAddr

    ; } publicDategetCreationTime
    (
        ) {return
    creationTime

    ; } publicbooleanisUserInRole (String
    role
        ){return currentRoles .contains
    (

    role ) ;}public
    List
        < String>
    getAvailableRoles

    ( ) {returnavailableRoles ;}
    public
        List <String>getCurrentRoles(){
    return

    currentRoles ;}publicvoid setAvailableRoles(List
    <
        String >availableRoles
    )

    { this.availableRoles= availableRoles;}
    public
        void setCurrentRoles(
    List

    < String >currentRoles){this. currentRoles=
    currentRoles
        ;}public boolean isAdmin(
    )

    { return getCurrentRoles().contains( RoleType.
    ROLE_ADMINISTRATOR
        )||getCurrentRoles ( ).
    contains

    ( RoleType .ROLE_SYSTEM_DEVELOPER)
    ;
        } @OverridepublicStringtoString(){return"UserInfo{" +
               "userName='"+userName+'\''+", availableRoles="+availableRoles+", currentRoles="
    +

    currentRoles+
    ", creationTime=" + creationTime+", locale="
    +
        locale + ", timeZone="
                + timeZone + ", remoteAddr='" + remoteAddr
                + '\'' + ", prevLoggedInTime="
                + prevLoggedInTime + ", loggedInTime="
                + loggedInTime + '}'
                ; } public String
                getLanguage ( ) {
                return getLocale ( ) . getLanguage
                ( ) ; }
                } 