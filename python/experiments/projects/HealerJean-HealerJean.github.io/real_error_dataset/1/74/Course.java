package com.hsp.domain;

import java.util.Set;

public class Course {
	private Integer id;
	private String name;
	private Set<StuCourse> stuCourses;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<StuCourse> getStuCourses() {
		return stuCourses;
	}
	public void setStuCourses(Set<StuCourse> stuCourses) {
		this.stuCourses = stuCourses;
	}
}
