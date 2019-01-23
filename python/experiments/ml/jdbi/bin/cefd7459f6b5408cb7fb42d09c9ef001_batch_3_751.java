/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.collector;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java. util.Map;importjava
. util.Optional;importjava
. util.Set;importjava
. util.SortedSet;importjava
. util.TreeSet;importjava.util

. stream .Collector;importstaticjava.util.stream
. Collectors .toCollection;importstaticjava.util.stream
. Collectors .toSet;importstaticorg.jdbi.v3.core.generic
. GenericTypes .findGenericParameter;importstaticorg.jdbi.v3.core.generic

. GenericTypes . getErasedType ;
    class SetCollectorFactory implementsCollectorFactory{privatefinalMap< Class<?> ,Collector <?, ? , ? >>collectors=newIdentityHashMap

    <>( )
        ;SetCollectorFactory(){collectors.put (Set.class,
        toSet());collectors.put (HashSet.class,toCollection(HashSet
        ::new));collectors.put (LinkedHashSet.class,toCollection(LinkedHashSet
        ::new));collectors.put (SortedSet.class,toCollection(TreeSet
        ::new));collectors.put (TreeSet.class,toCollection(TreeSet
    ::

    new)
    ) ; }@Override publicboolean accepts
        ( Type containerType ) { returncontainerTypeinstanceofParameterizedType&&collectors.containsKey(getErasedType
    (

    containerType)
    ) ;}@Override publicOptional< Type> elementType
        (TypecontainerType) { Class <?>erasedType=
        getErasedType (containerType); returnfindGenericParameter(
    containerType

    ,erasedType
    ) ;}@Override publicCollector <? ,?, ?> build
        (TypecontainerType) { Class <?>erasedType=
        getErasedType (containerType);returncollectors.
    get
(
