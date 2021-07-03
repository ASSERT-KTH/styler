/*
 /*
 * oxCore is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2018, Gluu
 */

package org.gluu.persist.key.impl;

import org.gluu.persist.exception.KeyConversionException;
import org.gluu.persist.key.impl.model.ParsedKey;
import org.gluu.util.StringHelper;

/**
 * DN to Generic key convert
 *
 * @author Yuriy Movchan Date: 05/30/2018
 */
public class GenericKeyConverter {

    public ParsedKey convertToKey(String dn) {
        if (StringHelper.isEmpty(dn)) {
            throw new KeyConversionException("Failed to convert empty DN to Key");
        }

        StringBuilder result = new StringBuilder();
        String[] tokens = dn.split(",");

        String orgInum = null;
        String attributeName = null;
        for (String token : tokens) {
            int pos = token.indexOf("=");
            if (pos == -1) {
                throw new KeyConversionException("Failed to convert empty DN to Key");
            }

            String name = token.substring(0, pos);
            if (attributeName == null) {
            	attributeName = name;
            }
            String value = token.substring(pos + 1, token.length());
            if (StringHelper.equalsIgnoreCase(name, "o")) {
                if (!StringHelper.equalsIgnoreCase(value, "gluu")) {
                    orgInum = value;
                }
                continue;
            }

            result.insert(0, "_" + value);
        }

        String key = result.toString();
        if (key.length() == 0) {
            key = "_";
        } else {
            key = key.substring(1);
        }

        return new ParsedKey(key, attributeName, orgInum);
    }

}
