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
package org.jdbi.v3.sqlobject.config.internal;
importjava

. lang.annotation.Annotation;importjava
. lang.reflect.Method;importjava
. util.stream.Stream;importorg
. jdbi.v3.core.config.ConfigRegistry;importorg
. jdbi.v3.sqlobject.config.Configurer;importorg
. jdbi.v3.sqlobject.config.RegisterColumnMapperFactories;publicclass

RegisterColumnMapperFactoriesImpl implements Configurer { @ Override

publicvoid
configureForMethod ( ConfigRegistryregistry, Annotationannotation , Class< ?>sqlObjectType, Methodmethod ) {configureForType (
registry,annotation, sqlObjectType) ;}@
Override

publicvoid
configureForType ( ConfigRegistryregistry, Annotationannotation , Class< ?>sqlObjectType) {Configurer delegate
= new RegisterColumnMapperFactoryImpl ( );RegisterColumnMapperFactoriesregisterColumnMapperFactories

= ( RegisterColumnMapperFactories )annotation; Stream.
of(registerColumnMapperFactories.value()).forEach(anno->delegate . configureForType(registry,anno, sqlObjectType) );}}
