package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.Discount;
import com.badrulamin.University_Management.exception.ResourceNotFoundException;
import com.badrulamin.University_Management.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    public Page<Discount> findAll(Pageable pageable) {
        return discountRepository.findAll(pageable);
    }

    public Discount findById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id));
    }

    public List<Discount> findByStudentId(Long studentId) {
        return discountRepository.findByStudentId(studentId);
    }

    public List<Discount> findByFeeTypeId(Long feeTypeId) {
        return discountRepository.findByFeeTypeIdAndIsActiveTrue(feeTypeId);
    }

    @Transactional
    public Discount save(Discount discount) {
        return discountRepository.save(discount);
    }

    @Transactional
    public Discount update(Long id, Discount discount) {
        Discount existing = findById(id);
        existing.setStudent(discount.getStudent());
        existing.setFeeType(discount.getFeeType());
        existing.setDiscountType(discount.getDiscountType());
        existing.setDiscountValue(discount.getDiscountValue());
        existing.setDescription(discount.getDescription());
        existing.setValidFrom(discount.getValidFrom());
        existing.setValidTo(discount.getValidTo());
        existing.setIsActive(discount.getIsActive());
        return discountRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Discount", "id", id);
        }
        discountRepository.deleteById(id);
    }

    public Double calculateDiscount(Long studentId, Long feeTypeId, Double originalAmount) {
        List<Discount> discounts = discountRepository.findByStudentIdAndIsActiveTrue(studentId);
        LocalDate today = LocalDate.now();

        double totalDiscount = 0.0;
        for (Discount discount : discounts) {
            if (discount.getFeeType() != null && discount.getFeeType().getId().equals(feeTypeId)) {
                if (discount.getValidFrom() != null && today.isBefore(discount.getValidFrom())) {
                    continue;
                }
                if (discount.getValidTo() != null && today.isAfter(discount.getValidTo())) {
                    continue;
                }
                if ("PERCENTAGE".equalsIgnoreCase(discount.getDiscountType())) {
                    totalDiscount += originalAmount * discount.getDiscountValue() / 100.0;
                } else if ("FIXED".equalsIgnoreCase(discount.getDiscountType())) {
                    totalDiscount += discount.getDiscountValue();
                }
            }
        }

        return Math.min(totalDiscount, originalAmount);
    }
}
