/*
 * Copyright (c) 2016 Kevin Herron
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

package org.eclipse.milo.opcua.stack.core.security;

import java.security.MessageDigest;
import java.security.Signature;
import javax.crypto.Cipher;
import javax.crypto.Mac;

import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;

public enum SecurityAlgorithm {

    None("", ""),

    /**
     * Symmetric Signature; transformation to be used with {@link Mac#getInstance(String)}.
     */
    HmacSha1("http://www.w3.org/2000/09/xmldsig#hmac-sha1", "HmacSHA1"),

    /**
     * Symmetric Signature; transformation to be used with {@link Mac#getInstance(String)}.
     */
    HmacSha256("http://www.w3.org/2000/09/xmldsig#hmac-sha256", "HmacSHA256"),

    /**
     * Symmetric Encryption; transformation to be used with {@link Cipher#getInstance(String)}.
     */
    Aes128("http://www.w3.org/2001/04/xmlenc#aes128-cbc", "AES/CBC/NoPadding"),

    /**
     * Symmetric Encryption; transformation to be used with {@link Cipher#getInstance(String)}.
     */
    Aes256("http://www.w3.org/2001/04/xmlenc#aes256-cbc", "AES/CBC/NoPadding"),

    /**
     * Asymmetric Signature; transformation to be used with {@link Signature#getInstance(String)}.
     */
    RsaSha1("http://www.w3.org/2000/09/xmldsig#rsa-sha1", "SHA1withRSA"),

    /**
     * Asymmetric Signature; transformation to be used with {@link Signature#getInstance(String)}.
     */
    RsaSha256("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", "SHA256withRSA"),

    /**
     * Asymmetric Encryption; transformation to be used with {@link Cipher#getInstance(String)}.
     */
    Rsa15("http://www.w3.org/2001/04/xmlenc#rsa-1_5", "RSA/ECB/PKCS1Padding"),

    /**
     * Asymmetric Encryption; transformation to be used with {@link Cipher#getInstance(String)}.
     */
    RsaOaep("http://www.w3.org/2001/04/xmlenc#rsa-oaep", "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),

    /**
     * Asymmetric Key Wrap
     */
    KwRsa15("http://www.w3.org/2001/04/xmlenc#rsa-1_5", ""),

    /**
     * Asymmetric Key Wrap
     */
    KwRsaOaep("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p", ""),

    /**
     * Key Derivation
     */
    PSha1("http://docs.oasis-open.org/ws-sx/ws-secureconversation/200512/dk/p_sha1", ""),

    /**
     * Key Derivation
     */
    PSha256("http://docs.oasis-open.org/ws-sx/ws-secureconversation/200512/dk/p_sha256", ""),

    /**
     * Cryptographic Hash; transformation to be used with {@link MessageDigest#getInstance(String)}.
     */
    Sha1("http://www.w3.org/2000/09/xmldsig#sha1", "SHA-1"),

    /**
     * Cryptographic Hash; transformation to be used with {@link MessageDigest#getInstance(String)}.
     */
    Sha256("http://www.w3.org/2001/04/xmlenc#sha256", "SHA-256");

    private final String uri;
    private final String transformation;

    SecurityAlgorithm(String uri, String transformation) {
        this.uri = uri;
        this.transformation = transformation;
    }

    /**
     * @return The URI identifying this security algorithm.
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return The transformation string to use with the appropriate provider SPI.
     */
    public String getTransformation() {
        return transformation;
    }

    public static SecurityAlgorithm fromUri(String securityAlgorithmUri) throws UaException {
        for (SecurityAlgorithm algorithm : values()) {
            if (algorithm.getUri().equals(securityAlgorithmUri)) {
                return algorithm;
            }
        }

        throw new UaException(StatusCodes.Bad_SecurityChecksFailed,
            "unknown securityAlgorithmUri: " + securityAlgorithmUri);
    }

}
