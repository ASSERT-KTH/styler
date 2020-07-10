package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.AbstractIntegrationTest;
import com.ctrip.framework.apollo.biz.entity.Audit;
import com.ctrip.framework.apollo.biz.entity.Cluster;
import com.ctrip.framework.apollo.biz.entity.Namespace;
import com.ctrip.framework.apollo.biz.repository.AppRepository;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.exception.ServiceException;
import com.ctrip.framework.apollo.core.ConfigConsts;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class AdminServiceTest extends AbstractIntegrationTest{

  @Autowired
  private AdminService adminService;

  @Autowired
  private AuditService auditService;

  @Autowired
  private AppRepository appRepository;

  @Autowired
  private ClusterService clusterService;

  @Autowired
  private NamespaceService namespaceService;

  @Test
  public void testCreateNewApp() {
    String appId = "someAppId";
    App app = new App();
    app.setAppId(appId);
    app.setName("someAppName");
    String owner = "someOwnerName";
    app.setOwnerName(owner);
    app.setOwnerEmail("someOwnerName@ctrip.com");
    app.setDataChangeCreatedBy(owner);
    app.setDataChangeLastModifiedBy(owner);
    app.setDataChangeCreatedTime(new Date());

    app = adminService.createNewApp(app);
    Assert.assertEquals(appId, app.getAppId());

    List<Cluster> clusters = clusterService.findParentClusters(app.getAppId());
    Assert.assertEquals(1, clusters.size());
    Assert.assertEquals(ConfigConsts.CLUSTER_NAME_DEFAULT, clusters.get(0).getName());

    List<Namespace> namespaces = namespaceService.findNamespaces(appId, clusters.get(0).getName());
    Assert.assertEquals(1, namespaces.size());
    Assert.assertEquals(ConfigConsts.NAMESPACE_APPLICATION, namespaces.get(0).getNamespaceName());

    List<Audit> audits = auditService.findByOwner(owner);
    Assert.assertEquals(4, audits.size());
  }

  @Test(expected = ServiceException.class)
  public void testCreateDuplicateApp() {
    String appId = "someAppId";
    App app = new App();
    app.setAppId(appId);
    app.setName("someAppName");
    String owner = "someOwnerName";
    app.setOwnerName(owner);
    app.setOwnerEmail("someOwnerName@ctrip.com");
    app.setDataChangeCreatedBy(owner);
    app.setDataChangeLastModifiedBy(owner);
    app.setDataChangeCreatedTime(new Date());

    appRepository.save(app);

    adminService.createNewApp(app);
  }

}
