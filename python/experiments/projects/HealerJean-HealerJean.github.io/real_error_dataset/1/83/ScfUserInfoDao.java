/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.user;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.user.ScfUserInfo;
import com.hlj.proj.data.pojo.user.ScfUserInfoQuery;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author duyang
 * @ClassName: ScfUserInfoDao
 * @date 2099/1/1
 * @Description: ScfUserInfoDao
 */
@Repository("scfUserInfoDao")
public class ScfUserInfoDao extends BaseDao {

	public int countByExample(ScfUserInfoQuery example) {
		return super.getSqlSession().selectOne("ScfUserInfoMapper.countByExample", example);
	}

	public int deleteByExample(ScfUserInfoQuery example) {
		return super.getSqlSession().delete("ScfUserInfoMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfUserInfoMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfUserInfo record) {
		return super.getSqlSession().insert("ScfUserInfoMapper.insert", record);
	}

	public int insertSelective(ScfUserInfo record) {
		return super.getSqlSession().insert("ScfUserInfoMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfUserInfo> list) {
		return super.batchInsert("ScfUserInfoMapper.insertSelective", list);
	}

	public int batchUpdate(List<ScfUserInfo> list) {
		return super.batchUpdate("ScfUserInfoMapper.updateByPrimaryKeySelective", list);
	}

	public List<ScfUserInfo> selectByExample(ScfUserInfoQuery example) {
		return super.getSqlSession().selectList("ScfUserInfoMapper.selectByExample", example);
	}

	public List<ScfUserInfo> selectPageByExample(ScfUserInfoQuery example) {
		return super.getSqlSession().selectList("ScfUserInfoMapper.selectPageByExample", example);
	}

	public ScfUserInfo selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfUserInfoMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfUserInfo record) {
		return super.getSqlSession().update("ScfUserInfoMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfUserInfo record) {
		return super.getSqlSession().update("ScfUserInfoMapper.updateByPrimaryKey", record);
	}


	/**
	 * 获取部门用户列表
	 */
	public int countUserByDepartment(ScfUserInfoQuery example) {
		return super.getSqlSession().selectOne("ScfUserInfoMapper.countUserByDepartment", example);
	}
	public List<ScfUserInfo> selectUserByDepartment(ScfUserInfoQuery example) {
		return super.getSqlSession().selectList("ScfUserInfoMapper.selectUserByDepartment", example);
	}



}
