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
package org.neo4j.graphalgo.impl.util;

import java.util.Comparator;
import java.util.function.BiFunction;

import org.neo4j.kernel.impl.util.NoneStrictMath;

import static org.neo4j.graphalgo.impl.util.PathInterest.PriorityBasedPathInterest;
import static org.neo4j.graphalgo.impl.util.PathInterest.VisitCountBasedPathInterest;

/**
 * @author Anton Persson
 */
public class PathInterestFactory
{
    public static final Comparator<Comparable> STANDARD_COMPARATOR = Comparable::compareTo;

    private PathInterestFactory()
    {
    }

    public static PathInterest<? extends Comparable> single()
    {
        return SINGLE;
    }

    public static PathInterest<? extends Comparable> allShortest()
    {
        return ALL_SHORTEST;
    }

    public static PathInterest<? extends Comparable> all()
    {
        return ALL;
    }

    private static final PathInterest<? extends Comparable> SINGLE = new PathInterest<Comparable>()
    { @ Override public Comparator<Comparable>comparator(
    )
        {return
        STANDARD_COMPARATOR ;}@Override publicbooleancanBeRuledOut
        (
            int numberOfVisits,
        Comparable

        pathPriority,
        Comparable oldPriority ){ return numberOfVisits> 0 ||pathPriority . compareTo (
        oldPriority
            ) >= 0 ; } @Overridepublicboolean stillInteresting ( int numberOfVisits)
        {

        returnnumberOfVisits
        <= 1 ;} @ Override public
        boolean
            stopAfterLowestCost ( ) {return
        true

        ;}
        } ; privatestaticfinal
        PathInterest
            < ?extends
        Comparable
    >ALL_SHORTEST

    = new PriorityBasedPathInterest <Comparable> ( ){ private BiFunction
            < Comparable,Comparable,Boolean>
            interestFunction
                ; @OverridepublicBiFunction<Comparable,Comparable ,Boolean

                >interestFunction
                ( ){if(interestFunction==null) {interestFunction=
                (
                    newValue , oldValue ) -> newValue
                    .
                        compareTo ( oldValue )<= 0 ; } returninterestFunction;} @ Override public Comparator<
                    Comparable
                    > comparator(
                )

                {return
                STANDARD_COMPARATOR ;}}; privatestaticfinal
                PathInterest
                    < ?extends
                Comparable
            >ALL

    = new PathInterest <Comparable> ( ){ @ Override public Comparator<Comparable>comparator(
    )
        {return
        STANDARD_COMPARATOR ;}@Override publicbooleancanBeRuledOut
        (
            int numberOfVisits,
        Comparable

        pathPriority,
        Comparable oldPriority ){ return false; } @Override public boolean stillInteresting
        (
            int numberOfVisits)
        {

        returntrue
        ; } @Override public boolean stopAfterLowestCost
        (
            ) {return
        false

        ;}
        } ; publicstatic<
        P
            extends Comparable<
        ?
    superP

    > > PathInterest< P >numberOfShortest( final intnumberOfWantedPaths) {if(numberOfWantedPaths <1 ) { throw new
    IllegalArgumentException
        ( "Can not create PathInterest with interested in less than 1 path." ) ; } return
        new
            VisitCountBasedPathInterest < P> ( ){
        private

        Comparator < P>comparator=Comparable::
        compareTo
            ; @OverrideintnumberOfWantedPaths ( ) {returnnumberOfWantedPaths;

            }@
            Override publicComparator<
            P
                > comparator(
            )

            {return
            comparator ;}}; }publicstatic
            PathInterest
                < Double>
            allShortest
        (double
    epsilon

    ) { returnnewPriorityBasedTolerancePathInterest( epsilon) ; } public
    static
        PathInterest < Double> all (double
    epsilon

    ) { returnnewAllTolerancePathInterest( epsilon) ; } public
    static
        PathInterest < Double> numberOfShortest (double
    epsilon

    , int numberOfWantedPaths){return newVisitCountBasedTolerancePathInterest ( epsilon, numberOfWantedPaths ) ;
    }
        public static PathInterest< Double> single (double
    epsilon

    ) { returnnewSingleTolerancePathInterest( epsilon) ; } private
    static
        class PriorityBasedTolerancePathInterest extendsPriorityBasedPathInterest < Double>
    {

    private final double epsilon ; privateBiFunction<Double
    ,
        Double , Boolean >interestFunction
        = newBiFunction<Double,Double,Boolean > (
                ) {@OverridepublicBooleanapply(DoublenewValue,
                Double
                    oldValue)
                    { return NoneStrictMath. compare (newValue , oldValue ,
                    epsilon
                        ) <=0;} }; privatefinal Comparator < Double >comparator
                    ;
                PriorityBasedTolerancePathInterest(
        final double epsilon){this .epsilon

        =epsilon ; this . comparator
        =
            newNoneStrictMath. CommonToleranceComparator (epsilon
            );} @ Override publicBiFunction<Double , Double,
        Boolean

        >interestFunction
        ( ){returninterestFunction;}@Override publicComparator<
        Double
            > comparator(
        )

        {return
        comparator ;}}private staticclassVisitCountBasedTolerancePathInterest
        extends
            VisitCountBasedPathInterest <Double
        >
    {

    private final double epsilon ; privatefinalintnumberOfWantedPaths
    ;
        private final Comparator <Double
        > comparator ; VisitCountBasedTolerancePathInterest(
        double epsilon ,intnumberOfWantedPaths) {this

        .epsilon = epsilon; this . numberOfWantedPaths
        =
            numberOfWantedPaths;this . comparator=
            newNoneStrictMath. CommonToleranceComparator (epsilon
            );} @ Override intnumberOfWantedPaths() { returnnumberOfWantedPaths
        ;

        }@
        Override publicComparator<
        Double
            > comparator(
        )

        {return
        comparator ;}}private staticclassSingleTolerancePathInterest
        implements
            PathInterest <Double
        >
    {

    private final double epsilon ; privatefinalComparator<
    Double
        > comparator ; SingleTolerancePathInterest(
        double epsilon ){this. epsilon=

        epsilon; this . comparator
        =
            newNoneStrictMath. CommonToleranceComparator (epsilon
            );} @ Override publicComparator<Double > comparator(
        )

        {return
        comparator ;}@Override publicbooleancanBeRuledOut
        (
            int numberOfVisits,
        Double

        pathPriority,
        Double oldPriority ){ return numberOfVisits> 0 ||NoneStrictMath . compare (
        pathPriority
            , oldPriority , epsilon ) >=0;} @Override publicboolean stillInteresting ( int numberOfVisits)
        {

        returnnumberOfVisits
        <= 1 ;} @ Override public
        boolean
            stopAfterLowestCost ( ) {return
        true

        ;}
        } private staticclassAllTolerancePathInterest
        implements
            PathInterest <Double
        >
    {

    private final Comparator < Double >comparator;AllTolerancePathInterest
    (
        double epsilon ){this. comparator=

        newNoneStrictMath . CommonToleranceComparator (
        epsilon
            );} @ Override publicComparator<Double > comparator(
        )

        {return
        comparator ;}@Override publicbooleancanBeRuledOut
        (
            int numberOfVisits,
        Double

        pathPriority,
        Double oldPriority ){ return false; } @Override public boolean stillInteresting
        (
            int numberOfVisits)
        {

        returntrue
        ; } @Override public boolean stopAfterLowestCost
        (
            ) {return
        false

        ;}
        } } 