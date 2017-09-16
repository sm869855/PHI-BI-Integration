package com.tcs.PHI.fileWriter;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.tcs.PHI.res.ResBean;

import java.io.*;
import java.util.*;

public class CsvWriter {
	
	private FileWriter fw;
	private CsvSchema schema = null;
    private CsvSchema.Builder schemaBuilder = CsvSchema.builder();   
    private HashMap<String,String> csvMap = new HashMap<String, String>();
    private ArrayList<HashMap<String,String>> data;
    private int price=0;
    private String storeId;
    
    
    public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public CsvWriter(String storeId){
    	this.storeId = storeId;
    }
    
    public void writeToCsv(List<ResBean> responseList){
      
    		writeToPayments(responseList.get(0));    		
    		/*writeToItemRs(storeId,responseList.get(1));
    		writeToItemRs(storeId,responseList.get(2));
    		writeToCKHeader(storeId,responseList.get(3));*/
    }
    
    public void writeToPayments(ResBean response){
    	
    		
    	data = new ArrayList<HashMap<String,String>>();
    	for(HashMap<String,String> map:response.getData()){
    		csvMap.put("Transaction Date", map.get("OpenDate.Typed"));
    		csvMap.put("Bill Number", map.get("OrderNum"));
    		csvMap.put("Store Code", getStoreId());
    		csvMap.put("Cashier Time", map.get("Delivery.BillTime"));
    		csvMap.put("Pay Type", map.get("PayTypes"));
    		csvMap.put("Total Bill Amount", map.get("DishDiscountSumInt"));
    		csvMap.put("Charity Donation Amount", "");
    		csvMap.put("Credit Card Number", map.get("CardNumber"));
    		csvMap.put("Card Name", map.get("CardOwner"));
    		csvMap.put("Amount Tendered", "");
    		csvMap.put("Payment Delete?", "No");    		
    	}
    	//System.out.println(csvMap.toString());
    	data.add(csvMap);
    	if (csvMap != null && !csvMap.isEmpty()) {
            for (String col : csvMap.keySet()) {
                schemaBuilder.addColumn(col);
            }
            schema = schemaBuilder.build().withLineSeparator("\r").withHeader();
           
        }
    	try{ 
			fw = new FileWriter("PAYMENT.csv");
	        //System.out.println(listOfResponse.get(0).keySet().size());	        
	        schema = schemaBuilder.build().withLineSeparator("\r").withHeader();	        
	        CsvMapper mapper = new CsvMapper();
	        mapper.writer(schema).writeValues(fw).writeAll(data);
	        fw.flush();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
    	System.out.println("++++++++++++++++++++++++++++++CSV files created++++++++++++++++++++++++++++++++++");
    }	
	
	public void writeToItemRs(ResBean response) {
		data = new ArrayList<HashMap<String,String>>();
    	for(HashMap<String,String> map:response.getData()){
    		csvMap.put("Transaction Date", map.get("OpenDate.Typed"));
    		csvMap.put("Bill Number", map.get("OrderNum"));
    		csvMap.put("Store Code", getStoreId());
    		csvMap.put("Deal flag", "");
    		csvMap.put("SKU",map.get("DishCode"));
    		csvMap.put("Item Price (before tax)",map.get("DishDiscountSumInt"));
    		csvMap.put("Number of Sold Items",map.get("DishAmountInt"));
    		price=Integer.parseInt(map.get("DishAmountInt"))*Integer.parseInt(map.get("DishDiscountSumInt"));
    		csvMap.put("Total price",Integer.toString(price));
    		csvMap.put("Discount Item Code",map.get("OrderDiscount.Type.IDs"));
    		csvMap.put("Discount Item Code",map.get("OrderDiscount.Type"));
    		csvMap.put("",map.get("DishTaxCategory.id"));
    		csvMap.put("",map.get("VAT.Sum"));
    		csvMap.put("",map.get("IncreaseSum"));
    		csvMap.put("",map.get("OpenTime"));
    		csvMap.put("",map.get("CloseTime"));
    		csvMap.put("",map.get("CashRegisterName.Number"));
    		csvMap.put("",map.get("-"));
    		csvMap.put("",map.get("DeletedWithWriteoff"));
    		csvMap.put("",map.get("DiscountSum"));
    	}
    	//System.out.println(csvMap.toString());
    	data.add(csvMap);
    	if (csvMap != null && !csvMap.isEmpty()) {
            for (String col : csvMap.keySet()) {
                schemaBuilder.addColumn(col);
            }
            schema = schemaBuilder.build().withLineSeparator("\r").withHeader();
           
        }
    	try{ 
			fw = new FileWriter("PAYMENT.csv");
	        //System.out.println(listOfResponse.get(0).keySet().size());	        
	        schema = schemaBuilder.build().withLineSeparator("\r").withHeader();	        
	        CsvMapper mapper = new CsvMapper();
	        mapper.writer(schema).writeValues(fw).writeAll(data);
	        fw.flush();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
	}
	
	public void writeToCKHeader(String storeId, ResBean response) {
		
	}	
	
	public void writeToMsMember(String storeId, List<HashMap<String, String>> response){
		
	}
}
