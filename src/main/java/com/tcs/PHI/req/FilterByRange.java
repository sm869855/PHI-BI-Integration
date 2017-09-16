
package com.tcs.PHI.req;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "filterType",
    "periodType",
    "from",
    "to",
    "includeLow",
    "includeHigh"
})
public class FilterByRange {
	@JsonIgnore
	private String pattern="yyyy-MM-dd'T'HH:mm:ss.SSS";
	@JsonIgnore
	private DateFormat sdf= new SimpleDateFormat(pattern);
	@JsonIgnore
	private String today;
	@JsonIgnore
	private String yesterday;

	
	

    @JsonProperty("filterType")
    private String filterType;
    @JsonProperty("periodType")
    private String periodType;
    @JsonProperty("from")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date from;
    @JsonProperty("to")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date to;
    @JsonProperty("includeLow")
    private Boolean includeLow;
    @JsonProperty("includeHigh")
    private Boolean includeHigh;
        

    /**
     * No args constructor for use in serialization
     * 
     */

    
    public FilterByRange(){
    }

    public FilterByRange(String filterType, String periodtype,Date from, Date to) {
        this.filterType = filterType;
        this.periodType=periodtype;
        this.from = from;
        this.to = to;
        this.includeLow = true;
        this.includeHigh = false;
    }
    
    public FilterByRange(String filterType,String periodtype, Date from, Date to, Boolean includeLow, Boolean includeHigh) {
        this.filterType = filterType;
        this.periodType=periodtype;
        this.from = from;
        this.to = to;
        this.includeLow = includeLow;
        this.includeHigh = includeHigh;
    }

    public String getPattern() {
		return pattern;
	}

	public String getToday() {
		Calendar date=Calendar.getInstance();
		this.today=this.sdf.format(date.getTime());
		return today;
	}

	public String getYesterday() {
		Calendar date=Calendar.getInstance();
		this.yesterday=this.sdf.format(date.getTime());
		date.add(Calendar.DATE, -1);
		return yesterday;
	}

	public DateFormat getSdf() {
		return sdf;
	}

	@JsonProperty("filterType")
    public String getFilterType() {
        return filterType;
    }

    @JsonProperty("filterType")
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }
    @JsonProperty("periodType")
    public String getPeriodType() {
		return periodType;
	}
    @JsonProperty("periodType")
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	@JsonProperty("from")
    public Date getFrom() {
        return from;
    }

    @JsonProperty("from")
    public void setFrom(Date from) {
        this.from = from;
    }

    //String arg to date
    public void addFromDate(String date) throws ParseException{
    	this.setFrom(this.getSdf().parse(date));
    }

    @JsonProperty("to")
    public Date getTo() {
        return to;
    }

    @JsonProperty("to")
    public void setTo(Date to) {
        this.to = to;
    }
    
    //String arg to date
    public void addToDate(String date) throws ParseException{
    	this.setTo(this.getSdf().parse(date));
    }

    @JsonProperty("includeLow")
    public Boolean getIncludeLow() {
        return includeLow;
    }

    @JsonProperty("includeLow")
    public void setIncludeLow(Boolean includeLow) {
        this.includeLow = includeLow;
    }

    @JsonProperty("includeHigh")
    public Boolean getIncludeHigh() {
        return includeHigh;
    }

    @JsonProperty("includeHigh")
    public void setIncludeHigh(Boolean includeHigh) {
        this.includeHigh = includeHigh;
    }



}
