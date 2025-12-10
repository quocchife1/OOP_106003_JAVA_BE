package com.example.rental.dto.partner;

import com.example.rental.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class PartnerResponse {
    private Long id;
    private String partnerCode;
    private String username;
    private String companyName;
    private String taxCode;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
    private UserStatus status;
}