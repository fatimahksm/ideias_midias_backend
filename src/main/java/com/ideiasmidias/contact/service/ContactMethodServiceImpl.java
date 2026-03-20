package com.ideiasmidias.contact.service;

import com.ideiasmidias.common.enums.ContactMethodType;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.contact.dto.ContactMethodRequest;
import com.ideiasmidias.contact.dto.ContactMethodResponse;
import com.ideiasmidias.contact.entity.ContactMethod;
import com.ideiasmidias.contact.repository.ContactMethodRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactMethodServiceImpl implements ContactMethodService {

    private final ContactMethodRepository contactMethodRepository;

    @Override
    public ContactMethodResponse create(ContactMethodRequest request) {
        ContactMethod contactMethod = new ContactMethod();
        applyRequestToEntity(contactMethod, request);

        ContactMethod saved = contactMethodRepository.save(contactMethod);
        return mapToResponse(saved);
    }

    @Override
    public ContactMethodResponse update(Long id, ContactMethodRequest request) {
        ContactMethod contactMethod = getEntityById(id);
        applyRequestToEntity(contactMethod, request);

        ContactMethod saved = contactMethodRepository.save(contactMethod);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ContactMethodResponse getById(Long id) {
        return mapToResponse(getEntityById(id));
    }

    @Override
    @Transactional
    public List<ContactMethodResponse> getAll() {
        return contactMethodRepository.findAllByOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<ContactMethodResponse> getAllActive() {
        return contactMethodRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<ContactMethodResponse> getByType(ContactMethodType type) {
        return contactMethodRepository.findAllByTypeOrderBySortOrderAscIdAsc(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<ContactMethodResponse> getActiveByType(ContactMethodType type) {
        return contactMethodRepository.findAllByTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(type)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        ContactMethod contactMethod = getEntityById(id);
        contactMethodRepository.delete(contactMethod);
    }

    private ContactMethod getEntityById(Long id) {
        return contactMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact method not found with id: " + id));
    }

    private void applyRequestToEntity(ContactMethod contactMethod, ContactMethodRequest request) {
        contactMethod.setType(request.getType());
        contactMethod.setLabelPt(request.getLabelPt());
        contactMethod.setLabelEn(request.getLabelEn());
        contactMethod.setValue(request.getValue());
        contactMethod.setIconName(request.getIconName());
        contactMethod.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        contactMethod.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private ContactMethodResponse mapToResponse(ContactMethod contactMethod) {
        return ContactMethodResponse.builder()
                .id(contactMethod.getId())
                .type(contactMethod.getType())
                .labelPt(contactMethod.getLabelPt())
                .labelEn(contactMethod.getLabelEn())
                .value(contactMethod.getValue())
                .iconName(contactMethod.getIconName())
                .isActive(contactMethod.getIsActive())
                .sortOrder(contactMethod.getSortOrder())
                .createdAt(contactMethod.getCreatedAt())
                .updatedAt(contactMethod.getUpdatedAt())
                .build();
    }
}