package com.tcs.PHI.application;

import com.tcs.PHI.service.ServiceBean;

public class App {

	public App(String storeId){
		new ServiceBean(storeId);
	}
	
	public static void main(String[] args) {
		
		/*LocalDateTime ldt = LocalDateTime.now();
		int startHour=10, startMinute=00, endHour=18, endminute = 00;
		String startsAt = ldt.toString().substring(0, 11).concat(Integer.toString(startHour)).concat(":").concat("00:").concat("00.000");
		String endsAt = ldt.toString().substring(0, 11).concat(Integer.toString(endHour)).concat(":").concat("00:").concat("00.000");
		
		System.out.println(startsAt+"\n"+endsAt);*/
		
		new App("16703"); 
		
		
    }
}
