package fr.inria.spirals.repairnator;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.

stream .JsonWriter;importjava.
io .File;importjava.
io .IOException;importjava.nio.

file
. Path ; /**
 * This class can be used in gson to properly serialize Path object.
 */ publicclassGsonPathTypeAdapterextends TypeAdapter
    <Path
    > { @Overridepublic voidwrite ( JsonWriterjsonWriter , Path path
        )throwsIOException{jsonWriter.
        beginObject();jsonWriter.name("path").value(path.toAbsolutePath().toString
        ());jsonWriter.
    endObject

    ()
    ; } @Overridepublic Pathread ( JsonReader jsonReader
        )throwsIOException{jsonReader.
        beginObject ( ) ;StringabsolutePath=jsonReader.
        nextString();jsonReader.
        endObject ( );returnnewFile(absolutePath).
    toPath
(
