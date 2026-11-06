package com.nhom8.pilates.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

public class PackageRequest {
    @NotBlank @Size(max=30) private String packageCode;
    @NotBlank @Size(max=150) private String name;
    @Size(max=2000) private String description;
    @Min(1) private int durationDays;
    @Min(1) private int sessionCount;
    @NotNull @DecimalMin("0.00") private BigDecimal price;
    @NotEmpty private Set<Long> classTypeIds;
    public PackageRequest() { }
    public String getPackageCode(){return packageCode;} public void setPackageCode(String v){packageCode=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public int getDurationDays(){return durationDays;} public void setDurationDays(int v){durationDays=v;}
    public int getSessionCount(){return sessionCount;} public void setSessionCount(int v){sessionCount=v;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
    public Set<Long> getClassTypeIds(){return classTypeIds;} public void setClassTypeIds(Set<Long> v){classTypeIds=v;}
}
