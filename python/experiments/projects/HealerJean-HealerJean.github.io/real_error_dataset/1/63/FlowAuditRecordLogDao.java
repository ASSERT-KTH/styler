/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.flow;

import java.util.List;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.flow.FlowAuditRecordLog;
import com.hlj.proj.data.pojo.flow.FlowAuditRecordLogQuery;
import org.springframework.stereotype.Repository;
/**
 * @author zhangyujin
 * @ClassName: FlowAuditRecordLogDao
 * @date 2099/1/1
 * @Description: FlowAuditRecordLogDao
 */
@Repository("flowAuditRecordLogDao")
public class FlowAuditRecordLogDao extends BaseDao {

	public int countByExample(FlowAuditRecordLogQuery example) {
		return super.getSqlSession().selectOne("FlowAuditRecordLogMapper.countByExample", example);
	}

	public int deleteByExample(FlowAuditRecordLogQuery example) {
		return super.getSqlSession().delete("FlowAuditRecordLogMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("FlowAuditRecordLogMapper.deleteByPrimaryKey", id);
	}

	public int insert(FlowAuditRecordLog record) {
		return super.getSqlSession().insert("FlowAuditRecordLogMapper.insert", record);
	}

	public int insertSelective(FlowAuditRecordLog record) {
		return super.getSqlSession().insert("FlowAuditRecordLogMapper.insertSelective", record);
	}

	public int batchInsert(List<FlowAuditRecordLog> list) {
		return super.batchInsert("FlowAuditRecordLogMapper.insertSelective", list);
	}

	public List<FlowAuditRecordLog> selectByExample(FlowAuditRecordLogQuery example) {
		return super.getSqlSession().selectList("FlowAuditRecordLogMapper.selectByExample", example);
	}

	public List<FlowAuditRecordLog> selectPageByExample(FlowAuditRecordLogQuery example) {
		return super.getSqlSession().selectList("FlowAuditRecordLogMapper.selectPageByExample", example);
	}

	public FlowAuditRecordLog selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("FlowAuditRecordLogMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(FlowAuditRecordLog record) {
		return super.getSqlSession().update("FlowAuditRecordLogMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(FlowAuditRecordLog record) {
		return super.getSqlSession().update("FlowAuditRecordLogMapper.updateByPrimaryKey", record);
	}

}
