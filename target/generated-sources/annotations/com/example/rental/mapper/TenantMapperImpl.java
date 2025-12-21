package com.example.rental.mapper;

import com.example.rental.dto.auth.AuthRegisterRequest;
import com.example.rental.dto.tenant.TenantResponse;
import com.example.rental.dto.tenant.TenantUpdateProfileRequest;
import com.example.rental.entity.Tenant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-11T12:17:25+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class TenantMapperImpl implements TenantMapper {

    @Override
    public Tenant registerRequestToTenant(AuthRegisterRequest registerRequest) {
        if ( registerRequest == null ) {
            return null;
        }

        Tenant.TenantBuilder tenant = Tenant.builder();

        tenant.phoneNumber( registerRequest.getPhone() );
        tenant.username( registerRequest.getUsername() );
        tenant.fullName( registerRequest.getFullName() );
        tenant.email( registerRequest.getEmail() );

        return tenant.build();
    }

    @Override
    public TenantResponse tenantToTenantResponse(Tenant tenant) {
        if ( tenant == null ) {
            return null;
        }

        TenantResponse tenantResponse = new TenantResponse();

        tenantResponse.setId( tenant.getId() );
        tenantResponse.setUsername( tenant.getUsername() );
        tenantResponse.setFullName( tenant.getFullName() );
        tenantResponse.setEmail( tenant.getEmail() );
        tenantResponse.setPhoneNumber( tenant.getPhoneNumber() );
        tenantResponse.setAddress( tenant.getAddress() );
        tenantResponse.setStatus( tenant.getStatus() );

        return tenantResponse;
    }

    @Override
    public void updateTenantFromDto(TenantUpdateProfileRequest request, Tenant target) {
        if ( request == null ) {
            return;
        }

        target.setFullName( request.getFullName() );
        target.setPhoneNumber( request.getPhoneNumber() );
        target.setAddress( request.getAddress() );
    }
}
