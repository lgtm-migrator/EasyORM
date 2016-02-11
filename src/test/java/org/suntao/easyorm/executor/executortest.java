package org.suntao.easyorm.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.defaults.DefaultSqlSessionFactory;

public class executortest {
	public static void main(String[] args) {
		SqlSession session = new DefaultSqlSessionFactory(
				"src/test/resources/xmltest.xml").openSession();
		Connection conn = null;
		try {
			conn = session.getConnection();
			PreparedStatement preparedStatement = conn
					.prepareStatement("select * from courseinfo ");
			ResultSet resultSet = preparedStatement.executeQuery();
			resultSet.next();
			System.out.println("" + resultSet.getString("course"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}
