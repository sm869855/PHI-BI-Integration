package com.tcs.PHI.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	public String from, to;
	
	private ReqBean request;
	private ResBean response;
	private RestTemplate template  = new RestTemplate();
	private CsvWriter csvWriter;
	private String storeId;
	private Properties props, storeProps, sftpProps;
	private List<String> storeSpecific;
	
	public ServiceBean(String storeId) {
		this.storeId = storeId;
		csvWriter = new CsvWriter(storeId);
	}
	
	public void doEverything() {
			
				
		/*List<ReqBean> requestList = Arrays.asList(createRequestForPAYMENT(from,to),createRequestForITEMRS(from,to),
						createRequestForCKHEADER(from,to),createRequestForMSMEMBER(from,to));
		List<ResBean> responseList = fetchResponseList(requestList);
		List<File> fileList = csvWriter.writeToCsv(responseList);	*/
				
				/*
				 * Fetching response in json format directly from API calls
				 */
				
				List<File> fileList1 = Arrays.asList(		
						csvWriter.writeToPayment(this.fetchResponseFromApi(createRequestForPAYMENT(from,to))),
						csvWriter.writeToItemRs(this.fetchResponseFromApi(createRequestForITEMRS(from,to))),
						csvWriter.writeToCKHeader(this.fetchResponseFromApi(createRequestForCKHEADER(from,to)))
				);
				
				
				
				/*
				 * Reading response from Dummy json files & then writing to csv files
				 */
				/*new File("/abc.txt");
		
				List<File> fileList2 = Arrays.asList(
						csvWriter.writeToPayment(this.fetchResponseFromJsonFile("payment_response.json")),
						csvWriter.writeToItemRs(this.fetchResponseFromJsonFile("itemrs_response.json")),
						csvWriter.writeToCKHeader(this.fetchResponseFromJsonFile("ckheader_response.json"))
				);*/
				
				
				
				//Zipping
				File file = new ZipMaker().compressToZip(fileList1, storeSpecific.get(0)); 
				System.out.println("Zip file created in "+file.getAbsolutePath());
				
				//SFTPing
				SFTPHandler ftp = new SFTPHandler(sftpProps.getProperty("SFTP_HOST"),
						Integer.parseInt(sftpProps.getProperty("SFTP_PORT")),sftpProps.getProperty("SFTP_USER"),
						sftpProps.getProperty("SFTP_PWD"),sftpProps.getProperty("SFTP_HOSTDIR"));
				ftp.sendFile(file);
			
		
	}
	
	public void fetchValuesfromPropertiesFile(String pFile1, String pFile2, String pFile3) {
		
		try {
			props = PropertiesLoaderUtils.loadProperties(new ClassPathResource(pFile1));
			UAT_HOST = props.getProperty("UAT_HOST");
			UAT_HOST_V2 = props.getProperty("UAT_HOST_V2");
			UAT_AUTH = props.getProperty("UAT_AUTH");
			UAT_OLAP = props.getProperty("UAT_OLAP");
			UAT_COUNTRY = props.getProperty("UAT_COUNTRY");
			
			storeProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource(pFile2));
			sftpProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource(pFile3));
			storeSpecific = Arrays.asList(storeProps.getProperty(storeId).split(","));
			from = storeSpecific.get(1);to = storeSpecific.get(2);
			LocalDateTime ldt = LocalDateTime.now();
			from = ldt.minusDays(1).toString().substring(0, 11).concat(from).concat(":").concat("00:").concat("00.000");
			to = ldt.toString().substring(0, 11).concat(to).concat(":").concat("00:").concat("00.000");
		} catch (IOException e) {
			e.printStackTrace();
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
	
	
	public List<ResBean> fetchResponseList(List<ReqBean> requestList){
		
		String url = UAT_OLAP.concat(getToken());
		List<ResBean> responseList = new ArrayList<ResBean>();
		
		for(ReqBean req: requestList){
		
			/*
			 * writing request to console
			 */
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
			
			/*
			 * Fetching response directly from API call
			 */
			
			try {
				response=fetchJsonFromPostResponse(url,req);
				System.out.println("Response Received:\n");
				System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getData()));
				responseList.add(response);
			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		//End of forEach loop	
		}
		return responseList;
	}
	
	public ResBean fetchResponseFromJsonFile(String fileName) {

				/*
				 * Fetching response by reading a dummy json file
				 */
			
				try {
					response = parseJsonToBean(fileName);
					System.out.println("Response Received:\n");
					System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response.getData()));
					
				} catch (JsonParseException e1) {
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		return response;
	}
	/*
	 * Fetching json format API response for a single request
	 */
	public ResBean fetchResponseFromApi(ReqBean request){
		
		String url = UAT_OLAP.concat(getToken());
		return fetchJsonFromPostResponse(url,request);
	}
	
	
	/*
	 * Read a json file into a Response bean(ResBean)
	 */
	public ResBean parseJsonToBean(String fileName) 
			throws JsonParseException, JsonMappingException, IOException {
		//ClassLoader loader = getClass().getClassLoader();
		ObjectMapper objm= new ObjectMapper();
		File file = ResourceUtils.getFile("classpath:"+fileName);
		ResBean res = objm.readValue(file,new TypeReference<ResBean>(){}); 
		return res;
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
	 public ReqBean createRequestForPAYMENT(String from, String to){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","Delivery.BillTime",
				"PayTypes","CardNumber","CardOwner","Storned"),Arrays.asList("DishDiscountSumInt"));
		request.addDefaultFilterByRangeforEntireYear();
		request.addDefaultFilterByValues();
//	    try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}
	
	public ReqBean createRequestForITEMRS(String from, String to){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","OrderDiscount.Type.IDs",
				"OrderDiscount.Type","DishTaxCategory.Id","VAT.Sum","IncreaseSum","OpenTime","CloseTime",
				"CashRegisterName.Number","DeletedWithWriteoff"),
				Arrays.asList("DishDiscountSumInt","DishAmountInt","DiscountSum"));
		request.addDefaultFilterByRangeforEntireYear();
		request.addDefaultFilterByValues();
//	     try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}
	
	public ReqBean createRequestForCKHEADER(String from, String to){
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","OrderNum","Delivery.IsDelivery",
				"Delivery.ServiceType","OrderType","GuestNum","OpenTime","CloseTime","OrderDiscount.Type.IDs",
				"VAT.Sum","IncreaseSum","Delivery.Phone","Delivery.CustomerName","Storned","HourClose",
				"Delivery.Courier.Id","Delivery.Index"),
				Arrays.asList("DishDiscountSumInt","DiscountSum"));
		request.addDefaultFilterByRangeforEntireYear();
		request.addDefaultFilterByValues();
//	     try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}
	
	public ReqBean createRequestForMSMEMBER(String from, String to){
		String[] str= {};
		ReqBean request = new ReqBean("SALES",Arrays.asList("OpenDate.Typed","Delivery.Phone","Delivery.CustomerName",
				"Delivery.Address","Delivery.Index"),Arrays.asList(str));
		request.addDefaultFilterByRangeforEntireYear();
		request.addDefaultFilterByValues();
//	     try {
//			System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return request;
	}	
}
