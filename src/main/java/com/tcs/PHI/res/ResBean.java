
package com.tcs.PHI.res;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "summary"
})
@Component
public class ResBean {

    @JsonProperty("data")
    private ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
    @JsonProperty("summary")
    private ArrayList<ArrayList<HashMap<String,String>>> summary = new ArrayList<ArrayList<HashMap<String,String>>>();
  
    
    public ResBean() {
    }

    /**
     * 
     * @param summary
     * @param data
     */
    public ResBean(ArrayList<HashMap<String,String>> data, ArrayList<ArrayList<HashMap<String,String>>> summary) {
        super();
        this.data = data;
        this.summary = summary;
    }

	public ArrayList<HashMap<String, String>> getData() {
		return data;
	}

	public ArrayList<ArrayList<HashMap<String, String>>> getSummary() {
		return summary;
	}

	
   
}
