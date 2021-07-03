package com.ctrip.framework.apollo.biz.repository;

import com.ctrip.framework.apollo.biz.entity.InstanceConfig;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstanceConfigRepository extends PagingAndSortingRepository<InstanceConfig, Long> {
  InstanceConfig findByInstanceIdAndConfigAppIdAndConfigNamespaceName(long instanceId, String configAppId, String configNamespaceName);
}
