package com.example.buddypunchclone;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroupResponseDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("owner")
    private EmployeeResponseDto owner;

    @SerializedName("employees")
    private List<EmployeeResponseDto> employees;

    // Default constructor
    public GroupResponseDto() {}

    // Parameterized constructor
    public GroupResponseDto(Long id, String name, String description,
                            EmployeeResponseDto owner, List<EmployeeResponseDto> employees) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.employees = employees;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EmployeeResponseDto getOwner() { return owner; }
    public void setOwner(EmployeeResponseDto owner) { this.owner = owner; }

    public List<EmployeeResponseDto> getEmployees() { return employees; }
    public void setEmployees(List<EmployeeResponseDto> employees) { this.employees = employees; }
}