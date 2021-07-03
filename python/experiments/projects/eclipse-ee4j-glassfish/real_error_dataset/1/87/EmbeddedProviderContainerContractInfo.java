/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.persistence.jpa;

import com.sun.appserv.connectors.internal.api.ConnectorRuntime;
import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.api.deployment.DeploymentContext;

import javax.naming.NamingException;

/**
 * Implementation of ProviderContainerContractInfo while running inside embedded server
 *
 * @author Mitesh Meswani
 */
public class EmbeddedProviderContainerContractInfo extends ServerProviderContainerContractInfo {

    public EmbeddedProviderContainerContractInfo(DeploymentContext deploymentContext, ConnectorRuntime connectorRuntime, boolean isDas) {
        super(deploymentContext, connectorRuntime, isDas);
    }

    @Override
    public boolean isWeavingEnabled() {
        return false; //Weaving is not enabled while running in embedded environment
    }

}
