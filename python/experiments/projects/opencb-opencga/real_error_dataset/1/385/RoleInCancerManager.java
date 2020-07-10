/*
 * Copyright 2015-2020 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.analysis.clinical;

import org.opencb.biodata.models.clinical.ClinicalProperty;
import org.opencb.commons.utils.FileUtils;
import org.opencb.commons.utils.URLUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.opencb.commons.utils.FileUtils.newBufferedReader;

public class RoleInCancerManager {
    private final String ROLE_IN_CANCER_URL = "http://resources.opencb.org/opencb/opencga/analysis/cancer-gene-census/cancer-gene-census.tsv";
    private static Map<String, ClinicalProperty.RoleInCancer> roleInCancer = null;

    public RoleInCancerManager() {
    }

    public Map<String, ClinicalProperty.RoleInCancer> getRoleInCancer() throws IOException {
        // Lazy loading
        if (roleInCancer == null) {
            roleInCancer = loadRoleInCancer();
        }
        return roleInCancer;
    }

    private Map<String, ClinicalProperty.RoleInCancer> loadRoleInCancer() throws IOException {

        // Donwload 'role in cancer' file
        File roleFile = URLUtils.download(new URL(ROLE_IN_CANCER_URL), Paths.get("/tmp"));

//        System.out.println("---------- RoleInCancer: roleFile = " + roleFile);

        try {
            FileUtils.checkFile(roleFile.toPath());
        } catch (Exception e) {
            return null;
        }

        Map<String, ClinicalProperty.RoleInCancer> roleInCancer = new HashMap<>();

        BufferedReader bufferedReader = newBufferedReader(roleFile.toPath());
        List<String> lines = bufferedReader.lines().collect(Collectors.toList());
        Set<ClinicalProperty.RoleInCancer> set = new HashSet<>();

        boolean first = true;
        for (String line : lines) {
            if (first) {
                first = false;
                continue;
            }

            set.clear();
            String[] split = line.split("\t");
            // Sanity check
            if (split.length > 1) {
                String[] roles = split[14].replace("\"", "").split(",");
                for (String role : roles) {
                    switch (role.trim().toLowerCase()) {
                        case "oncogene":
                            set.add(ClinicalProperty.RoleInCancer.ONCOGENE);
                            break;
                        case "tsg":
                            set.add(ClinicalProperty.RoleInCancer.TUMOR_SUPPRESSOR_GENE);
                            break;
                        default:
                            break;
                    }
                }
            }

            // Update set
            if (set.size() > 0) {
                if (set.size() == 2) {
                    roleInCancer.put(split[0], ClinicalProperty.RoleInCancer.BOTH);
                } else {
                    roleInCancer.put(split[0], set.iterator().next());
                }
            }
        }

//        System.out.println("---------- RoleInCancer: size = " + roleInCancer.size());

        // Delete 'role in cancer' file
        roleFile.delete();

        return roleInCancer;
    }
}
