package com.ctrip.apollo.biz.repository;

import com.ctrip.apollo.biz.BizTestConfiguration;
import com.ctrip.apollo.biz.entity.AppNamespace;
import com.ctrip.apollo.core.ConfigConsts;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = BizTestConfiguration.class)
@Transactional
@Rollback
public class AppNamespaceRepositoryTest {

  @Autowired
  private AppNamespaceRepository repository;

  @Test
  public void testFindAllPublicAppNamespaces(){
    List<AppNamespace> appNamespaceList = repository.findByNameNot(ConfigConsts.NAMESPACE_DEFAULT);
    Assert.assertEquals(4, appNamespaceList.size());
  }

}
