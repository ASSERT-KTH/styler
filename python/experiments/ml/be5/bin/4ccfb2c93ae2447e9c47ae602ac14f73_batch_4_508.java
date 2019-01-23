package com.developmentontheedge.be5.server.model;


import java.util.Arrays;
import java.util.Base64;

public classBase64File {private Stringname; privatebyte [ ]data
;
    privateStringmimeTypes ; publicBase64File
    (Stringname , byte[
    ]data, String mimeTypes)
{

this . name=name
;
    this .data
=

data ;this. mimeTypes=mimeTypes
;
    } publicString
getName

( ) {returnname
;
    } publicbyte
[

]getData
( ) {returndata
;
    } public String getMimeTypes (
            ) { return mimeTypes ; } @OverridepublicStringtoString(){return"{\"type\":\"Base64File\",\"name\":\"" + name+
"\", \"data\":\"data:"

+mimeTypes
+ ";base64," +Base64. getEncoder(
)
    . encodeToString( data )+ "\"}" ;}
    @ Overridepublic boolean equals ( Objecto) { if(this==o) return true;

    if ( o ==null|| getClass(

    ) !=o . getClass ( ))returnfalse;Base64Filethat=( Base64File )o; if (name != null?
    ! name.equals(that.name) :that.name!= null )return
    false ; if ( ! Arrays.equals(data,that. data ))return false ;return
mimeTypes

!=null
? mimeTypes .equals(
that
    . mimeTypes ) : that . mimeTypes ==null;}@ Override publicint
    hashCode ( ) { int result =name!=null?name.
    hashCode ( ) : 0 ; result= 31 * result +Arrays.hashCode( data );result
    = 31*
result
+
