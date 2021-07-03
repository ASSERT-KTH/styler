package de.peass.measurement.rca.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.peass.PeassGlobalInfos;
import de.peass.config.MeasurementConfiguration;
import de.peass.dependency.analysis.data.ChangedEntity;
import de.peass.measurement.analysis.statistics.TestcaseStatistic;
import de.peass.statistics.StatisticUtil;
import de.precision.analysis.repetitions.bimodal.CompareData;

/**
 * Saves the call tree structure and measurement data of the call tree
 * 
 * If the measurements are added call by call, the API is: 1) call setVersions with the versions to compare 2) call newVM with the current version 3) call addMeasurement with all
 * values 4) repeat 2) and 3) until all measurements are read 5) call createStatistics with both versions
 * 
 * @author reichelt
 *
 */
@JsonDeserialize(using = CallTreeNodeDeserializer.class)
public class CallTreeNode extends BasicNode {

   private static final Logger LOG = LogManager.getLogger(CallTreeNode.class);

   @JsonIgnore
   private final CallTreeNode parent;
   protected final List<CallTreeNode> children = new ArrayList<>();
   protected final Map<String, CallTreeStatistics> data = new HashMap<>();

   @JsonIgnore
   protected MeasurementConfiguration config;

   private CallTreeNode otherVersionNode;

   /**
    * Creates a root node
    */
   public CallTreeNode(final String call, final String kiekerPattern, final String otherKiekerPattern, final MeasurementConfiguration config) {
      super(call, kiekerPattern, otherKiekerPattern);
      this.parent = null;
      this.config = config;
   }

   protected CallTreeNode(final String call, final String kiekerPattern, final String otherKiekerPattern, final CallTreeNode parent) {
      super(call, kiekerPattern, otherKiekerPattern);
      this.parent = parent;
      this.config = parent.config;
   }

   public void setConfig(final MeasurementConfiguration config) {
      this.config = config;
   }
   
   @Override
   public List<CallTreeNode> getChildren() {
      return children;
   }

   public CallTreeNode appendChild(final String call, final String kiekerPattern, final String otherKiekerPattern) {
      final CallTreeNode added = new CallTreeNode(call, kiekerPattern, otherKiekerPattern, this);
      children.add(added);
      return added;
   }

   public CallTreeNode getParent() {
      return parent;
   }

   public void addMeasurement(final String version, final Long duration) {
      checkDataAddPossible(version);
      LOG.debug("Adding measurement: {}", version);
      data.get(version).addMeasurement(duration);
   }

   /**
    * Adds the measurement of *one full VM* to the measurements of the version
    * 
    * @param version
    * @param statistic
    */
   public void addAggregatedMeasurement(final String version, final List<StatisticalSummary> statistic) {
      checkDataAddPossible(version);
      removeWarmup(statistic);
      data.get(version).addAggregatedMeasurement(statistic);
   }

   private void removeWarmup(final List<StatisticalSummary> statistic) {
      if (config.getNodeWarmup() > 0) {
         int remainingWarmup = config.getNodeWarmup();
         StatisticalSummary borderSummary = null;
         for (Iterator<StatisticalSummary> it = statistic.iterator(); it.hasNext();) {
            StatisticalSummary chunk = it.next();
            long countOfExecutions = chunk.getN() * config.getRepetitions();
            if (remainingWarmup - countOfExecutions > 0) {
               remainingWarmup -= countOfExecutions;
               LOG.debug("Reducing warmup by {}, remaining warmup {}", countOfExecutions, remainingWarmup);
               it.remove();
            } else {
               if (remainingWarmup > 0) {
                  final long reducedN = countOfExecutions - remainingWarmup;
                  borderSummary = new StatisticalSummaryValues(chunk.getMean(), chunk.getVariance(), reducedN,
                        chunk.getMax(), chunk.getMin(), chunk.getMean() * reducedN);
               } else {
                  // Since there is no warmup remaining, the first summary is just removed and added later on
                  borderSummary = chunk;
               }
               it.remove();
               break;
            }
         }
         if (borderSummary != null) {
            statistic.add(0, borderSummary);
         } else {
            LOG.warn("Warning! Reading aggregated data which contain less executions than the warmup " + config.getNodeWarmup());
         }
         for (StatisticalSummary summary : statistic) {
            LOG.trace("After removing: {} {} Sum: {}", summary.getMean(), summary.getN(), summary.getSum());
         }
         LOG.trace("Overall mean: {}", StatisticUtil.getMean(statistic));
      }
   }

   private void checkDataAddPossible(final String version) {
      if (PeassGlobalInfos.isTwoVersionRun) {
         if (otherVersionNode == null) {
            throw new RuntimeException("Other version node needs to be defined before measurement! Node: " + call);
         }
         if (otherVersionNode.getCall().equals(CauseSearchData.ADDED) && version.equals(config.getVersion())) {
            throw new RuntimeException("Added methods may not contain data");
         }
      }
      if (call.equals(CauseSearchData.ADDED) && version.equals(config.getVersionOld())) {
         throw new RuntimeException("Added methods may not contain data, trying to add data for " + version);
      }

   }

   public boolean hasMeasurement(final String version) {
      return data.get(version).getResults().size() > 0;
   }

   public List<OneVMResult> getResults(final String version) {
      final CallTreeStatistics statistics = data.get(version);
      return statistics != null ? statistics.getResults() : null;
   }

   public void newVM(final String version) {
      final CallTreeStatistics statistics = data.get(version);
      LOG.debug("Adding VM: {} {} VMs: {}", call, version, statistics.getResults().size());
      statistics.newResult();
   }

   private void newVersion(final String version) {
      LOG.debug("Adding version: {}", version);
      CallTreeStatistics statistics = data.get(version);
      if (statistics == null) {
         statistics = new CallTreeStatistics(config.getNodeWarmup());
         data.put(version, statistics);
      }
   }
   
   public CompareData getComparableStatistics(final String versionOld, final String version) {
      List<OneVMResult> before = data.get(versionOld) != null ? data.get(versionOld).getResults() :null;
      List<OneVMResult> after = data.get(version) != null ? data.get(version).getResults() : null;
      
      CompareData cd = CompareData.createCompareDataFromOneVMResults(before, after);
      return cd;
   }

   public SummaryStatistics getStatistics(final String version) {
      LOG.trace("Getting data: {}", version);
      final CallTreeStatistics statistics = data.get(version);
      return statistics != null ? statistics.getStatistics() : null;
   }

   public void createStatistics(final String version) {
      LOG.debug("Creating statistics: {} Call: {}", version, call);
      final CallTreeStatistics callTreeStatistics = data.get(version);
      callTreeStatistics.createStatistics();
      LOG.debug("Mean: " + callTreeStatistics.getStatistics().getMean() + " " + callTreeStatistics.getStatistics().getStandardDeviation());
   }

   @Override
   public String toString() {
      return kiekerPattern.toString();
   }

   public ChangedEntity toEntity() {
      if (call.equals(CauseSearchData.ADDED)) {
         return otherVersionNode.toEntity();
      } else {
         final int index = call.lastIndexOf(ChangedEntity.METHOD_SEPARATOR);
         final ChangedEntity entity = new ChangedEntity(call.substring(0, index), "", call.substring(index + 1));
         return entity;
      }
   }

   @JsonIgnore
   public TestcaseStatistic getTestcaseStatistic() {
      LOG.debug("Creating statistics for {} {} Keys: {}", config.getVersion(), config.getVersionOld(), data.keySet());
      final CallTreeStatistics currentVersionStatistics = data.get(config.getVersion());
      final SummaryStatistics current = currentVersionStatistics.getStatistics();
      final CallTreeStatistics previousVersionStatistics = data.get(config.getVersionOld());
      final SummaryStatistics previous = previousVersionStatistics.getStatistics();
      try {
         final TestcaseStatistic testcaseStatistic = new TestcaseStatistic(previous, current,
               previousVersionStatistics.getCalls(), currentVersionStatistics.getCalls());
         return testcaseStatistic;
      } catch (NumberIsTooSmallException t) {
         LOG.debug("Data: " + current.getN() + " " + previous.getN());
         final String otherCall = otherVersionNode != null ? otherVersionNode.getCall() : "Not Existing";
         throw new RuntimeException("Could not read " + call + " Other Version: " + otherCall, t);
      }
   }

   @JsonIgnore
   public TestcaseStatistic getPartialTestcaseStatistic() {
      final CallTreeStatistics currentVersionStatistics = data.get(config.getVersion());
      final SummaryStatistics current = currentVersionStatistics.getStatistics();
      final CallTreeStatistics previousVersionStatistics = data.get(config.getVersionOld());
      final SummaryStatistics previous = previousVersionStatistics.getStatistics();

      if (firstHasValues(current, previous)) {
         final TestcaseStatistic testcaseStatistic = new TestcaseStatistic(previous, current, 0, currentVersionStatistics.getCalls());
         testcaseStatistic.setChange(true);
         return testcaseStatistic;
      } else if (firstHasValues(previous, current)) {
         final TestcaseStatistic testcaseStatistic = new TestcaseStatistic(previous, current, previousVersionStatistics.getCalls(), 0);
         testcaseStatistic.setChange(true);
         return testcaseStatistic;
      } else if ((current == null || current.getN() == 0) && (previous == null || previous.getN() == 0)) {
         LOG.error("Could not measure {}", this);
         final TestcaseStatistic testcaseStatistic = new TestcaseStatistic(previous, current, 0, 0);
         testcaseStatistic.setChange(true);
         return testcaseStatistic;
      } else {
         throw new RuntimeException("Partial statistics should exactly be created if one node is unmeasurable");
      }
   }

   private boolean firstHasValues(final SummaryStatistics first, final SummaryStatistics second) {
      return (second == null || second.getN() == 0) && (first != null && first.getN() > 0);
   }

   /**
    * @deprecated use initVersions instead, and asure that the MeasurementConfig already has the correct versions
    */
   @Deprecated
   @JsonIgnore
   public void setVersions(final String version, final String predecessor) {
      LOG.debug("Set versions: {}", version, predecessor);
      config.setVersion(version);
      config.setVersionOld(predecessor);
      resetStatistics();
      newVersion(version);
      newVersion(predecessor);
   }
   
   public void initVersions() {
      LOG.debug("Init versions: {}", config.getVersion(), config.getVersionOld());
      resetStatistics();
      newVersion(config.getVersionOld());
      newVersion(config.getVersion());
   }

   @JsonIgnore
   public int getTreeSize() {
      int size = 1;
      for (final CallTreeNode child : children) {
         size += child.getTreeSize();
      }
      return size;
   }

   protected void resetStatistics() {
      data.values().forEach(statistics -> statistics.resetResults());
   }

   @JsonIgnore
   public CallTreeNode getOtherVersionNode() {
      return otherVersionNode;
   }

   public void setOtherVersionNode(final CallTreeNode otherVersionNode) {
      this.otherVersionNode = otherVersionNode;
   }

   @JsonIgnore
   public String getMethod() {
      final String method = call.substring(call.lastIndexOf('#'));
      return method;
   }

   @JsonIgnore
   public String getParameters() {
      final String parameters = kiekerPattern.substring(kiekerPattern.indexOf('('));
      return parameters;
   }

   @JsonIgnore
   public int getEss() {
      return parent != null ? parent.getEss() + 1 : 0;
   }

   @JsonIgnore
   public int getEoi() {
      int eoi;
      if (parent != null) {
         int predecessorIndex = parent.getChildren().indexOf(this) - 1;
         if (predecessorIndex >= 0) {
            CallTreeNode predecessor = parent.getChildren().get(predecessorIndex);
            eoi = predecessor.getEoi() + predecessor.getAllChildCount() + 1;
         } else {
            eoi = parent.getEoi() + 1;
         }
      } else {
         eoi = 0;
      }
      return eoi;
   }

   private int getAllChildCount() {
      int childs = 0;
      for (CallTreeNode child : children) {
         childs += child.getAllChildCount() + 1;
      }
      return childs;
   }

   @JsonIgnore
   public int getPosition() {
      if (parent != null) {
         for (int childIndex = 0; childIndex < parent.getChildren().size(); childIndex++) {
            if (parent.getChildren().get(childIndex) == this) {
               return childIndex;
            }
         }
         return -1;
      } else {
         return 0;
      }
   }

   public long getCallCount(final String version) {
      return data.get(version).getResults().stream().mapToLong(result -> result.getCalls()).sum();
   }

   @Override
   public int hashCode() {
      return kiekerPattern.hashCode();
   }

   @Override
   public boolean equals(final Object obj) {
      if (obj instanceof CallTreeNode) {
         final CallTreeNode other = (CallTreeNode) obj;
         boolean equal = other.getKiekerPattern().equals(kiekerPattern);
         if (equal) {
            if ((this.parent == null) != (other.parent == null)) {
               equal = false;
            } else if (parent != null) {
               equal &= this.parent.equals(other.parent);
               equal &= (this.getPosition() == other.getPosition());
            }
         }
         return equal;
      } else {
         return false;
      }
   }

}