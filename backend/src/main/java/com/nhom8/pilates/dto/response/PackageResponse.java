package com.nhom8.pilates.dto.response;

import com.nhom8.pilates.entity.PilatesPackage;
import java.math.BigDecimal;
import java.util.List;

public class PackageResponse {
    private final Long id;
    private final String packageCode;
    private final String name;
    private final String description;
    private final int durationDays;
    private final int sessionCount;
    private final BigDecimal price;
    private final String status;
    private final List<String> classTypes;
    private final List<Long> classTypeIds;

    public PackageResponse(PilatesPackage source) {
        this.id = source.getId();
        this.packageCode = source.getPackageCode();
        this.name = source.getName();
        this.description = source.getDescription();
        this.durationDays = source.getDurationDays();
        this.sessionCount = source.getSessionCount();
        this.price = source.getPrice();
        this.status = source.getStatus().name();
        this.classTypes = source.getClassTypes().stream().map(type -> type.getName()).sorted().toList();
        this.classTypeIds = source.getClassTypes().stream().map(type -> type.getId()).sorted().toList();
    }
    public Long getId() { return id; }
    public String getPackageCode() { return packageCode; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDurationDays() { return durationDays; }
    public int getSessionCount() { return sessionCount; }
    public BigDecimal getPrice() { return price; }
    public String getStatus() { return status; }
    public List<String> getClassTypes() { return classTypes; }
    public List<Long> getClassTypeIds() { return classTypeIds; }
}
