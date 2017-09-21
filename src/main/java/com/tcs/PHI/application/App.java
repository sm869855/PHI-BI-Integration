package com.tcs.PHI.application;

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
		new App("IDN","16735");        
    }
}
