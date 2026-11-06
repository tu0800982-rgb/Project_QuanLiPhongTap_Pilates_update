package com.nhom8.pilates;

import com.nhom8.pilates.exception.BusinessException;
import com.nhom8.pilates.repository.*;
import com.nhom8.pilates.service.ReportService;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ReportServiceTest {
    @Test
    void endDateBeforeStartDateMustBeRejected() {
        ReportService service=new ReportService(mock(MemberRepository.class),mock(EmployeeRepository.class),
                mock(PilatesPackageRepository.class),mock(PilatesClassRepository.class),
                mock(ClassScheduleRepository.class),mock(ClassRegistrationRepository.class),
                mock(AttendanceRepository.class),mock(PaymentRepository.class));
        BusinessException error=assertThrows(BusinessException.class,
                () -> service.overview(LocalDate.of(2026,11,6),LocalDate.of(2026,11,5)));
        assertEquals("INVALID_DATE_RANGE",error.getCode());
    }
}
