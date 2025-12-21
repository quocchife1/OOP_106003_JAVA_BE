package com.example.rental.mapper;

import com.example.rental.dto.auth.PartnerRegisterRequest;
import com.example.rental.dto.partner.PartnerResponse;
import com.example.rental.dto.partner.PartnerUpdateProfileRequest;
import com.example.rental.entity.Partners;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-11T12:17:25+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class PartnerMapperImpl implements PartnerMapper {

    @Override
    public PartnerResponse toResponse(Partners partner) {
        if ( partner == null ) {
            return null;
        }

        PartnerResponse.PartnerResponseBuilder partnerResponse = PartnerResponse.builder();

        partnerResponse.id( partner.getId() );
        partnerResponse.partnerCode( partner.getPartnerCode() );
        partnerResponse.username( partner.getUsername() );
        partnerResponse.companyName( partner.getCompanyName() );
        partnerResponse.taxCode( partner.getTaxCode() );
        partnerResponse.contactPerson( partner.getContactPerson() );
        partnerResponse.email( partner.getEmail() );
        partnerResponse.phoneNumber( partner.getPhoneNumber() );
        partnerResponse.address( partner.getAddress() );
        partnerResponse.status( partner.getStatus() );

        return partnerResponse.build();
    }

    @Override
    public List<PartnerResponse> toResponseList(List<Partners> partners) {
        if ( partners == null ) {
            return null;
        }

        List<PartnerResponse> list = new ArrayList<PartnerResponse>( partners.size() );
        for ( Partners partners1 : partners ) {
            list.add( toResponse( partners1 ) );
        }

        return list;
    }

    @Override
    public Partners toEntity(PartnerRegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        Partners.PartnersBuilder partners = Partners.builder();

        partners.phoneNumber( request.getPhone() );
        partners.username( request.getUsername() );
        partners.companyName( request.getCompanyName() );
        partners.taxCode( request.getTaxCode() );
        partners.contactPerson( request.getContactPerson() );
        partners.email( request.getEmail() );
        partners.address( request.getAddress() );

        return partners.build();
    }

    @Override
    public void updatePartnerFromDto(PartnerUpdateProfileRequest request, Partners target) {
        if ( request == null ) {
            return;
        }

        target.setCompanyName( request.getCompanyName() );
        target.setTaxCode( request.getTaxCode() );
        target.setContactPerson( request.getContactPerson() );
        target.setAddress( request.getAddress() );
    }
}
