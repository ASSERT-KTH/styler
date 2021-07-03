/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.user;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.user.ScfUserDepartment;
import com.hlj.proj.data.pojo.user.ScfUserRefUserDepartment;
import com.hlj.proj.data.pojo.user.ScfUserRefUserDepartmentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duyang
 * @ClassName: ScfUserRefUserDepartmentDao
 * @date 2099/1/1
 * @Description: ScfUserRefUserDepartmentDao
 */
@Repository("scfUserRefUserDepartmentDao")
public class ScfUserRefUserDepartmentDao extends BaseDao {

	public int countByExample(ScfUserRefUserDepartmentQuery example) {
		return super.getSqlSession().selectOne("ScfUserRefUserDepartmentMapper.countByExample", example);
	}

	public int deleteByExample(ScfUserRefUserDepartmentQuery example) {
		return super.getSqlSession().delete("ScfUserRefUserDepartmentMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfUserRefUserDepartmentMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfUserRefUserDepartment record) {
		return super.getSqlSession().insert("ScfUserRefUserDepartmentMapper.insert", record);
	}

	public int insertSelective(ScfUserRefUserDepartment record) {
		return super.getSqlSession().insert("ScfUserRefUserDepartmentMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfUserRefUserDepartment> list) {
		return super.batchInsert("ScfUserRefUserDepartmentMapper.insertSelective", list);
	}

	public List<ScfUserRefUserDepartment> selectByExample(ScfUserRefUserDepartmentQuery example) {
		return super.getSqlSession().selectList("ScfUserRefUserDepartmentMapper.selectByExample", example);
	}

	public List<ScfUserRefUserDepartment> selectPageByExample(ScfUserRefUserDepartmentQuery example) {
		return super.getSqlSession().selectList("ScfUserRefUserDepartmentMapper.selectPageByExample", example);
	}

	public ScfUserRefUserDepartment selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfUserRefUserDepartmentMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfUserRefUserDepartment record) {
		return super.getSqlSession().update("ScfUserRefUserDepartmentMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfUserRefUserDepartment record) {
		return super.getSqlSession().update("ScfUserRefUserDepartmentMapper.updateByPrimaryKey", record);
	}

	public ScfUserDepartment selectByExampleToUser(ScfUserRefUserDepartmentQuery query) {
		return super.getSqlSession().selectOne("ScfUserRefUserDepartmentMapper.selectByExampleToUser", query);
	}
}
