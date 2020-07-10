/*
 * Copyright © 2013-2020, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.seed.core.internal.crypto;

import com.google.common.io.BaseEncoding;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicBoolean;
import org.seedstack.coffig.Coffig;
import org.seedstack.coffig.spi.ConfigFunction;
import org.seedstack.coffig.spi.ConfigFunctionHolder;
import org.seedstack.coffig.spi.ConfigurationComponent;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.crypto.CryptoConfig;
import org.seedstack.seed.crypto.EncryptionService;

public class DecryptFunction implements ConfigFunctionHolder {
    private final AtomicBoolean initInProgress = new AtomicBoolean(false);
    private EncryptionServiceFactory encryptionServiceFactory;
    private CryptoConfig.KeyStoreConfig masterKeyStoreConfig;
    private Exception storedException;

    @Override
    public void initialize(Coffig coffig) {
        try {
            initInProgress.set(true);
            coffig.getOptional(CryptoConfig.KeyStoreConfig.class, "crypto.keystores.master").ifPresent(cfg -> {
                try {
                    KeyStore keyStore = new KeyStoreLoader().load(CryptoConfig.MASTER_KEY_STORE_NAME, cfg);
                    encryptionServiceFactory = new EncryptionServiceFactory(keyStore);
                    masterKeyStoreConfig = cfg;
                } catch (Exception e) {
                    storedException = e;
                }
            });
        } finally {
            initInProgress.set(false);
        }
    }

    @Override
    public ConfigurationComponent fork() {
        return new DecryptFunction();
    }

    @ConfigFunction
    String decrypt(String alias, String value) {
        if (initInProgress.get()) {
            // Cannot decrypt anything during initialization phase
            return value;
        } else {
            if (encryptionServiceFactory == null) {
                if (storedException != null) {
                    throw SeedException.wrap(storedException, CryptoErrorCode.MISSING_MASTER_KEYSTORE);
                } else {
                    throw SeedException.createNew(CryptoErrorCode.MISSING_MASTER_KEYSTORE);
                }
            }
            EncryptionService encryptionService = CryptoPlugin.getMasterEncryptionService(encryptionServiceFactory,
                    masterKeyStoreConfig,
                    alias);
            return new String(encryptionService.decrypt(BaseEncoding.base16().decode(value)), StandardCharsets.UTF_8);
        }
    }
}