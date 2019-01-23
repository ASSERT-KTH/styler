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


;private List< String> currentRoles; privateDate
creationTime;
    private Locale locale ;
    private
        TimeZone timeZone ;private
        String remoteAddr;privateTimestamp prevLoggedInTime;
        private TimestamploggedInTime;public UserInfo(

        String userName ,Collection
        < String >availableRoles
        , Collection <String
        > currentRoles ){
        this . userName=
        userName ; this.

        availableRoles =newArrayList <> (availableRoles); this. currentRoles=newArrayList <>
        (
            currentRoles); this .creationTime
            =newDate ( ) ;this.locale=Locale.
            US;} public String getUserName(){returnuserName;

            }publicvoid setUserName ( StringuserName){
            this.userName = userName;}public
        Locale

        getLocale ( ){String
        lang
            = locale.
        getLanguage

        ( ) ;if( !"kz"
        .
            equals(lang ) )return
        locale

        ; // fix for incorrect Kyrgyz language code in IE 6.0 // should be 'ky' but IE sets it to 'kz'returnnew
        Locale
            ( "ky" , locale.getCountry(),
            locale .getVariant());}publicvoid
                setLocale (Locale
            locale
            )
            { this .locale=locale ;}publicTimeZonegetTimeZone( ){returntimeZone;}public
        void

        setTimeZone ( TimeZonetimeZone) {this
        .
            timeZone=timeZone ; }public
        void

        setTimeZone ( StringtimeZoneID)
        {
            this .timeZone
        =

        timeZoneID != null?TimeZone .getTimeZone
        (
            timeZoneID): null ;}
        public

        Timestamp getLoggedInTime (){ returnloggedInTime
        ;
            }publicTimestamp getPrevLoggedInTime ( ) { return prevLoggedInTime;}publicStringgetRemoteAddr ( ){
        return

        remoteAddr ; }publicvoid
        setRemoteAddr
            ( StringremoteAddr
        )

        { this .remoteAddr=
        remoteAddr
            ; }public
        Date

        getCreationTime ( ){return
        creationTime
            ; }public
        boolean

        isUserInRole ( Stringrole) {return
        currentRoles
            .contains( role );
        }

        public List <String>
        getAvailableRoles
            ( ){
        return

        availableRoles ; }publicList <String
        >
            getCurrentRoles (){returncurrentRoles;}
        public

        void setAvailableRoles(List< String>availableRoles
        )
            { this.
        availableRoles

        = availableRoles;}public voidsetCurrentRoles(
        List
            < String>
        currentRoles

        ) { this.currentRoles=currentRoles; }public
        boolean
            isAdmin() { returngetCurrentRoles
        (

        ) . contains(RoleType.ROLE_ADMINISTRATOR) ||getCurrentRoles
        (
            ).contains ( RoleType.
        ROLE_SYSTEM_DEVELOPER

        ) ; }@Override
        public
            String toString(){return"UserInfo{"+"userName='"+userName +
                   '\''+", availableRoles="+availableRoles+", currentRoles="+currentRoles+", creationTime="
        +

        creationTime+
        ", locale=" + locale+", timeZone="
        +
            timeZone + ", remoteAddr='"
                    + remoteAddr + '\'' + ", prevLoggedInTime="
                    + prevLoggedInTime + ", loggedInTime="
                    + loggedInTime + '}'
                    ; } public String
                    getLanguage ( ) {
                    return getLocale ( )
                    . getLanguage ( ) ; }
                    } 