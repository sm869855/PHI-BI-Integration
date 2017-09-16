package com.tcs.PHI.fileWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.tcs.PHI.res.ResBean;

public class PolWriter {
	
	private FileWriter fw;
	
	private String temp=new String();
	private String storeId;
    
    
    public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public PolWriter(String storeId){
    	this.storeId = storeId;
    }
	
	public void writeAll(List<ResBean> responseList){
		writeToDCR(responseList);
		writeToTLG(responseList);
		writeToESUM(responseList);
		writeToCIO(responseList);
		writeToCOG(responseList);
	}
	
	public void writeToDCR(List<ResBean> responseList){
		
		String date = LocalDate.now().toString();
		int day = LocalDate.now().getDayOfMonth();
		int month = LocalDate.now().getMonthValue();
		String dayNo = Integer.toString(day);
		String mon = Integer.toString(month);
		if(day < 10){
			dayNo = new StringBuilder("0").append(dayNo).toString();
		}
		if(month < 10){
			mon = new StringBuilder("0").append(mon).toString();
		}
		String filename= new StringBuilder().append(dayNo).append(mon).append("DCR.pol").toString();
			
		try{
			fw = new FileWriter(filename);
			fw.write("FILENAME,"+filename+"\n"+"DATE,"+date.replace("-","")+"\n"+"STORE,"+getStoreId()+"\n"+"COMPANY NUMBER,1\n");
			
			writeReadings(responseList);
			writeSALES(responseList);
			writeTotalDepartment(responseList);
			fw.close();
		}catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		System.out.println("DCR file created & written.....");
	}

	public void writeToTLG(List<ResBean> responseList){}
	
	public void writeToESUM(List<ResBean> responseList){}
	
	public void writeToCIO(List<ResBean> responseList){}
	
	public void writeToCOG(List<ResBean> responseList){}
	
	//Methods to write DCR fields
	public void writeReadings(List<ResBean> responseList) {	
		
		/*Readings,RESETS,
		Readings,Close Reading,
		Readings,Open Reading,
		Readings,= Register Sales,
		Readings,Deletions,
		Readings,Gross W/Tax,
		Readings,Total Net Sales,*/
		
		int qty=0;
		
		double resets=0.000,close=0.000,open=0.000,regSales=0.000,
				del = 0.000,grossWithTax=0.000,totalNetSales=0.000;
		//Calculating the values from API call response
		for (HashMap<String,String> map : responseList.get(0).getData()) { 
				qty+= Integer.parseInt(map.get("UniqOrderId"));
    			del+= Integer.parseInt(map.get("DishReturnSum"));
    			grossWithTax+= Integer.parseInt(map.get("DishSumInt"));
    			totalNetSales+= Integer.parseInt(map.get("DishDiscountSumInt.withoutVAT"));
		}
		//Writing calculated values to the DCR file
		String deletions1=new StringBuilder("Readings,RESETS,").append(qty).append(",").append(resets).toString();
		String deletions2=new StringBuilder("Readings,Close Reading,").append(qty).append(",").append(close).toString();
		String deletions3=new StringBuilder("Readings,Open Reading,").append(qty).append(",").append(open).toString();
		String deletions4=new StringBuilder("Readings,= Register Sales,").append(qty).append(",").append(regSales).toString();
		String deletions5=new StringBuilder("Readings,Deletions,").append(qty).append(",").append(del).toString();
		String deletions6=new StringBuilder("Readings,Gross W/Tax,").append(qty).append(",").append(grossWithTax).toString();
		String deletions7=new StringBuilder("Readings,Total Net Sales,").append(qty).append(",").append(totalNetSales).toString();
	try {	
		
		fw.write(deletions1+"\n"+deletions2+"\n"+deletions3+"\n"+deletions4+"\n"+deletions5+"\n"+deletions6+"\n"+deletions7+"\n");
	
	}catch(IOException ioe){ 
		System.err.println("IOException: " + ioe.getMessage());
	}
	temp = deletions6;
}

	public void writeSALES(List<ResBean> responseList) {
	int qty=0,qty2=0,qty3=0;
	double coupons=0.000,discounts=0.000,gross=0.000,tax=0.000,minusTaxAdj=0.000,plusTaxAdj=0.000;
	//Calculating the values from API call response
	for (HashMap<String,String> map : responseList.get(0).getData()) {
		
		if (map.get("OrderDiscount.Type")!=""){
			qty2+= Integer.parseInt(map.get("UniqOrderId"));
			discounts+= Double.parseDouble(map.get("DiscountSum"));
		}
		if (Integer.parseInt(map.get("VAT.Sum")) != 0){
			qty3+= Integer.parseInt(map.get("UniqOrderId"));
			tax+= Integer.parseInt(map.get("VAT.Sum"));
		}
		qty+= Integer.parseInt(map.get("UniqOrderId"));
		gross+= Integer.parseInt(map.get("DishSumInt")) - Integer.parseInt(map.get("VAT.Sum"));
	}
	
	//Writing calculated values to the DCR file
	/*SALES,Coupons,0,0.000
	SALES,Discounts,0,0.000
	SALES,- Tax Adjustment,0,0.000
	SALES,Gross,7,539.000
	SALES,Tax,0,0.000
	SALES,+ Tax Adjustment,0,0.000*/
	String sales1=new StringBuilder("SALES,Coupons,").append("0").append(",").append(coupons).toString();
	String sales2=new StringBuilder("SALES,Discounts,").append(qty2).append(",").append(discounts).toString();
	String sales3=new StringBuilder("SALES,- Tax Adjustment,").append("0").append(",").append(minusTaxAdj).toString();
	String sales4=new StringBuilder("SALES,Gross,").append(qty).append(",").append(gross).toString();
	String sales5=new StringBuilder("SALES,Tax,").append(qty3).append(",").append(tax).toString();
	String sales6=new StringBuilder("SALES,+ Tax Adjustment,").append("0").append(",").append(plusTaxAdj).toString();
	
	try {	
		
		fw.write(sales1+"\n"+sales2+"\n"+sales3+"\n"+sales4+"\n"+sales5+"\n"+sales6+"\n");
	
	}catch(IOException ioe){ 
		System.err.println("IOException: " + ioe.getMessage());
	}
}

	public void writeTotalDepartment(List<ResBean> responseList) {
	int qtyDineIn=0,qtyTakeout=0,qtyHomeDel=0,qtyDT=0,qtyCater=0,qtyEmpMeal=0,
			qtyCarHop=0,qtyBday=0,qtyTnT=0,qtyPartyCat=0,qtyFam=0,qtyCarHopCc=0;
	
	double dineIn=0.000,takeOut=0.000,homeDel=0.000,driveTh=0.000,cater=0.000,empMeal=0.000,
			carHop=0.000,bDay=0.000,tnT=0.000,partyCat=0.000,family=0.000,carHopCc=0.000;
	
	//Calculating the values from API call response
	for (HashMap<String,String> map : responseList.get(0).getData()) {
		
		if(map.get("Delivery.IsDelivery").equalsIgnoreCase("ORDER_WITHOUT_DELIVERY") && 
				map.get("Delivery.ServiceType") == null &&
				map.get("Banquet").equalsIgnoreCase("FALSE")){
			qtyDineIn += Integer.parseInt(map.get("UniqOrderId"));
			dineIn += Double.parseDouble(map.get("DishDiscountSumInt"));
		}
		if(map.get("Delivery.IsDelivery").equalsIgnoreCase("DELIVERY_ORDER") &&
				map.get("Delivery.ServiceType").equalsIgnoreCase("PICKUP")){
			qtyTakeout += Integer.parseInt(map.get("UniqOrderId"));
			takeOut+=Double.parseDouble(map.get("DishDiscountSumInt"));
		}
		if(map.get("Delivery.IsDelivery").equalsIgnoreCase("DELIVERY_ORDER") &&
				map.get("Delivery.ServiceType").equalsIgnoreCase("COURIER")){
			qtyHomeDel += Integer.parseInt(map.get("UniqOrderId"));
			homeDel+=Double.parseDouble(map.get("DishDiscountSumInt"));
		}
		if(map.get("Delivery.IsDelivery").equalsIgnoreCase("ORDER_WITHOUT_DELIVERY") &&
				map.get("Delivery.ServiceType") == null && map.get("Banquet").equalsIgnoreCase("TRUE")){
			qtyCater += Integer.parseInt(map.get("UniqOrderId"));
			cater+=Double.parseDouble(map.get("DishDiscountSumInt"));
		}
		if(map.get("NonCashPaymentType") != null  && map.get("NonCashPaymentType").equalsIgnoreCase("EMPLOYEE MEAL")){
			qtyEmpMeal += Integer.parseInt(map.get("UniqOrderId"));
			empMeal+=Double.parseDouble(map.get("DiscountSum"));
		}    		
	}
	
	//Writing calculated values to the DCR file
	/*Total Department,DINE IN,5,436.000
	Total Department,TAKE OUT,0,0.000
	Total Department,HOME DELIVERY,2,103.000
	Total Department,DRIVE THROUGH,0,0.000
	Total Department,CATERING,0,0.000
	Total Department,EMPLOYEE MEAL,0,0.000
	Total Department,CAR HOPE,0,0.000
	Total Department,BIRTHDAY,0,0.000
	Total Department,TRIAL AND TRAINING,0,0.000
	Total Department,PARTYHALL CATERING,0,0.000
	Total Department,FAMILY,0,0.000
	Total Department,CAR HOPE CC,0,0.000*/
	String td1=new StringBuilder("Total Department,DINE IN,").append(qtyDineIn).append(",").append(dineIn).toString();
	String td2=new StringBuilder("Total Department,TAKE OUT,").append(qtyTakeout).append(",").append(takeOut).toString();
	String td3=new StringBuilder("Total Department,HOME DELIVERY,").append(qtyHomeDel).append(",").append(homeDel).toString();
	String td4=new StringBuilder("Total Department,DRIVE THROUGH,").append("0").append(",").append(driveTh).toString();
	String td5=new StringBuilder("Total Department,CATERING,").append(qtyCater).append(",").append(cater).toString();
	String td6=new StringBuilder("Total Department,EMPLOYEE MEAL,").append(qtyEmpMeal).append(",").append(empMeal).toString();
	String td7=new StringBuilder("Total Department,CAR HOPE,").append("0").append(",").append(carHop).toString();
	String td8=new StringBuilder("Total Department,BIRTHDAY,").append("0").append(",").append(bDay).toString();
	String td9=new StringBuilder("Total Department,TRIAL AND TRAINING,").append("0").append(",").append(tnT).toString();
	String td10=new StringBuilder("Total Department,PARTYHALL CATERING,").append(qtyCater).append(",").append(cater).toString();
	String td11=new StringBuilder("Total Department,FAMILY,").append("0").append(",").append(family).toString();
	String td12=new StringBuilder("Total Department,CAR HOPE CC,").append("0").append(",").append(carHopCc).toString();
	
	try {	
		
		fw.write(td1+"\n"+td2+"\n"+td3+"\n"+td4+"\n"+td5+"\n"+td6+"\n"+td7+"\n"+td8+"\n"+td9+"\n"+td10+"\n"+td11+"\n"+td12);
	
	}catch(IOException ioe){ 
		System.err.println("IOException: " + ioe.getMessage());
	}		
}

	public void writeTenderDepartment(List<ResBean> responseList) {
		
		int qty1=0,qty2=0;		
		double cash=0.000,cc=0.000;
		//Calculating the values from API call response
		for (HashMap<String,String> map : responseList.get(0).getData()) {
			
			if (map.get("PayTypes").equalsIgnoreCase("CASH")){
				qty1+= Integer.parseInt(map.get("UniqOrderId"));
				cash+= Double.parseDouble(map.get("DishDiscountSumInt"));
			}
			if (map.get("PayTypes").equalsIgnoreCase("CREDIT") || map.get("PayTypes").equalsIgnoreCase("CREDIT CARD")){
				qty2+= Integer.parseInt(map.get("UniqOrderId"));
				cash+= Double.parseDouble(map.get("DishDiscountSumInt"));
			}			
		}
		String tenderDept1=new StringBuilder("Tender Department,CASH,").append(qty1).append(",").append(cash).toString();
		String tenderDept2=new StringBuilder("Tender Department,PRE-PAID COUPONS,").append("0").append(",").append("0.000").toString();
		String tenderDept3=new StringBuilder("Tender Department,CREDIT CARD,").append(qty2).append(",").append(cc).toString();
		String tenderDept4=new StringBuilder("Tender Department,TECOM COUPONS,").append("0").append(",").append("0.000").toString();
		String tenderDept5=new StringBuilder("Tender Department,ONLINE CREDIT,").append("0").append(",").append("0.000").toString();
		String tenderDept6=new StringBuilder("Tender Department,Etisalat Prepaid 25,").append("0").append(",").append("0.000").toString();
		String tenderDept7=new StringBuilder("Tender Department,Etisalat Prepaid 50,").append("0").append(",").append("0.000").toString();
		String tenderDept8=new StringBuilder("Tender Department,Etisalat Prepaid 100,").append("0").append(",").append("0.000").toString();
		String tenderDept9=new StringBuilder("Tender Department,Etisalat Prepaid 200,").append("0").append(",").append("0.000").toString();
		String tenderDept10=new StringBuilder("Tender Department,Etisalat Prepaid 300,").append("0").append(",").append("0.000").toString();
		String tenderDept11=new StringBuilder("Tender Department,Gulf News,").append("0").append(",").append("0.000").toString();

		try {	
			
			fw.write(tenderDept1+"\n"+tenderDept2+"\n"+tenderDept3+"\n"+tenderDept4+"\n"+tenderDept5+"\n"+tenderDept6+"\n"
					+tenderDept7+"\n"+tenderDept8+"\n"+tenderDept9+"\n"+tenderDept10+"\n"+tenderDept11);
		
		}catch(IOException ioe){ 
			System.err.println("IOException: " + ioe.getMessage());
		}
		
	}

	public void writeCouponDepartment(List<ResBean> responseList) {
		
		String dummy = "Coupon Department,MARKETING COUPONS,0,0.000"+"\n"+
				"Coupon Department,COMPLIMENTARY MEAL,0,0.000"+"\n"+
				"Coupon Department,COMPANY MEAL,0,0.000"+"\n"+
				"Coupon Department,REST HOSPITALITY,0,0.000"+"\n"+
				"Coupon Department,GV SCRATCH COUPON,0,0.000"+"\n"+
				"Coupon Department,Outsource Staff Meal,0,0.000"+"\n"+
				"Coupon Department,Gulf News Coupon,0,0.000"+"\n"+
				"Coupon Department,Gulf News 50 Aed,0,0.000"+"\n";
		try {	
			
			fw.write(dummy);
		
		}catch(IOException ioe){ 
			System.err.println("IOException: " + ioe.getMessage());
		}
		
	}

	public void writeDiscountDepartment(List<ResBean> responseList) {
		int[] qty = {0,0,0,0,0,0,0,0,0,0,0,0};
		double[] discount = {0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000};
		//OrderDiscount.Type
		for (HashMap<String,String> map : responseList.get(0).getData()) {
			if(map.get("OrderDiscount.Type").equalsIgnoreCase("EMPLOYEE MEALS")){
				qty[1]+=Integer.parseInt(map.get("UniqOrderId"));discount[1]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("MARKETTING DISCOUNT")){
				qty[2]+=Integer.parseInt(map.get("UniqOrderId"));discount[2]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("RESTAURANT HOSP.")){
				qty[3]+=Integer.parseInt(map.get("UniqOrderId"));discount[3]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("CATERING DISCOUNT")){
				qty[4]+=Integer.parseInt(map.get("UniqOrderId"));discount[4]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("TRIAL AND TRAINING")){
				qty[5]+=Integer.parseInt(map.get("UniqOrderId"));discount[5]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("PARTYHALL CATERING")){
				qty[6]+=Integer.parseInt(map.get("UniqOrderId"));discount[6]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("10% LATE DELIVERY")){
				qty[7]+=Integer.parseInt(map.get("UniqOrderId"));discount[7]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("Head Office")){
				qty[8]+=Integer.parseInt(map.get("UniqOrderId"));discount[8]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("GES")){
				qty[9]+=Integer.parseInt(map.get("UniqOrderId"));discount[9]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("50% Americana Staff")){
				qty[10]+=Integer.parseInt(map.get("UniqOrderId"));discount[10]+=Double.parseDouble(map.get("DiscountSum"));
			}else if(map.get("OrderDiscount.Type").equalsIgnoreCase("PHUT Charity")){
				qty[11]+=Integer.parseInt(map.get("UniqOrderId"));discount[11]+=Double.parseDouble(map.get("DiscountSum"));
			}else{
				qty[0]+=Integer.parseInt(map.get("UniqOrderId"));discount[0]+=Double.parseDouble(map.get("DiscountSum"));
			}			
		}
		
		String dis1=new StringBuilder("Discount Department,EMPLOYEE MEALS,").append(qty[1]).append(",").append(discount[1]).toString();
		String dis2=new StringBuilder("Discount Department,MARKETTING DISCOUNT,").append(qty[2]).append(",").append(discount[2]).toString();
		String dis3=new StringBuilder("Discount Department,RESTAURANT HOSP.,").append(qty[3]).append(",").append(discount[3]).toString();
		String dis4=new StringBuilder("Discount Department,CATERING DISCOUNT,").append(qty[4]).append(",").append(discount[4]).toString();
		String dis5=new StringBuilder("Discount Department,TRIAL AND TRAINING,").append(qty[5]).append(",").append(discount[5]).toString();
		String dis6=new StringBuilder("Discount Department,PARTYHALL CATERING,").append(qty[6]).append(",").append(discount[6]).toString();
		String dis7=new StringBuilder("Discount Department,10% LATE DELIVERY,").append(qty[7]).append(",").append(discount[7]).toString();
		String dis8=new StringBuilder("Discount Department,Head Office,").append(qty[8]).append(",").append(discount[8]).toString();
		String dis9=new StringBuilder("Discount Department,GES,").append(qty[9]).append(",").append(discount[9]).toString();
		String dis10=new StringBuilder("Discount Department,50% Americana Staff,").append(qty[10]).append(",").append(discount[10]).toString();
		String dis11=new StringBuilder("Discount Department,PHUT Charity,").append(qty[11]).append(",").append(discount[11]).toString();
		String dis12=new StringBuilder("Discount Department,Emirates Transport,").append(qty[0]).append(",").append(discount[0]).toString();
		
		
				try {	
					
					fw.write(dis1+"\n"+dis2+"\n"+dis3+"\n"+dis4+"\n"+dis5+"\n"+dis6+
							"\n"+dis7+"\n"+dis8+"\n"+dis9+"\n"+dis10+"\n"+dis11+"\n"+dis12);		
				}catch(IOException ioe){ 
					System.err.println("IOException: " + ioe.getMessage());
				}
		
	}

	public void writeItemDepartment(FileWriter fw) {
		int[] qty = {0,0,0,0,0,0,0,0,0,0,0,0,0};
		double[] itemDept = {0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000,0.000};
		
		
		
		
	}

	public void writeTaxRecap(FileWriter fw) {}

	public void writeControlTotals(FileWriter fw) {}

	public void writeFinRespo(FileWriter fw) {}

	//Methods to write TLG fields
	
	
	//Methods to write ESUM fields
	
	
	//Methods to write CIO fields
	
	
	//Methods to write COG fields
}
