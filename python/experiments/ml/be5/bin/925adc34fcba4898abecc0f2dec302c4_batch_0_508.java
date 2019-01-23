package com.developmentontheedge.be5.server.model;


import java.util.Arrays;
import java.util.Base64;

public classBase64File {private
Stringname
    ;private byte[ ]data;private
    StringmimeTypes ;publicBase64File(Stringname ,byte[]
    data, StringmimeTypes ){this.

    name= name;this.data= data; this.mimeTypes =mimeTypes ; }public
    String
        getName() { returnname
        ;}public byte []
        getData() { returndata
    ;

    } public StringgetMimeTypes(
    )
        { returnmimeTypes
    ;

    } @Overridepublic StringtoString(
    )
        { return"{\"type\":\"Base64File\",\"name\":\""
    +

    name + "\", \"data\":\"data:"+mimeTypes
    +
        ";base64," +Base64
    .

    getEncoder(
    ) . encodeToString(data
    )
        + "\"}" ; } @
                Override public boolean equals ( Object o){if(this==o)return true ;if
    (

    o==
    null || getClass() !=o
    .
        getClass () ) returnfalse ; Base64Filethat
        = (Base64File ) o ; if(name != null?!name.equals ( that.

        name ) : that.name !=null

        ) returnfalse ; if ( !Arrays.equals(data,that. data ))return false ;return mimeTypes !=null
        ? mimeTypes.equals(that.mimeTypes) :that.mimeTypes== null ;}
        @ Override public int hashCode (){intresult=name!= null ?name. hashCode ()
    :

    0;
    result = 31*result
    +
        Arrays . hashCode ( data ) ; result=31*result + (mimeTypes
        != null ? mimeTypes . hashCode ():0);return
        result ; } } 