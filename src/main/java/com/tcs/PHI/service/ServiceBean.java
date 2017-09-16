package com.tcs.PHI.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.tcs.PHI.fileWriter.CsvWriter;
import com.tcs.PHI.fileWriter.PolWriter;
import com.tcs.PHI.req.ReqBean;
import com.tcs.PHI.res.ResBean;

@Service
public class ServiceBean {
	
	private static final Logger log= LoggerFactory.getLogger(ServiceBean.class);
	public String UAT_HOST; //= "https://16735-meta-uat.iiko.it/resto/api/";
	public String UAT_HOST_V2; //= "https://16735-meta-uat.iiko.it/resto/api/v2/";
	public String UAT_AUTH; //= "https://16735-meta-uat.iiko.it/resto/api/auth?login=admin&pass=2155245b2c002a1986d3f384af93be813537a476";
	public String UAT_OLAP; //= "https://16735-meta-uat.iiko.it/resto/api/v2/reports/olap?key=";
	public String UAT_COUNTRY;
	
	private ReqBean request;
	private Map<String,ReqBean> requestMap;
	private List<ResBean> responseList = new ArrayList<ResBean>();
	private RestTemplate template  = new RestTemplate();	
	private PolWriter polWriter;
	private CsvWriter csvWriter;
	//private String storeId;
	
	public ServiceBean(String countryName,String storeId) {
		//this.storeId = storeId;
		requestMap = new HashMap<String,ReqBean>();
		Resource resource = new ClassPathResource(countryName+".properties");
		Properties props;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
			UAT_HOST = props.getProperty("UAT_HOST");
			UAT_HOST_V2 = props.getProperty("UAT_HOST_V2");
			UAT_AUTH = props.getProperty("UAT_AUTH");
			UAT_OLAP = props.getProperty("UAT_OLAP");
			UAT_COUNTRY = props.getProperty("UAT_COUNTRY");
			if(countryName.equalsIgnoreCase("UAE")){
				polWriter = new PolWriter(storeId);
			}else if(countryName.equalsIgnoreCase("IDN")){
				csvWriter = new CsvWriter(storeId);
			}
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
			requestMap.put("DCR",createRequestForDCR());
			/*requestMap.put("TLG",service.createRequestForTLG());
			requestMap.put("ESUM",service.createRequestForESUM());
			requestMap.put("CIO",service.createRequestForCIO());
			requestMap.put("COG",service.createRequestForCOG());*/
		}else if(UAT_COUNTRY.equalsIgnoreCase("IDN")) {
			System.out.println("##########################################"); 
			requestMap = new HashMap<String,ReqBean>();
			requestMap.put("PAYMENT",createRequestForPAYMENT());			
			/*requestMap.put("ITEMRS",service.createRequestForITEMRS());
			requestMap.put("MSMEMBER",service.createRequestForMSMEMBER());
			requestMap.put("CKHEADER",service.createRequestForCKHEADER());*/
		}
		
		System.out.println(this.requestMap.get("PAYMENT").toString());
	}
	
	
	
	public void fetchResponseList(){
		
		//ServiceBean service = new ServiceBean();
		//for(Map.Entry<String, List<ReqBean>> entry : this.requestMap.entrySet()){
		
		for(Map.Entry<String, ReqBean> entry : this.requestMap.entrySet()){
		
			//writing req
			String json = null;
			try {
				json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(entry.getValue());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			//System.out.println(json);
			String url = UAT_OLAP.concat(getToken());
			System.out.println("++++++++++++++++++++++++"+url+"++++++++++++++++++++++++");
			ResBean response=fetchJsonFromPostResponse(url,entry.getValue()); //fetching response
			System.out.println(response.toString());
			/*ResBean response=service.fetchJsonFromPostResponse(url,entry.getValue().get(0));this.responseList.add(response);
			ResBean response=service.fetchJsonFromPostResponse(url,entry.getValue().get(1));this.responseList.add(response);
			ResBean response=service.fetchJsonFromPostResponse(url,entry.getValue().get(2));this.responseList.add(response);*/
			
		    this.responseList.add(response);
		
		    //writing res
		    for(HashMap<String,String> data: response.getData()){
		    	System.out.println(data);
		    }
		}
		
		
	}
	
	public void createFiles(){
		if(UAT_COUNTRY.equalsIgnoreCase("UAE")){
			polWriter.writeAll(this.responseList);
			
		}else if(UAT_COUNTRY.equalsIgnoreCase("IDN")){
			csvWriter.writeToCsv(this.responseList);
		}
		//else for other countries to be added hereon......		
		
	}
	
	
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
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-13T00:00:00.000");
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
	
	public ReqBean createRequestForPAYMENT(){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","Delivery.BillTime",
				"PayTypes","CardNumber","CardOwner"),Arrays.asList("DishDiscountSumInt"));
		try {
			request.addDefaultFilterByRange("2017-01-01T00:00:00.000","2017-09-12T00:00:00.000");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		request.addDefaultFilterByValues();
		return request;
	}
}
