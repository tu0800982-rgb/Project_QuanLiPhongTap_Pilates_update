package com.nhom8.pilates.dto.response;

public class PackageRegistrationResponse {
    private final MemberPackageResponse memberPackage;
    private final PaymentResponse payment;
    public PackageRegistrationResponse(MemberPackageResponse memberPackage, PaymentResponse payment) {
        this.memberPackage = memberPackage; this.payment = payment;
    }
    public MemberPackageResponse getMemberPackage() { return memberPackage; }
    public PaymentResponse getPayment() { return payment; }
}
