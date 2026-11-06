package com.nhom8.pilates.service;

import com.nhom8.pilates.dto.request.*;
import com.nhom8.pilates.dto.response.*;
import com.nhom8.pilates.entity.*;
import com.nhom8.pilates.enums.*;
import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class ClassManagementService {
    private final PilatesClassRepository classRepository;
    private final ClassTypeRepository classTypeRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final ClassRegistrationRepository registrationRepository;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;

    public ClassManagementService(PilatesClassRepository classRepository, ClassTypeRepository classTypeRepository,
                                  ClassScheduleRepository scheduleRepository,
                                  ClassRegistrationRepository registrationRepository,
                                  EmployeeRepository employeeRepository, CurrentUserService currentUserService) {
        this.classRepository=classRepository; this.classTypeRepository=classTypeRepository;
        this.scheduleRepository=scheduleRepository; this.registrationRepository=registrationRepository;
        this.employeeRepository=employeeRepository; this.currentUserService=currentUserService;
    }

    @Transactional
    public ClassResponse createClass(CreateClassRequest request) {
        String code=request.getClassCode().trim().toUpperCase();
        if (classRepository.findByClassCode(code).isPresent())
            throw new BusinessException("CLASS_CODE_EXISTS", "Mã lớp đã tồn tại", HttpStatus.CONFLICT);
        ClassType type=classTypeRepository.findById(request.getClassTypeId())
                .orElseThrow(() -> new BusinessException("CLASS_TYPE_NOT_FOUND", "Không tìm thấy loại lớp", HttpStatus.NOT_FOUND));
        PilatesClass item=new PilatesClass(); item.setClassCode(code); item.setName(request.getName().trim());
        item.setClassType(type); item.setLocation(request.getLocation().trim());
        item.setDefaultCapacity(request.getDefaultCapacity()); item.setStatus(RecordStatus.ACTIVE);
        return new ClassResponse(classRepository.save(item));
    }

    @Transactional(readOnly=true)
    public List<ClassResponse> listClasses() {
        return classRepository.findAll().stream().map(ClassResponse::new).toList();
    }

    @Transactional
    public ClassResponse updateClass(Long id,CreateClassRequest request){
        PilatesClass item=classRepository.findById(id).orElseThrow(()->new BusinessException("CLASS_NOT_FOUND","Không tìm thấy lớp",HttpStatus.NOT_FOUND));
        String code=request.getClassCode().trim().toUpperCase();
        classRepository.findByClassCode(code).filter(other->!other.getId().equals(id)).ifPresent(other->{throw new BusinessException("CLASS_CODE_EXISTS","Mã lớp đã tồn tại",HttpStatus.CONFLICT);});
        ClassType type=classTypeRepository.findById(request.getClassTypeId()).orElseThrow(()->new BusinessException("CLASS_TYPE_NOT_FOUND","Không tìm thấy loại lớp",HttpStatus.NOT_FOUND));
        item.setClassCode(code);item.setName(request.getName().trim());item.setClassType(type);item.setLocation(request.getLocation().trim());item.setDefaultCapacity(request.getDefaultCapacity());
        return new ClassResponse(classRepository.save(item));
    }

    @Transactional
    public ClassResponse updateClassStatus(Long id,RecordStatus status){
        PilatesClass item=classRepository.findById(id).orElseThrow(()->new BusinessException("CLASS_NOT_FOUND","Không tìm thấy lớp",HttpStatus.NOT_FOUND));
        if(status==RecordStatus.INACTIVE&&scheduleRepository.findFirstByPilatesClassIdAndStartsAtAfterAndStatusNotOrderByStartsAt(id,Instant.now(),ScheduleStatus.CANCELLED).isPresent())throw new BusinessException("CLASS_HAS_UPCOMING_SCHEDULE","Không thể ngừng lớp còn lịch sắp tới",HttpStatus.CONFLICT);
        item.setStatus(status);return new ClassResponse(classRepository.save(item));
    }

    @Transactional
    public ScheduleResponse createSchedule(CreateScheduleRequest request) {
        if (!request.getEndsAt().isAfter(request.getStartsAt()))
            throw new BusinessException("INVALID_SCHEDULE_TIME", "Giờ kết thúc phải sau giờ bắt đầu", HttpStatus.BAD_REQUEST);
        PilatesClass item=classRepository.findById(request.getClassId())
                .orElseThrow(() -> new BusinessException("CLASS_NOT_FOUND", "Không tìm thấy lớp", HttpStatus.NOT_FOUND));
        if (item.getStatus()!=RecordStatus.ACTIVE)
            throw new BusinessException("CLASS_INACTIVE", "Lớp đang ngừng hoạt động", HttpStatus.CONFLICT);
        Employee trainer=employeeRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new BusinessException("TRAINER_NOT_FOUND", "Không tìm thấy huấn luyện viên", HttpStatus.NOT_FOUND));
        if (trainer.getEmployeeType()!=EmployeeType.TRAINER || trainer.getStatus()!=RecordStatus.ACTIVE)
            throw new BusinessException("TRAINER_UNAVAILABLE", "Huấn luyện viên không hoạt động", HttpStatus.CONFLICT);
        if (scheduleRepository.countTrainerConflicts(trainer.getId(), request.getStartsAt(), request.getEndsAt())>0)
            throw new BusinessException("TRAINER_SCHEDULE_CONFLICT", "Huấn luyện viên bị trùng lịch", HttpStatus.CONFLICT);
        ClassSchedule schedule=new ClassSchedule(); schedule.setPilatesClass(item); schedule.setTrainer(trainer);
        schedule.setStartsAt(request.getStartsAt()); schedule.setEndsAt(request.getEndsAt());
        schedule.setCapacity(request.getCapacity()); schedule.setStatus(ScheduleStatus.OPEN);
        scheduleRepository.save(schedule);
        return new ScheduleResponse(schedule,0);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id,CreateScheduleRequest request){
        ClassSchedule schedule=scheduleRepository.findDetailedById(id).orElseThrow(()->new BusinessException("SCHEDULE_NOT_FOUND","Không tìm thấy lịch học",HttpStatus.NOT_FOUND));
        if(!request.getEndsAt().isAfter(request.getStartsAt()))throw new BusinessException("INVALID_SCHEDULE_TIME","Giờ kết thúc phải sau giờ bắt đầu",HttpStatus.BAD_REQUEST);
        int registered=(int)registrationRepository.countByScheduleIdAndStatus(id,RegistrationStatus.REGISTERED);
        if(request.getCapacity()<registered)throw new BusinessException("CAPACITY_BELOW_REGISTRATIONS","Sức chứa không thể nhỏ hơn số hội viên đã đăng ký",HttpStatus.CONFLICT);
        PilatesClass item=classRepository.findById(request.getClassId()).orElseThrow(()->new BusinessException("CLASS_NOT_FOUND","Không tìm thấy lớp",HttpStatus.NOT_FOUND));
        Employee trainer=employeeRepository.findById(request.getTrainerId()).orElseThrow(()->new BusinessException("TRAINER_NOT_FOUND","Không tìm thấy huấn luyện viên",HttpStatus.NOT_FOUND));
        if(item.getStatus()!=RecordStatus.ACTIVE||trainer.getEmployeeType()!=EmployeeType.TRAINER||trainer.getStatus()!=RecordStatus.ACTIVE)throw new BusinessException("SCHEDULE_RESOURCE_INACTIVE","Lớp hoặc huấn luyện viên không hoạt động",HttpStatus.CONFLICT);
        if(scheduleRepository.countTrainerConflictsExcluding(trainer.getId(),id,request.getStartsAt(),request.getEndsAt())>0)throw new BusinessException("TRAINER_SCHEDULE_CONFLICT","Huấn luyện viên bị trùng lịch",HttpStatus.CONFLICT);
        schedule.setPilatesClass(item);schedule.setTrainer(trainer);schedule.setStartsAt(request.getStartsAt());schedule.setEndsAt(request.getEndsAt());schedule.setCapacity(request.getCapacity());
        return new ScheduleResponse(scheduleRepository.save(schedule),registered);
    }

    @Transactional
    public ScheduleResponse updateScheduleStatus(Long id,ScheduleStatus status){
        ClassSchedule schedule=scheduleRepository.findDetailedById(id).orElseThrow(()->new BusinessException("SCHEDULE_NOT_FOUND","Không tìm thấy lịch học",HttpStatus.NOT_FOUND));
        if(schedule.getStatus()==ScheduleStatus.COMPLETED&&status!=ScheduleStatus.COMPLETED)throw new BusinessException("COMPLETED_SCHEDULE_IMMUTABLE","Lịch đã hoàn thành không thể mở lại",HttpStatus.CONFLICT);
        long registered=registrationRepository.countByScheduleIdAndStatus(id,RegistrationStatus.REGISTERED);
        if(status==ScheduleStatus.CANCELLED&&registered>0)throw new BusinessException("SCHEDULE_HAS_REGISTRATIONS","Hãy xử lý các đăng ký trước khi hủy lịch",HttpStatus.CONFLICT);
        schedule.setStatus(status);
        return new ScheduleResponse(scheduleRepository.save(schedule),registered);
    }

    @Transactional(readOnly=true)
    public List<ScheduleResponse> upcoming() {
        User actor=currentUserService.requireUser();
        List<ClassSchedule> schedules=scheduleRepository.findUpcoming(Instant.now());
        if (actor.getRole().getCode()==RoleCode.TRAINER) {
            Employee trainer=employeeRepository.findByUserId(actor.getId())
                    .orElseThrow(() -> new BusinessException("TRAINER_PROFILE_NOT_FOUND", "Không có hồ sơ huấn luyện viên", HttpStatus.NOT_FOUND));
            schedules=schedules.stream().filter(s -> s.getTrainer().getId().equals(trainer.getId())).toList();
        } else if (actor.getRole().getCode()==RoleCode.MEMBER) {
            schedules=schedules.stream().filter(s -> s.getStatus()==ScheduleStatus.OPEN).toList();
        }
        return schedules.stream().map(s -> new ScheduleResponse(s,
                registrationRepository.countByScheduleIdAndStatus(s.getId(),RegistrationStatus.REGISTERED))).toList();
    }
}
