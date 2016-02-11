package org.suntao.easyorm.fulltest;

import java.util.List;

import org.suntao.easyorm.annotation.SQL;

public interface courseinfoMapper {
	@SQL(value = "select * from courseinfo")
	public List<courseinfo> selectAll();

	@SQL(value = "select * from courseinfo where courseid = ?")
	public courseinfo selectOne(int id);

	@SQL(value = "select * from courseinfo")
	public int count();
}
