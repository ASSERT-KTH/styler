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

import java.util.ArrayList;
import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * VersionEye Team backed by a JsonObject.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 57dee6cf5e0841d2a6d1d9f094f0625d4771f605 $
 * @since 1.0.0
 */
final class RtTeam implements Team {

    /**
     * This team as Json.
     */
    private JsonObject team;
    
    /**
     * This team's organization.
     */
    private Organization orga;
    
    /**
     * Original RtVersionEye.
     */
    private RtVersionEye versionEye;

    /**
     * Ctor.
     * @param team Given team.
     * @param orga This team's organization.
     * @param versionEye The original RtVersionEye.
     */
    RtTeam(
        final JsonObject team, final Organization orga,
        final RtVersionEye versionEye
    ) {
        this.team = team;
        this.versionEye = versionEye;
        this.orga = orga;
    }
    
    @Override
    public String teamId() {
        return this.team.getString("ids");
    }

    @Override
    public String name() {
        return this.team.getString("name");
    }

    @Override
    public boolean versionNotifications() {
        return this.team.getBoolean("version_notifications");
    }

    @Override
    public boolean licenseNotifications() {
        return this.team.getBoolean("license_notifications");
    }

    @Override
    public boolean securityNotifications() {
        return this.team.getBoolean("security_notifications");
    }

    @Override
    public boolean monday() {
        return this.team.getBoolean("monday");
    }

    @Override
    public boolean tuesday() {
        return this.team.getBoolean("tuesday");
    }

    @Override
    public boolean wednesday() {
        return this.team.getBoolean("wednesday");
    }

    @Override
    public boolean thursday() {
        return this.team.getBoolean("thursday");
    }

    @Override
    public boolean friday() {
        return this.team.getBoolean("friday");
    }

    @Override
    public boolean saturday() {
        return this.team.getBoolean("saturday");
    }

    @Override
    public boolean sunday() {
        return this.team.getBoolean("sunday");
    }

    @Override
    public List<UserData> users() {
        final JsonArray users = this.team.getJsonArray("users");
        final List<UserData> members = new ArrayList<>();
        for(int idx = 0; idx < users.size(); idx++) {
            members.add(new JsonUserData(users.getJsonObject(idx)));
        }
        return members;
    }

    @Override
    public String createdAt() {
        return this.team.getString("created_at");
    }

    @Override
    public String updatedAt() {
        return this.team.getString("updated_at");
    }

    @Override
    public JsonObject json() {
        return this.team;
    }

    @Override
    public Projects projects() {
        return new RtProjects(this.versionEye, this);
    }

    @Override
    public Organization organization() {
        return this.orga;
    }

    @Override
    public RtVersionEye versionEye() {
        return this.versionEye;
    }

}
