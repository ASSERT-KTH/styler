package de.peass.measurement.rca.treeanalysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.peass.dependency.execution.MeasurementConfiguration;
import de.peass.measurement.analysis.Relation;
import de.peass.measurement.analysis.StatisticUtil;
import de.peass.measurement.rca.CauseSearcherConfig;
import de.peass.measurement.rca.data.CallTreeNode;

public abstract class DifferentNodeDeterminer {

   private static final Logger LOG = LogManager.getLogger(DifferentNodeDeterminer.class);

   protected List<CallTreeNode> measurePredecessor = new LinkedList<>();

   protected final List<CallTreeNode> measureNextlevelPredecessor = new LinkedList<>();
   protected final List<CallTreeNode> measureNextLevel = new LinkedList<>();

   protected final List<CallTreeNode> currentLevelDifferent = new LinkedList<>();

   protected final CauseSearcherConfig causeSearchConfig;
   protected final MeasurementConfiguration measurementConfig;

   public DifferentNodeDeterminer(final CauseSearcherConfig causeSearchConfig, final MeasurementConfiguration measurementConfig) {
      this.causeSearchConfig = causeSearchConfig;
      this.measurementConfig = measurementConfig;
   }

   public void calculateDiffering() {
      final Iterator<CallTreeNode> predecessorIterator = measurePredecessor.iterator();
      // final Iterator<CallTreeNode> currentIterator = needToMeasureCurrent.iterator();
      for (; predecessorIterator.hasNext();) {
         final CallTreeNode currentPredecessorNode = predecessorIterator.next();
         // final CallTreeNode currentVersionNode = currentIterator.next();
         final SummaryStatistics statisticsPredecessor = currentPredecessorNode.getStatistics(measurementConfig.getVersionOld());
         final SummaryStatistics statisticsVersion = currentPredecessorNode.getStatistics(measurementConfig.getVersion());
         calculateNodeDifference(currentPredecessorNode, statisticsPredecessor, statisticsVersion);
      }
   }

   private void calculateNodeDifference(final CallTreeNode currentPredecessorNode, final SummaryStatistics statisticsPredecessor, final SummaryStatistics statisticsVersion) {
      if (statisticsPredecessor == null || statisticsVersion == null) {
         LOG.debug("Statistics is null, is different: {} vs {}", statisticsPredecessor, statisticsVersion);
         currentLevelDifferent.add(currentPredecessorNode);
      } else {
         printComparisonInfos(currentPredecessorNode, statisticsPredecessor, statisticsVersion);
         if (statisticsPredecessor.getN() > 0 && statisticsVersion.getN() > 0) {
            final Relation relation = StatisticUtil.agnosticTTest(statisticsPredecessor, statisticsVersion, measurementConfig);
            LOG.debug("Relation: {}", relation);
            if (relation == Relation.UNEQUAL && needsEnoughTime(statisticsPredecessor, statisticsVersion)) {
               addChildsToMeasurement(currentPredecessorNode, statisticsPredecessor, statisticsVersion);
            } else {
               LOG.info("No remeasurement");
            }
         }
      }
   }

   private void printComparisonInfos(final CallTreeNode currentPredecessorNode, final SummaryStatistics statisticsPredecessor, final SummaryStatistics statisticsVersion) {
      LOG.debug("Comparison {} - {}",
            currentPredecessorNode.getKiekerPattern(),
            currentPredecessorNode.getOtherVersionNode() != null ? currentPredecessorNode.getOtherVersionNode().getKiekerPattern() : null);
      LOG.debug("Predecessor: {} {} Current: {} {} ",
            statisticsPredecessor.getMean(), statisticsPredecessor.getStandardDeviation(),
            statisticsVersion.getMean(), statisticsVersion.getStandardDeviation());
   }

   private void addChildsToMeasurement(final CallTreeNode currentPredecessorNode, final SummaryStatistics statisticsPredecessor, final SummaryStatistics statisticsVersion) {
      measureNextlevelPredecessor.addAll(currentPredecessorNode.getChildren());
      final List<CallTreeNode> currentNodes = buildCurrentDiffering(currentPredecessorNode);
      measureNextLevel.addAll(currentNodes);

      final int childsRemeasure = getRemeasureChilds(currentPredecessorNode);

      if (childsRemeasure == 0) {
         LOG.debug("Adding {} - no childs needs to be remeasured, T={}", currentPredecessorNode, childsRemeasure,
               TestUtils.homoscedasticT(statisticsPredecessor, statisticsVersion));
         LOG.debug("Childs: {}", currentPredecessorNode.getChildren());
         currentLevelDifferent.add(currentPredecessorNode);
      }
   }

   private boolean needsEnoughTime(final SummaryStatistics statisticsPredecessor, final SummaryStatistics statisticsVersion) {
      double relativeDifference = Math.abs(statisticsPredecessor.getMean() - statisticsVersion.getMean()) / statisticsVersion.getMean();
      double relativeDeviationPredecessor = statisticsPredecessor.getStandardDeviation() / statisticsPredecessor.getMean();
      double relativeDeviationVersion = statisticsVersion.getStandardDeviation() / statisticsVersion.getMean();
      double relativeStandardDeviation = Math.sqrt((Math.pow(relativeDeviationPredecessor, 2) +
            Math.pow(relativeDeviationVersion, 2)) / 2);
      return relativeDifference > causeSearchConfig.getMinTime() * relativeStandardDeviation;
   }

   private List<CallTreeNode> buildCurrentDiffering(final CallTreeNode currentPredecessorNode) {
      final List<CallTreeNode> currentNodes = new LinkedList<>();
      currentPredecessorNode.getChildren().forEach(node -> currentNodes.add(node.getOtherVersionNode()));
      return currentNodes;
   }

   private int getRemeasureChilds(final CallTreeNode predecessorNode) {
      int childsRemeasure = 0;
      LOG.debug("Children: {}", predecessorNode.getChildren().size());
      for (final CallTreeNode testChild : predecessorNode.getChildren()) {
         LOG.debug("Child: {} Parent: {}", testChild, measureNextlevelPredecessor);
         if (measureNextlevelPredecessor.contains(testChild)) {
            childsRemeasure++;
         }
      }
      return childsRemeasure;
   }

   public List<CallTreeNode> getCurrentLevelDifferent() {
      return currentLevelDifferent;
   }
}