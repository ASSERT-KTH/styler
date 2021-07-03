/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.biz.entity;

import com.ctrip.framework.apollo.common.entity.BaseEntity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Privilege")
@SQLDelete(sql = "Update Privilege set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Privilege extends BaseEntity {

  @Column(name = "Name", nullable = false)
  private String name;

  @Column(name = "PrivilType", nullable = false)
  private String privilType;

  @Column(name = "NamespaceId")
  private long namespaceId;

  public String getName() {
    return name;
  }

  public long getNamespaceId() {
    return namespaceId;
  }

  public String getPrivilType() {
    return privilType;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNamespaceId(long namespaceId) {
    this.namespaceId = namespaceId;
  }

  public void setPrivilType(String privilType) {
    this.privilType = privilType;
  }

  public String toString() {
    return toStringHelper().add("namespaceId", namespaceId).add("privilType", privilType)
        .add("name", name).toString();
  }
}
