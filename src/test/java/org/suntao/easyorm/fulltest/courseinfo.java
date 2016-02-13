package org.suntao.easyorm.fulltest;

public class courseinfo {
	int courseid;
	String course;
	int classhour;
	float score;
	int teacherid;

	@Override
	public String toString() {
		return String.format("%d %d %d %s", courseid, teacherid, classhour,
				course);
	}
}
