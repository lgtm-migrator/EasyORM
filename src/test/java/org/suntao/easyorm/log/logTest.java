package org.suntao.easyorm.log;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class logTest {
	private static Logger logger = Logger.getLogger(logTest.class.getName());

	public static void main(String[] args) {
		test();
	}

	public static void test() {
		logger.log(Level.ALL, "ALL");
		logger.log(Level.CONFIG, "CONFIG");
		logger.log(Level.FINE, "FINE");
		logger.log(Level.FINEST, "FINEST");
		logger.log(Level.INFO, "INFO");
		logger.log(Level.OFF, "OFF");
		logger.log(Level.WARNING, "WARNING");
		logger.log(Level.SEVERE, "SEVERE");

	}
}
