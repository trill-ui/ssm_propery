package com.learn.dao;

import java.util.List;
import java.util.Map;

/**
 * 基础Dao(还需在XML文件里，有对应的SQL语句)
 * 
 */
public interface BaseDao<T> {
	//保存
	void save(T t);
	void save(Map<String, Object> map);
	//批量删除
	void saveBatch(List<T> list);

	//修改
	int update(T t);
	int update(Map<String, Object> map);

	//删除
	int delete(Object id);
	int delete(Map<String, Object> map);
	int deleteBatch(Object[] id);

    //查询
    //根据id查询单个信息
	T queryObject(Object id);
	//根据条件查询，返回多个信息
	List<T> queryList(Map<String, Object> map);
	//根据id查询多个信息
	List<T> queryList(Object id);
	//计数查询（有参）
	int queryTotal(Map<String, Object> map);
    //计数查询（无参）
	int queryTotal();
}
