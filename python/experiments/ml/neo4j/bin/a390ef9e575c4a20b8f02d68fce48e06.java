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
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.BranchCollisionDetector;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalBranch;

public class StandardBranchCollisionDetector implements BranchCollisionDetector
{
    private final Map<Node, Collection<TraversalBranch>[] > paths = newHashMap<> ( 1000)
    ; private final Evaluatorevaluator
    ; private finalSet<Path > returnedPaths = newHashSet<>()
    ; privatePredicate<Path > pathPredicate =Predicates.alwaysTrue()

    ;@
    Deprecated publicStandardBranchCollisionDetector ( Evaluator evaluator
    )
        {this. evaluator =evaluator
    ;

    } publicStandardBranchCollisionDetector ( Evaluatorevaluator ,Predicate<Path > pathPredicate
    )
        {this. evaluator =evaluator
        ; if ( pathPredicate != null
        )
            {this. pathPredicate =pathPredicate
        ;
    }

    }@
    Override@SuppressWarnings ( "unchecked"
    ) publicCollection<Path >evaluate ( TraversalBranchbranch , Direction direction
    )
        {
        // [0] for paths from start, [1] for paths from endCollection<TraversalBranch>[ ] pathsHere =paths.get (branch.endNode( ))
        ; int index =direction.ordinal()
        ; if ( pathsHere == null
        )
            { pathsHere = newCollection[ ]{ newArrayList<>() , newArrayList<>( )}
            ;paths.put (branch.endNode() , pathsHere)
        ;
        }pathsHere[index].add ( branch)

        ;
        // If there are paths from the other side then include all the
        // combined pathsCollection<TraversalBranch > otherCollections =pathsHere[ index == 0 ? 1 :0]
        ; if (!otherCollections.isEmpty( )
        )
            {Collection<Path > foundPaths = newArrayList<>()
            ; for ( TraversalBranch otherBranch : otherCollections
            )
                { TraversalBranch startPath = index == 0 ? branch :otherBranch
                ; TraversalBranch endPath = index == 0 ? otherBranch :branch
                ; BidirectionalTraversalBranchPath path = newBidirectionalTraversalBranchPath
                        (startPath , endPath)
                ; if (isAcceptablePath ( path )
                )
                    { if (returnedPaths.add ( path ) &&includePath (path ,startPath , endPath )
                    )
                        {foundPaths.add ( path)
                    ;
                }
            }

            } if (!foundPaths.isEmpty( )
            )
                { returnfoundPaths
            ;
        }
        } returnnull
    ;

    } private booleanisAcceptablePath ( BidirectionalTraversalBranchPath path
    )
        { returnpathPredicate.test ( path)
    ;

    } protected booleanincludePath ( Pathpath , TraversalBranchstartPath , TraversalBranch endPath
    )
        { Evaluation eval =evaluator.evaluate ( path)
        ; if (!eval.continues( )
        )
            {startPath.evaluation ( eval)
            ;endPath.evaluation ( eval)
        ;
        } returneval.includes()
    ;
}
