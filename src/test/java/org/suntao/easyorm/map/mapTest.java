package org.suntao.easyorm.map;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.suntao.easyorm.executor.defaults.SimpleExecutor;
import org.suntao.easyorm.session.SqlSession;
import org.suntao.easyorm.session.defaults.DefaultSqlSessionFactory;

public class mapTest {
	/**
	 * 测试类
	 * 
	 * @author suntao
	 *
	 */
	class courseinfo {
		private int classhour;
		private String course;
		private int courseid;
		private float score;
		private int teacherid;

		public int getClasshour() {
			return classhour;
		}

		public String getCourse() {
			return course;
		}

		public int getCourseid() {
			return courseid;
		}

		public float getScore() {
			return score;
		}

		public String getStrInfo() {
			return "" + courseid + course + score + classhour + teacherid;
		}

		public int getTeacherid() {
			return teacherid;
		}

		public void setClasshour(int classhour) {
			this.classhour = classhour;
		}

		public void setCourse(String course) {
			this.course = course;
		}

		public void setCourseid(int courseid) {
			this.courseid = courseid;
		}

		public void setScore(float score) {
			this.score = score;
		}

		public void setTeacherid(int teacherid) {
			this.teacherid = teacherid;
		}
	}

	static String sqlStr = "select * from courseinfo";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		mapTest mt = new mapTest();
		try {
			Method m = mt.getClass().getDeclaredMethod("getCss");
			Type t = m.getGenericReturnType();
			System.out.println(t);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<courseinfo> getCss() {
		return null;
	}

}
