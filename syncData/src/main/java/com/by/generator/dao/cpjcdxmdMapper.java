package com.by.generator.dao;

import com.by.generator.bean.cpjcdxmd;

public interface cpjcdxmdMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(cpjcdxmd record);

    int insertSelective(cpjcdxmd record);

    cpjcdxmd selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(cpjcdxmd record);

    int updateByPrimaryKey(cpjcdxmd record);
}