package com.ctrip.framework.apollo.portal.entity.vo;

import java.util.LinkedList;
import java.util.List;

public class ReleaseCompareResult {

  private List<EntityPair<KVEntity>> changes = new LinkedList<>();

  public void addEntityPair(KVEntity firstEntity, KVEntity secondEntity){
    changes.add(new EntityPair<>(firstEntity, secondEntity));
  }

  public List<EntityPair<KVEntity>> getChanges() {
    return changes;
  }

  public void setChanges(
      List<EntityPair<KVEntity>> changes) {
    this.changes = changes;
  }
}
