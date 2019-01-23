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
package org.neo4j.graphdb.impl.traversal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.neo4j.function.Predicates;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;importorg
. neo4j.graphdb.traversal.BranchCollisionDetector;importorg
. neo4j.graphdb.traversal.Evaluation;importorg
. neo4j.graphdb.traversal.Evaluator;importorg

. neo4j . graphdb .
traversal
    . TraversalBranch ;publicclassStandardBranchCollisionDetectorimplementsBranchCollisionDetector{privatefinalMap< Node , Collection <TraversalBranch>[ ] >paths
    = new HashMap <>
    ( 1000 );privatefinal Evaluator evaluator ; privatefinalSet<Path>
    returnedPaths =newHashSet< > ( );privatePredicate<Path

    >pathPredicate
    = Predicates. alwaysTrue ( )
    ;
        @Deprecatedpublic StandardBranchCollisionDetector (Evaluator
    evaluator

    ) {this . evaluator= evaluator;}public StandardBranchCollisionDetector (
    Evaluator
        evaluator,Predicate < Path>
        pathPredicate ) { this . evaluator
        =
            evaluator;if ( pathPredicate!=
        null
    )

    {this
    .pathPredicate= pathPredicate ;
    } }@Override@ SuppressWarnings( "unchecked" )public Collection < Path
    >
        evaluate
        (TraversalBranchbranch,Directiondirection ) { // [0] for paths from start, [1] for paths from endCollection<TraversalBranch >[]pathsHere= paths.
        get ( branch .endNode());
        int index = direction . ordinal
        (
            ) ; if (pathsHere== null) {pathsHere=newCollection[ ] {newArrayList<> ()
            ,newArrayList< >()};paths . put(
        branch
        .endNode(),pathsHere) ; }pathsHere

        [
        index
        ].add( branch ) ;// If there are paths from the other side then include all the// combined paths Collection < TraversalBranch > otherCollections =pathsHere[
        index == 0?1:0] ;
        if
            (!otherCollections. isEmpty ( ) ){Collection<Path>
            foundPaths = new ArrayList < > (
            )
                ; for ( TraversalBranch otherBranch : otherCollections ) { TraversalBranchstartPath
                = index == 0 ? branch : otherBranch ; TraversalBranchendPath
                = index == 0 ?otherBranch
                        :branch ; BidirectionalTraversalBranchPathpath
                = new BidirectionalTraversalBranchPath( startPath , endPath
                )
                    ; if (isAcceptablePath(path ) ) { if( returnedPaths. add( path ) &&
                    includePath
                        (path,startPath , endPath)
                    )
                {
            foundPaths

            . add (path);}} }
            if
                ( !foundPaths
            .
        isEmpty
        ( ))
    {

    return foundPaths ;} } return null
    ;
        } privatebooleanisAcceptablePath( BidirectionalTraversalBranchPath path)
    {

    return pathPredicate .test ( path) ; }protected boolean includePath (
    Path
        path , TraversalBranch startPath,TraversalBranchendPath ) {Evaluation
        eval = evaluator.evaluate(path) ;
        if
            (!eval. continues ()
            ){startPath. evaluation (eval
        )
        ; endPath.evaluation(eval)
    ;
}
