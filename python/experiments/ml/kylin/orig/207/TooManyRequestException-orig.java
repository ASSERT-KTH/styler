/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.rest.exception;

import org.apache.kylin.rest.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestException extends RuntimeException {

    private static final long serialVersionUID = -6798154278095441848L;

    private String code;

    /**
     * legacy support, new APIs should not call this. Instead, new APIs should provide return code
     */
    public TooManyRequestException(String msg) {
        super(msg);
        this.code = ResponseCode.CODE_UNDEFINED;
    }

    public TooManyRequestException(String msg, String code) {
        super(msg);
        this.code = code;
    }

    public TooManyRequestException(String msg, String code, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }


    public String getCode() {
        return code;
    }

}
