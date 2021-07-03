/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.flow;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.flow.ScfFlowRecord;
import com.hlj.proj.data.pojo.flow.ScfFlowRecordQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("scfFlowRecordDao")
public class ScfFlowRecordDao extends BaseDao {

	public int countByExample(ScfFlowRecordQuery example) {
		return super.getSqlSession().selectOne("ScfFlowRecordMapper.countByExample", example);
	}

	public int deleteByExample(ScfFlowRecordQuery example) {
		return super.getSqlSession().delete("ScfFlowRecordMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfFlowRecordMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfFlowRecord record) {
		return super.getSqlSession().insert("ScfFlowRecordMapper.insert", record);
	}

	public int insertSelective(ScfFlowRecord record) {
		return super.getSqlSession().insert("ScfFlowRecordMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfFlowRecord> list) {
		return super.batchInsert("ScfFlowRecordMapper.insertSelective", list);
	}

	public List<ScfFlowRecord> selectByExample(ScfFlowRecordQuery example) {
		return super.getSqlSession().selectList("ScfFlowRecordMapper.selectByExample", example);
	}

	public List<ScfFlowRecord> selectPageByExample(ScfFlowRecordQuery example) {
		return super.getSqlSession().selectList("ScfFlowRecordMapper.selectPageByExample", example);
	}

	public ScfFlowRecord selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfFlowRecordMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfFlowRecord record) {
		return super.getSqlSession().update("ScfFlowRecordMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfFlowRecord record) {
		return super.getSqlSession().update("ScfFlowRecordMapper.updateByPrimaryKey", record);
	}

}
