package com.tcs.PHI.application;

import java.time.LocalDateTime;

import com.tcs.PHI.service.ServiceBean;

public class App {

	public App(String storeId){
		
		ServiceBean bean = new ServiceBean(storeId);
		bean.fetchValuesfromPropertiesFile("/IDN.properties", "/storeMapping.properties", "/sftp.properties");
		bean.doEverything();
		
	}
	
	public static void main(String[] args) {
		
		/*LocalDateTime ldt = LocalDateTime.now();
		int startHour=10, startMinute=00, endHour=18, endminute = 00;
		String startsAt = ldt.toString().substring(0, 11).concat(Integer.toString(startHour)).concat(":").concat("00:").concat("00.000");
		String endsAt = ldt.toString().substring(0, 11).concat(Integer.toString(endHour)).concat(":").concat("00:").concat("00.000");
		System.out.println(ldt.toString());*/
		
		//new App(args[0]);
		new App("16704");
		
    }
}
