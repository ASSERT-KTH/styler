/*
 * Copyright (C) 2019 xiaomi.com, Inc. All Rights Reserved.
 */package com.hlj.proj.data.dao.mybatis.dao.flow;

import com.hlj.proj.data.dao.mybatis.dao.BaseDao;
import com.hlj.proj.data.pojo.flow.ScfFlowDefinition;
import com.hlj.proj.data.pojo.flow.ScfFlowDefinitionQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("scfFlowDefinitionDao")
public class ScfFlowDefinitionDao extends BaseDao {

	public int countByExample(ScfFlowDefinitionQuery example) {
		return super.getSqlSession().selectOne("ScfFlowDefinitionMapper.countByExample", example);
	}

	public int deleteByExample(ScfFlowDefinitionQuery example) {
		return super.getSqlSession().delete("ScfFlowDefinitionMapper.deleteByExample", example);
	}

	public int deleteByPrimaryKey(long id) {
		return super.getSqlSession().delete("ScfFlowDefinitionMapper.deleteByPrimaryKey", id);
	}

	public int insert(ScfFlowDefinition record) {
		return super.getSqlSession().insert("ScfFlowDefinitionMapper.insert", record);
	}

	public int insertSelective(ScfFlowDefinition record) {
		return super.getSqlSession().insert("ScfFlowDefinitionMapper.insertSelective", record);
	}

	public int batchInsert(List<ScfFlowDefinition> list) {
		return super.batchInsert("ScfFlowDefinitionMapper.insertSelective", list);
	}

	public List<ScfFlowDefinition> selectByExample(ScfFlowDefinitionQuery example) {
		return super.getSqlSession().selectList("ScfFlowDefinitionMapper.selectByExample", example);
	}

	public List<ScfFlowDefinition> selectPageByExample(ScfFlowDefinitionQuery example) {
		return super.getSqlSession().selectList("ScfFlowDefinitionMapper.selectPageByExample", example);
	}

	public ScfFlowDefinition selectByPrimaryKey(long id) {
		return super.getSqlSession().selectOne("ScfFlowDefinitionMapper.selectByPrimaryKey", id);
	}

	public int updateByPrimaryKeySelective(ScfFlowDefinition record) {
		return super.getSqlSession().update("ScfFlowDefinitionMapper.updateByPrimaryKeySelective", record);
	}

	public int updateByPrimaryKey(ScfFlowDefinition record) {
		return super.getSqlSession().update("ScfFlowDefinitionMapper.updateByPrimaryKey", record);
	}

}
