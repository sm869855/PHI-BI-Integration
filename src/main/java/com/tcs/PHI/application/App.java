package com.tcs.PHI.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.tcs.PHI.service.ServiceBean;

public class App {
	
	private ServiceBean service;	

	public App(String countryName,String storeId){
		this.service = new ServiceBean(countryName,storeId);
	}
	
	public ServiceBean getService() {
		return service;
	}

	public void setService(ServiceBean service) {
		this.service = service;
	}
	
	public static void main(String[] args) {
        
        //App restApiCon = new App("IDN",args[0]);
		
//		ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("+07:00"));
//		System.out.println(zdt.toString().substring(0, 11));
		
		LocalDateTime ldt = LocalDateTime.now();
		
		int startHour=10, startMinute=00, endHour=18, endminute = 00;
		
		String startsAt = ldt.toString().substring(0, 11).concat(Integer.toString(startHour)).concat(":").concat("00:").concat("00.000");
		String endsAt = ldt.toString().substring(0, 11).concat(Integer.toString(endHour)).concat(":").concat("00:").concat("00.000");
		
//		ZonedDateTime marketOpens = ZonedDateTime.of(LocalDate.of(
//				LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth()),
//				LocalTime.of(startHour, startMinute),ZoneId.of("America/New_York"));
//		ZonedDateTime marketCloses = marketOpens.with(LocalTime.of(endHour, endminute));
		
		System.out.println(startsAt+"\n"+endsAt);
		
		new App("IDN",args[0]); 
		
		
    }
}
