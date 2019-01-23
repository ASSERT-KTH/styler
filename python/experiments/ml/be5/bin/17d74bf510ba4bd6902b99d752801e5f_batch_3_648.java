package com.developmentontheedge.be5.base.model;

import com.developmentontheedge.be5.metadata.RoleType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;importjava
.util .Locale;importjava.util.TimeZone;publicclass
UserInfoimplements Serializable{privateStringuserName;privateList<String>availableRoles


;private List < String >
currentRoles
    ; private DatecreationTime
    ; privateLocalelocale; privateTimeZone
    timeZone ;privateStringremoteAddr ;private

    Timestamp prevLoggedInTime ;private
    Timestamp loggedInTime ;public
    UserInfo ( StringuserName
    , Collection <String
    > availableRoles ,Collection
    < String >currentRoles

    ) {this. userName= userName;this. availableRoles= newArrayList<> (availableRoles
    )
        ;this. currentRoles =new
        ArrayList<> ( currentRoles );this.creationTime=new
        Date() ; this .locale=Locale.US;

        }publicString getUserName ( ){returnuserName
        ;}public void setUserName(StringuserName
    )

    { this .userName=
    userName
        ; }public
    Locale

    getLocale ( ){String lang=
    locale
        .getLanguage( ) ;if
    (

    ! "kz" .equals(
    lang
        ) ) return locale;// fix for incorrect Kyrgyz language code in IE 6.0// should be 'ky' but IE sets it to 'kz'returnnew
        Locale ("ky",locale.getCountry(),
            locale .getVariant
        (
        )
        ) ; }publicvoidsetLocale (Localelocale){this .locale=locale;}public
    TimeZone

    getTimeZone ( ){return timeZone;
    }
        publicvoidsetTimeZone ( TimeZonetimeZone
    )

    { this .timeZone=
    timeZone
        ; }public
    void

    setTimeZone ( StringtimeZoneID) {this
    .
        timeZone=timeZoneID != null?
    TimeZone

    . getTimeZone (timeZoneID) :null
    ;
        }publicTimestamp getLoggedInTime ( ) { return loggedInTime;}publicTimestampgetPrevLoggedInTime ( ){
    return

    prevLoggedInTime ; }publicString
    getRemoteAddr
        ( ){
    return

    remoteAddr ; }publicvoid
    setRemoteAddr
        ( StringremoteAddr
    )

    { this .remoteAddr=
    remoteAddr
        ; }public
    Date

    getCreationTime ( ){return creationTime;
    }
        publicbooleanisUserInRole ( Stringrole
    )

    { return currentRoles.contains
    (
        role );
    }

    public List <String> getAvailableRoles(
    )
        { returnavailableRoles;}publicList<
    String

    > getCurrentRoles(){ returncurrentRoles;
    }
        public voidsetAvailableRoles
    (

    List <String>availableRoles ){this
    .
        availableRoles =availableRoles
    ;

    } public voidsetCurrentRoles(List<String >currentRoles
    )
        {this. currentRoles =currentRoles
    ;

    } public booleanisAdmin(){return getCurrentRoles(
    )
        .contains( RoleType .ROLE_ADMINISTRATOR
    )

    || getCurrentRoles ().
    contains
        ( RoleType.ROLE_SYSTEM_DEVELOPER);}@OverridepublicString toString
               (){return"UserInfo{"+"userName='"+userName+'\''
    +

    ", availableRoles="+
    availableRoles + ", currentRoles="+currentRoles
    +
        ", creationTime=" + creationTime
                + ", locale=" + locale + ", timeZone="
                + timeZone + ", remoteAddr='"
                + remoteAddr + '\''
                + ", prevLoggedInTime=" + prevLoggedInTime
                + ", loggedInTime=" + loggedInTime
                + '}' ; }
                public String getLanguage ( ) {
                return getLocale ( )
                . getLanguage ( )
                ;}
    }

    