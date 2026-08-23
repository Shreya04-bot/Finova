package com.finova.service;

import com.finova.dto.BeneficiaryRequest;
import com.finova.dto.BeneficiaryResponse;
import com.finova.entity.Beneficiary;
import com.finova.entity.User;
import com.finova.exception.ResourceNotFoundException;
import com.finova.repository.BeneficiaryRepository;
import com.finova.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;

    public List<BeneficiaryResponse> getMyBeneficiaries(Long userId) {
        return beneficiaryRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BeneficiaryResponse addBeneficiary(Long userId, BeneficiaryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Beneficiary beneficiary = Beneficiary.builder()
                .beneficiaryName(request.getBeneficiaryName())
                .accountNumber(request.getAccountNumber())
                .bankName(request.getBankName())
                .user(user)
                .build();

        beneficiaryRepository.save(beneficiary);
        return toResponse(beneficiary);
    }

    public void deleteBeneficiary(Long userId, Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));

        if (!beneficiary.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Beneficiary not found");
        }

        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponse toResponse(Beneficiary b) {
        return BeneficiaryResponse.builder()
                .id(b.getId())
                .beneficiaryName(b.getBeneficiaryName())
                .accountNumber(b.getAccountNumber())
                .bankName(b.getBankName())
                .createdAt(b.getCreatedAt())
                .build();
    }
}