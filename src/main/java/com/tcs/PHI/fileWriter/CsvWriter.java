package com.tcs.PHI.fileWriter;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.tcs.PHI.res.ResBean;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class CsvWriter {
	
    private int price=0;
    private String storeId;
    
    
    public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public CsvWriter(String storeId){
		
		List<String> values;
		try {
			values = Arrays.asList(PropertiesLoaderUtils.loadProperties(new ClassPathResource("storeMapping.properties")).getProperty(storeId).split(","));
			this.setStoreId(values.get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /*public List<File> writeToCsv(List<ResBean> responseList){
    	
    		List<File> fileList = new ArrayList<File>();
    		
    		fileList.add(writeToPayment(responseList.get(0)));
    		
    		fileList.add(writeToItemRs(responseList.get(1)));
    		
    		fileList.add(writeToCKHeader(responseList.get(2)));
    		
    		fileList.add(writeToMsMember(responseList.get(3)));
    		
    		//Returning list of generated files
    		return fileList;
    }*/
    
    public File writeToPayment(ResBean response){
    	
    	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
    	CsvSchema.Builder schemaBuilder = CsvSchema.builder();
    	File file = new File("/Users/subhankarmaitra/Documents/PHI BI Integration Git Repo/PAYMENT.csv");
    	for(HashMap<String,String> map:response.getData()){
    		
    		HashMap<String,String> csvMap = new HashMap<String, String>();
    		
    		//date format dd/mm/yy from 2017-09-19
    		StringBuilder input = new StringBuilder();
    		input.append(map.get("OpenDate.Typed").substring(8)).append("/").
    		append(map.get("OpenDate.Typed").substring(5, 7)).append("/").append(map.get("OpenDate.Typed").substring(2, 4));
    		System.out.println(input);
    		csvMap.put("Tgl_Transaksi",input.toString());
    		
    		csvMap.put("No_Check", map.get("OrderNum"));
    		csvMap.put("Kode_outlet", getStoreId());
    		//time in format hh:mm:ss
    		csvMap.put("Jam_Bayar", map.get("Delivery.BillTime")!=null?map.get("Delivery.BillTime").substring(11,19):null);
    		csvMap.put("Tipe_Bayar", map.get("PayTypes"));
    		csvMap.put("Nilai_Pembayaran", map.get("DishDiscountSumInt"));
    		csvMap.put("Nilai_Tip", "---Tidak diatur di iiko---");
    		csvMap.put("Nomor Kartu", map.get("CardNumber"));
    		csvMap.put("Pemilik_Kartu", map.get("CardOwner"));
    		csvMap.put("Bayar_Cashnya", "---tidak didukung di iiko---");
    		csvMap.put("Hapus_Payment", map.get("Storned")==null?"0":"1");
    		data.add(csvMap);
    	}
    //data.stream().forEach(System.out::println);
    	List<String> headers = Arrays.asList("Tgl_Transaksi","No_Check","Kode_outlet","Jam_Bayar","Tipe_Bayar","Nilai_Pembayaran",
    			"Nilai_Tip","Nomor Kartu","Pemilik_Kartu","Bayar_Cashnya","Hapus_Payment");
    	if (data!= null && !data.isEmpty()) {
            for (String col : headers) {
            		//System.out.println(col); 
                schemaBuilder.addColumn(col);
            }
            CsvSchema schema = schemaBuilder.build().withLineSeparator("\r").withHeader();
    
    	try{ 
    			FileWriter fw = new FileWriter(file);	        
	        CsvMapper mapper = new CsvMapper();
	        mapper.writer(schema).writeValues(fw).writeAll(data);
	        fw.flush();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
    	}
    	System.out.println("++++++++++++++++++++++++++++++PAYMENT.CSV file created++++++++++++++++++++++++++++++++++");
    	return file;
    }	
	
    public File writeToItemRs(ResBean response) {
    	
    	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
    	CsvSchema.Builder schemaBuilder = CsvSchema.builder();
    	List<String> mealDeals = Arrays.asList("iikoCard5","mealDeal1","mealDeal2","mealDeal3");
    	File file = new File("/Users/subhankarmaitra/Documents/PHI BI Integration Git Repo/ITEMRS.csv");
    	
    	for(HashMap<String,String> map:response.getData()){
    		
    		HashMap<String,String> csvMap = new HashMap<String, String>();
    		
    		//date format dd/mm/yy from 2017-09-19
    		StringBuilder input = new StringBuilder();
    		input.append(map.get("OpenDate.Typed").substring(8)).append("/").
    		append(map.get("OpenDate.Typed").substring(5, 7)).append("/").append(map.get("OpenDate.Typed").substring(2, 4));
    		System.out.println(input);
    		csvMap.put("Tgl_Transaksi",input.toString());
    		
    		csvMap.put("No_Check", map.get("OrderNum"));
    		csvMap.put("Kode_outlet", getStoreId());
    		csvMap.put("Set_Menu", mealDeals.contains(map.get("OrderDiscount.Type"))==false?"0":"1");
    		csvMap.put("Kode_Menu",map.get("DishCode"));
    		csvMap.put("Harga Satuan",map.get("DishDiscountSumInt"));
    		csvMap.put("Qty",map.get("DishAmountInt")); 
    		price=Math.multiplyExact(Integer.parseInt(map.get("DishAmountInt")), Integer.parseInt(map.get("DishDiscountSumInt")));
    		csvMap.put("Total",Integer.toString(price));
    		csvMap.put("Kode_DiscItem",map.get("OrderDiscount.Type.IDs"));
    		csvMap.put("Nilai_DiscItem",map.get("OrderDiscount.Type"));
    		csvMap.put("Kd_Tax",map.get("DishTaxCategory.id"));
    		csvMap.put("Nilai_Tax",map.get("VAT.Sum"));
    		csvMap.put("Nila_SC",map.get("IncreaseSum"));
    		
    		//2017-09-19T16:00:15 -> 19/09/2017
    		input = new StringBuilder();
    		input.append(map.get("OpenTime").substring(8,10)).append("/").
    		append(map.get("OpenDate.Typed").substring(5, 7)).append("/").append(map.get("OpenDate.Typed").substring(0, 4));
    		System.out.println(input);
    		csvMap.put("Tgl_Masuk",input.toString());
    		//2017-09-19T16:03:10.641 -> 16:03:10
    		csvMap.put("Jam_Masuk",map.get("CloseTime").substring(11,19));
    		csvMap.put("Tty_ID",map.get("CashRegisterName.Number"));
    		csvMap.put("Item_ID","-");
    		csvMap.put("Hapus_Itemtrs",map.get("DeletedWithWriteoff").equalsIgnoreCase("NOT_DELETED")?"0":"1");
    		csvMap.put("Diskon_Bon",map.get("DiscountSum"));
    		data.add(csvMap);
    	}
    //data.stream().forEach(System.out::println);
    	List<String> headers = Arrays.asList("Tgl_Transaksi","No_Check","Kode_outlet","Set_Menu","Kode_Menu","Harga Satuan","Qty",
    			"Total","Kode_DiscItem","Nilai_DiscItem","Kd_Tax","Nilai_Tax","Nila_SC","Tgl_Masuk","Jam_Masuk","Tty_ID","Item_ID",
    			"Hapus_Itemtrs","Diskon_Bon");
    	if (data != null && !data.isEmpty()) {
            for (String col : headers) {
            		//System.out.println(col); 
                schemaBuilder.addColumn(col);
            }
    
    	try{ 
    			FileWriter fw1 = new FileWriter(file);
	        //System.out.println(listOfResponse.get(0).keySet().size());	
    			CsvSchema schema = schemaBuilder.build().withLineSeparator("\r").withHeader();	        
	        CsvMapper mapper = new CsvMapper();
	        mapper.writer(schema).writeValues(fw1).writeAll(data);
	        fw1.flush();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
    	}
    	System.out.println("++++++++++++++++++++++++++++++ITEMRS.CSV file created++++++++++++++++++++++++++++++++++");
    	return file;
	}
	
	public File writeToCKHeader(ResBean response) {
		
		
    	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
    	CsvSchema.Builder schemaBuilder = CsvSchema.builder();
    	File file = new File("/Users/subhankarmaitra/Documents/PHI BI Integration Git Repo/CKHEADER.csv");
    	
    	for(HashMap<String,String> map:response.getData()){
    		
    		HashMap<String,String> csvMap = new HashMap<String, String>();
    		
    		//date format dd/mm/yy from 2017-09-19
    		StringBuilder input = new StringBuilder();
    		input.append(map.get("OpenDate.Typed").substring(8)).append("/").
    		append(map.get("OpenDate.Typed").substring(5, 7)).append("/").append(map.get("OpenDate.Typed").substring(2, 4));
    		System.out.println(input);
    		csvMap.put("Tgl_Transaksi", input.toString());
    		
			String transType = null;
			if(map.get("Delivery.IsDelivery").equalsIgnoreCase("ORDER_WITHOUT_DELIVERY") &&
			map.get("Delivery.ServiceType") == null){
				transType = "1";
			}else if(map.get("Delivery.IsDelivery").equalsIgnoreCase("DELIVERY_ORDER") && 
			map.get("Delivery.ServiceType").equalsIgnoreCase("COURIER")){
				transType = "3";
			}else if(map.get("Delivery.IsDelivery").equalsIgnoreCase("DELIVERY_ORDER") && 
			map.get("Delivery.ServiceType").equalsIgnoreCase("PICKUP")){
				transType = "2";
			}
			else if(map.get("Delivery.IsDelivery").equalsIgnoreCase("ORDER_WITHOUT_DELIVERY") && 
			map.get("Delivery.ServiceType") == null && map.get("OrderType").equalsIgnoreCase("Express")){
				transType = "4";
			}
			else{
				//Condition for any other type
				transType = "5";
			}
			
		csvMap.put("Tipe_Transaksi", transType);
    		csvMap.put("No_Check", map.get("OrderNum"));
    		csvMap.put("Kode_outlet", getStoreId());
    		csvMap.put("Jumlah_Tamu", map.get("GuestNum"));
    		csvMap.put("Jam_Masuk",map.get("OpenTime").substring(11));
    		csvMap.put("Jam_Keluar",map.get("CloseTime").substring(11,19)); 
    		csvMap.put("Subtotal",map.get("DishDiscountSumInt"));
    		csvMap.put("Total_Discount",map.get("DiscountSum"));
    		csvMap.put("Kode_Discount",map.get("OrderDiscount.Type.IDs"));
    		csvMap.put("Nilai_Pajak",map.get("VAT.Sum"));
    		csvMap.put("Service_charge",map.get("IncreaseSum"));
    		csvMap.put("Telp. Customer",map.get("Delivery.Phone"));
    		csvMap.put("Nama Customer",map.get("Delivery.CustomerName"));
    		csvMap.put("Hapus_ckheader",map.get("Storned")==null?"0":"1");
    		csvMap.put("Jam_Cetak", map.get("CloseTime").substring(11,19));
    		csvMap.put("ID_Delman",map.get("Delivery.Courier.Id"));
    		csvMap.put("Jumlah_Antaran","1");
    		csvMap.put("Podding",map.get("Delivery.Index"));
    		data.add(csvMap);
    	}
    	//data.stream().forEach(System.out::println);
    	List<String> headers = Arrays.asList("Tgl_Transaksi","Tipe_Transaksi","No_Check","Kode_outlet","Jumlah_Tamu","Jam_Masuk",
    			"Jam_Keluar","Subtotal","Total_Discount","Kode_Discount","Nilai_Pajak","Service_charge","Telp. Customer","Nama Customer",
    			"Hapus_ckheader","Jam_Cetak","ID_Delman","Jumlah_Antaran","Podding");
    	if (data != null && !data.isEmpty()) {
            for (String col : headers) {
                schemaBuilder.addColumn(col);
            }
    
    	try{ 
    		Writer fw2 = new OutputStreamWriter(new FileOutputStream(file),StandardCharsets.UTF_8);   
    		CsvSchema schema = schemaBuilder.build().withLineSeparator("\r").withHeader();	        
	        CsvMapper mapper = new CsvMapper();
	        mapper.writer(schema).writeValues(fw2).writeAll(data);
	        fw2.flush();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
    	}
    	System.out.println("++++++++++++++++++++++++++++++CKHEADER.CSV file created++++++++++++++++++++++++++++++++++");
    	return file;
	}	
	
	public File writeToMsMember(ResBean response){
		
		
    	ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
    	CsvSchema.Builder schemaBuilder = CsvSchema.builder();
    	File file = new File("/Users/subhankarmaitra/Documents/PHI BI Integration Git Repo/MSMEMBER.csv");
    	
    	for(HashMap<String,String> map:response.getData()){
    		
    		HashMap<String,String> csvMap = new HashMap<String, String>();
    		csvMap.put("No.Telp", map.get("Delivery.Phone"));
    		csvMap.put("Kode_Outlet", getStoreId());
    		csvMap.put("Nama Customer", map.get("Delivery.CustomerName"));
    		csvMap.put("Alamat", map.get("Delivery.Address"));
    		csvMap.put("Podding",map.get("Delivery.Index"));
    		data.add(csvMap);
    	}
    	
    	//data.stream().forEach(System.out::println);
    	List<String> headers = Arrays.asList("No.Telp","Kode_Outlet","Nama Customer","Alamat","Podding");
    	if (data != null && !data.isEmpty()) {
            for (String col : headers) {
                schemaBuilder.addColumn(col);
            }
    
    	try{ 
    		FileWriter fw3 = new FileWriter(file);      
    		CsvSchema schema = schemaBuilder.build().withLineSeparator("\r").withHeader();	        
	        CsvMapper mapper = new CsvMapper();
	        mapper.writer(schema).writeValues(fw3).writeAll(data);
	        fw3.flush();
		}catch(IOException ioe){
			System.out.println(ioe.getMessage());
		}
    	}
    	System.out.println("++++++++++++++++++++++++++++++MSMEMBER.CSV file created++++++++++++++++++++++++++++++++++");
    	return file;
	}
	
}
