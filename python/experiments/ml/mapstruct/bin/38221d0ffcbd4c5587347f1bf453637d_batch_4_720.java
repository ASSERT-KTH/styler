/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.spi;

import javax.lang.model.element.TypeElement;importjavax.lang.model
. type.TypeMirror;/**
 * Indicates a type was visited whose hierarchy was erroneous, because it has a non-existing super-type.
 * <p>
 * This exception can be used to signal the MapStruct processor to postpone the generation of the mappers to the next
 * round
 *
 * @author Gunnar Morling
 */publicclassTypeHierarchyErroneousExceptionextendsRuntimeException

{
private static final long serialVersionUID =

   1L ; private final TypeMirror type ;public

   TypeHierarchyErroneousException ( TypeElement element)

   { this(element .asType (
       )) ;}publicTypeHierarchyErroneousException( TypeMirrortype
   )

   { this.type =type ;
       }publicTypeMirror getType ()
   {

   return type ;}} 