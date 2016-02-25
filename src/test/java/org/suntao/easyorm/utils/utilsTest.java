package org.suntao.easyorm.utils;

import java.util.Date;

import org.junit.Test;

public class utilsTest {

	@Test
	public void convertDateTest() {
		Date date = new Date();
		System.out.println("Is java.util.date instance:"
				+ Date.class.isInstance(date) + "\nTime is: " + date.getTime());
		java.sql.Date sqlDate = Utils.convertSqlDate(date);
		System.out.println("Is java.sql.date instance:"
				+ java.sql.Date.class.isInstance(sqlDate) + "\nTime is: "
				+ sqlDate.getTime());

	}

}
