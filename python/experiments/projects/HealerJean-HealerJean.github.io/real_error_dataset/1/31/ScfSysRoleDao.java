/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.system;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.system.ScfSysRole;
import com.hlj.proj.data.pojo.system.ScfSysRoleQuery;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author duyang
 * @ClassName: ScfSysRoleDao
 * @date 2099/1/1
 * @Description: ScfSysRoleDao
 */
@Repository("scfSysRoleDao")
public class ScfSysRoleDao extends BaseDao {

	public int countByExample(ScfSysRoleQuery example) {
		return super.getSqlSession().selectOne("ScfSysRoleMapper.countByExample", example);
	}

	public int countByExampleLike(ScfSysRoleQuery example) {
		return super.getSqlSession().selectOne("ScfSysRoleMapper.countByExampleLike", example);
	}

	public int deleteByExample(ScfSysRoleQuery example) {
		return super.getSqlSession().delete("ScfSysRoleMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfSysRoleMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfSysRole record) {
		return super.getSqlSession().insert("ScfSysRoleMapper.insert", record);
	}

	public int insertSelective(ScfSysRole record) {
		return super.getSqlSession().insert("ScfSysRoleMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfSysRole> list) {
		return super.batchInsert("ScfSysRoleMapper.insertSelective", list);
	}

	public List<ScfSysRole> selectByExample(ScfSysRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRoleMapper.selectByExample", example);
	}

	public List<ScfSysRole> selectPageByExample(ScfSysRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRoleMapper.selectPageByExample", example);
	}

	public List<ScfSysRole> selectByExampleLike(ScfSysRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRoleMapper.selectByExampleLike", example);
	}

	public List<ScfSysRole> selectPageByExampleLike(ScfSysRoleQuery example) {
		return super.getSqlSession().selectList("ScfSysRoleMapper.selectPageByExampleLike", example);
	}

	public ScfSysRole selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfSysRoleMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfSysRole record) {
		return super.getSqlSession().update("ScfSysRoleMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfSysRole record) {
		return super.getSqlSession().update("ScfSysRoleMapper.updateByPrimaryKey", record);
	}

}
