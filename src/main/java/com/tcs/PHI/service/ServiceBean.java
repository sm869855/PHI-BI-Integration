package com.tcs.PHI.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.PHI.SFTPhandlerService.SFTPHandler;
import com.tcs.PHI.ZIPcreatorService.ZipMaker;
import com.tcs.PHI.fileWriter.CsvWriter;
import com.tcs.PHI.req.ReqBean;
import com.tcs.PHI.res.ResBean;


@Service
public class ServiceBean {
	
	private static final Logger log= LoggerFactory.getLogger(ServiceBean.class);
	public String UAT_HOST;
	public String UAT_HOST_V2; 
	public String UAT_AUTH;
	public String UAT_OLAP;
	public String UAT_COUNTRY;
	Properties props, storeProps, sftpProps;
	private ReqBean request;
	private RestTemplate template  = new RestTemplate();
	private CsvWriter csvWriter;
	
	public ServiceBean(String storeId) {
		
		try {
			props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("IDN.properties"));
			UAT_HOST = props.getProperty("UAT_HOST");
			UAT_HOST_V2 = props.getProperty("UAT_HOST_V2");
			UAT_AUTH = props.getProperty("UAT_AUTH");
			UAT_OLAP = props.getProperty("UAT_OLAP");
			UAT_COUNTRY = props.getProperty("UAT_COUNTRY");
			
			storeProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("storeMapping.properties"));
			sftpProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("sftp.properties"));
			
			
				List<ReqBean> requestList = Arrays.asList(createRequestForPAYMENT(),createRequestForITEMRS(),createRequestForCKHEADER(),createRequestForMSMEMBER());
				List<ResBean> responseList = fetchResponseList(requestList);
				csvWriter = new CsvWriter(storeId);
				//String filePath = csvWriter.writeToCsv(responseList);
				List<File> fileList = csvWriter.writeToCsv(responseList);
				File file = new ZipMaker().compressToZip(fileList, storeProps.getProperty(storeId));
				System.out.println("Zip file created in "+file.getAbsolutePath());
				
//				String SFTPHOST = "202.57.2.229";
//			    int SFTPPORT = 22;
//			    String SFTPUSER = "iikophid";
//			    String SFTPPASS = "19iikophid";
//			    String SFTPWORKINGDIR = "/iikophid/";
				
	SFTPHandler ftp = new SFTPHandler(sftpProps.getProperty("SFTP_HOST"),Integer.parseInt(sftpProps.getProperty("SFTP_PORT")),
				sftpProps.getProperty("SFTP_USER"),sftpProps.getProperty("SFTP_PWD"),sftpProps.getProperty("SFTP_HOSTDIR"));
				ftp.sendFile(file);
			
		} catch (IOException ioe) {
			ioe.getMessage();
		}		
	}
	
	/*
	 * to fetch API authentication token
	 */
	public String getToken() {
		String url=UAT_AUTH;
		String q= template.getForObject(url, String.class);
		log.info(q);
		return q;
		
	}
	/*
	 * Token generation from @param URL and @param loginId @param pwd
	 */
	public String getToken(String partialUrl,String loginId,String password) {
		String url=partialUrl+"?login="+loginId+"&pass="+password;
		String q= template.getForObject(url, String.class);
		log.info(q);
		return q;
	}
	
	/*
	 * to create json Requests
	 */	
	public void createRequestList(){
		
		System.out.println(UAT_COUNTRY);
		
		if(UAT_COUNTRY.equalsIgnoreCase("UAE")){			
			//requestMap.put("DCR",createRequestForDCR());
			/*requestMap.put("TLG",service.createRequestForTLG());
			requestMap.put("ESUM",service.createRequestForESUM());
			requestMap.put("CIO",service.createRequestForCIO());
			requestMap.put("COG",service.createRequestForCOG());*/
		}else if(UAT_COUNTRY.equalsIgnoreCase("IDN")) {
			System.out.println("##########################################"); 
//			requestMap = new HashMap<String,ReqBean>();
//			requestMap.put("PAYMENT",createRequestForPAYMENT());			
//			requestMap.put("ITEMRS",createRequestForITEMRS());
//			requestMap.put("CKHEADER",createRequestForCKHEADER());
//			requestMap.put("MSMEMBER",createRequestForMSMEMBER());
			//this.requestList = Arrays.asList(createRequestForPAYMENT(),createRequestForITEMRS(),createRequestForCKHEADER(),createRequestForMSMEMBER()); 
		}
		
		//System.out.println(this.requestMap.get("PAYMENT").toString());
	}
	
	
	
	public List<ResBean> fetchResponseList(List<ReqBean> requestList){
		
		//ServiceBean service = new ServiceBean();
		//for(Map.Entry<String, List<ReqBean>> entry : this.requestMap.entrySet()){
		String url = UAT_OLAP.concat(getToken());
		List<ResBean> responseList = new ArrayList<ResBean>();
		for(ReqBean req: requestList){
		
			//writing request to console
			String json = null;
			try {
				json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(req);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			System.out.println("Request posted to iiko:\n"); 
			System.out.println(json);
			System.out.println("++++++++++++++++++++++++URL Fired++++++++++++++++++++++++");
			System.out.println(url);
			ResBean response=fetchJsonFromPostResponse(url,req); //fetching response
			System.out.println("Response Received:\n");
			try {
				System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getData()));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			responseList.add(response);
			/*ResBean response=service.fetchJsonFromPostResponse(url,entry.getValue().get(0));this.responseList.add(response);
			ResBean response=service.fetchJsonFromPostResponse(url,entry.getValue().get(1));this.responseList.add(response);
			ResBean response=service.fetchJsonFromPostResponse(url,entry.getValue().get(2));this.responseList.add(response);*/
		}
		return responseList;
	}
	
//	public void createFiles(){
//		if(UAT_COUNTRY.equalsIgnoreCase("UAE")){
//			polWriter.writeAll(this.responseList);
//			
//		}else if(UAT_COUNTRY.equalsIgnoreCase("IDN")){
//			csvWriter.writeToCsv(this.responseList);
//		}
//		//else for other countries to be added hereon......		
//		
//	}
	
	
	/*
	 * to fetch Get methods response from iiko APIs
	 */
	public ResBean fetchJsonFromGetResponse(String url){
		
		ResBean response=new ResBean();
		response=template.getForObject(url,ResBean.class);
		return response;
	}

	public Object fetchXmlFromGetResponse(String url){
		
		Object object=template.getForObject(url,Object.class);
		return object;
	}	
	/*
	 * to fetch Post methods response from iiko APIs for json api s
	 */	
	public ResBean fetchJsonFromPostResponse(String url,ReqBean req){
		
		ResBean response=new ResBean();	
		 HttpHeaders header= new HttpHeaders();
		 header.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
		 HttpEntity<ReqBean> reqEntity= new HttpEntity<ReqBean>(req,header);
		 ResponseEntity<ResBean> resEntity = template.postForEntity(url,reqEntity,ResBean.class);
		 /*or
		 * ResponseEntity<ResBean> resEntity = rt.exchange(url,HttpMethod.POST,reqEntity,Resbean.class);
		 */
		response= resEntity.getBody();
		return response;
		
	}
	
	/*
	 * to fetch Post methods response from iiko APIs for XML api s
	 */
	
	
	public ResponseEntity<Object> fetchXmlFromPostResponse(String url,Object req){
		
		 HttpHeaders header= new HttpHeaders();
		 header.setContentType(org.springframework.http.MediaType.APPLICATION_XML);
		 HttpEntity<Object> reqEntity= new HttpEntity<Object>(req,header);
		 ResponseEntity<Object> resEntity = template.postForEntity(url,reqEntity,Object.class);
		 /*or
		 * ResponseEntity<ResBean> resEntity = rt.exchange(url,HttpMethod.POST,reqEntity,Resbean.class);
		 */
		return resEntity;
		
	}
	
	/*
	 * forming the request
	 */

	/*
	public ReqBean makeAnyRequest(String reportType, List<String> groupByRowFields, List<String> aggregateFields,Map<String,Object> filters){
			
		
		ReqBean request= new ReqBean(reportType, groupByRowFields, aggregateFields, filters);
		return request;
		
	}*/
	
	public ReqBean createRequestForDCR(){
		//Filters are Default, set in Super Constructor
		ReqBean request = new ReqBean("SALES",Arrays.asList("Department","OpenDate.Typed","PayTypes","OrderDiscount.Type","DishCategory",
				"OrderDiscount.Type","Delivery.IsDelivery","Delivery.ServiceType","Banquet","NonCashPaymentType"),
				Arrays.asList("UniqOrderId","DishReturnSum","VAT.Sum","DiscountSum",
						"DishSumInt","DishDiscountSumInt","DishDiscountSumInt.withoutVAT"));
		try {
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-30T00:00:00.000");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		request.addDefaultFilterByValues();
		return request;
	}
	
	/*public List<ReqBean> createRequestForDCR(){
		List<ReqBean> requestList = new ArrayList<ReqBean>();
		ReqBean request= new ReqBean ();
		request.setReportType("SALES");
		request.setGroupByRowFields(Arrays.asList("Department","OpenDate.Typed",
				"OrderDiscount.Type","Delivery.IsDelivery","Delivery.ServiceType","Banquet","NonCashPaymentType"));
		request.setAggregateFields(Arrays.asList("UniqOrderId","DishReturnSum","VAT.Sum","DiscountSum",
				"DishSumInt","DishDiscountSumInt","DishDiscountSumInt.withoutVAT"));
		requestList.add(request);
		return requestList;
	}*/
	
	public ReqBean createRequestForTLG() {
		
		return request;
	}
	
	public ReqBean createRequestForESUM(){
		
		
		return request;
	}
	
	public ReqBean createRequestForCIO(){
	
		
		return request;
	}
	
	public ReqBean createRequestForCOG(){
		
		
		return request;
	}
	
	
	/*
	 * Methods for creating json request for Indonesia .csv files
	 */
	 public ReqBean createRequestForPAYMENT(){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","Delivery.BillTime",
				"PayTypes","CardNumber","CardOwner","Storned"),Arrays.asList("DishDiscountSumInt"));
		try {
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-30T00:00:00.000");
		 }catch (ParseException e) {
			e.printStackTrace();
		 }
		request.addDefaultFilterByValues();
//	    try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}
	
	public ReqBean createRequestForITEMRS(){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","OrderDiscount.Type.IDs",
				"OrderDiscount.Type","DishTaxCategory.Id","VAT.Sum","IncreaseSum","OpenTime","CloseTime",
				"CashRegisterName.Number","DeletedWithWriteoff"),
				Arrays.asList("DishDiscountSumInt","DishAmountInt","DiscountSum"));
		try {
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-30T00:00:00.000");
		 }catch (ParseException e) {
			e.printStackTrace();
		 }
		request.addDefaultFilterByValues();
//	     try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}
	
	public ReqBean createRequestForCKHEADER(){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","Delivery.IsDelivery",
				"Delivery.ServiceType","OrderType","GuestNum","OpenTime","CloseTime","OrderDiscount.Type.IDs",
				"VAT.Sum","IncreaseSum","Delivery.Phone","Delivery.CustomerName","Storned","HourClose",
				"Delivery.Courier.Id","Delivery.Index"),
				Arrays.asList("DishDiscountSumInt","DiscountSum"));
		try {
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-30T00:00:00.000");
		 }catch (ParseException e) {
			e.printStackTrace();
		 }
		request.addDefaultFilterByValues();
//	     try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}
	
	public ReqBean createRequestForMSMEMBER(){
		String[] str= {};
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","Delivery.Phone","Delivery.CustomerName",
				"Delivery.Address","Delivery.Index"),Arrays.asList(str));
		try {
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-30T00:00:00.000");
		 }catch (ParseException e) {
			e.printStackTrace();
		 }
		request.addDefaultFilterByValues();
//	     try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}	
}
