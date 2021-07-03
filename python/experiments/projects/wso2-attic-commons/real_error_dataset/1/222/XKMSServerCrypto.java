/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.xkms2.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.axiom.om.util.Base64;
import org.apache.axis2.AxisFault;
import org.apache.commons.discovery.Resource;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.resource.DiscoverResources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.X509NameTokenizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.keys.content.KeyValue;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import org.wso2.xkms2.Authentication;
import org.wso2.xkms2.InvalidReason;
import org.wso2.xkms2.KRSSRequest;
import org.wso2.xkms2.KRSSResult;
import org.wso2.xkms2.KeyBinding;
import org.wso2.xkms2.KeyBindingAbstractType;
import org.wso2.xkms2.KeyUsage;
import org.wso2.xkms2.LocateRequest;
import org.wso2.xkms2.LocateResult;
import org.wso2.xkms2.MessageAbstractType;
import org.wso2.xkms2.PrototypeKeyBinding;
import org.wso2.xkms2.QueryKeyBinding;
import org.wso2.xkms2.RecoverKeyBinding;
import org.wso2.xkms2.RecoverRequest;
import org.wso2.xkms2.RecoverResult;
import org.wso2.xkms2.RegisterRequest;
import org.wso2.xkms2.RegisterResult;
import org.wso2.xkms2.ReissueKeyBinding;
import org.wso2.xkms2.ReissueRequest;
import org.wso2.xkms2.ReissueResult;
import org.wso2.xkms2.RequestAbstractType;
import org.wso2.xkms2.RespondWith;
import org.wso2.xkms2.ResultMajor;
import org.wso2.xkms2.ResultMinor;
import org.wso2.xkms2.ResultType;
import org.wso2.xkms2.Status;
import org.wso2.xkms2.StatusValue;
import org.wso2.xkms2.UnverifiedKeyBinding;
import org.wso2.xkms2.UseKeyWith;
import org.wso2.xkms2.ValidReason;
import org.wso2.xkms2.ValidateRequest;
import org.wso2.xkms2.ValidateResult;
import org.wso2.xkms2.ValidityInterval;
import org.wso2.xkms2.XKMSException;
import org.wso2.xkms2.core.XKMSRequestData;
import org.wso2.xkms2.util.XKMSKeyUtil;
import org.wso2.xkms2.util.XKMSUtil;

public class XKMSServerCrypto {

    private static final Log LOG = LogFactory.getLog((XKMSServerCrypto.class)
            .getName());

    public static final String XKMS_SERVER_AUTHENTICATION_CODE = "org.wso2.xkms2.service.crypto.authen.code";

    public static final String XKMS_KEY_STORE_LOCATION = "org.wso2.xkms2.service.crypto.keystore.location";

    public static final String XKMS_KEY_STORE_PASSWORD = "org.wso2.xkms2.service.crypto.keystore.password";

    public static final String XKMS_SERVER_CERT_ALIACE = "org.wso2.xkms2.service.crypto.server.cert.aliase";

    public static final String XKMS_SERVER_KEY_PASSWORD = "org.wso2.xkms2.service.crypto.server.key.password";

    public static final String XKMS_ISSUER_CERT_ALIACE = "org.wso2.xkms2.service.crypto.issuer.cert.aliase";

    public static final String XKMS_ISSUER_KEY_PASSWORD = "org.wso2.xkms2.service.crypto.issuer.key.password";

    public static final String XKMS_DEFAULT_EXPIRY_INTERVAL = "org.wso2.xkms2.service.crypto.default.expriy.interval";

    public static final String XKMS_DEFAULT_PRIVATE_KEY_PASSWORD = "org.wso2.xkms2.service.crypto.default.private.key.password";

    public static final String XKMS_ENABLE_PERSISTENCE = "org.wso2.xkms2.service.crypto.persistence.enabled";

    public static final String PROP_ID_CERT_PROVIDER = "org.wso2.xkms2.service.crypto.cert.provider";

    static String SKI_OID = "2.5.29.14";

    private static CertificateFactory certFact;

    /** KeyStore which is used to store X509Certificate and private key entiries */
    private KeyStore keystore = null;

    protected KeyStore cacerts = null;

    /** Certificate to be used to sign issued X509Certificates */
    private X509Certificate cacert = null;

    /** Private key to be used to sign issued X509Certificates */
    private PrivateKey cakey = null;

    /** Private key to be used to sign response messages */
    private PrivateKey sekey = null;

    private Key authkey = null;

    private Key enkey = null;

    private Date caexpiry = null;

    private int validityPeriod;

    private Properties properties;

    private ClassLoader classLoader;

    private boolean canSupportPersistence = false;

    private boolean saveKeystore = false;

    private Document doc;

    public XKMSServerCrypto(Properties properties) throws XKMSException {
        this(properties, XKMSServerCrypto.class.getClassLoader());

    }

    public XKMSServerCrypto(Properties properties, ClassLoader classLoader)
            throws XKMSException {
        this.properties = properties;
        this.classLoader = classLoader;
        Init();
    }

    /*
     * Initializes this XKMSServerCrypto instance.
     */
    private void Init() throws XKMSException {
        String alias;
        String passcode;

        // setting the Server Authentication Key.
        passcode = properties
                .getProperty(XKMSServerCrypto.XKMS_SERVER_AUTHENTICATION_CODE);
        if (passcode != null && passcode.length() > 0) {
            authkey = XKMSKeyUtil.getAuthenticationKey(passcode);
        }

        // setting the <PrivateKey> encryption key
        passcode = properties
                .getProperty(XKMSServerCrypto.XKMS_SERVER_AUTHENTICATION_CODE);
        if (passcode != null && passcode.length() > 0) {
            enkey = XKMSKeyUtil.getPrivateKey(passcode, "DESede");
        }

        loadKeyStore();

        // settting Issuer certificate
        alias = properties.getProperty(XKMS_ISSUER_CERT_ALIACE);
        cacert = getCertificate(alias);

        caexpiry = cacert.getNotAfter();

        // setting Issuer key
        alias = properties.getProperty(XKMS_ISSUER_CERT_ALIACE);
        passcode = properties.getProperty(XKMS_ISSUER_KEY_PASSWORD);
        cakey = getPrivateKey(alias, passcode);

        // setting XKMS response message signing key
        alias = properties.getProperty(XKMS_SERVER_CERT_ALIACE);
        passcode = properties.getProperty(XKMS_SERVER_KEY_PASSWORD);
        sekey = getPrivateKey(alias, passcode);

        // setting the validaity period
        String noOfDays = properties.getProperty(XKMS_DEFAULT_EXPIRY_INTERVAL);
        if (noOfDays != null) {
            validityPeriod = Integer.parseInt(noOfDays);
        } else {
            // the default is one year
            validityPeriod = 365;
        }

        // setting persistence flag
        String persistence = properties.getProperty(XKMS_ENABLE_PERSISTENCE);
        if (persistence != null) {
            saveKeystore = Boolean.getBoolean(persistence);
        } else {
            saveKeystore = false;
        }

    }

    public ResultType process(XKMSRequestData data) throws AxisFault {

        RequestAbstractType request = data.getRequest();
        this.doc = data.getDocument();

        if (request instanceof RegisterRequest) {
            return handleRegisterRequest((RegisterRequest) request);

        } else if (request instanceof ValidateRequest) {
            return handleValidateRequest((ValidateRequest) request);

        } else if (request instanceof ReissueRequest) {
            return handleReissueRequest((ReissueRequest) request);

        } else if (request instanceof LocateRequest) {
            return handleLocateRequest((LocateRequest) request);

        } else if (request instanceof RecoverRequest) {
            return handleRecoverRequest((RecoverRequest) request);
        }

        return null;
    }

    public RegisterResult handleRegisterRequest(RegisterRequest request) {

        try {
            prepare(request, request.getPrototypeKeyBinding());
            validate(request);

            PrototypeKeyBinding pkb = request.getPrototypeKeyBinding();
            String identifer = getSubjectDN(pkb);

            if (identifer == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No SubjectDN is specified");
                }

                throw new XKMSException(XKMSException.FAILURE, "NoSubjectDN");
            }

            PublicKey public1 = pkb.getKeyValue();
            PrivateKey private1 = null;

            // if a public key is not provided we need to generate both the
            // public key and private key
            if (public1 == null) {
                KeyPair keypair = XKMSKeyUtil.generateRSAKeyPair();
                public1 = keypair.getPublic();
                private1 = keypair.getPrivate();
            }

            // calculating the start and expiery dates.
            ValidityInterval validityInterval = pkb.getValidityInterval();
            Date[] adjustedInterval;
            if (validityInterval != null) {
                adjustedInterval = getAdjustedValidityInterval(validityInterval
                        .getNotBefore(), validityInterval.getOnOrAfter());
            } else {
                adjustedInterval = getAdjustedValidityInterval((Date) null,
                        (Date) null);
            }

            long serialNum = nextSerialNumber();
            String aliase = createAlias(serialNum);
            BigInteger serialNumber = BigInteger.valueOf(serialNum);

            X509Certificate cert;

            List keyUsage = pkb.getKeyUsage();
            if (keyUsage == null
                    || keyUsage.isEmpty()
                    || (keyUsage.size() == 1 && keyUsage
                            .contains(KeyUsage.EXCHANGE))) {
                cert = XKMSKeyUtil.getX509Certificate(identifer, serialNumber,
                        adjustedInterval[0], adjustedInterval[1], public1,
                        cacert, cakey);

            } else {

                cert = XKMSKeyUtil.getX509Certificate(identifer, serialNumber,
                        adjustedInterval[0], adjustedInterval[1], keyUsage
                                .contains(KeyUsage.SIGNATURE), keyUsage
                                .contains(KeyUsage.ENCRYPTION), public1,
                        cacert, cakey);
            }

            try {
                keystore.setCertificateEntry(aliase, cert);
                if (LOG.isDebugEnabled()) {
                    LOG
                            .debug("Adding the newly constructed X509Certificate to the keystore - \n "
                                    + cert);
                }

                if (private1 != null) {
                    Certificate[] chain = new Certificate[] { cert };
                    keystore.setKeyEntry(aliase, private1,
                            getPrivateKeyPassword(), chain);

                    if (LOG.isDebugEnabled()) {
                        LOG
                                .debug("Added the newly construct Private Key to the keystore - \n"
                                        + private1);
                    }
                }

                if (saveKeystore) {
                    saveKeystore();
                }

            } catch (KeyStoreException e) {
                LOG.error("Adding the certificate to keystore failed", e);
                throw new XKMSException(e);
            }

            RegisterResult result = XKMSUtil.createRegisterResult();
            buildResultType(request, result, aliase, keystore);
            return result;

        } catch (XKMSException ex) {

            RegisterResult resultType = XKMSUtil.createRegisterResult();
            buildFault(request, resultType, ex);
            return resultType;
        }
    }

    public ReissueResult handleReissueRequest(ReissueRequest request) {

        try {
            prepare(request, request.getReissueKeyBinding());
            validate(request);

            ReissueKeyBinding rkb = request.getReissueKeyBinding();
            X509Certificate cert = rkb.getCertValue();

            if (cert == null) {
                throw new XKMSException(XKMSException.FAILURE, "CertNotPresent");
            }

            String alias = getAliasForX509Cert(cert.getIssuerDN().getName(),
                    cert.getSerialNumber());

            if (alias == null) {
                throw new XKMSException(XKMSException.FAILURE, "CertNotFound");
            }

            ReissueResult result = XKMSUtil.creatReissueResult();
            buildResultType(request, result, alias, keystore);
            return result;

        } catch (XKMSException ex) {
            ReissueResult result = XKMSUtil.creatReissueResult();
            buildFault(request, result, ex);
            return result;
        }
    }

    public RecoverResult handleRecoverRequest(RecoverRequest request) {
        try {
            prepare(request, request.getRecoverKeyBinding());
            // TODO Validate ..
            RecoverKeyBinding recoverKeyBinding = request
                    .getRecoverKeyBinding();
            String[] aliases = null;

            X509Certificate cert = recoverKeyBinding.getCertValue();
            if (cert != null) {
                String issuerDN = cert.getIssuerDN().getName();
                BigInteger serialNumber = cert.getSerialNumber();

                String alias = getAliasForX509Cert(issuerDN, serialNumber);
                if (alias != null) {
                    aliases = new String[] { alias };
                }

            } else {
                String subjectDN = getSubjectDN(recoverKeyBinding);

                if (subjectDN != null) {
                    aliases = getAliasesForDN(subjectDN);
                }
            }

            if (aliases == null) {
                String keyName = recoverKeyBinding.getKeyName();
                if (keyName != null) {
                    aliases = new String[] { keyName };
                }
            }

            if (aliases == null || aliases.length < 1) {
                throw new XKMSException(XKMSException.NO_MATCH, "keyNotFound");
            }

            RecoverResult recoverResult = XKMSUtil.createRecoverResult();
            buildResultType(request, recoverResult, aliases[0], keystore);
            return recoverResult;

        } catch (XKMSException ex) {
            RecoverResult recoverResult = XKMSUtil.createRecoverResult();
            buildFault(request, recoverResult, ex);
            return recoverResult;
        }

    }

    /**
     * @param locate
     * @return
     */
    public LocateResult handleLocateRequest(LocateRequest locate) {
        try {
            QueryKeyBinding keybinding = locate.getQueryKeyBinding();

            String identifer = getSubjectDN(keybinding);
            String[] aliases = null;

            if (identifer == null) {
                KeyInfo keyInfo = keybinding.getKeyInfo();

                if (keyInfo != null) {
                    try {
                        KeyName keyName = keyInfo.itemKeyName(0);
                        if (keyName != null) {
                            aliases = new String[] { keyName.getKeyName() };
                        }
                    } catch (XMLSecurityException xme) {
                        throw new XKMSException(xme);
                    }
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("No SubjectDN is specified");
                }
            } else {
                aliases = getAliasesForDN(identifer);
            }

            byte[] skiValue = getSKIValue(keybinding);
            if (skiValue != null) {
                String alias = getAliasForX509Cert(skiValue);
                if (alias != null) {
                    aliases = new String[] { alias };
                }
            }

            if (aliases == null || aliases.length == 0) {
                throw new XKMSException("KeyNotFound");
            }

            List keyUsage = keybinding.getKeyUsage();
            boolean digitalSigning = keyUsage.contains(KeyUsage.SIGNATURE);
            boolean dataEncryption = keyUsage.contains(KeyUsage.ENCRYPTION);

            List list = new ArrayList();
            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                X509Certificate cert = getCertificate(alias);

                if (cert != null) {
                    boolean[] ku = cert.getKeyUsage();

                    if (digitalSigning && !ku[0]) {
                        continue;
                    }
                    if (dataEncryption && !ku[3]) {
                        continue;
                    }
                    list.add(alias);
                }
            }

            if (list.isEmpty()) {
                throw new XKMSException(XKMSException.NO_MATCH, "KeyNotFound");
            }

            LocateResult result = XKMSUtil.createLocateResult();
            buildResultType((RequestAbstractType) locate, (ResultType) result);

            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                String alias = (String) iterator.next();
                X509Certificate[] certs = getCertificates(alias);
                UnverifiedKeyBinding ukb = new UnverifiedKeyBinding();

                addKeyInfo(locate.getRespondWith(), alias, certs, ukb);
                addKeyUsage(certs[0], ukb);
                result.addUnverifiedKeyBinding(ukb);
            }

            return result;

        } catch (XKMSException ex) {
            LocateResult result = XKMSUtil.createLocateResult();
            buildFault(locate, result, ex);
            return result;
        }
    }

    public ValidateResult handleValidateRequest(ValidateRequest validateRequest) {

        try {
            QueryKeyBinding queryKeyBinding = validateRequest
                    .getQueryKeyBinding();
            List respondWith = validateRequest.getRespondWith();

            KeyInfo keyInfo = queryKeyBinding.getKeyInfo();
            X509Certificate cert = null;

            if (keyInfo != null) {
                try {
                    cert = keyInfo.getX509Certificate();
                } catch (KeyResolverException e) {
                    throw new XKMSException(XKMSException.FAILURE, "keystore",
                            e);
                }
            }

            if (cert == null) {
                throw new XKMSException(XKMSException.FAILURE, "CertNotPresent");
            }

            if (verifyTrust(cert)) {
                ValidateResult validateResult = XKMSUtil.createValidateResult();
                buildResultType((RequestAbstractType) validateRequest,
                        (ResultType) validateResult);

                String subjectDN = cert.getIssuerDN().getName();
                BigInteger issuerSerial = cert.getSerialNumber();

                String alias = getAliasForX509Cert(subjectDN, issuerSerial);
                X509Certificate[] certs = getCertificates(alias);

                KeyBinding keyBinding = XKMSUtil.createKeyBinding();
                validateResult.addKeyBinding(keyBinding);

                addKeyInfo(respondWith, alias, certs, keyBinding);
                addKeyUsage(certs[0], keyBinding);
                addStatus(true, keyBinding);

                return validateResult;

            } else {

                ValidateResult result = XKMSUtil.createValidateResult();
                buildResultType((RequestAbstractType) validateRequest,
                        (ResultType) result);

                KeyBinding keybinding = XKMSUtil.createKeyBinding();
                X509Certificate[] certs = new X509Certificate[] { cert };

                addKeyInfo(validateRequest.getRespondWith(), null, certs,
                        keybinding);
                addKeyUsage(certs[0], keybinding);
                addStatus(false, keybinding);
                result.addKeyBinding(keybinding);

                return result;
            }

        } catch (XKMSException ex) {
            ValidateResult resultType = XKMSUtil.createValidateResult();
            buildFault(validateRequest, resultType, ex);
            return resultType;
        }
    }

    /*
     * Prepares an KRSSRequest message for validation.
     */
    private void prepare(KRSSRequest request,
            KeyBindingAbstractType abstractType) throws XKMSException {

        Authentication authentication = request.getAuthentication();
        authentication.setKeyBindingAuthenticationKey(authkey);

        KeyInfo keyInfo = abstractType.getKeyInfo();
        if (keyInfo != null) {
            try {

                KeyName itemKeyName = keyInfo.itemKeyName(0);
                if (itemKeyName != null) {
                    abstractType.setKeyName(itemKeyName.getKeyName());
                }

                PublicKey public1 = keyInfo.getPublicKey();
                if (public1 != null) {
                    abstractType.setKeyValue(public1);
                }

                X509Certificate cert = keyInfo.getX509Certificate();
                if (cert != null) {
                    abstractType.setCertValue(cert);
                    abstractType.setKeyValue(cert.getPublicKey());
                }

            } catch (KeyResolverException e) {
                LOG.error("", e);
                throw new XKMSException(XKMSException.FAILURE, "noKey", e);

            } catch (XMLSecurityException e) {
                LOG.error("", e);
                throw new XKMSException(XKMSException.FAILURE, "noKey", e);
            }
        }
    }

    private void validate(ReissueRequest reissueRequest) throws XKMSException {
        validate((KRSSRequest) reissueRequest);

        ReissueKeyBinding rkb = reissueRequest.getReissueKeyBinding();
        XMLSignature pop = reissueRequest.getProofOfPocession();

        Key key = rkb.getKeyValue();
        if (key == null) {
            X509Certificate cert = rkb.getCertValue();

            if (cert != null) {
                key = cert.getPublicKey();
            }
        }

        if (pop != null && key != null) {
            try {
                pop.checkSignatureValue(key);
            } catch (XMLSignatureException se) {
                throw new XKMSException(
                        "Proof-Of-Pocession varification failed", se);
            }
        }

    }

    private void validate(RegisterRequest registerRequest) throws XKMSException {

        validate((KRSSRequest) registerRequest);

        PrototypeKeyBinding pkb = registerRequest.getPrototypeKeyBinding();
        XMLSignature proofOfPossession = registerRequest.getProofOfPossession();

        Key key = pkb.getKeyValue();

        if (key != null) {
            try {
                proofOfPossession.checkSignatureValue(key);

            } catch (XMLSignatureException e) {
                LOG.error("", e);

                throw new XKMSException(
                        XKMSException.PROOF_OF_POSSESSION_REQUIRED,
                        "popRequired", e);
            }
        }
    }

    private void validate(KRSSRequest request) throws XKMSException {

        validate((MessageAbstractType) request);

        Authentication authentication = request.getAuthentication();
        XMLSignature keyBindingAuthentication = authentication
                .getKeyBindingAuthentication();

        Key keyBindingAuthenticationKey = authentication
                .getKeyBindingAuthenticationKey();
        try {
            if (!keyBindingAuthentication
                    .checkSignatureValue(keyBindingAuthenticationKey)) {
                throw new XKMSException(XKMSException.NO_AUTHENTICATION,
                        "invalidXMLSign");
            }
            System.out.println("success");
        } catch (XMLSignatureException e) {
            LOG.error("", e);
            throw new XKMSException(XKMSException.NO_AUTHENTICATION,
                    "invalidXMLSign", e);
        }
    }

    private void validate(MessageAbstractType abstractType)
            throws XKMSException {

        XMLSignature signature = abstractType.getSignature();
        if (signature != null) {
            X509Certificate x509Certificate = abstractType.getSignCert();

            try {
                signature.checkSignatureValue(x509Certificate);
            } catch (XMLSignatureException e) {
                LOG.error("", e);
                throw new XKMSException(XKMSException.NO_AUTHENTICATION,
                        "failedXMLSign", e);
            }
        }
    }

    private void buildResultType(RegisterRequest request,
            RegisterResult result, String aliase, KeyStore ks)
            throws XKMSException {

        buildResultType((RequestAbstractType) request, result, aliase, ks);
        org.wso2.xkms2.PrivateKey privateKey = getPrivateKey(aliase, ks);

        if (privateKey != null) {
            result.setPrivateKey(privateKey);
        }
    }

    private void buildResultType(ReissueRequest request, ReissueResult result,
            String aliase, KeyStore ks) throws XKMSException {

        buildResultType((RequestAbstractType) request, (KRSSResult) result,
                aliase, ks);
    }

    private void buildResultType(RecoverRequest request, RecoverResult result,
            String aliase, KeyStore ks) throws XKMSException {

        buildResultType((RequestAbstractType) request, (KRSSResult) result,
                aliase, ks);
        ;
        org.wso2.xkms2.PrivateKey privateKey = getPrivateKey(aliase, ks);

        if (privateKey != null) {
            result.setPrivateKey(privateKey);
        }
    }

    private void buildResultType(RequestAbstractType request,
            KRSSResult result, String aliase, KeyStore ks) throws XKMSException {

        buildResultType((RequestAbstractType) request, (ResultType) result);

        KeyBinding kb = XKMSUtil.createKeyBinding();
        result.addKeyBinding(kb);

        X509Certificate[] certs = getCertificates(aliase);

        List respondWithList = request.getRespondWith();
        addKeyInfo(respondWithList, aliase, certs, kb);

        addValidationInterval(certs[0], kb);
        addKeyUsage(certs[0], kb);
        addStatus(true, kb);
    }

    private void buildResultType(RequestAbstractType request, ResultType result)
            throws XKMSException {

        result.setServiceURI(request.getServiceURI());
        result.setResultMajor(ResultMajor.SUCCESS);
        result.setSignKey(sekey);
    }

    private org.wso2.xkms2.PrivateKey getPrivateKey(String aliase, KeyStore ks)
            throws XKMSException {

        String passcode = properties
                .getProperty(XKMS_DEFAULT_PRIVATE_KEY_PASSWORD);

        PrivateKey private1 = getPrivateKey(aliase, passcode);

        if (private1 != null) {
            X509Certificate cert = getCertificate(aliase);
            PublicKey public1 = cert.getPublicKey();
            KeyPair keyPair = new KeyPair(public1, private1);
            org.wso2.xkms2.PrivateKey privateKey = new org.wso2.xkms2.PrivateKey();
            privateKey.setRSAKeyPair(keyPair);
            privateKey.setKey(enkey);
            return privateKey;

        } else {
            return null;
        }
    }

    private String getSubjectDN(KeyBindingAbstractType abstractType) {
        List useKeyWiths = abstractType.getUseKeyWith();

        if (useKeyWiths == null || useKeyWiths.isEmpty()) {
            return null;
        }

        for (Iterator iterator = useKeyWiths.iterator(); iterator.hasNext();) {
            UseKeyWith useKeyWith = (UseKeyWith) iterator.next();

            if (UseKeyWith.PKIX.equals(useKeyWith.getApplication())) {
                return useKeyWith.getIdentifier();
            }
        }

        return null;
    }

    private byte[] getSKIValue(KeyBindingAbstractType abstractType) {
        List useKeyWiths = abstractType.getUseKeyWith();

        if (useKeyWiths == null || useKeyWiths.isEmpty()) {
            return null;
        }

        for (Iterator iterator = useKeyWiths.iterator(); iterator.hasNext();) {
            UseKeyWith useKeyWith = (UseKeyWith) iterator.next();

            if (UseKeyWith.SKI.equals(useKeyWith.getApplication())) {
                return Base64.decode(useKeyWith.getIdentifier());
            }
        }

        return null;
    }

    /**
     * Returns PKIX keystore of this sample service.
     * 
     * @return the KeyStore of this sample service.
     */
    private void loadKeyStore() {

        InputStream is = null;

        try {
            String keyStorelocation = properties
                    .getProperty(XKMSServerCrypto.XKMS_KEY_STORE_LOCATION);
            String password = properties
                    .getProperty(XKMSServerCrypto.XKMS_KEY_STORE_PASSWORD);

            File keyStoreFile = new File(keyStorelocation);

            if (!keyStoreFile.exists()) {
                String wso2wsashome = System.getProperty("wso2wsas.home");

                if (wso2wsashome != null) {
                    keyStoreFile = new File(wso2wsashome + File.separator
                            + "conf" + File.separator + keyStorelocation);
                }
            }
            /**
             * If we don't find it, then look on the file system.
             */
            if (keyStoreFile.exists()) {
                try {
                    is = new FileInputStream(keyStorelocation);
                    properties.put(XKMS_KEY_STORE_LOCATION, keyStoreFile
                            .getAbsolutePath());
                    canSupportPersistence = true;

                } catch (FileNotFoundException e) {
                    throw new Exception(e);
                }
            }

            if (is == null) {
                // Look for the keystore in classpaths
                DiscoverResources disc = new DiscoverResources();
                disc.addClassLoader(JDKHooks.getJDKHooks()
                        .getThreadContextClassLoader());
                disc.addClassLoader(classLoader);

                ResourceIterator iterator = disc
                        .findResources(keyStorelocation);
                if (iterator.hasNext()) {
                    Resource resource = iterator.nextResource();
                    is = resource.getResourceAsStream();
                }

                if (is == null) {
                    iterator = disc.findResources("META-INF/"
                            + keyStorelocation);
                    if (iterator.hasNext()) {
                        Resource resource = iterator.nextResource();
                        is = resource.getResourceAsStream();
                    }
                }
            }
            if (is == null) {
                throw new Exception("specified keystore doesn't exist");
            }

            keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, password.toCharArray());

        } catch (Exception e) {
            throw new RuntimeException("Can't load keystore", e);

        } finally {
            // is.close();
        }
    }

    /**
     * Persist the keystore if persistance is enabled.
     * 
     * @throws XKMSException
     */
    private void saveKeystore() throws XKMSException {
        if (canSupportPersistence) {
            try {
                String location = properties
                        .getProperty(XKMS_KEY_STORE_LOCATION);
                FileOutputStream outputStream = new FileOutputStream(location);
                String storePass = properties
                        .getProperty(XKMS_KEY_STORE_PASSWORD);
                keystore.store(outputStream, storePass.toCharArray());

            } catch (FileNotFoundException ex) {
                throw new XKMSException(ex);
            } catch (KeyStoreException e) {
                throw new XKMSException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new XKMSException(e);
            } catch (CertificateException e) {
                throw new XKMSException(e);
            } catch (IOException e) {
                throw new XKMSException(e);
            }
        }
    }

    /**
     * @see org.apache.ws.security.components.crypto.Crypto#getPrivateKey(java.lang.String,
     *      java.lang.String)
     */
    public PrivateKey getPrivateKey(String alias, String password)
            throws IllegalArgumentException {

        if (alias == null) {
            throw new IllegalArgumentException("alias is null");
        }
        try {

            boolean b = keystore.isKeyEntry(alias);
            if (!b) {
                if (LOG.isDebugEnabled()) {
                    LOG.error("Cannot find key for alias: " + alias);
                }
                return null;
            }

            Key keyTmp = keystore.getKey(alias, password.toCharArray());

            if (!(keyTmp instanceof PrivateKey)) {
                throw new IllegalArgumentException(
                        "Key is not a private key, alias: " + alias);
            }

            return (PrivateKey) keyTmp;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addKeyInfo(List respondWiths, String aliase,
            X509Certificate[] certs, KeyBindingAbstractType abstractType) {

        KeyInfo keyInfo = new KeyInfo(doc);

        for (Iterator iterator = respondWiths.iterator(); iterator.hasNext();) {
            RespondWith respondWith = (RespondWith) iterator.next();

            if (respondWith.equals(RespondWith.KEY_NAME)) {
                KeyName keyName = new KeyName(doc, aliase);
                keyInfo.add(keyName);

            } else if (respondWith.equals(RespondWith.KEY_VALUE)) {
                PublicKey publicKey = certs[0].getPublicKey();
                KeyValue keyValue = new KeyValue(doc, publicKey);
                keyInfo.add(keyValue);

            } else if (respondWith.equals(RespondWith.X_509_CERT)) {
                addX509Certificate(certs[0], keyInfo);

            } else if (respondWith.equals(RespondWith.X_509_CHAIN)) {
                for (int i = 0; i < certs.length; i++) {
                    addX509Certificate(certs[i], keyInfo);
                }

            } else {
                // TODO Implement the other RespondWith elements.
            }
        }
        abstractType.setKeyInfo(keyInfo);
    }

    private void addX509Certificate(X509Certificate cert, KeyInfo keyInfo) {

        X509Data data = new X509Data(doc);
        try {
            data.addCertificate(cert);
        } catch (XMLSecurityException se) {
            throw new RuntimeException(
                    "Adding the X509Certificate to X509Data object failed", se);
        }
        keyInfo.add(data);
    }

    private void addKeyUsage(X509Certificate cert, KeyBindingAbstractType kb) {

        // CHECKME
        // /////////////////////////////////////////////////////////////////////
        // setting the KeyUsage values
        boolean[] keyUsage = cert.getKeyUsage();
        if (keyUsage != null) {
            // digitalSignature
            if (keyUsage[0]) {
                kb.addKeyUsage(KeyUsage.SIGNATURE);
            }
            // dataEncipherment
            if (keyUsage[3]) {
                kb.addKeyUsage(KeyUsage.ENCRYPTION);
            }
        }
        // ////////////////////////////////////////////////////////////////////
    }

    private void addValidationInterval(X509Certificate cert, KeyBinding kb) {
        Date start = cert.getNotBefore();
        Date expiry = cert.getNotAfter();

        Calendar notBefore = Calendar.getInstance();
        notBefore.setTime(start);
        Calendar notAfter = Calendar.getInstance();
        notAfter.setTime(expiry);

        kb.setValidityInterval(notBefore, notAfter);
    }

    private void addStatus(boolean valid, KeyBinding kb) {
        Status status = new Status();
        status
                .setStatusValue((valid) ? StatusValue.VALID
                        : StatusValue.INVALID);
        if (valid) {
            status.addValidReason(ValidReason.ISSUER_TRUST);
            status.addValidReason(ValidReason.REVOCATION_STATUS);
            status.addValidReason(ValidReason.SIGNATURE);
            status.addValidReason(ValidReason.VALIDITY_INTERVAL);
        } else {
            status.addInvalidReason(InvalidReason.ISSUER_TRUST);
            status.addInvalidReason(InvalidReason.REVOCATION_STATUS);
            status.addInvalidReason(InvalidReason.SIGNATURE);
            status.addInvalidReason(InvalidReason.VALIDITY_INTERVAL);
        }
        kb.setStatus(status);
    }

    /*
     * Calcuates the start and expiry dates. The arguments are only advisory.
     */
    private Date[] getAdjustedValidityInterval(Calendar notBefore,
            Calendar notAfter) {
        Date start = (notBefore == null) ? null : notBefore.getTime();
        Date expiry = (notAfter == null) ? null : notAfter.getTime();
        return getAdjustedValidityInterval(start, expiry);
    }

    /*
     * 
     */
    private Date[] getAdjustedValidityInterval(Date notBefore, Date notAfter) {
        Date start;
        Date expiryDate;

        Calendar ca = Calendar.getInstance();
        Date today = ca.getTime();

        if (notBefore != null) {
            if (notBefore.before(today)) {
                start = today;
            } else {
                start = notBefore;
            }
        } else {
            start = today;
        }

        if (notAfter == null) {
            ca.add(Calendar.DAY_OF_YEAR, validityPeriod);
            notAfter = ca.getTime();
        }

        if (notAfter.after(caexpiry)) {
            expiryDate = caexpiry;
        } else {
            expiryDate = notAfter;
        }

        return new Date[] { start, expiryDate };
    }

    private void buildFault(RequestAbstractType requestType,
            ResultType resultType, XKMSException ex) {

        resultType.setServiceURI(requestType.getServiceURI());

        Throwable t = ex.getCause();
        if (t != null) {
            LOG.fatal("Exception is thrown when processing", t);
        }

        int errorCode = ex.getErrorCode();
        switch (errorCode) {
        case XKMSException.NO_AUTHENTICATION:
            resultType.setResultMajor(ResultMajor.SENDER);
            resultType.setResultMinor(ResultMinor.NO_AUTHENTICATION);
            break;
        case XKMSException.NO_MATCH:
            resultType.setResultMajor(ResultMajor.SUCCESS);
            resultType.setResultMinor(ResultMinor.NO_MATCH);
            break;
        default:

            resultType.setResultMajor(ResultMajor.RECEIVER);
            resultType.setResultMinor(ResultMinor.FAILURE);
        }
    }

    private char[] getPrivateKeyPassword() {
        String password = properties
                .getProperty(XKMS_DEFAULT_PRIVATE_KEY_PASSWORD);
        return password.toCharArray();
    }

    /**
     * Creates an alias based on serial number
     * 
     * @param serialNum
     *            the serial number
     * @return the newly created alias
     */
    private String createAlias(long serialNum) {
        String aliase = String.valueOf(serialNum);
        return aliase;
    }

    /**
     * We choose the current time of the server in milliseconds as the serial
     * number of the issued certificate.
     * 
     * @return serialNumber
     */
    private long nextSerialNumber() {
        return System.currentTimeMillis();
    }

    /**
     * @see org.apache.ws.security.components.crypto.Crypto#getAliasForX509Cert(java.lang.String,
     *      java.math.BigInteger)
     */
    public String getAliasForX509Cert(String issuer, BigInteger serialNumber)
            throws XKMSException {
        return getAliasForX509Cert(issuer, serialNumber, true);
    }

    /**
     * 
     * @param issuer
     * @param serialNumber
     * @param useSerialNumber
     * @return
     * @throws WSSecurityException
     */
    private String getAliasForX509Cert(String issuer, BigInteger serialNumber,
            boolean useSerialNumber) throws XKMSException {
        Vector issuerRDN = splitAndTrim(issuer);
        X509Certificate x509cert = null;
        Vector certRDN = null;
        Certificate cert = null;

        try {
            for (Enumeration e = keystore.aliases(); e.hasMoreElements();) {
                String alias = (String) e.nextElement();
                Certificate[] certs = keystore.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a
                    // result.
                    cert = keystore.getCertificate(alias);
                    if (cert == null) {
                        return null;
                    }
                } else {
                    cert = certs[0];
                }
                if (!(cert instanceof X509Certificate)) {
                    continue;
                }
                x509cert = (X509Certificate) cert;
                if (!useSerialNumber
                        || useSerialNumber
                        && x509cert.getSerialNumber().compareTo(serialNumber) == 0) {
                    certRDN = splitAndTrim(x509cert.getIssuerDN().getName());
                    if (certRDN.equals(issuerRDN)) {
                        return alias;
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new XKMSException("keystore");
        }
        return null;
    }

    private Vector splitAndTrim(String inString) {
        X509NameTokenizer nmTokens = new X509NameTokenizer(inString);
        Vector vr = new Vector();

        while (nmTokens.hasMoreTokens()) {
            vr.add(nmTokens.nextToken());
        }
        java.util.Collections.sort(vr);
        return vr;
    }

    /**
     * Lookup X509 Certificates in the keystore according to a given DN of the
     * subject of the certificate <p/> The search gets all alias names of the
     * keystore and gets the certificate (chain) for each alias. Then the DN of
     * the certificate is compared with the parameters.
     * 
     * @param subjectDN
     *            The DN of subject to look for in the keystore
     * @return Vector with all alias of certificates with the same DN as given
     *         in the parameters
     * @throws org.apache.ws.security.WSSecurityException
     * 
     */
    public String[] getAliasesForDN(String subjectDN) throws XKMSException {

        // The DN to search the keystore for
        Vector subjectRDN = splitAndTrim(subjectDN);
        Vector aliases = getAlias(subjectRDN, keystore);

        // If we can't find the issuer in the keystore then look at cacerts
        if (aliases.size() == 0 && cacerts != null) {
            aliases = getAlias(subjectRDN, cacerts);
        }

        // Convert the vector into an array
        String[] result = new String[aliases.size()];
        for (int i = 0; i < aliases.size(); i++)
            result[i] = (String) aliases.elementAt(i);

        return result;
    }

    private Vector getAlias(Vector subjectRDN, KeyStore store)
            throws XKMSException {
        // Store the aliases found
        Vector aliases = new Vector();

        Certificate cert = null;

        try {
            for (Enumeration e = store.aliases(); e.hasMoreElements();) {
                String alias = (String) e.nextElement();

                Certificate[] certs = store.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a
                    // result.
                    cert = store.getCertificate(alias);
                    if (cert == null) {
                        return null;
                    }
                    certs = new Certificate[] { cert };
                } else {
                    cert = certs[0];
                }
                if (cert instanceof X509Certificate) {
                    Vector foundRDN = splitAndTrim(((X509Certificate) cert)
                            .getSubjectDN().getName());

                    if (subjectRDN.equals(foundRDN)) {
                        aliases.add(alias);
                    }
                }
            }
        } catch (KeyStoreException e) {
            throw new XKMSException(e);
        }
        return aliases;
    }

    /**
     * @see org.apache.ws.security.components.crypto.Crypto#getCertificateFactory()
     */
    public CertificateFactory getCertificateFactory() throws XKMSException {
        if (certFact == null) {
            try {
                String provider = properties.getProperty(PROP_ID_CERT_PROVIDER);
                if (provider == null || provider.length() == 0) {
                    certFact = CertificateFactory.getInstance("X.509");
                } else {
                    certFact = CertificateFactory
                            .getInstance("X.509", provider);
                }
            } catch (CertificateException e) {
                throw new XKMSException("unsupportedCertType");

            } catch (NoSuchProviderException e) {
                throw new XKMSException("noSecProvider");
            }
        }

        return certFact;
    }

    /**
     * Overridden because there's a bug in the base class where they don't use
     * the provider variant for the certificate validator.
     * 
     * @param certs
     *            Certificate chain to validate
     * @return true if the certificate chain is valid, false otherwise
     * @throws WSSecurityException
     */
    public boolean validateCertPath(X509Certificate[] certs)
            throws XKMSException {

        try {
            // Generate cert path
            List cert_list = Arrays.asList(certs);
            CertPath path = getCertificateFactory().generateCertPath(cert_list);

            // Use the certificates in the keystore as TrustAnchors
            PKIXParameters param = new PKIXParameters(this.keystore);

            // Do not check a revocation list
            param.setRevocationEnabled(false);

            String provider = properties
                    .getProperty("org.apache.ws.security.crypto.merlin.cert.provider");

            CertPathValidator validator = null;

            if (provider == null || provider.length() == 0) {
                validator = CertPathValidator.getInstance("PKIX");
            } else {
                validator = CertPathValidator.getInstance("PKIX", provider);
            }
            validator.validate(path, param);
        } catch (java.security.NoSuchProviderException e) {
            throw new XKMSException("certpath");
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new XKMSException("certpath");
        } catch (java.security.cert.CertificateException e) {
            throw new XKMSException("certpath");
        } catch (java.security.InvalidAlgorithmParameterException e) {
            throw new XKMSException("certpath");
        } catch (java.security.cert.CertPathValidatorException e) {
            throw new XKMSException("certpath");
        } catch (java.security.KeyStoreException e) {
            throw new XKMSException("certpath");
        }

        return true;
    }

    /**
     * Gets the list of certificates for a given alias. <p/>
     * 
     * @param alias
     *            Lookup certificate chain for this alias
     * @return Array of X509 certificates for this alias name, or null if this
     *         alias does not exist in the keystore
     */
    public X509Certificate getCertificate(String alias) throws XKMSException {
        Certificate[] certs = null;
        Certificate cert = null;
        try {
            if (this.keystore != null) {
                // There's a chance that there can only be a set of trust stores
                certs = keystore.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a
                    // result.
                    cert = keystore.getCertificate(alias);
                }
            }

            if (certs == null && cert == null && cacerts != null) {
                // Now look into the trust stores
                certs = cacerts.getCertificateChain(alias);
                if (certs == null) {
                    cert = cacerts.getCertificate(alias);
                }
            }

            if (cert != null) {
                return (X509Certificate) cert;

            } else if (certs == null) {
                // At this pont we don't have certs or a cert
                return null;
            } else {
                return (X509Certificate) certs[0];

            }
        } catch (KeyStoreException e) {
            throw new XKMSException("keystore");
        }
    }

    /**
     * Gets the list of certificates for a given alias. <p/>
     * 
     * @param alias
     *            Lookup certificate chain for this alias
     * @return Array of X509 certificates for this alias name, or null if this
     *         alias does not exist in the keystore
     */
    public X509Certificate[] getCertificates(String alias) throws XKMSException {
        Certificate[] certs = null;
        Certificate cert = null;
        try {
            if (this.keystore != null) {
                // There's a chance that there can only be a set of trust stores
                certs = keystore.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a
                    // result.
                    cert = keystore.getCertificate(alias);
                }
            }

            if (certs == null && cert == null && cacerts != null) {
                // Now look into the trust stores
                certs = cacerts.getCertificateChain(alias);
                if (certs == null) {
                    cert = cacerts.getCertificate(alias);
                }
            }

            if (cert != null) {
                certs = new Certificate[] { cert };
            } else if (certs == null) {
                // At this pont we don't have certs or a cert
                return null;
            }
        } catch (KeyStoreException e) {
            throw new XKMSException("keystore");
        }

        X509Certificate[] x509certs = new X509Certificate[certs.length];
        for (int i = 0; i < certs.length; i++) {
            x509certs[i] = (X509Certificate) certs[i];
        }
        return x509certs;
    }

    /**
     * Evaluate whether a given certificate should be trusted. Hook to allow
     * subclasses to implement custom validation methods however they see fit.
     * <p/> Policy used in this implementation: 1. Search the keystore for the
     * transmitted certificate 2. Search the keystore for a connection to the
     * transmitted certificate (that is, search for certificate(s) of the issuer
     * of the transmitted certificate 3. Verify the trust path for those
     * certificates found because the search for the issuer might be fooled by a
     * phony DN (String!)
     * 
     * @param cert
     *            the certificate that should be validated against the keystore
     * @return true if the certificate is trusted, false if not (AxisFault is
     *         thrown for exceptions during CertPathValidation)
     * @throws WSSecurityException
     */
    protected boolean verifyTrust(X509Certificate cert) throws XKMSException {

        // If no certificate was transmitted, do not trust the signature
        if (cert == null) {
            return false;
        }

        String[] aliases = null;
        String alias = null;
        X509Certificate[] certs;

        String subjectString = cert.getSubjectDN().getName();
        String issuerString = cert.getIssuerDN().getName();
        BigInteger issuerSerial = cert.getSerialNumber();

        if (LOG.isDebugEnabled()) {
            LOG.debug("WSHandler: Transmitted certificate has subject "
                    + subjectString);
            LOG.debug("WSHandler: Transmitted certificate has issuer "
                    + issuerString + " (serial " + issuerSerial + ")");
        }

        // FIRST step
        // Search the keystore for the transmitted certificate

        // Search the keystore for the alias of the transmitted certificate

        alias = getAliasForX509Cert(issuerString, issuerSerial);

        if (alias != null) {
            // Retrieve the certificate for the alias from the keystore

            certs = getCertificates(alias);

            // If certificates have been found, the certificates must be
            // compared
            // to ensure againgst phony DNs (compare encoded form including
            // signature)
            if (certs != null && certs.length > 0 && cert.equals(certs[0])) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Direct trust for certificate with "
                            + subjectString);
                }
                return true;
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No alias found for subject from issuer with "
                        + issuerString + " (serial " + issuerSerial + ")");
            }
        }

        // SECOND step
        // Search for the issuer of the transmitted certificate in the keystore

        // Search the keystore for the alias of the transmitted certificates
        // issuer

        aliases = getAliasesForDN(issuerString);

        // If the alias has not been found, the issuer is not in the keystore
        // As a direct result, do not trust the transmitted certificate
        if (aliases == null || aliases.length < 1) {
            if (LOG.isDebugEnabled()) {
                LOG
                        .debug("No aliases found in keystore for issuer "
                                + issuerString + " of certificate for "
                                + subjectString);
            }
            return false;
        }

        // THIRD step
        // Check the certificate trust path for every alias of the issuer found
        // in the keystore
        for (int i = 0; i < aliases.length; i++) {
            alias = aliases[i];

            if (LOG.isDebugEnabled()) {
                LOG.debug("Preparing to validate certificate path with alias "
                        + alias + " for issuer " + issuerString);
            }

            // Retrieve the certificate(s) for the alias from the keystore

            certs = getCertificates(alias);

            // If no certificates have been found, there has to be an error:
            // The keystore can find an alias but no certificate(s)
            if (certs == null | certs.length < 1) {
                throw new XKMSException("noCertForAlias");
            }

            // Form a certificate chain from the transmitted certificate
            // and the certificate(s) of the issuer from the keystore
            // First, create new array
            X509Certificate[] x509certs = new X509Certificate[certs.length + 1];
            // Then add the first certificate ...
            x509certs[0] = cert;
            // ... and the other certificates
            for (int j = 0; j < certs.length; j++) {
                cert = certs[i];
                x509certs[certs.length + j] = cert;
            }
            certs = x509certs;

            // Use the validation method from the crypto to check whether the
            // subjects certificate was really signed by the issuer stated in
            // the certificate

            if (validateCertPath(certs)) {
                if (LOG.isDebugEnabled()) {
                    LOG
                            .debug("WSHandler: Certificate path has been verified for certificate with subject "
                                    + subjectString);
                }
                return true;
            }
        }

        LOG
                .debug("WSHandler: Certificate path could not be verified for certificate with subject "
                        + subjectString);
        return false;
    }

    public String getAliasForX509Cert(byte[] skiBytes) throws XKMSException {
        Certificate cert = null;
        boolean found = false;

        try {
            for (Enumeration e = keystore.aliases(); e.hasMoreElements();) {
                String alias = (String) e.nextElement();
                Certificate[] certs = keystore.getCertificateChain(alias);
                if (certs == null || certs.length == 0) {
                    // no cert chain, so lets check if getCertificate gives us a
                    // result.
                    cert = keystore.getCertificate(alias);
                    if (cert == null) {
                        return null;
                    }
                } else {
                    cert = certs[0];
                }
                if (!(cert instanceof X509Certificate)) {
                    continue;
                }
                byte[] data = getSKIBytesFromCert((X509Certificate) cert);
                if (data.length != skiBytes.length) {
                    continue;
                }
                if (Arrays.equals(data, skiBytes)) {
                    return alias;
                }
            }
        } catch (KeyStoreException e) {
            throw new XKMSException(e);
        }
        return null;
    }

    public byte[] getSKIBytesFromCert(X509Certificate cert)
            throws XKMSException {
        /*
         * Gets the DER-encoded OCTET string for the extension value (extnValue)
         * identified by the passed-in oid String. The oid string is represented
         * by a set of positive whole numbers separated by periods.
         */
        byte[] derEncodedValue = cert.getExtensionValue(SKI_OID);

        if (cert.getVersion() < 3 || derEncodedValue == null) {
            PublicKey key = cert.getPublicKey();
            if (!(key instanceof RSAPublicKey)) {
                throw new XKMSException("noSKIHandling");
            }
            byte[] encoded = key.getEncoded();
            // remove 22-byte algorithm ID and header
            byte[] value = new byte[encoded.length - 22];
            System.arraycopy(encoded, 22, value, 0, value.length);
            MessageDigest sha;
            try {
                sha = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException ex) {
                throw new XKMSException("noSKIHandling");
            }
            sha.reset();
            sha.update(value);
            return sha.digest();
        }

        /**
         * Strip away first four bytes from the DerValue (tag and length of
         * ExtensionValue OCTET STRING and KeyIdentifier OCTET STRING)
         */
        byte abyte0[] = new byte[derEncodedValue.length - 4];

        System.arraycopy(derEncodedValue, 4, abyte0, 0, abyte0.length);
        return abyte0;
    }
}
