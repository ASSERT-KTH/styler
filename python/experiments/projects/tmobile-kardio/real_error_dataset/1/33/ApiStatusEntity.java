/*******************************************************************************
 * Copyright 2019 T-Mobile USA, Inc.
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
 * See the LICENSE file for additional language around disclaimer of warranties.
 * Trademark Disclaimer: Neither the name of "T-Mobile, USA" nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 ******************************************************************************/
package com.tmobile.kardio.surveiller.db.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class for api_status
 */
@Entity
@Table(name="api_status")
public class ApiStatusEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
    @Column(name="api_status_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long apiStatusId;
	
	@ManyToOne
	@JoinColumn(name="component_id")
	private ComponentEntity component;
	
	@ManyToOne
	@JoinColumn(name="environment_id")
	private EnvironmentEntity environment;
	
	@Column(name="status_date")
	private Date statusDate;

	@Column(name="total_api")
	private int totalApi;
	
	@Column(name="delta_value")
	private int deltaValue;

	/**
	 * @return the containerStatsId
	 */
	public Long getApiStatusId() {
		return apiStatusId;
	}

	/**
	 * @param containerStatsId the containerStatsId to set
	 */
	public void setApiStatusId(Long apiStatusId) {
		this.apiStatusId = apiStatusId;
	}

	/**
	 * @return the component
	 */
	public ComponentEntity getComponent() {
		return component;
	}

	/**
	 * @param component the component to set
	 */
	public void setComponent(ComponentEntity component) {
		this.component = component;
	}

	/**
	 * @return the environment
	 */
	public EnvironmentEntity getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(EnvironmentEntity environment) {
		this.environment = environment;
	}

	/**
	 * @return the statsDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statsDate the statsDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the totalContainer
	 */
	public int getTotalApi() {
		return totalApi;
	}

	/**
	 * @param totalContainer the totalContainer to set
	 */
	public void setTotalApi(int totalApi) {
		this.totalApi = totalApi;
	}

	/**
	 * @return the deltaValue
	 */
	public int getDeltaValue() {
		return deltaValue;
	}

	/**
	 * @param deltaValue the deltaValue to set
	 */
	public void setDeltaValue(int deltaValue) {
		this.deltaValue = deltaValue;
	}
}
