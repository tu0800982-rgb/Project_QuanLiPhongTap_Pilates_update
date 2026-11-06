package com.nhom8.pilates.dto.response;

import java.time.LocalDate;
import java.util.List;

public class TrainerDashboardResponse {
    private final LocalDate date; private final long classesToday; private final List<ScheduleResponse> upcomingSchedules;
    public TrainerDashboardResponse(LocalDate date,long classesToday,List<ScheduleResponse> upcomingSchedules) {
        this.date=date;this.classesToday=classesToday;this.upcomingSchedules=upcomingSchedules;
    }
    public LocalDate getDate(){return date;} public long getClassesToday(){return classesToday;}
    public List<ScheduleResponse> getUpcomingSchedules(){return upcomingSchedules;}
}
