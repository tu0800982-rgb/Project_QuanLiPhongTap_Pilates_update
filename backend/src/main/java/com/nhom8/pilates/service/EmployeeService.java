package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.EmployeeRequest;
import com.nhom8.pilates.dto.response.EmployeeResponse;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employees;private final UserRepository users;private final RoleRepository roles;private final PasswordEncoder encoder;
    public EmployeeService(EmployeeRepository employees,UserRepository users,RoleRepository roles,PasswordEncoder encoder){this.employees=employees;this.users=users;this.roles=roles;this.encoder=encoder;}
    @Transactional(readOnly=true) public List<EmployeeResponse> list(){return employees.findAll().stream().map(EmployeeResponse::new).toList();}
    @Transactional public EmployeeResponse create(EmployeeRequest r){validateType(r.getEmployeeType());String email=r.getEmail().trim().toLowerCase();
        if(r.getPassword()==null||r.getPassword().isBlank())throw new BusinessException("PASSWORD_REQUIRED","Mật khẩu là bắt buộc",HttpStatus.BAD_REQUEST);
        if(users.existsByEmailIgnoreCase(email))throw new BusinessException("EMAIL_EXISTS","Email đã tồn tại",HttpStatus.CONFLICT);
        if(employees.existsByPhone(r.getPhone()))throw new BusinessException("PHONE_EXISTS","Số điện thoại đã tồn tại",HttpStatus.CONFLICT);
        String code=r.getEmployeeCode().trim().toUpperCase();if(employees.existsByEmployeeCode(code))throw new BusinessException("EMPLOYEE_CODE_EXISTS","Mã nhân viên đã tồn tại",HttpStatus.CONFLICT);
        Role role=roles.findByCode(r.getEmployeeType()==EmployeeType.TRAINER?RoleCode.TRAINER:RoleCode.STAFF).orElseThrow();
        User user=new User();user.setEmail(email);user.setFullName(r.getFullName().trim());user.setPasswordHash(encoder.encode(r.getPassword()));user.setRole(role);user.setStatus(AccountStatus.ACTIVE);users.save(user);
        Employee item=new Employee();item.setUser(user);item.setEmployeeCode(code);apply(item,r);item.setStatus(RecordStatus.ACTIVE);return new EmployeeResponse(employees.save(item));}
    @Transactional public EmployeeResponse update(Long id,EmployeeRequest r){validateType(r.getEmployeeType());Employee item=find(id);if(item.getEmployeeType()==EmployeeType.ADMIN)throw new BusinessException("ADMIN_IMMUTABLE","Không chỉnh sửa tài khoản Admin hệ thống qua chức năng nhân sự",HttpStatus.FORBIDDEN);String email=r.getEmail().trim().toLowerCase();
        if(!item.getUser().getEmail().equalsIgnoreCase(email)&&users.existsByEmailIgnoreCase(email))throw new BusinessException("EMAIL_EXISTS","Email đã tồn tại",HttpStatus.CONFLICT);
        if(employees.existsByPhoneAndIdNot(r.getPhone(),id))throw new BusinessException("PHONE_EXISTS","Số điện thoại đã tồn tại",HttpStatus.CONFLICT);
        String code=r.getEmployeeCode().trim().toUpperCase();if(employees.existsByEmployeeCodeAndIdNot(code,id))throw new BusinessException("EMPLOYEE_CODE_EXISTS","Mã nhân viên đã tồn tại",HttpStatus.CONFLICT);
        item.setEmployeeCode(code);apply(item,r);item.getUser().setEmail(email);item.getUser().setFullName(item.getFullName());if(r.getPassword()!=null&&!r.getPassword().isBlank())item.getUser().setPasswordHash(encoder.encode(r.getPassword()));users.save(item.getUser());return new EmployeeResponse(employees.save(item));}
    @Transactional public EmployeeResponse status(Long id,RecordStatus status){Employee item=find(id);if(item.getEmployeeType()==EmployeeType.ADMIN)throw new BusinessException("ADMIN_IMMUTABLE","Không thể khóa tài khoản Admin hệ thống",HttpStatus.FORBIDDEN);item.setStatus(status);item.getUser().setStatus(status==RecordStatus.ACTIVE?AccountStatus.ACTIVE:AccountStatus.INACTIVE);return new EmployeeResponse(employees.save(item));}
    private void apply(Employee e,EmployeeRequest r){e.setFullName(r.getFullName().trim());e.setPhone(r.getPhone());e.setEmployeeType(r.getEmployeeType());e.setSpecialization(r.getSpecialization());e.setBiography(r.getBiography());}
    private Employee find(Long id){return employees.findById(id).orElseThrow(()->new BusinessException("EMPLOYEE_NOT_FOUND","Không tìm thấy nhân sự",HttpStatus.NOT_FOUND));}
    private void validateType(EmployeeType type){if(type==EmployeeType.ADMIN)throw new BusinessException("ADMIN_CREATION_FORBIDDEN","Không tạo thêm tài khoản Admin qua chức năng nhân sự",HttpStatus.FORBIDDEN);}
}
