/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.utils;

import org.ldaptive.*;
import org.ldaptive.pool.BlockingConnectionPool;
import org.ldaptive.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapUtils {

    private final Logger logger = LoggerFactory.getLogger(LdapUtils.class);

    private ConnectionFactory connFactory;

    private ConnectionConfig config = new ConnectionConfig();

    private BlockingConnectionPool pool;

    private String user;

    private String password;

    private boolean ssl;

    private String url;

    public LdapUtils() {
        this.user = System.getProperty("LDAP_USER", "cn=exampleuser,dc=org");
        this.password = System.getProperty("LDAP_PASSWORD", "password");
        this.ssl = System.getProperty("LDAP_SSL", "false").equals("true") ? true : false;
        this.url = (this.ssl == true ? "ldaps://" : "ldap://") + System.getProperty("LDAP_URL", "example.host.com");
    }

    public void connect() {
        this.config.setLdapUrl(this.url);
        this.config.setUseSSL(this.ssl);
        this.config.setConnectionInitializer(new BindConnectionInitializer(user, new Credential(password)));
        this.pool = new BlockingConnectionPool(new DefaultConnectionFactory(this.config));
        if (!this.pool.isInitialized()) {
            this.pool.initialize();
        }
        this.connFactory = new PooledConnectionFactory(this.pool);
    }

    public SearchResult search(SearchRequest request) throws LdapException {
        Connection conn = null;
        try {
            logger.debug("Connecting to LDAP");
            conn = this.connFactory.getConnection();
            SearchOperation search = new SearchOperation(conn);
            Response<SearchResult> response = search.execute(request);
            return response.getResult();
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public void add(LdapEntry entry) throws LdapException {
        Connection conn = null;
        try {
            conn = this.connFactory.getConnection();
            AddOperation add = new AddOperation(conn);
            add.execute(new AddRequest(entry.getDn(), entry.getAttributes()));
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public void modify(String dn, AttributeModification ... modifications) throws LdapException {
        Connection conn = null;
        try {
            conn = this.connFactory.getConnection();
            ModifyOperation modify = new ModifyOperation(conn);
            modify.execute(new ModifyRequest(dn, modifications));
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }

    public void delete(String dn) throws LdapException {
        Connection conn = null;
        try {
            conn = this.connFactory.getConnection();
            DeleteOperation delete = new DeleteOperation(conn);
            delete.execute(new DeleteRequest(dn));
        } catch (LdapException e) {
            throw e;
        } finally {
            conn.close();
        }
    }
}
