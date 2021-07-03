package com.hlj.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Course entity. @author MyEclipse Persistence Tools
 */

public class Course implements java.io.Serializable {

	// Fields

	private Integer cid;
	private String cname;
	private Integer ccredit;
	private Set stucourses = new HashSet(0);

	// Constructors

	/** default constructor */
	public Course() {
	}

	/** minimal constructor */
	public Course(String cname, Integer ccredit) {
		this.cname = cname;
		this.ccredit = ccredit;
	}

	/** full constructor */
	public Course(String cname, Integer ccredit, Set stucourses) {
		this.cname = cname;
		this.ccredit = ccredit;
		this.stucourses = stucourses;
	}

	// Property accessors

	public Integer getCid() {
		return this.cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public String getCname() {
		return this.cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public Integer getCcredit() {
		return this.ccredit;
	}

	public void setCcredit(Integer ccredit) {
		this.ccredit = ccredit;
	}

	public Set getStucourses() {
		return this.stucourses;
	}

	public void setStucourses(Set stucourses) {
		this.stucourses = stucourses;
	}

}