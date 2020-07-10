/*******************************************************************************
 * Copyright 2019 T-Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.kardio.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "k8s_tps_latency_history")
public class K8sTpsLatencyHistoryEntity extends GeneralTpsLatencyHistoryEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	

	@Id
    @Column(name="k8s_tps_latency_history_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long k8sTpsLatencyId;


	/**
	 * @return the k8sTpsLatencyId
	 */
	public long getK8sTpsLatencyId() {
		return k8sTpsLatencyId;
	}


	/**
	 * @param k8sTpsLatencyId the k8sTpsLatencyId to set
	 */
	public void setK8sTpsLatencyId(long k8sTpsLatencyId) {
		this.k8sTpsLatencyId = k8sTpsLatencyId;
	}
	
}
