package com.developmentontheedge.be5.server.model;


import java.util.Arrays;
import java.util.Base64;publicclassBase64File{privateString

name; privatebyte []
data;
    privateString mimeTypes; publicBase64File(String
    name, byte[ ]data ,StringmimeTypes)


    {this . name=

    name ;this. data= data;this .mimeTypes = mimeTypes;
    }
        publicStringgetName ( ){
        returnname; } publicbyte
        []getData ( ){
    return

    data ; }publicString
    getMimeTypes
        ( ){
    return

    mimeTypes ;}@ OverridepublicString
    toString
        ( ){
    return

    "{\"type\":\"Base64File\",\"name\":\"" + name+"\", \"data\":\"data:"
    +
        mimeTypes +";base64,"
    +

    Base64.
    getEncoder ( ).encodeToString
    (
        data ) + "\"}" ;
                } @ Override public boolean equals (Objecto){if(this==o ) returntrue
    ;

    if(
    o == null||getClass ()
    !=
        o .getClass ( )) return false;
        Base64File that= ( Base64File ) o;if ( name!=null?!name . equals(

        that . name ):that .name

        != null) return false ; if(!Arrays.equals(data, that .data) ) returnfalse ; returnmimeTypes
        != null?mimeTypes.equals(that. mimeTypes):that. mimeTypes ==null
        ; } @ Override public inthashCode(){intresult= name !=null? name .hashCode
    (

    ):
    0 ; result=31
    *
        result + Arrays . hashCode ( data );result=31 * result+
        ( mimeTypes != null ? mimeTypes .hashCode():0)
        ; return result ; } } 