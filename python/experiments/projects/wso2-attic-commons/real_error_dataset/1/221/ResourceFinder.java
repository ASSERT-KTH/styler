/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ode.bpel.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * General interace for retrieving resources identified by a URI.
 * 
 * @author Maciej Szefler - m s z e f l e r @ g m a i l . c o m
 */
public interface ResourceFinder {
  
    /**
     * Obtain an input stream to the resource at the given URI. 
     * @param uri resource URI
     * @return input stream or <code>null</code> if the resource is not found
     * @throws MalformedURLException in case of invalid URI
     * @throws IOException in case of read error
     */
    InputStream openResource(URI uri) throws MalformedURLException, IOException;
    
    
	/**
	 * Retrieves the base URI that the BPEL Process execution context is running relative to.
	 * 
	 * @return URI - the URI representing the absolute physical file path location that this process is defined within.
	 */
	 public URI getBaseResourceURI();

}

