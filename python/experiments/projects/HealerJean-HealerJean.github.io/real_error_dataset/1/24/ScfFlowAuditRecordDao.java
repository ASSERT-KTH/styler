/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.flow;

import java.util.List;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.flow.*;
import org.springframework.stereotype.Repository;

/**
 * @author zhangyujin
 * @ClassName: ScfFlowAuditRecordDao
 * @date 2099/1/1
 * @Description: ScfFlowAuditRecordDao
 */
@Repository("scfFlowAuditRecordDao")
public class ScfFlowAuditRecordDao extends BaseDao {

	public int countByExample(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().selectOne("ScfFlowAuditRecordMapper.countByExample", example);
	}

	public int deleteByExample(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().delete("ScfFlowAuditRecordMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfFlowAuditRecordMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfFlowAuditRecord record) {
		return super.getSqlSession().insert("ScfFlowAuditRecordMapper.insert", record);
	}

	public int insertSelective(ScfFlowAuditRecord record) {
		return super.getSqlSession().insert("ScfFlowAuditRecordMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfFlowAuditRecord> list) {
		return super.batchInsert("ScfFlowAuditRecordMapper.insertSelective", list);
	}

	public List<ScfFlowAuditRecord> selectByExample(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().selectList("ScfFlowAuditRecordMapper.selectByExample", example);
	}

	public List<ScfFlowAuditRecord> selectPageByExample(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().selectList("ScfFlowAuditRecordMapper.selectPageByExample", example);
	}

	public ScfFlowAuditRecord selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfFlowAuditRecordMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfFlowAuditRecord record) {
		return super.getSqlSession().update("ScfFlowAuditRecordMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfFlowAuditRecord record) {
		return super.getSqlSession().update("ScfFlowAuditRecordMapper.updateByPrimaryKey", record);
	}


	public List<ScfFlowAuditRecord> jobCollect(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().selectList("ScfFlowAuditRecordMapper.jobCollect", example);
	}

	public List<ScfFlowAuditRecord> readyAudits(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().selectList("ScfFlowAuditRecordMapper.readyAudits", example);
	}

	public int countReadyAudits(ScfFlowAuditRecordQuery example) {
		return super.getSqlSession().selectOne("ScfFlowAuditRecordMapper.countReadyAudits", example);
	}
}
