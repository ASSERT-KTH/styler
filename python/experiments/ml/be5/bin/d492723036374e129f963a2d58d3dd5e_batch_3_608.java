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


classUserInfo implementsSerializable {private StringuserName ;private List<

String > availableRoles;
private List <String
> currentRoles ;private
Date creationTime ;private
Locale locale ;private
TimeZone timeZone ;private

String remoteAddr;private TimestampprevLoggedInTime ;privateTimestamploggedInTime ;public UserInfo(StringuserName ,Collection
<
    String>availableRoles , Collection<
    String>currentRoles ) { this.userName=userName;this
    .availableRoles= new ArrayList <>(availableRoles);this

    .currentRoles= new ArrayList <>(currentRoles
    );this . creationTime=newDate
(

) ; this.locale
=
    Locale .US
;

} public StringgetUserName( ){
return
    userName;} public voidsetUserName
(

String userName ){this
.
    userName = userName ;}publicLocalegetLocale(
    ) {Stringlang=locale.getLanguage()
        ; if(
    !
    "kz"
    . equals (lang)) returnlocale;// fix for incorrect Kyrgyz language code in IE 6.0// should be 'ky' but IE sets it to 'kz'return newLocale("ky",locale.
getCountry

( ) ,locale. getVariant(
)
    );} public voidsetLocale
(

Locale locale ){this
.
    locale =locale
;

} public TimeZonegetTimeZone( ){
return
    timeZone;} public voidsetTimeZone
(

TimeZone timeZone ){this .timeZone
=
    timeZone;} public void setTimeZone ( String timeZoneID){this.timeZone = timeZoneID!=
null

? TimeZone .getTimeZone(
timeZoneID
    ) :null
;

} public TimestampgetLoggedInTime(
)
    { returnloggedInTime
;

} public TimestampgetPrevLoggedInTime(
)
    { returnprevLoggedInTime
;

} public StringgetRemoteAddr( ){
return
    remoteAddr;} public voidsetRemoteAddr
(

String remoteAddr ){this
.
    remoteAddr =remoteAddr
;

} public DategetCreationTime( ){
return
    creationTime ;}publicbooleanisUserInRole(String
role

) {returncurrentRoles. contains(role
)
    ; }public
List

< String>getAvailableRoles( ){return
availableRoles
    ; }public
List

< String >getCurrentRoles(){return currentRoles;
}
    publicvoidsetAvailableRoles ( List<
String

> availableRoles ){this.availableRoles= availableRoles;
}
    publicvoidsetCurrentRoles ( List<
String

> currentRoles ){this
.
    currentRoles =currentRoles;}publicbooleanisAdmin(){ return
           getCurrentRoles().contains(RoleType.ROLE_ADMINISTRATOR)||
getCurrentRoles

()
. contains (RoleType.
ROLE_SYSTEM_DEVELOPER
    ) ; }
            @ Override public String toString (
            ) { return "UserInfo{"
            + "userName='" + userName
            + '\'' + ", availableRoles="
            + availableRoles + ", currentRoles="
            + currentRoles + ", creationTime="
            + creationTime + ", locale=" + locale
            + ", timeZone=" + timeZone
            + ", remoteAddr='" + remoteAddr
            +'\''
+

", prevLoggedInTime=" + prevLoggedInTime+", loggedInTime="
+
    loggedInTime +'}';}publicStringgetLanguage(
)
{
