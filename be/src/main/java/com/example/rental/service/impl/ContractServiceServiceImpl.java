package com.example.rental.service.impl;

import com.example.rental.dto.contractservice.ContractServiceRequest;
import com.example.rental.dto.contractservice.ContractServiceResponse;
import com.example.rental.entity.Contract;
import com.example.rental.entity.ContractService;
import com.example.rental.entity.RentalServiceItem;
import com.example.rental.mapper.ContractServiceMapper;
import com.example.rental.repository.ContractRepository;
import com.example.rental.repository.ContractServiceRepository;
import com.example.rental.repository.RentalServiceRepository;
import com.example.rental.service.ContractServiceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContractServiceServiceImpl implements ContractServiceService {

    private final ContractRepository contractRepository;
    private final ContractServiceRepository contractServiceRepository;
    private final RentalServiceRepository rentalServiceRepository;

    @Override
    public ContractServiceResponse addServiceToContract(Long contractId, ContractServiceRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        RentalServiceItem service = rentalServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found"));

        ContractService entity = ContractServiceMapper.toEntity(request, service);
        entity.setContract(contract);

        return ContractServiceMapper.toResponse(contractServiceRepository.save(entity));
    }

    @Override
    public List<ContractServiceResponse> getServicesByContract(Long contractId) {
        return contractServiceRepository.findByContractId(contractId).stream()
                .map(ContractServiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeService(Long id) {
        contractServiceRepository.deleteById(id);
    }
}
