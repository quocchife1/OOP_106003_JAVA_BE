package com.example.rental.mapper;

import com.example.rental.dto.branch.BranchRequest;
import com.example.rental.dto.branch.BranchResponse;
import com.example.rental.entity.Branch;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-11T12:17:25+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class BranchMapperImpl implements BranchMapper {

    @Override
    public BranchResponse toResponse(Branch branch) {
        if ( branch == null ) {
            return null;
        }

        BranchResponse.BranchResponseBuilder branchResponse = BranchResponse.builder();

        branchResponse.id( branch.getId() );
        branchResponse.branchCode( branch.getBranchCode() );
        branchResponse.branchName( branch.getBranchName() );
        branchResponse.address( branch.getAddress() );
        branchResponse.phoneNumber( branch.getPhoneNumber() );

        return branchResponse.build();
    }

    @Override
    public Branch toEntity(BranchRequest request) {
        if ( request == null ) {
            return null;
        }

        Branch.BranchBuilder branch = Branch.builder();

        branch.branchCode( request.getBranchCode() );
        branch.branchName( request.getBranchName() );
        branch.address( request.getAddress() );
        branch.phoneNumber( request.getPhoneNumber() );

        return branch.build();
    }

    @Override
    public void updateEntityFromRequest(BranchRequest request, Branch branch) {
        if ( request == null ) {
            return;
        }

        branch.setBranchCode( request.getBranchCode() );
        branch.setBranchName( request.getBranchName() );
        branch.setAddress( request.getAddress() );
        branch.setPhoneNumber( request.getPhoneNumber() );
    }
}
