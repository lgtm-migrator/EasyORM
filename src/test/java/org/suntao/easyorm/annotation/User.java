package org.suntao.easyorm.annotation;

import java.util.Date;

public class User {
	Integer age;
	Date birthdate;
	String name;

	public User() {
		super();

	}

	public User(String name, Integer age, Date birthdate) {
		super();
		this.name = name;
		this.age = age;
		this.birthdate = birthdate;
	}

	public Integer getAge() {
		return age;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public String getName() {
		return name;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public void setName(String name) {
		this.name = name;
	}
}
