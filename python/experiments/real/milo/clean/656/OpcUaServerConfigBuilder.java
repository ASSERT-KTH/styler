/*
 * Copyright (c) 2016 Kevin Herron and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.milo.opcua.sdk.server.api.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.identity.AnonymousIdentityValidator;
import org.eclipse.milo.opcua.sdk.server.identity.IdentityValidator;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.application.CertificateManager;
import org.eclipse.milo.opcua.stack.core.application.CertificateValidator;
import org.eclipse.milo.opcua.stack.core.channel.ChannelConfig;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.BuildInfo;
import org.eclipse.milo.opcua.stack.core.types.structured.SignedSoftwareCertificate;
import org.eclipse.milo.opcua.stack.core.types.structured.UserTokenPolicy;
import org.eclipse.milo.opcua.stack.server.config.UaTcpStackServerConfig;
import org.eclipse.milo.opcua.stack.server.config.UaTcpStackServerConfigBuilder;

import static com.google.common.collect.Lists.newArrayList;

public class OpcUaServerConfigBuilder extends UaTcpStackServerConfigBuilder {

    private String hostname = getDefaultHostname();
    private List<String> bindAddresses = newArrayList("0.0.0.0");
    private int bindPort = Stack.DEFAULT_PORT;
    private EnumSet<SecurityPolicy> securityPolicies = EnumSet.of(SecurityPolicy.None);
    private IdentityValidator identityValidator = AnonymousIdentityValidator.INSTANCE;

    private BuildInfo buildInfo = new BuildInfo(
        "", "", "", "", "", DateTime.MIN_VALUE);

    private Function<String, Set<String>> hostnameResolver = OpcUaServer::getHostnames;

    private OpcUaServerConfigLimits limits =
        new OpcUaServerConfigLimits() {
        };

    public OpcUaServerConfigBuilder setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public OpcUaServerConfigBuilder setBindAddresses(List<String> bindAddresses) {
        this.bindAddresses = bindAddresses;
        return this;
    }

    public OpcUaServerConfigBuilder setBindPort(int bindPort) {
        this.bindPort = bindPort;
        return this;
    }

    public OpcUaServerConfigBuilder setSecurityPolicies(EnumSet<SecurityPolicy> securityPolicies) {
        this.securityPolicies = securityPolicies;
        return this;
    }

    public OpcUaServerConfigBuilder setIdentityValidator(IdentityValidator identityValidator) {
        this.identityValidator = identityValidator;
        return this;
    }

    public OpcUaServerConfigBuilder setBuildInfo(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
        return this;
    }

    public OpcUaServerConfigBuilder setLimits(OpcUaServerConfigLimits limits) {
        this.limits = limits;
        return this;
    }

    public OpcUaServerConfigBuilder setHostnameResolver(Function<String, Set<String>> hostnameResolver) {
        this.hostnameResolver = hostnameResolver;
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setServerName(String serverName) {
        super.setServerName(serverName);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setApplicationName(LocalizedText applicationName) {
        super.setApplicationName(applicationName);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setApplicationUri(String applicationUri) {
        super.setApplicationUri(applicationUri);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setProductUri(String productUri) {
        super.setProductUri(productUri);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setCertificateManager(CertificateManager certificateManager) {
        super.setCertificateManager(certificateManager);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setCertificateValidator(CertificateValidator certificateValidator) {
        super.setCertificateValidator(certificateValidator);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setUserTokenPolicies(List<UserTokenPolicy> userTokenPolicies) {
        super.setUserTokenPolicies(userTokenPolicies);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setSoftwareCertificates(List<SignedSoftwareCertificate> softwareCertificates) {
        super.setSoftwareCertificates(softwareCertificates);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setExecutor(ExecutorService executor) {
        super.setExecutor(executor);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setChannelConfig(ChannelConfig channelConfig) {
        super.setChannelConfig(channelConfig);
        return this;
    }

    @Override
    public OpcUaServerConfigBuilder setStrictEndpointUrlsEnabled(boolean strictEndpointUrlsEnforced) {
        super.setStrictEndpointUrlsEnabled(strictEndpointUrlsEnforced);
        return this;
    }

    public OpcUaServerConfig build() {
        UaTcpStackServerConfig stackServerConfig = super.build();

        return new OpcUaServerConfigImpl(
            stackServerConfig,
            hostname,
            bindAddresses,
            bindPort,
            securityPolicies,
            identityValidator,
            buildInfo,
            limits,
            hostnameResolver
        );
    }

    private static String getDefaultHostname() {
        try {
            return System.getProperty("hostname",
                InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    public static final class OpcUaServerConfigImpl implements OpcUaServerConfig {

        private final UaTcpStackServerConfig stackServerConfig;

        private final String hostname;
        private final List<String> bindAddresses;
        private final int bindPort;
        private final EnumSet<SecurityPolicy> securityPolicies;
        private final IdentityValidator identityValidator;
        private final BuildInfo buildInfo;
        private final OpcUaServerConfigLimits limits;
        private final Function<String, Set<String>> hostnameResolver;

        public OpcUaServerConfigImpl(UaTcpStackServerConfig stackServerConfig,
                                     String hostname,
                                     List<String> bindAddresses,
                                     int bindPort,
                                     EnumSet<SecurityPolicy> securityPolicies,
                                     IdentityValidator identityValidator,
                                     BuildInfo buildInfo,
                                     OpcUaServerConfigLimits limits,
                                     Function<String, Set<String>> hostnameResolver) {

            this.stackServerConfig = stackServerConfig;

            this.hostname = hostname;
            this.bindAddresses = bindAddresses;
            this.bindPort = bindPort;
            this.securityPolicies = securityPolicies;
            this.identityValidator = identityValidator;
            this.buildInfo = buildInfo;
            this.limits = limits;
            this.hostnameResolver = hostnameResolver;
        }

        @Override
        public String getHostname() {
            return hostname;
        }

        @Override
        public List<String> getBindAddresses() {
            return bindAddresses;
        }

        @Override
        public int getBindPort() {
            return bindPort;
        }

        @Override
        public IdentityValidator getIdentityValidator() {
            return identityValidator;
        }

        @Override
        public BuildInfo getBuildInfo() {
            return buildInfo;
        }

        @Override
        public OpcUaServerConfigLimits getLimits() {
            return limits;
        }

        @Override
        public EnumSet<SecurityPolicy> getSecurityPolicies() {
            return securityPolicies;
        }

        @Override
        public String getServerName() {
            return stackServerConfig.getServerName();
        }

        @Override
        public LocalizedText getApplicationName() {
            return stackServerConfig.getApplicationName();
        }

        @Override
        public String getApplicationUri() {
            return stackServerConfig.getApplicationUri();
        }

        @Override
        public String getProductUri() {
            return stackServerConfig.getProductUri();
        }

        @Override
        public CertificateManager getCertificateManager() {
            return stackServerConfig.getCertificateManager();
        }

        @Override
        public CertificateValidator getCertificateValidator() {
            return stackServerConfig.getCertificateValidator();
        }

        @Override
        public ExecutorService getExecutor() {
            return stackServerConfig.getExecutor();
        }

        @Override
        public List<UserTokenPolicy> getUserTokenPolicies() {
            return stackServerConfig.getUserTokenPolicies();
        }

        @Override
        public List<SignedSoftwareCertificate> getSoftwareCertificates() {
            return stackServerConfig.getSoftwareCertificates();
        }

        @Override
        public ChannelConfig getChannelConfig() {
            return stackServerConfig.getChannelConfig();
        }

        @Override
        public boolean isStrictEndpointUrlsEnabled() {
            return stackServerConfig.isStrictEndpointUrlsEnabled();
        }

        @Override
        public Function<String, Set<String>> getHostnameResolver() {
            return hostnameResolver;
        }

    }

}
