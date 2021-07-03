/**
 * Copyright (c) 2009-2010 TIBCO Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genxdm.processor.w3c.xs.validation.impl;

import javax.xml.namespace.QName;

import org.genxdm.processor.w3c.xs.validation.api.VxPSVI;
import org.genxdm.xs.exceptions.AbortException;
import org.genxdm.xs.exceptions.SchemaExceptionHandler;
import org.genxdm.xs.types.Type;


interface ModelAnalyzer
{
	void startDocument();

	void endDocument() throws AbortException;

	VxPSVI startElement(QName name, Type localType, Boolean explicitNil) throws AbortException;

	VxPSVI endElement() throws AbortException;

	void setExceptionHandler(SchemaExceptionHandler handler);
}
