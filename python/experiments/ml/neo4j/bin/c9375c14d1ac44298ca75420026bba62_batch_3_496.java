/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.index.schema;

import org.apache.commons.lang3.mutable.MutableLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.neo4j.cursor.RawCursor;
import org.neo4j.index.internal.gbptree.Hit;
import org.neo4j.index.internal.gbptree.SimpleLongLayout;
import org.neo4j.test.extension.Inject;
import org.neo4j.test.extension.RandomExtension;
import org.neo4j.test.rule.RandomRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@ExtendWith(RandomExtension.class)classCombinedPartSeekerTest { private staticfinalComparator< Hit<MutableLong ,MutableLong

>>
HIT_COMPARATOR =Comparator

.comparing
( Hit::key ) ;
@
    Inject
    RandomRule random ; @ Testvoid shouldCombineAllParts( )throws IOException{ // givenSimpleLongLayout layout= new SimpleLongLayout(
    0,"",true,1,2,3);List < RawCursor < Hit<MutableLong,MutableLong>
    , IOException > >parts=new ArrayList< > ()
    ;intpartCount=random.nextInt(1 , 20 ) ;List<Hit<MutableLong
    , MutableLong > >expectedAllData=new ArrayList< > ()
    ; int maxKey = random .nextInt ( 100 ,10_000 ); for
    (
        int i = 0;i< partCount; i ++)
        {intdataSize=random.nextInt(0 , 100 ) ;List<Hit < MutableLong,
        MutableLong > > partData = newArrayList < > (dataSize ); for
        (
            int j = 0;j< dataSize ;j
            ++){long key =random.nextLong ( maxKey) ; partData. add (new SimpleHit < > ( new MutableLong(
        key
        ),newMutableLong ( key*
        2))) ; }partData . sort (HIT_COMPARATOR
        );parts. add (new
    SimpleSeeker
    (partData)) ; expectedAllData.

    addAll
    (partData);}expectedAllData . sort ( HIT_COMPARATOR);// when CombinedPartSeeker< MutableLong ,MutableLong

    >
    combinedSeeker = newCombinedPartSeeker<>(layout , parts ) ;
    // then
        for( Hit<MutableLong,MutableLong >expectedHit
        :expectedAllData){assertTrue( combinedSeeker . next());Hit

        <MutableLong ,MutableLong>actualHit=combinedSeeker.get() ;assertEquals(expectedHit.key(). longValue(
        ), actualHit.key().longValue()) ;assertEquals(expectedHit.value(). longValue(
    )
    ,actualHit .value(). longValue(
    )
    ); }assertFalse(combinedSeeker. next(
)

) ; // And just ensure it will return false again after that assertFalse ( combinedSeeker.next());}privatestaticclass
SimpleSeeker
    implements RawCursor <Hit<MutableLong,MutableLong>,IOException >{
    private finalIterator<Hit<MutableLong ,MutableLong

    > >data ;privateHit<MutableLong,MutableLong>current ; private
    SimpleSeeker
        (Iterable< Hit <MutableLong,MutableLong>>
    data

    ){
    this . data=data
    .
        iterator ( );}@Override public
        boolean
            next ( ){if(data.
            hasNext ()
        )
        { current=
    data

    .next
    ( ) ;returntrue
    ;
        }
    return

    false;
    } @Overridepublicvoidclose( ){// Nothing to close
    }
        @ Overridepublic
    Hit
<
MutableLong
