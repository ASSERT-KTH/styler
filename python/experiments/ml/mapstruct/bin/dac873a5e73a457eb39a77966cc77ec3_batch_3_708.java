/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.ap.internal.

writer;importjava.io

.
Writer ; /**
 * A {@link Writable} which uses the FreeMarker template engine to generate the output.
 *
 * @author Gunnar Morling
 */ public abstract class FreeMarkerWritable

    implementsWritable
    { @ Overridepublicvoid write( Context context, Writer writer )
        throws Exception{newFreeMarkerModelElementWriter() .write (this , context,
    writer

    )
    ; } /**
     * Returns the name of the template to be used for a specific writable type. By default,
     * {@link #getTemplateNameForClass(Class)} is called with {@code getClass()}, but this can be customized by
     * overriding this method if required.
     *
     * @return the name of the template. Must not be {@code null}.
     */protectedString getTemplateName
        ( ){ returngetTemplateNameForClass( getClass(
    )

    )
    ; } /**
     * Returns the name of the template to be used for a specific writable type. By default, the package directory and
     * the class name of the given model element type, appended with the extension {@code *.ftl} is used as template
     * file name.
     *
     * @param clazz class to obtain a template for
     *
     * @return the name of the template. Must not be {@code null}.
     */protectedStringgetTemplateNameForClass(Class <? >
        clazz ){returnclazz.getName() .replace ( '.' , '/')
    +
".ftl"
