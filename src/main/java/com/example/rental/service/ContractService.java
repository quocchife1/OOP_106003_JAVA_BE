package com.example.rental.service;

import com.example.rental.dto.contract.ContractCreateRequest;
import com.example.rental.entity.Contract;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ContractService {
    Contract createContract(ContractCreateRequest request) throws IOException;
    List<Contract> findAll();
    Optional<Contract> findById(Long id);
    Contract uploadSignedContract(Long id, MultipartFile file) throws IOException;
    Resource downloadContract(Long id) throws IOException;
}
