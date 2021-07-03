package com.ctrip.apollo.biz.service.impl;

import com.ctrip.apollo.biz.entity.Cluster;
import com.ctrip.apollo.biz.entity.ConfigItem;
import com.ctrip.apollo.biz.entity.ReleaseSnapshot;
import com.ctrip.apollo.biz.entity.Version;
import com.ctrip.apollo.biz.repository.ClusterRepository;
import com.ctrip.apollo.biz.repository.ConfigItemRepository;
import com.ctrip.apollo.biz.repository.ReleaseSnapShotRepository;
import com.ctrip.apollo.biz.repository.VersionRepository;
import com.ctrip.apollo.biz.service.AdminConfigService;
import com.ctrip.apollo.biz.utils.ApolloBeanUtils;
import com.ctrip.apollo.core.dto.ClusterDTO;
import com.ctrip.apollo.core.dto.ConfigItemDTO;
import com.ctrip.apollo.core.dto.ReleaseSnapshotDTO;
import com.ctrip.apollo.core.dto.VersionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("adminConfigService")
public class AdminConfigServiceImpl implements AdminConfigService {

  @Autowired
  private VersionRepository versionRepository;
  @Autowired
  private ReleaseSnapShotRepository releaseSnapShotRepository;
  @Autowired
  private ClusterRepository clusterRepository;
  @Autowired
  private ConfigItemRepository configItemRepository;

  @Override
  public List<ReleaseSnapshotDTO> findReleaseSnapshotByReleaseId(long releaseId) {
    if (releaseId <= 0) {
      return Collections.EMPTY_LIST;
    }

    List<ReleaseSnapshot> releaseSnapShots = releaseSnapShotRepository.findByReleaseId(releaseId);

    if (releaseSnapShots == null || releaseSnapShots.size() == 0) {
      return Collections.EMPTY_LIST;
    }

    return ApolloBeanUtils.batchTransform(ReleaseSnapshotDTO.class, releaseSnapShots);
  }


  @Override
  public List<VersionDTO> findVersionsByApp(long appId) {
    if (appId <= 0) {
      return Collections.EMPTY_LIST;
    }

    List<Version> versions = versionRepository.findByAppId(appId);
    if (versions == null || versions.size() == 0) {
      return Collections.EMPTY_LIST;
    }

    return ApolloBeanUtils.batchTransform(VersionDTO.class, versions);
  }

  @Override
  public VersionDTO loadVersionById(long versionId) {
    if (versionId <= 0) {
      return null;
    }
    Version version = versionRepository.findById(versionId);
    if (version == null){
      return null;
    }
    VersionDTO dto = ApolloBeanUtils.transfrom(VersionDTO.class, version);
    return dto;
  }

  @Override
  public List<ClusterDTO> findClustersByApp(long appId) {
    if (appId <= 0) {
      return Collections.EMPTY_LIST;
    }
    List<Cluster> clusters = clusterRepository.findByAppId(appId);
    if (clusters == null || clusters.size() == 0) {
      return Collections.EMPTY_LIST;
    }

    return ApolloBeanUtils.batchTransform(ClusterDTO.class, clusters);
  }

  @Override
  public List<ConfigItemDTO> findConfigItemsByClusters(List<Long> clusterIds) {
    if (clusterIds == null || clusterIds.size() == 0) {
      return Collections.EMPTY_LIST;
    }
    List<ConfigItem> configItems = configItemRepository.findByClusterIdIsIn(clusterIds);
    if (configItems == null || configItems.size() == 0) {
      return Collections.EMPTY_LIST;
    }

    return ApolloBeanUtils.batchTransform(ConfigItemDTO.class, configItems);
  }

}
