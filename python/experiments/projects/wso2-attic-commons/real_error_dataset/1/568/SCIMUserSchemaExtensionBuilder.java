/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
package org.wso2.charon.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.schema.SCIMAttributeSchema;
import org.wso2.charon.core.schema.SCIMEnterpriseUserSchemaExtension;

public class SCIMUserSchemaExtensionBuilder {

	private static SCIMUserSchemaExtensionBuilder configReader = new SCIMUserSchemaExtensionBuilder();

	public static SCIMUserSchemaExtensionBuilder getInstance() {
		return configReader;
	}

	public void readConfiguration(String configFilePath) throws CharonException {
		File provisioningConfig = new File(configFilePath);
		try {
			InputStream inputStream = new FileInputStream(provisioningConfig);
			JSONArray attributeConfigArray = new JSONArray(inputStream.toString());

			for (int index = 0; index < attributeConfigArray.length(); ++index) {
				JSONObject attributeConfig = attributeConfigArray.getJSONObject(index);
			}

			inputStream.close();
		} catch (FileNotFoundException e) {
			throw new CharonException(SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file not found!");
		} catch (JSONException e) {
			throw new CharonException("Error while parsing " +
			                          SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!");
		} catch (IOException e) {
			throw new CharonException("Error while closing " +
			                          SCIMConfigConstants.SCIM_SCHEMA_EXTENSION_CONFIG + " file!");
		}
	}
	
	public SCIMAttributeSchema getSCIMUserSchemaExtension() {
		return SCIMEnterpriseUserSchemaExtension.ENTERPRISE_EXTENSION;
	}

}
