package org.suntao.easyorm.annotation;

import java.util.List;

public interface userMapper {
	@SQL(value = "select * from user")
	int countSql();

	@SQL(value = "delete * from user where id = {#id}")
	Boolean deleteuserByid(@Param int id);

	User selectOne(@Param int id);

	@SQL(value = "select * from user where id = {#id}")
	List<User> selectUserById(@Param int id, @Param String name);
}
