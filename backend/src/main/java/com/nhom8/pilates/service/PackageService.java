package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.PackageRequest;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.RecordStatus;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class PackageService {
    private final PilatesPackageRepository packages;private final ClassTypeRepository classTypes;
    public PackageService(PilatesPackageRepository packages,ClassTypeRepository classTypes){this.packages=packages;this.classTypes=classTypes;}
    @Transactional(readOnly=true) public List<PackageResponse> list(){return packages.findAllWithClassTypes().stream().map(PackageResponse::new).toList();}
    @Transactional(readOnly=true) public List<ClassTypeResponse> types(){return classTypes.findAll().stream().map(ClassTypeResponse::new).toList();}
    @Transactional public PackageResponse create(PackageRequest request){
        String code=request.getPackageCode().trim().toUpperCase();if(packages.findByPackageCode(code).isPresent())throw conflict();
        PilatesPackage item=new PilatesPackage();apply(item,request,code);return new PackageResponse(packages.save(item));
    }
    @Transactional public PackageResponse update(Long id,PackageRequest request){
        PilatesPackage item=find(id);String code=request.getPackageCode().trim().toUpperCase();
        if(packages.existsByPackageCodeAndIdNot(code,id))throw conflict();apply(item,request,code);return new PackageResponse(packages.save(item));
    }
    @Transactional public PackageResponse status(Long id,RecordStatus status){PilatesPackage item=find(id);item.setStatus(status);return new PackageResponse(packages.save(item));}
    private PilatesPackage find(Long id){return packages.findById(id).orElseThrow(()->new BusinessException("PACKAGE_NOT_FOUND","Không tìm thấy gói tập",HttpStatus.NOT_FOUND));}
    private void apply(PilatesPackage item,PackageRequest r,String code){
        List<ClassType> types=classTypes.findAllById(r.getClassTypeIds());if(types.size()!=r.getClassTypeIds().size())throw new BusinessException("CLASS_TYPE_NOT_FOUND","Có loại lớp không tồn tại",HttpStatus.NOT_FOUND);
        item.setPackageCode(code);item.setName(r.getName().trim());item.setDescription(r.getDescription());item.setDurationDays(r.getDurationDays());
        item.setSessionCount(r.getSessionCount());item.setPrice(r.getPrice());item.setClassTypes(new HashSet<>(types));if(item.getStatus()==null)item.setStatus(RecordStatus.ACTIVE);
    }
    private BusinessException conflict(){return new BusinessException("PACKAGE_CODE_EXISTS","Mã gói đã tồn tại",HttpStatus.CONFLICT);}
}
