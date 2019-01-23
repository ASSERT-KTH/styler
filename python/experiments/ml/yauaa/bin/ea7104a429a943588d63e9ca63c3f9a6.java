/*
 * Yet Another UserAgent Analyzer
 * Copyright (C) 2013-2018 Niels Basjes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.basjes.parse.useragent.analyze;

import org.antlr.v4.runtime.tree.ParseTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class MatchesList implements Collection<MatchesList.Match>, Serializable { public static final class Match implements Serializable
    { private Stringkey
    ; private Stringvalue
    ; private ParseTreeresult

    ; publicMatch( Stringkey , Stringvalue , ParseTreeresult )
        {fill(key ,value ,result)
    ;

    } public voidfill( StringnKey , StringnValue , ParseTreenResult )
        {this. key =nKey
        ;this. value =nValue
        ;this. result =nResult
    ;

    } public StringgetKey( )
        { returnkey
    ;

    } public StringgetValue( )
        { returnvalue
    ;

    } public ParseTreegetResult( )
        { returnresult
    ;
}

} private intsize
; private intmaxSize

; privateMatch[ ]allElements

; publicMatchesList( intnewMaxSize )
    { maxSize =newMaxSize

    ; size =0
    ; allElements = newMatch[maxSize]
    ; for( int i =0 ; i <maxSize ;i++ )
        {allElements[i ] = newMatch(null ,null ,null)
    ;
}

}@
Override public intsize( )
    { returnsize
;

}@
Override public booleanisEmpty( )
    { return size ==0
;

}@
Override public voidclear( )
    { size =0
;

} public booleanadd( Stringkey , Stringvalue , ParseTreeresult )
    { if( size >=maxSize )
        {increaseCapacity()
    ;

    }allElements[size].fill(key ,value ,result)
    ;size++
    ; returntrue
;

}@
Override publicIterator<Match >iterator( )
    { return newIterator<Match>( )
        { int offset =0

        ;@
        Override public booleanhasNext( )
            { return offset <size
        ;

        }@
        Override public Matchnext( )
            { if(!hasNext() )
                { throw newNoSuchElementException("Array index out of bounds")
            ;
            } returnallElements[offset++]
        ;
    }}
;

}@
Override publicObject[ ]toArray( )
    { returnArrays.copyOf(this.allElements ,this.size)
;

} private static final int CAPACITY_INCREASE =3

; private voidincreaseCapacity( )
    { int newMaxSize =maxSize +CAPACITY_INCREASE
    ;Match[ ] newAllElements = newMatch[newMaxSize]
    ;System.arraycopy(allElements ,0 ,newAllElements ,0 ,maxSize)
    ; for( int i =maxSize ; i <newMaxSize ;i++ )
        {newAllElements[i ] = newMatch(null ,null ,null)
    ;
    } allElements =newAllElements
    ; maxSize =newMaxSize
;

} publicList<String >toStrings( )
    {List<String > result = newArrayList<>(size)
    ; for( Matchmatch :this )
        {result.add( "{ \"" +match. key + "\"=\"" +match. value +"\" }")
    ;
    } returnresult
;

}
// ============================================================
// Everything else is NOT supported

// ============================================================@
Override public booleanadd( Matchmatch )
    { throw newUnsupportedOperationException()
;

}@
Override public booleanaddAll(Collection< ? extendsMatch >collection )
    { throw newUnsupportedOperationException()
;

}@
Override public booleanremove( Objecto )
    { throw newUnsupportedOperationException()
;

}@
Override public booleanremoveAll(Collection<? >collection )
    { throw newUnsupportedOperationException()
;

}@
Override public booleanretainAll(Collection<? >collection )
    { throw newUnsupportedOperationException()
;

}@
Override public booleancontains( Objecto )
    { throw newUnsupportedOperationException()
;

}@
Override public booleancontainsAll(Collection<? >collection )
    { throw newUnsupportedOperationException()
;

}@
Override public<T >T[ ]toArray(T[ ]ts )
    { throw newUnsupportedOperationException()
;
}
