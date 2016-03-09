package org.suntao.easyorm.poolTest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.suntao.easyorm.exceptions.ConnectionPoolException;
import org.suntao.easyorm.pool.ConnectionPool;

public class pooltest {
	public static void main(String[] args) {
		ConnectionPool connectionPool = new ConnectionPool(
				"com.mysql.jdbc.Driver", "jdbc:mysql://42.96.206.67:3306/stu",
				"stu", "admin");
		List<Connection> conns = new ArrayList<Connection>();
		for (int i = 0; i < 20; i++) {
			Connection current = null;
			try {
				current = connectionPool.getConnection();
			} catch (ConnectionPoolException e) {
				e.printStackTrace();
			}
			if (current != null)
				conns.add(current);
			System.out.println(current);
		}
		for (Connection conn : conns) {
			try {
				connectionPool.returnConnection(conn);
			} catch (ConnectionPoolException e) {
				e.printStackTrace();
			}
		}
		connectionPool.releasePool();
	}
}
