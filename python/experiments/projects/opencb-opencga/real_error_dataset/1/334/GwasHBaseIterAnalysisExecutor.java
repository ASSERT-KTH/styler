/*
 * Copyright 2015-2017 OpenCB
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

package org.opencb.opencga.analysis.variant.gwas;

import org.opencb.commons.datastore.core.ObjectMap;
import org.opencb.oskar.analysis.AnalysisResult;
import org.opencb.oskar.analysis.variant.gwas.AbstractGwasExecutor;
import org.opencb.oskar.analysis.variant.gwas.GwasConfiguration;
import org.opencb.oskar.core.annotations.AnalysisExecutor;
import org.opencb.oskar.core.annotations.AnalysisExecutorRequirement;

import java.nio.file.Path;
import java.util.List;

import static org.opencb.oskar.core.annotations.AnalysisExecutorRequirement.AnalysisRequirementFramework;
import static org.opencb.oskar.core.annotations.AnalysisExecutorRequirement.AnalysisRequirementSource;

@AnalysisExecutor(id = "HBaseIter", analysis = "GWAS")
@AnalysisExecutorRequirement(source = AnalysisRequirementSource.HBASE, framework = AnalysisRequirementFramework.ITERATOR)
public class GwasHBaseIterAnalysisExecutor extends AbstractGwasExecutor {

    public GwasHBaseIterAnalysisExecutor() {
    }

    public GwasHBaseIterAnalysisExecutor(List<String> list1, List<String> list2, ObjectMap params, Path outDir, GwasConfiguration configuration) {
        super(list1, list2, params, outDir, configuration);
    }


    @Override
    public AnalysisResult exec() {
        System.out.println("This class must be moved to opencga-storage-hadoop");
        return null;
    }
}
