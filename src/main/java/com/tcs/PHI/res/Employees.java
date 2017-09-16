package com.tcs.PHI.res;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employees
{
private ArrayList<Employee> Employees;
Employee Employee ;

Employees(){}
public Employees(Employee e1){
	this.Employee=e1;
}
@XmlElement
public ArrayList<Employee> getEmployees ()
{
	
	return Employees;
}

public void setEmployees (Employee employee)
{
	Employees.add(employee);
}

@Override
public String toString()
{
	if(Employees.isEmpty()!=true)
	for (Employee e:Employees)
		return "this is  [employee = "+e ;
	return "none there";
}
}
