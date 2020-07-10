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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import com.jcabi.http.mock.MkAnswer;
import com.jcabi.http.mock.MkContainer;
import com.jcabi.http.mock.MkGrizzlyContainer;
import com.jcabi.http.request.JdkRequest;

/**
 * Unit tests for {@link RtProjects}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 4cf2ad13bf0a23fe804b755ed435f30b4ee33e61 $
 * @since 1.0.0
 *
 */
@SuppressWarnings("resource")
public final class RtProjectsTestCase {
    
    /**
     * RtProjects can fetch a team's projects.
     * @throws IOException If something goes wrong with the HTTP call.
     */
    @Test
    public void fetchesTeamsProjects() throws IOException {
        final MkContainer container = new MkGrizzlyContainer().next(
            new MkAnswer.Simple(
                HttpURLConnection.HTTP_OK,
                this.readResource("projects.json")
            )
        ).start();

        final RtVersionEye versionEye =
            new RtVersionEye(new JdkRequest(container.home()));
        
        final Organization fakeOrga = Mockito.mock(Organization.class);
        Mockito.when(fakeOrga.name()).thenReturn("amihaiemil_orga");
        Mockito.when(fakeOrga.apiKey()).thenReturn("123orgakey");

        final Team fakeTeam = Mockito.mock(Team.class);
        Mockito.when(fakeTeam.name()).thenReturn("FakeTeam");
        Mockito.when(fakeTeam.organization()).thenReturn(fakeOrga);
        
        final Projects projects = new RtProjects(
            versionEye, fakeTeam
        );
        final List<Project> fetched = projects.fetch();
        MatcherAssert.assertThat(fetched.size(), Matchers.greaterThan(0));
        for(int idx = 0; idx < fetched.size(); idx++) {
            MatcherAssert.assertThat(
                fetched.get(idx).organization().name(),
                Matchers.equalTo("amihaiemil_orga")
            );
            MatcherAssert.assertThat(
                fetched.get(idx).team().name(),
                Matchers.equalTo(fakeTeam.name())
            );
        }
    }
    
    /**
     * RtProjects knows to which team the projects belong.
     * @throws IOException If something goes wrong with the HTTP call.
     */
    @Test
    public void knowsTeam() throws IOException {
        final Organization fakeOrga = Mockito.mock(Organization.class);
        Mockito.when(fakeOrga.name()).thenReturn("amihaiemil_orga");
        Mockito.when(fakeOrga.apiKey()).thenReturn("123orgakey");

        final Team fakeTeam = Mockito.mock(Team.class);
        Mockito.when(fakeTeam.name()).thenReturn("FakeTeam");
        Mockito.when(fakeTeam.organization()).thenReturn(fakeOrga);
        
        final Projects projects = new RtProjects(
            new RtVersionEye(), fakeTeam
        );
        MatcherAssert.assertThat(
            projects.team().name(),
            Matchers.equalTo(fakeTeam.name())
        );
    }
    
    /**
     * Read resource for test.
     * @param resourceName Name of the file being read.
     * @return String content of the resource file.
     * @throws IOException If it goes wrong.
     */
    private String readResource(final String resourceName) throws IOException {
        final InputStream stream = new FileInputStream(
            new File("src/test/resources/" + resourceName)
        );
        return new String(IOUtils.toByteArray(stream));
    }
}
