package com.nhom8.pilates.dto.response;

import java.util.List;

public class AdminDashboardResponse {
    private final AdminReportResponse overview;
    private final List<PaymentResponse> recentTransactions;
    public AdminDashboardResponse(AdminReportResponse overview,List<PaymentResponse> recentTransactions) {
        this.overview=overview;this.recentTransactions=recentTransactions;
    }
    public AdminReportResponse getOverview(){return overview;}
    public List<PaymentResponse> getRecentTransactions(){return recentTransactions;}
}
