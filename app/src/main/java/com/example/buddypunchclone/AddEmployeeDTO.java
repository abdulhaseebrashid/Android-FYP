// File: AddEmployeeDTO.java
package com.example.buddypunchclone;

import java.io.Serializable;

public class AddEmployeeDTO implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String birthDate;        // Format: "yyyy-MM-dd"
    private String hireDate;         // Format: "yyyy-MM-dd"
    private String terminationDate;  // Format: "yyyy-MM-dd"
    private Double annualSalary;
    private String additionalInfo;
    private Long userTypeId;

    // Default Constructor
    public AddEmployeeDTO() {}

    // Parameterized Constructor
    public AddEmployeeDTO(Long id, String firstName, String lastName, String email, String phoneNumber,
                          String username, String birthDate, String hireDate, String terminationDate,
                          Double annualSalary, String additionalInfo, Long userTypeId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.terminationDate = terminationDate;
        this.annualSalary = annualSalary;
        this.additionalInfo = additionalInfo;
        this.userTypeId = userTypeId;
    }

    // Getters and Setters for all fields

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public String getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(String terminationDate) {
        this.terminationDate = terminationDate;
    }

    public Double getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(Double annualSalary) {
        this.annualSalary = annualSalary;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Long getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Long userTypeId) {
        this.userTypeId = userTypeId;
    }
}
