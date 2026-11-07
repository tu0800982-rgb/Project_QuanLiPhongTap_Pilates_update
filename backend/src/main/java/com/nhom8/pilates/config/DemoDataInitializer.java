package com.nhom8.pilates.config;

import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {
    private final RoleRepository roles; private final UserRepository users; private final MemberRepository members;
    private final EmployeeRepository employees; private final ClassTypeRepository classTypes;
    private final PilatesPackageRepository packages; private final PilatesClassRepository classes;
    private final ClassScheduleRepository schedules; private final MemberPackageRepository memberPackages;
    private final PaymentRepository payments; private final ClassRegistrationRepository registrations;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(RoleRepository roles,UserRepository users,MemberRepository members,
            EmployeeRepository employees,ClassTypeRepository classTypes,PilatesPackageRepository packages,
            PilatesClassRepository classes,ClassScheduleRepository schedules,MemberPackageRepository memberPackages,
            PaymentRepository payments,ClassRegistrationRepository registrations,PasswordEncoder passwordEncoder) {
        this.roles=roles;this.users=users;this.members=members;this.employees=employees;this.classTypes=classTypes;
        this.packages=packages;this.classes=classes;this.schedules=schedules;this.memberPackages=memberPackages;
        this.payments=payments;this.registrations=registrations;this.passwordEncoder=passwordEncoder;
    }

    @Override @Transactional
    public void run(String... args) {
        Map<RoleCode,Role> roleMap=createRoles();
        User admin=createUser("admin@pilates.com","Vũ Thành Đạt",roleMap.get(RoleCode.ADMIN));
        User staff=createUser("staff@pilates.com","Phạm Thu Hà",roleMap.get(RoleCode.STAFF));
        User trainerUser=createUser("trainer@pilates.com","Nguyễn Minh Khang",roleMap.get(RoleCode.TRAINER));
        User memberUser=createUser("member@pilates.com","Trần An Member",roleMap.get(RoleCode.MEMBER));
        createEmployee(admin,"AD001","0900000001",EmployeeType.ADMIN,null);
        createEmployee(staff,"ST001","0900000002",EmployeeType.STAFF,null);
        createEmployee(trainerUser,"TR001","0900000003",EmployeeType.TRAINER,"Reformer Pilates, Pilates nền tảng");
        normalizeExistingEmployeeNames();
        createMember(memberUser);createCatalog();createLinkedData(staff,trainerUser,memberUser);
    }

    private Map<RoleCode,Role> createRoles() {
        Map<RoleCode,String> names=Map.of(RoleCode.ADMIN,"Quản trị viên",RoleCode.STAFF,"Nhân viên",
                RoleCode.TRAINER,"Huấn luyện viên",RoleCode.MEMBER,"Hội viên");
        Map<RoleCode,Role> result=new EnumMap<>(RoleCode.class);
        for(RoleCode code:RoleCode.values()) result.put(code,roles.findByCode(code)
                .orElseGet(() -> roles.save(new Role(code,names.get(code)))));
        return result;
    }

    private User createUser(String email,String name,Role role) {
        User user=users.findByEmailIgnoreCase(email).orElseGet(() -> {
            User created=new User();created.setEmail(email);created.setFullName(name);
            created.setPasswordHash(passwordEncoder.encode("123456"));created.setRole(role);
            created.setStatus(AccountStatus.ACTIVE);return users.save(created);
        });
        if(isPlaceholderName(user.getFullName())) {
            user.setFullName(name);users.save(user);
        }
        return user;
    }

    private boolean isPlaceholderName(String name) {
        return name!=null&&Set.of("Quản trị viên","Nhân viên Studio","Nguyễn Minh Trainer").contains(name.trim());
    }

    private void normalizeExistingEmployeeNames() {
        Map<String,String> replacements=Map.of(
                "Quản trị viên","Vũ Thành Đạt",
                "Nhân viên Studio","Phạm Thu Hà",
                "Nguyễn Minh Trainer","Nguyễn Minh Khang",
                "CODEX","Đỗ Gia Huy");
        for(Employee item:employees.findAll()) {
            String replacement=replacements.get(item.getFullName()==null?null:item.getFullName().trim());
            if(replacement==null)continue;
            item.setFullName(replacement);item.getUser().setFullName(replacement);
            users.save(item.getUser());employees.save(item);
        }
    }

    private void createEmployee(User user,String code,String phone,EmployeeType type,String specialization) {
        Optional<Employee> existing=employees.findByUserId(user.getId());
        if(existing.isPresent()) {
            Employee item=existing.get();
            if(isPlaceholderName(item.getFullName())) {item.setFullName(user.getFullName());employees.save(item);}
            return;
        }
        Employee item=new Employee();item.setUser(user);item.setEmployeeCode(code);item.setFullName(user.getFullName());
        item.setPhone(phone);item.setEmployeeType(type);item.setSpecialization(specialization);
        item.setBiography(type==EmployeeType.TRAINER?"Huấn luyện viên demo của Pilates Studio – Nhóm 8.":null);
        item.setStatus(RecordStatus.ACTIVE);employees.save(item);
    }

    private void createMember(User user) {
        if(members.findByUserId(user.getId()).isPresent())return;
        Member item=new Member();item.setUser(user);item.setMemberCode("MB001");item.setFullName(user.getFullName());
        item.setPhone("0900000004");item.setDateOfBirth(LocalDate.of(2002,8,15));item.setGender("FEMALE");
        item.setAddress("Thành phố Hồ Chí Minh");item.setEmergencyContact("0900000014");
        item.setStatus(RecordStatus.ACTIVE);members.save(item);
    }

    private void createCatalog() {
        ClassType mat=classTypes.findByCode("MAT").orElseGet(() -> classTypes.save(new ClassType("MAT","Mat Pilates")));
        ClassType reformer=classTypes.findByCode("REFORMER")
                .orElseGet(() -> classTypes.save(new ClassType("REFORMER","Reformer Pilates")));
        if(packages.findByPackageCode("PKG-STARTER").isEmpty()) {
            PilatesPackage item=new PilatesPackage();item.setPackageCode("PKG-STARTER");item.setName("Gói Khởi Đầu");
            item.setDescription("Gói demo 12 buổi trong 30 ngày, sử dụng lớp Mat và Reformer.");
            item.setDurationDays(30);item.setSessionCount(12);item.setPrice(new BigDecimal("1800000.00"));
            item.setStatus(RecordStatus.ACTIVE);item.getClassTypes().add(mat);item.getClassTypes().add(reformer);packages.save(item);
        }
    }

    private void createLinkedData(User staff,User trainerUser,User memberUser) {
        Employee trainer=employees.findByUserId(trainerUser.getId()).orElseThrow();
        Member member=members.findByUserId(memberUser.getId()).orElseThrow();
        PilatesPackage demoPackage=packages.findByPackageCode("PKG-STARTER").orElseThrow();
        ClassType reformer=classTypes.findByCode("REFORMER").orElseThrow();
        PilatesClass pilatesClass=classes.findByClassCode("CLS-REF-01").orElseGet(() -> {
            PilatesClass item=new PilatesClass();item.setClassCode("CLS-REF-01");item.setName("Reformer Foundation");
            item.setClassType(reformer);item.setLocation("Studio A");item.setDefaultCapacity(12);
            item.setStatus(RecordStatus.ACTIVE);return classes.save(item);
        });
        MemberPackage memberPackage=memberPackages
                .findByMemberIdAndStatusOrderByEndDateDesc(member.getId(),MemberPackageStatus.ACTIVE)
                .stream().findFirst().orElseGet(() -> {
                    LocalDate today=LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));MemberPackage item=new MemberPackage();
                    item.setMember(member);item.setPilatesPackage(demoPackage);item.setStartDate(today.minusDays(3));
                    item.setEndDate(today.plusDays(26));item.setTotalSessions(12);item.setRemainingSessions(12);
                    item.setStatus(MemberPackageStatus.ACTIVE);item.setCreatedBy(staff);return memberPackages.save(item);
                });
        if(payments.findByMemberPackageId(memberPackage.getId()).isEmpty()) {
            Payment item=new Payment();item.setTransactionCode("DEMO-PAY-001");item.setMember(member);
            item.setMemberPackage(memberPackage);item.setAmount(demoPackage.getPrice());item.setMethod(PaymentMethod.BANK_TRANSFER);
            item.setStatus(PaymentStatus.PAID);item.setPaidAt(Instant.now());item.setPerformedBy(staff);payments.save(item);
        }
        ClassSchedule schedule=schedules.findFirstByPilatesClassIdAndStartsAtAfterAndStatusNotOrderByStartsAt(
                pilatesClass.getId(),Instant.now(),ScheduleStatus.CANCELLED)
                .orElseGet(() -> {
                    ZonedDateTime start=LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusDays(1).atTime(9,0)
                            .atZone(ZoneId.of("Asia/Ho_Chi_Minh"));ClassSchedule item=new ClassSchedule();
                    item.setPilatesClass(pilatesClass);item.setTrainer(trainer);item.setStartsAt(start.toInstant());
                    item.setEndsAt(start.plusHours(1).toInstant());item.setCapacity(12);item.setStatus(ScheduleStatus.OPEN);
                    return schedules.save(item);
                });
        if(!registrations.existsByScheduleIdAndMemberIdAndStatus(schedule.getId(),member.getId(),RegistrationStatus.REGISTERED)) {
            ClassRegistration item=new ClassRegistration();item.setSchedule(schedule);item.setMember(member);
            item.setMemberPackage(memberPackage);item.setStatus(RegistrationStatus.REGISTERED);item.setRegisteredBy(staff);
            registrations.save(item);
        }
    }
}
