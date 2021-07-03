/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.system;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.system.ScfSysRefUserRole;
import com.hlj.proj.data.pojo.system.ScfSysRefUserRoleQuery;
import com.hlj.proj.data.pojo.system.ScfSysRole;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author duyang
 * @ClassName: ScfSysRefUserRoleDao
 * @date 2099/1/1
 * @Description: ScfSysRefUserRoleDao
 */
@Repository("scfSysRefUserRoleDao")
public class ScfSysRefUserRoleDao extends BaseDao {

	public int countByExample(ScfSysRefUserRoleQuery example) {
		return super.getSqlSession().selectOne("ScfSysRefUserRoleMapper.countByExample", example);
	}

	public int deleteByExample(ScfSysRefUserRoleQuery example) {
		return super.getSqlSession().delete("ScfSysRefUserRoleMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfSysRefUserRoleMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfSysRefUserRole record) {
		return super.getSqlSession().insert("ScfSysRefUserRoleMapper.insert", record);
	}

	public int insertSelective(ScfSysRefUserRole record) {
		return super.getSqlSession().insert("ScfSysRefUserRoleMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfSysRefUserRole> list) {
		return super.batchInsert("ScfSysRefUserRoleMapper.insertSelective", list);
	}

	public List<ScfSysRefUserRole> selectByExample(ScfSysRefUserRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRefUserRoleMapper.selectByExample", example);
	}

	public List<ScfSysRefUserRole> selectPageByExample(ScfSysRefUserRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRefUserRoleMapper.selectPageByExample", example);
	}

	public ScfSysRefUserRole selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfSysRefUserRoleMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfSysRefUserRole record) {
		return super.getSqlSession().update("ScfSysRefUserRoleMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfSysRefUserRole record) {
		return super.getSqlSession().update("ScfSysRefUserRoleMapper.updateByPrimaryKey", record);
	}

	public List<ScfSysRole> queryListToRole(ScfSysRefUserRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRefUserRoleMapper.selectByExampleToRole", example);
	}
}
