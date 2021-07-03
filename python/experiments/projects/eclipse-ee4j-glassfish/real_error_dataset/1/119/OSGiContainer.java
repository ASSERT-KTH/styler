/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.extras.osgicontainer;

import org.glassfish.api.container.Container;
import org.glassfish.api.deployment.Deployer;
import org.jvnet.hk2.annotations.Service;

import jakarta.inject.Singleton;

/**
 * OSGi container, will just be used to manage OSGi bundles through deployment
 * backend
 *
 * @author Jerome Dochez
 */
@Service(name = OSGiSniffer.CONTAINER_NAME)
@Singleton
public class OSGiContainer implements Container {

    public Class<? extends Deployer> getDeployer() {
        return OSGiDeployer.class;
    }

    public String getName() {
        return OSGiSniffer.CONTAINER_NAME; // used for reporting purpose,so any string is fine actually
    }

}
