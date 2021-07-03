package com.ctrip.apollo.biz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Group extends BaseEntity{

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false)
  private long clusterId;

  @Column(nullable = false)
  private long namespaceId;

  public long getClusterId() {
    return clusterId;
  }

  public long getId() {
    return id;
  }

  public long getNamespaceId() {
    return namespaceId;
  }

  public void setClusterId(long clusterId) {
    this.clusterId = clusterId;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setNamespaceId(long namespaceId) {
    this.namespaceId = namespaceId;
  }

}
