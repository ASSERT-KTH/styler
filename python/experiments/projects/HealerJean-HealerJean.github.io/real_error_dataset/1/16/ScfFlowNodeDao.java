/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.flow;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.flow.ScfFlowNode;
import com.hlj.proj.data.pojo.flow.ScfFlowNodeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository("scfFlowNodeDao")
public class ScfFlowNodeDao extends BaseDao {

	public int countByExample(ScfFlowNodeQuery example) {
		return super.getSqlSession().selectOne("ScfFlowNodeMapper.countByExample", example);
	}

	public int deleteByExample(ScfFlowNodeQuery example) {
		return super.getSqlSession().delete("ScfFlowNodeMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfFlowNodeMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfFlowNode record) {
		return super.getSqlSession().insert("ScfFlowNodeMapper.insert", record);
	}

	public int insertSelective(ScfFlowNode record) {
		return super.getSqlSession().insert("ScfFlowNodeMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfFlowNode> list) {
		return super.batchInsert("ScfFlowNodeMapper.insertSelective", list);
	}

	public List<ScfFlowNode> selectByExample(ScfFlowNodeQuery example) {
		return super.getSqlSession().selectList("ScfFlowNodeMapper.selectByExample", example);
	}

	public List<ScfFlowNode> selectPageByExample(ScfFlowNodeQuery example) {
		return super.getSqlSession().selectList("ScfFlowNodeMapper.selectPageByExample", example);
	}

	public ScfFlowNode selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfFlowNodeMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfFlowNode record) {
		return super.getSqlSession().update("ScfFlowNodeMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfFlowNode record) {
		return super.getSqlSession().update("ScfFlowNodeMapper.updateByPrimaryKey", record);
	}

}
