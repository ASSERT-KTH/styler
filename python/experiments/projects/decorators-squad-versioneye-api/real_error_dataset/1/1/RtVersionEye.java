/**
 * Copyright (c) 2017, Mihai Emil Andronache
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.amihaiemil.versioneye;

import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;

/**
 * OOP wrapper for the VersionEye API.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 9b9b01e03feec004837e7b1c106df4de0ea24baa $
 * @since 1.0.0
 *
 */
public final class RtVersionEye implements VersionEye {

    /**
     * HTTP request.
     */
    private Request entry;

    /**
     * Ctor.
     */
    public RtVersionEye() {
        this("");
    }

    /**
     * Ctor.
     * @param token Api token.
     */
    public RtVersionEye(final String token) {
        this.entry = new JdkRequest("https://www.versioneye.com/api/v2")
            .header("Accept", "application/json");
        if(!token.isEmpty()) {
            this.entry = this.entry.header("Cookie", "api_key=" + token);
        }
    }

    @Override
    public Services services() {
        return new RtServices(this.entry);
    }

}
