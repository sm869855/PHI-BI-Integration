
package com.tcs.PHI.req;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "reportType",
    "groupByRowFields",
    "groupByColFields",
    "aggregateFields",
    "filters"
})
@Component
public class ReqBean {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("reportType")
    private String reportType;
    @JsonProperty("groupByRowFields")
    private List<String> groupByRowFields = new ArrayList<String>();
    @JsonProperty("groupByColFields")
    private List<String> groupByColFields = new ArrayList<String>();
    @JsonProperty("aggregateFields")
    private List<String> aggregateFields = new ArrayList<String>();
    @JsonProperty("filters")
    private Map<String, Object> filters = new HashMap<String, Object>();
    private boolean storned = false, deletedWithWriteOff = false , orderDeleted = false;

    public ReqBean() {
    	
    	//Setting Default Filters
    	
    	
    }
    
    public ReqBean(String reportType, List<String> groupByRowFields,List<String> aggregateFields){
    	 super();
    	 this.reportType = reportType;
     this.groupByRowFields = groupByRowFields;
     this.aggregateFields = aggregateFields;
     
    }
    

    /**
     * 
     * @param groupByColFields
     * @param id
     * @param reportType
     * @param groupByRowFields
     * @param name
     * @param aggregateFields
     * @param filters
     */
    public ReqBean(String id, String name, String reportType, List<String> groupByRowFields, List<String> groupByColFields, List<String> aggregateFields, Map<String, Object> filters) {
        super();
        this.id = id;
        this.name = name;
        this.reportType = reportType;
        this.groupByRowFields = groupByRowFields;
        this.groupByColFields = groupByColFields;
        this.aggregateFields = aggregateFields;
        this.filters = filters;
    }
    
    //without  groupByColFields
    
    public ReqBean(String id, String name, String reportType, List<String> groupByRowFields, List<String> aggregateFields, Map<String, Object> filters) {
        super();
        this.id = id;
        this.name = name;
        this.reportType = reportType;
        this.groupByRowFields = groupByRowFields;        
        this.aggregateFields = aggregateFields;
        this.filters = filters;
    }
    
    //without id, name, groupByColFields
  
    public ReqBean (String reportType, List<String> groupByRowFields, List<String> aggregateFields, Map<String, Object> filters) {
        super();
        this.reportType = reportType;
        this.groupByRowFields = groupByRowFields;        
        this.aggregateFields = aggregateFields;
        this.filters = filters;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("reportType")
    public String getReportType() {
        return reportType;
    }

    @JsonProperty("reportType")
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    @JsonProperty("groupByRowFields")
    public List<String> getGroupByRowFields() {
        return groupByRowFields;
    }

    @JsonProperty("groupByRowFields")
    public void setGroupByRowFields(List<String> groupByRowFields) {
        this.groupByRowFields = groupByRowFields;
    }

    @JsonProperty("groupByColFields")
    public List<String> getGroupByColFields() {
        return groupByColFields;
    }

    @JsonProperty("groupByColFields")
    public void setGroupByColFields(List<String> groupByColFields) {
        this.groupByColFields = groupByColFields;
    }

    @JsonProperty("aggregateFields")
    public List<String> getAggregateFields() {
        return aggregateFields;
    }

    @JsonProperty("aggregateFields")
    public void setAggregateFields(List<String> aggregateFields) {
        this.aggregateFields = aggregateFields;
    }

    @JsonProperty("filters")
    public Map<String, Object> getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    /*
     * *****************************adding aggregate fields****************************
     */
    public void addAggregateField(String... s){
    	this.getAggregateFields().addAll(Arrays.asList(s));
    }
    
    /*
     * **********************************adding GroupByColFields*************************
     */
    public void addGroupByColField(String... s){
    	this.getGroupByColFields().addAll(Arrays.asList(s));
    }
    
    /*
     * ************************************adding GroupByRowFields****************************
     */
    public void addGroupByRowField(String... s){
    	this.getGroupByRowFields().addAll(Arrays.asList(s));
    }
    
	/*
	 * ****************************************forming filters******************************************************************
	 * ****************************************date @format-yyyy-MM-dd'T'HH:mm:ss.SSS**************************************S
	 */
    
    /*
     * ***************Range Filter for the entire year****************
    */
    public void addDefaultFilterByRangeforEntireYear() {
    	FilterByRange filterbyRange=new FilterByRange();
    	filterbyRange.setFilterType("DateRange");
    	filterbyRange.setPeriodType("CUSTOM");
    	filterbyRange.setFrom("2017-01-01T00:00:00.000");
        filterbyRange.setTo(filterbyRange.getToday());
        filterbyRange.setIncludeLow(true);
        filterbyRange.setIncludeHigh(false);
        this.getFilters().put("OpenDate", filterbyRange);
    }
    
    /*
     * ***************Default range Filter i.e. from yesterday midnight to today midnight****************
    */
    public void addDefaultFilterByRange() throws ParseException{
    	FilterByRange filterbyRange=new FilterByRange();
    	filterbyRange.setFilterType("DateRange");
    	filterbyRange.setPeriodType("CUSTOM");
    	filterbyRange.setFrom(filterbyRange.getToday());
        filterbyRange.setTo(filterbyRange.getYesterday());
        filterbyRange.setIncludeLow(true);
        filterbyRange.setIncludeHigh(false);
        this.getFilters().put("OpenDate", filterbyRange);
    }
    /*
     * Date range Filter with 
     * @param -from date @format-yyyy-MM-dd'T'HH:mm:ss.SSS
     * @param- to date	@format-yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    public void addDefaultFilterByRange(String from,String to) {
    		FilterByRange filterbyRange=new FilterByRange();
    		filterbyRange.setFilterType("DateRange");
    		filterbyRange.setPeriodType("CUSTOM");
    		filterbyRange.setFrom(from);
    		filterbyRange.setTo(to);
        filterbyRange.setIncludeLow(true);
        filterbyRange.setIncludeHigh(false);
		this.getFilters().put("OpenDate", filterbyRange);
    }
    
    /*
     * ****************************range filter without passing includeHigh and includeLow***********************
     * ****************************@format-yyyy-MM-dd'T'HH:mm:ss.SSS******************
     */
	public void addFilterByRange(String fieldToFilterByRange,String filterType,String periodtype, String from, String to) throws ParseException{		
		FilterByRange filterbyRange=new FilterByRange();
    	filterbyRange.setFilterType(filterType);
    	filterbyRange.setPeriodType(periodtype);
    	filterbyRange.setFrom(from);
        filterbyRange.setTo(to);
        filterbyRange.setIncludeLow(true);
        filterbyRange.setIncludeHigh(false);
		this.getFilters().put(fieldToFilterByRange, filterbyRange);	
	}
	
	/*
	 * *****************range filter passing all************************
	 * *****************@format-yyyy-MM-dd'T'HH:mm:ss.SSS*************
	 */
	public void addFilterByRange(String fieldToFilterByRange,String filterType,String periodtype, String from, String to, Boolean includeLow, Boolean includeHigh) throws ParseException{		
		FilterByRange filterbyRange=new FilterByRange();
    	filterbyRange.setFilterType(filterType);
    	filterbyRange.setPeriodType(periodtype);
    	filterbyRange.setFrom(from);
        filterbyRange.setTo(to);
        filterbyRange.setIncludeLow(includeLow);
        filterbyRange.setIncludeHigh(includeHigh);
		this.getFilters().put(fieldToFilterByRange, filterbyRange);	
	}
	
	/*
	 * **************Filter by values with all params**************
	 */
	public void addFilterByValues(String fieldToFilterByValues, String filterType, List<String> values){		
		FilterByValues filterByvalues= new FilterByValues(filterType, values);
		this.getFilters().put(fieldToFilterByValues, filterByvalues);
	}
	
	/*
	 * **************Filter by values with @param:fieldName  @param:filterType  @param:filterValue**************
	 */
	public void addFilterByValues(String fieldToFilterByValues, String filterType, String value){		
		ArrayList<String> values= new ArrayList<>();
		values.add(value);
		FilterByValues filterByvalues= new FilterByValues(filterType, values);
		this.getFilters().put(fieldToFilterByValues, filterByvalues);
	}
	
	/*
	 * *****************default normal value filters with Storned ExcludeValues ,DeletedWithWriteoff NOT_DELETED,OrderDeleted NOT_DELETED*********************
	 */
	public void addDefaultFilterByValues(){
		/*
		 * for storned
		 */
		ArrayList<String>  value1= new ArrayList<>();
		value1.add("TRUE");value1.add("FALSE");
		FilterByValues filterByvalues1= new FilterByValues("ExcludeValues", value1);
		this.getFilters().put("Storned", filterByvalues1);
		/*
		 * for DeletedWithWriteoff
		 */
		ArrayList<String>  value2= new ArrayList<>();
		value2.add("NOT_DELETED");
		FilterByValues filterByvalues2= new FilterByValues("IncludeValues", value2);
		this.getFilters().put("DeletedWithWriteoff", filterByvalues2);		
		/*
		 * for OrderDeleted
		 */
		ArrayList<String>  value3= new ArrayList<>();
		value3.add("NOT_DELETED");
		FilterByValues filterByvalues3= new FilterByValues("IncludeValues", value3);
		this.getFilters().put("OrderDeleted", filterByvalues3);		
	}
	public void addFilterByValuesForItemsDeleted(){
		/*
		 * for storned
		 */
		ArrayList<String>  value1= new ArrayList<>();
		value1.add("TRUE");value1.add("FALSE");
		FilterByValues filterByvalues1= new FilterByValues("ExcludeValues", value1);
		this.getFilters().put("Storned", filterByvalues1);
		/*
		 * for DeletedWithWriteoff
		 */
		ArrayList<String>  value2= new ArrayList<>();
		value2.add("NOT_DELETED");
		FilterByValues filterByvalues2= new FilterByValues("ExcludeValues", value2);
		this.getFilters().put("DeletedWithWriteoff", filterByvalues2);
		/*
		 * for OrderDeleted
		 */
		ArrayList<String>  value3= new ArrayList<>();
		value3.add("NOT_DELETED");
		FilterByValues filterByvalues3= new FilterByValues("IncludeValues", value3);
		this.getFilters().put("OrderDeleted", filterByvalues3);		
	}
	public void addFilterByValuesForOrdersDeleted(){
		/*
		 * for storned
		 */
		ArrayList<String>  value1= new ArrayList<>();
		value1.add("TRUE");value1.add("FALSE");
		FilterByValues filterByvalues1= new FilterByValues("ExcludeValues", value1);
		this.getFilters().put("Storned", filterByvalues1);
		/*
		 * for DeletedWithWriteoff
		 */
		ArrayList<String>  value2= new ArrayList<>();
		value2.add("NOT_DELETED");
		FilterByValues filterByvalues2= new FilterByValues("IncludeValues", value2);
		this.getFilters().put("DeletedWithWriteoff", filterByvalues2);
		/*
		 * for OrderDeleted
		 */
		ArrayList<String>  value3= new ArrayList<>();
		value3.add("NOT_DELETED");
		FilterByValues filterByvalues3= new FilterByValues("ExcludeValues", value3);
		this.getFilters().put("OrderDeleted", filterByvalues3);
	}

}
