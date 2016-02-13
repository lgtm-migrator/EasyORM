package org.suntao.easyorm.annotation;

public class Course {
	private int courseid;
	private int teacherid;
	private String course;
	private int hours;
	private float score;

	@Override
	public String toString() {
		return String.format("%d %d %d %s", courseid, teacherid, hours, course);

	}

	public String getCourseName() {
		return course;
	}

	public int getHours() {
		return hours;
	}

	public float getScore() {
		return score;
	}

	public void setCourseName(String courseName) {
		this.course = courseName;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getCourseid() {
		return courseid;
	}

	public void setCourseid(int courseid) {
		this.courseid = courseid;
	}

	public int getTeacherid() {
		return teacherid;
	}

	public void setTeacherid(int teacherid) {
		this.teacherid = teacherid;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}
}
