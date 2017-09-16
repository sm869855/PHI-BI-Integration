package com.tcs.PHI.res;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee {
	
		private String birthday;

	    private String phone;

	    private String client;

	    private String deactivationDate;

	    private String cellPhone;

	    private String activationDate;

	    private String password;

	    private String id;

	    private String pinCode;

	    private String[] roleCodes;

	    private String taxpayerIdNumber;

	    private String name;

	    private String login;

	    private String employee;

	    private String firstName;

	    private String supplier;

	    private String note;

	    private String preferredDepartmentCode;

	    private String middleName;

	    private String lastName;

	    private String hireDate;

	    private String code;

	    private String deleted;

	    private String mainRoleCode;

	    private String[] departmentCodes;

	    private String address;

	    private String email;

	    private String hireDocumentNumber;

	    private String cardNumber;

	    private String[] responsibilityDepartmentCodes;
	     
	    
	    public Employee(String firstName, String middleName, String lastName, String email) {
			super();
			this.firstName = firstName;
			this.middleName = middleName;
			this.lastName = lastName;
			this.email = email;
		}

		@XmlElement
	    public String getBirthday ()
	    {
	        return birthday;
	    }

	    public void setBirthday (String birthday)
	    {
	        this.birthday = birthday;
	    }
	    @XmlElement
	    public String getPhone ()
	    {
	        return phone;
	    }

	    public void setPhone (String phone)
	    {
	        this.phone = phone;
	    }
	    @XmlElement
	    public String getClient ()
	    {
	        return client;
	    }

	    public void setClient (String client)
	    {
	        this.client = client;
	    }
	    @XmlElement
	    public String getDeactivationDate ()
	    {
	        return deactivationDate;
	    }

	    public void setDeactivationDate (String deactivationDate)
	    {
	        this.deactivationDate = deactivationDate;
	    }
	    @XmlElement
	    public String getCellPhone ()
	    {
	        return cellPhone;
	    }

	    public void setCellPhone (String cellPhone)
	    {
	        this.cellPhone = cellPhone;
	    }
	    @XmlElement
	    public String getActivationDate ()
	    {
	        return activationDate;
	    }

	    public void setActivationDate (String activationDate)
	    {
	        this.activationDate = activationDate;
	    }
	    @XmlElement
	    public String getPassword ()
	    {
	        return password;
	    }

	    public void setPassword (String password)
	    {
	        this.password = password;
	    }
	    @XmlElement
	    public String getId ()
	    {
	        return id;
	    }

	    public void setId (String id)
	    {
	        this.id = id;
	    }
	    @XmlElement
	    public String getPinCode ()
	    {
	        return pinCode;
	    }

	    public void setPinCode (String pinCode)
	    {
	        this.pinCode = pinCode;
	    }
	    @XmlElement
	    public String[] getRoleCodes ()
	    {
	        return roleCodes;
	    }

	    public void setRoleCodes (String[] roleCodes)
	    {
	        this.roleCodes = roleCodes;
	    }
	    @XmlElement
	    public String getTaxpayerIdNumber ()
	    {
	        return taxpayerIdNumber;
	    }

	    public void setTaxpayerIdNumber (String taxpayerIdNumber)
	    {
	        this.taxpayerIdNumber = taxpayerIdNumber;
	    }
	    @XmlElement
	    public String getName ()
	    {
	        return name;
	    }

	    public void setName (String name)
	    {
	        this.name = name;
	    }
	    @XmlElement
	    public String getLogin ()
	    {
	        return login;
	    }

	    public void setLogin (String login)
	    {
	        this.login = login;
	    }
	    @XmlElement
	    public String getEmployee ()
	    {
	        return employee;
	    }

	    public void setEmployee (String employee)
	    {
	        this.employee = employee;
	    }
	    @XmlElement
	    public String getFirstName ()
	    {
	        return firstName;
	    }

	    public void setFirstName (String firstName)
	    {
	        this.firstName = firstName;
	    }
	    @XmlElement
	    public String getSupplier ()
	    {
	        return supplier;
	    }

	    public void setSupplier (String supplier)
	    {
	        this.supplier = supplier;
	    }
	    @XmlElement
	    public String getNote ()
	    {
	        return note;
	    }

	    public void setNote (String note)
	    {
	        this.note = note;
	    }
	    @XmlElement
	    public String getPreferredDepartmentCode ()
	    {
	        return preferredDepartmentCode;
	    }

	    public void setPreferredDepartmentCode (String preferredDepartmentCode)
	    {
	        this.preferredDepartmentCode = preferredDepartmentCode;
	    }
	    @XmlElement
	    public String getMiddleName ()
	    {
	        return middleName;
	    }

	    public void setMiddleName (String middleName)
	    {
	        this.middleName = middleName;
	    }
	    @XmlElement
	    public String getLastName ()
	    {
	        return lastName;
	    }

	    public void setLastName (String lastName)
	    {
	        this.lastName = lastName;
	    }
	    @XmlElement
	    public String getHireDate ()
	    {
	        return hireDate;
	    }

	    public void setHireDate (String hireDate)
	    {
	        this.hireDate = hireDate;
	    }
	    @XmlElement
	    public String getCode ()
	    {
	        return code;
	    }

	    public void setCode (String code)
	    {
	        this.code = code;
	    }
	    @XmlElement
	    public String getDeleted ()
	    {
	        return deleted;
	    }

	    public void setDeleted (String deleted)
	    {
	        this.deleted = deleted;
	    }
	    @XmlElement
	    public String getMainRoleCode ()
	    {
	        return mainRoleCode;
	    }

	    public void setMainRoleCode (String mainRoleCode)
	    {
	        this.mainRoleCode = mainRoleCode;
	    }
	    @XmlElement
	    public String[] getDepartmentCodes ()
	    {
	        return departmentCodes;
	    }

	    public void setDepartmentCodes (String[] departmentCodes)
	    {
	        this.departmentCodes = departmentCodes;
	    }
	    @XmlElement
	    public String getAddress ()
	    {
	        return address;
	    }

	    public void setAddress (String address)
	    {
	        this.address = address;
	    }
	    @XmlElement
	    public String getEmail ()
	    {
	        return email;
	    }

	    public void setEmail (String email)
	    {
	        this.email = email;
	    }
	    @XmlElement
	    public String getHireDocumentNumber ()
	    {
	        return hireDocumentNumber;
	    }

	    public void setHireDocumentNumber (String hireDocumentNumber)
	    {
	        this.hireDocumentNumber = hireDocumentNumber;
	    }
	    @XmlElement
	    public String getCardNumber ()
	    {
	        return cardNumber;
	    }

	    public void setCardNumber (String cardNumber)
	    {
	        this.cardNumber = cardNumber;
	    }
	    @XmlElement
	    public String[] getResponsibilityDepartmentCodes ()
	    {
	        return responsibilityDepartmentCodes;
	    }

	    public void setResponsibilityDepartmentCodes (String[] responsibilityDepartmentCodes)
	    {
	        this.responsibilityDepartmentCodes = responsibilityDepartmentCodes;
	    }

	    @Override
	    public String toString()
	    {
	        return "ClassPojo [birthday = "+birthday+", phone = "+phone+", client = "+client+", deactivationDate = "+deactivationDate+", cellPhone = "+cellPhone+", activationDate = "+activationDate+", password = "+password+", id = "+id+", pinCode = "+pinCode+", roleCodes = "+roleCodes+", taxpayerIdNumber = "+taxpayerIdNumber+", name = "+name+", login = "+login+", employee = "+employee+", firstName = "+firstName+", supplier = "+supplier+", note = "+note+", preferredDepartmentCode = "+preferredDepartmentCode+", middleName = "+middleName+", lastName = "+lastName+", hireDate = "+hireDate+", code = "+code+", deleted = "+deleted+", mainRoleCode = "+mainRoleCode+", departmentCodes = "+departmentCodes+", address = "+address+", email = "+email+", hireDocumentNumber = "+hireDocumentNumber+", cardNumber = "+cardNumber+", responsibilityDepartmentCodes = "+responsibilityDepartmentCodes+"]";
	    }
	}
				
				
