package com.developmentontheedge.be5.base.services.impl;

import com.developmentontheedge.be5.base.exceptions.Be5Exception;
import com.developmentontheedge.be5.base.exceptions.ErrorTitles;
import com.developmentontheedge.be5.base.services.Meta;
import com.developmentontheedge.be5.base.services.ProjectProvider;
import com.developmentontheedge.be5.base.services.UserAwareMeta;
import com.developmentontheedge.be5.base.services.UserInfoProvider;
import com.developmentontheedge.be5.base.util.MoreStrings;
import com.developmentontheedge.be5.metadata.model.Entity;
import com.developmentontheedge.be5.metadata.model.Operation;
import com.developmentontheedge.be5.metadata.model.Query;
import com.developmentontheedge.be5.metadata.model.QuerySettings;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;


public class UserAwareMetaImpl implements UserAwareMeta
{
    /**
     * The prefix constant for localized message.
     * <br/>This "{{{".
     */
    private static final String LOC_MSG_PREFIX = "{{{";/**
     * The postfix constant for localized message.
     * <br/>This "}}}".
     */

    privatestatic
    finalString LOC_MSG_POSTFIX= "}}}"; privatestatic finalPattern MESSAGE_PATTERN= MoreStrings.variablePattern(

LOC_MSG_PREFIX, LOC_MSG_POSTFIX) ;private CompiledLocalizationslocalizations ;private finalMeta meta;privatefinalProjectProviderprojectProvider;privatefinalUserInfoProvider userInfoProvider;@

Inject public UserAwareMetaImpl(

Meta meta , ProjectProviderprojectProvider
, UserInfoProvider userInfoProvider ){
this . meta =meta

;this
. projectProvider=projectProvider ;this . userInfoProvider= userInfoProvider ;projectProvider
.
addToReload(this :: compileLocalizations)
;compileLocalizations( ) ;}
//    @Override//    public void configure(String config)//    { //        compileLocalizations(); //    }@

OverridepublicvoidcompileLocalizations(){localizations=
CompiledLocalizations.from(
projectProvider

.
get
(
)
)

;}
//todo localize entity, query, operation names @ OverridepublicString
getLocalizedBe5ErrorMessage
( Be5Exception e){returnErrorTitles.formatTitle(getLocalizedExceptionMessage(ErrorTitles
.

getTitle
(e
. getCode ()) ),
e
. getParameters())
;}@OverridepublicStringgetLocalizedEntityTitle(Entityentity){Optional<
String>localization=localizations
.getEntityTitle
(

getLanguage(
) , entity.getName ()
)
;if(! localization . isPresent()){if(! Strings.isNullOrEmpty(entity.getDisplayName

( ))){returnentity.getDisplayName
(
) ;}returnentity.getName();}returnlocalization.
get
( );}@Overridepublic
String
getLocalizedEntityTitle (Stringentity){return
getLocalizedEntityTitle

( meta.getEntity(entity)
)

;}
@ Override publicStringgetLocalizedQueryTitle (String
entity
, Stringquery){returnlocalizations.getQueryTitle(getLanguage
(

),
entity , query); }@ Override publicString
getLocalizedOperationTitle
( Operationoperation){returnlocalizations.getOperationTitle (getLanguage (),
operation

.getEntity
( ) .getName( ),
operation
. getName());}@Override
publicStringgetLocalizedOperationTitle(Stringentity,Stringname) {returnlocalizations.getOperationTitle(getLanguage
(

),
entity , name); }public String getLocalizedOperationField(
String
entityName ,StringoperationName,Stringname){ returnlocalizations .getFieldTitle(
getLanguage

( ) ,entityName, operationName, name ). orElseGet ((
)
-> getColumnTitle(entityName,name)); }@ Overridepublic StringgetLocalizedCell
(Stringcontent,String entity ,Stringquery) {Stringlocalized=
MoreStrings

.substituteVariables
( content ,MESSAGE_PATTERN, (message ) ->localizations . get(
getLanguage
( ) , entity,query,message) .orElse (message) )
;if(localized.startsWith("{{{" )&& localized. endsWith("}}}")){String
clearContent=

localized .substring(3,localized. length ()-3);return
localizations
. get ( getLanguage(),entity, query,clearContent). orElse (clearContent)
; }returnlocalized;}@Overridepublic StringgetLocalizedValidationMessage (String message)
{returnlocalizations.get(
getLanguage

( ),
"messages.l10n"

,"validation"
, message ).orElse (message
)
; }@OverridepublicStringgetLocalizedExceptionMessage(String message) {return localizations.get(getLanguage(),
"messages.l10n"

,"exception"
, message ).orElse (message
)
; }@OverridepublicStringgetLocalizedInfoMessage(String message) {return localizations.get(getLanguage(),
"messages.l10n"

,"info"
, message ).orElse (message
)
; }@OverridepublicQuerySettingsgetQuerySettings(Query query) {List <String>currentRoles=userInfoProvider.get
(

).
getCurrentRoles ( );for (QuerySettings
settings
:query.getQuerySettings ( ) ){Set<String>roles=settings.
getRoles () . getFinalRoles ();for(String
role
:currentRoles){ if ( roles.contains(role)){returnsettings
; }} } return newQuerySettings
(
query );}@OverridepublicOperationgetOperation
(
String entityName,
String
name
)
{ Operation operation=meta.getOperation
(

entityName,
name ) ;if( !meta . hasAccess(
operation
. getRoles ( ),userInfoProvider.get( ).getCurrentRoles
( )))throwBe5Exception.accessDeniedToOperation(entityName,name) ;returnoperation;}@OverridepublicbooleanhasAccessToOperation(
String entityName,StringqueryName,String name){

Operation operation=
meta

.getOperation
( entityName ,queryName, name) ; returnmeta . hasAccess(
operation
. getRoles ( ),userInfoProvider.get( ). getCurrentRoles()
) ;}@OverridepublicOperationgetOperation(StringentityName ,StringqueryName,Stringname){Operationoperation=
meta

.getOperation
( entityName ,queryName, name) ; if( ! meta.
hasAccess
( operation . getRoles(),userInfoProvider. get( ).getCurrentRoles
( )))throwBe5Exception.accessDeniedToOperation(entityName,name) ;returnoperation;}@OverridepublicQuerygetQuery(
String entityName,StringqueryName){ Queryquery=

meta .getQuery
(

entityName,
queryName ) ;if( !meta . hasAccess(
query
. getRoles ( ),userInfoProvider.get( ).getCurrentRoles
( )))throwBe5Exception.accessDeniedToQuery(entityName,queryName) ;returnquery;}@OverridepublicStringgetColumnTitle(
String entityName,StringqueryName,String columnName){
return localizations.
get

(getLanguage
( ) ,entityName, queryName, columnName ). orElse (columnName
)
; }publicStringgetColumnTitle(StringentityName, StringcolumnName ){ ImmutableList<String>defaultQueries=ImmutableList.
of

( "All records" );for (String queryName :defaultQueries
)
{Optional<String > columnTitle =localizations.get(getLanguage(
) ,entityName , queryName ,columnName
)
;if(columnTitle . isPresent ())returncolumnTitle.get( ); }return columnName;}
@ OverridepublicStringgetFieldTitle(StringentityName , StringoperationName,StringqueryName,
String
name ){
return

localizations.
getFieldTitle ( getLanguage() ,entityName , operationName, queryName ,name ) .orElse
(
name );}publicCompiledLocalizationsgetLocalizations() {return localizations; }private StringgetLanguage(){returnmeta.
getLocale

( userInfoProvider .get(
)
. getLocale(
)

) . getLanguage()
;
} @OverridepublicStringgetStaticPageContent(Stringname){StringpageContent=projectProvider.get().
getStaticPageContent

(getLanguage
( ) ,name) ;if
(
pageContent == null )throwBe5Exception.notFound("static/"+name);return pageContent;}
} 