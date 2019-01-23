package fr.inria.spirals.repairnator;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.
stream .JsonWriter;importjava.io.

File
; import java . io.IOException; import
    java.
    nio . file.Path ;/**
 * This class can be used in gson to properly serialize Path object.
 */ public classGsonPathTypeAdapter extends TypeAdapter <
        Path>{@Overridepublic
        voidwrite(JsonWriterjsonWriter,Pathpath)throwsIOException{jsonWriter.beginObject();jsonWriter.
        name("path").value
    (

    path.
    toAbsolutePath ( ).toString () ) ; jsonWriter
        .endObject();}
        @ Override public Pathread(JsonReaderjsonReader)
        throwsIOException{jsonReader.beginObject
        ( ) ;StringabsolutePath=jsonReader.nextString()
    ;
jsonReader
