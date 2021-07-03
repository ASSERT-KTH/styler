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

package org.opencb.opencga.analysis.sample.qc;

import org.apache.commons.lang3.StringUtils;
import org.opencb.opencga.analysis.individual.qc.IndividualQcUtils;
import org.opencb.opencga.analysis.tools.OpenCgaTool;
import org.opencb.opencga.catalog.exceptions.CatalogException;
import org.opencb.opencga.core.exceptions.ToolException;
import org.opencb.opencga.core.models.common.Enums;
import org.opencb.opencga.core.models.sample.Sample;
import org.opencb.opencga.core.tools.annotations.Tool;
import org.opencb.opencga.core.tools.variant.SampleQcAnalysisExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Tool(id = SampleQcAnalysis.ID, resource = Enums.Resource.SAMPLE, description = SampleQcAnalysis.DESCRIPTION)
public class SampleQcAnalysis extends OpenCgaTool {

    public static final String ID = "sample-qc";
    public static final String DESCRIPTION = "Run quality control (QC) for a given sample. It includes variant stats, FastQC," +
            "samtools/flagstat, picard/CollectHsMetrics and gene coverage stats; and for somatic samples, mutational signature";

    public  static final String VARIANT_STATS_STEP = "variant-stats";
    public  static final String FASTQC_STEP = "fastqc";
    public  static final String HS_METRICS_STEP = "hs-metrics";
    public  static final String FLAG_STATS_STEP = "flag-stats";
    public  static final String GENE_COVERAGE_STEP = "gene-coverage-stats";
    public  static final String MUTATIONAL_SIGNATUR_STEP = "mutational-signature";

    private String studyId;
    private String sampleId;
    private String bamFile;
    private String fastaFile;
    private String baitFile;
    private String targetFile;
    private String variantStatsId;
    private String variantStatsDecription;
    private Map<String, String> variantStatsQuery;
    private String signatureId;
    private Map<String, String> signatureQuery;
    private List<String> genesForCoverageStats;

    private Sample sample;

    @Override
    protected void check() throws Exception {
        super.check();
        setUpStorageEngineExecutor(studyId);

        if (StringUtils.isEmpty(studyId)) {
            throw new ToolException("Missing study ID.");
        }

        try {
            studyId = catalogManager.getStudyManager().get(studyId, null, token).first().getFqn();
        } catch (CatalogException e) {
            throw new ToolException(e);
        }

        // Sanity check
        if (StringUtils.isEmpty(sampleId)) {
            throw new ToolException("Missing sample ID.");
        }

        sample = IndividualQcUtils.getValidSampleById(studyId, sampleId, catalogManager, token);
        if (sample == null) {
            throw new ToolException("Sample '" + sampleId + "' not found.");
        }

    }

    @Override
    protected List<String> getSteps() {
        return Arrays.asList(VARIANT_STATS_STEP, FASTQC_STEP, FLAG_STATS_STEP, HS_METRICS_STEP, GENE_COVERAGE_STEP,
                MUTATIONAL_SIGNATUR_STEP);
    }

    @Override
    protected void run() throws ToolException {

        SampleQcAnalysisExecutor executor = getToolExecutor(SampleQcAnalysisExecutor.class);

        // Set up executor
        executor.setStudyId(studyId)
                .setSample(sample)
                .setBamFile(bamFile)
                .setFastaFile(fastaFile)
                .setBaitFile(baitFile)
                .setTargetFile(targetFile)
                .setVariantStatsId(variantStatsId)
                .setVariantStatsDecription(variantStatsDecription)
                .setVariantStatsQuery(variantStatsQuery)
                .setSignatureId(signatureId)
                .setSignatureQuery(signatureQuery)
                .setGenesForCoverageStats(genesForCoverageStats);

        // Step by step
        step(VARIANT_STATS_STEP, () -> executor.setQc(SampleQcAnalysisExecutor.Qc.VARIAN_STATS).execute());
        step(FASTQC_STEP, () -> executor.setQc(SampleQcAnalysisExecutor.Qc.FASTQC).execute());
        step(FLAG_STATS_STEP, () -> executor.setQc(SampleQcAnalysisExecutor.Qc.FLAG_STATS).execute());
        step(HS_METRICS_STEP, () -> executor.setQc(SampleQcAnalysisExecutor.Qc.HS_METRICS).execute());
        step(GENE_COVERAGE_STEP, () -> executor.setQc(SampleQcAnalysisExecutor.Qc.GENE_COVERAGE_STATS).execute());
        step(MUTATIONAL_SIGNATUR_STEP, () -> executor.setQc(SampleQcAnalysisExecutor.Qc.MUTATIONAL_SIGNATURE).execute());
    }

    public String getStudyId() {
        return studyId;
    }

    public SampleQcAnalysis setStudyId(String studyId) {
        this.studyId = studyId;
        return this;
    }

    public String getSampleId() {
        return sampleId;
    }

    public SampleQcAnalysis setSampleId(String sampleId) {
        this.sampleId = sampleId;
        return this;
    }

    public String getBamFile() {
        return bamFile;
    }

    public SampleQcAnalysis setBamFile(String bamFile) {
        this.bamFile = bamFile;
        return this;
    }

    public String getFastaFile() {
        return fastaFile;
    }

    public SampleQcAnalysis setFastaFile(String fastaFile) {
        this.fastaFile = fastaFile;
        return this;
    }

    public String getBaitFile() {
        return baitFile;
    }

    public SampleQcAnalysis setBaitFile(String baitFile) {
        this.baitFile = baitFile;
        return this;
    }

    public String getTargetFile() {
        return targetFile;
    }

    public SampleQcAnalysis setTargetFile(String targetFile) {
        this.targetFile = targetFile;
        return this;
    }

    public String getVariantStatsId() {
        return variantStatsId;
    }

    public SampleQcAnalysis setVariantStatsId(String variantStatsId) {
        this.variantStatsId = variantStatsId;
        return this;
    }

    public String getVariantStatsDecription() {
        return variantStatsDecription;
    }

    public SampleQcAnalysis setVariantStatsDecription(String variantStatsDecription) {
        this.variantStatsDecription = variantStatsDecription;
        return this;
    }

    public Map<String, String> getVariantStatsQuery() {
        return variantStatsQuery;
    }

    public SampleQcAnalysis setVariantStatsQuery(Map<String, String> variantStatsQuery) {
        this.variantStatsQuery = variantStatsQuery;
        return this;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public SampleQcAnalysis setSignatureId(String signatureId) {
        this.signatureId = signatureId;
        return this;
    }

    public Map<String, String> getSignatureQuery() {
        return signatureQuery;
    }

    public SampleQcAnalysis setSignatureQuery(Map<String, String> signatureQuery) {
        this.signatureQuery = signatureQuery;
        return this;
    }

    public List<String> getGenesForCoverageStats() {
        return genesForCoverageStats;
    }

    public SampleQcAnalysis setGenesForCoverageStats(List<String> genesForCoverageStats) {
        this.genesForCoverageStats = genesForCoverageStats;
        return this;
    }
}
