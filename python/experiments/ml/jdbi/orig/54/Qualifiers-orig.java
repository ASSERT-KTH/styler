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
package org.jdbi.v3.core.qualifier;

import static java.util.stream.Collectors.toSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.jdbi.v3.core.internal.AnnotationFactory;
import org.jdbi.v3.meta.Beta;

/**
 * Utility class for type qualifiers supported by Jdbi core.
 */
@Beta
public class Qualifiers {
    private Qualifiers() {}

    /**
     * Returns an {@link NVarchar} qualifying annotation instance.
     */
    public static NVarchar nVarchar() {
        return AnnotationFactory.create(NVarchar.class);
    }

    /**
     * Returns the set of qualifying annotations on the given elements.
     * @param elements the annotated elements. Null elements are ignored.
     * @return the set of qualifying annotations on the given elements.
     */
    public static Set<Annotation> getQualifiers(AnnotatedElement... elements) {
        return Arrays.stream(elements)
            .filter(Objects::nonNull)
            .map(AnnotatedElement::getAnnotations)
            .flatMap(Arrays::stream)
            .filter(anno -> anno.annotationType().isAnnotationPresent(Qualifier.class))
            .collect(toSet());
    }
}
