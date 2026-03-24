package com.ideiasmidias.sectionattribute.service;

import com.ideiasmidias.common.exception.BadRequestException;
import com.ideiasmidias.common.exception.ConflictException;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.section.entity.Section;
import com.ideiasmidias.section.repository.SectionRepository;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionRequest;
import com.ideiasmidias.sectionattribute.dto.SectionAttributeDefinitionResponse;
import com.ideiasmidias.sectionattribute.dto.SectionItemAttributeValueRequest;
import com.ideiasmidias.sectionattribute.dto.SectionItemAttributeValueResponse;
import com.ideiasmidias.sectionattribute.entity.SectionAttributeDefinition;
import com.ideiasmidias.sectionattribute.entity.SectionItemAttributeValue;
import com.ideiasmidias.sectionattribute.repository.SectionAttributeDefinitionRepository;
import com.ideiasmidias.sectionattribute.repository.SectionItemAttributeValueRepository;
import com.ideiasmidias.item.entity.SectionItem;
import com.ideiasmidias.item.repository.SectionItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionAttributeDefinitionServiceImpl implements SectionAttributeDefinitionService {

    private final SectionAttributeDefinitionRepository definitionRepository;
    private final SectionItemAttributeValueRepository valueRepository;
    private final SectionRepository sectionRepository;
    private final SectionItemRepository sectionItemRepository;

    @Override
    public SectionAttributeDefinitionResponse create(SectionAttributeDefinitionRequest request) {
        Section section = getSection(request.getSectionId());
        validateCodeUniqueness(section.getId(), request.getCode(), null);

        SectionAttributeDefinition definition = new SectionAttributeDefinition();
        applyDefinitionRequest(definition, section, request);
        return mapDefinitionResponse(definitionRepository.save(definition));
    }

    @Override
    public SectionAttributeDefinitionResponse update(Long id, SectionAttributeDefinitionRequest request) {
        SectionAttributeDefinition definition = getDefinition(id);
        Section section = getSection(request.getSectionId());
        validateCodeUniqueness(section.getId(), request.getCode(), id);

        applyDefinitionRequest(definition, section, request);
        return mapDefinitionResponse(definitionRepository.save(definition));
    }

    @Override
    @Transactional
    public SectionAttributeDefinitionResponse getById(Long id) {
        return mapDefinitionResponse(getDefinition(id));
    }

    @Override
    @Transactional
    public List<SectionAttributeDefinitionResponse> getBySection(Long sectionId, boolean onlyActive) {
        List<SectionAttributeDefinition> definitions = onlyActive
                ? definitionRepository.findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId)
                : definitionRepository.findAllBySection_IdOrderBySortOrderAscIdAsc(sectionId);
        return definitions.stream().map(this::mapDefinitionResponse).toList();
    }

    @Override
    public void delete(Long id) {
        SectionAttributeDefinition definition = getDefinition(id);
        if (valueRepository.countByAttributeDefinition_Id(id) > 0) {
            throw new BadRequestException("Cannot delete attribute definition while it still has item values");
        }
        definitionRepository.delete(definition);
    }

    @Override
    public List<SectionItemAttributeValue> replaceValuesForItem(Long sectionId, Long sectionItemId, List<SectionItemAttributeValueRequest> values) {
        SectionItem sectionItem = sectionItemRepository.findById(sectionItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Section item not found with id: " + sectionItemId));

        if (!sectionItem.getSection().getId().equals(sectionId)) {
            throw new BadRequestException("Item does not belong to the provided section");
        }

        List<SectionAttributeDefinition> definitions = definitionRepository.findAllBySection_IdAndIsActiveTrueOrderBySortOrderAscIdAsc(sectionId);
        Map<Long, SectionAttributeDefinition> definitionById = definitions.stream()
                .collect(Collectors.toMap(SectionAttributeDefinition::getId, Function.identity()));

        valueRepository.deleteAllBySectionItem_Id(sectionItemId);
        if (values == null || values.isEmpty()) {
            return List.of();
        }

        List<SectionItemAttributeValue> newValues = values.stream().map(valueRequest -> {
            SectionAttributeDefinition definition = definitionById.get(valueRequest.getAttributeDefinitionId());
            if (definition == null) {
                throw new BadRequestException("Attribute definition does not belong to the item section: " + valueRequest.getAttributeDefinitionId());
            }
            validateRequiredValue(definition, valueRequest);

            SectionItemAttributeValue value = new SectionItemAttributeValue();
            value.setSectionItem(sectionItem);
            value.setAttributeDefinition(definition);
            value.setValueText(valueRequest.getValueText());
            value.setValueNumber(valueRequest.getValueNumber());
            value.setValueDate(valueRequest.getValueDate());
            value.setValueBoolean(valueRequest.getValueBoolean());
            return value;
        }).toList();

        return valueRepository.saveAll(newValues);
    }

    @Override
    public List<SectionItemAttributeValueResponse> toValueResponses(List<SectionItemAttributeValue> values) {
        return values.stream()
                .map(value -> SectionItemAttributeValueResponse.builder()
                        .id(value.getId())
                        .attributeDefinitionId(value.getAttributeDefinition().getId())
                        .attributeCode(value.getAttributeDefinition().getCode())
                        .labelPt(value.getAttributeDefinition().getLabelPt())
                        .labelEn(value.getAttributeDefinition().getLabelEn())
                        .valueText(value.getValueText())
                        .valueNumber(value.getValueNumber())
                        .valueDate(value.getValueDate())
                        .valueBoolean(value.getValueBoolean())
                        .build())
                .toList();
    }

    private void validateRequiredValue(SectionAttributeDefinition definition, SectionItemAttributeValueRequest request) {
        if (!Boolean.TRUE.equals(definition.getIsRequired())) {
            return;
        }

        boolean hasValue = request.getValueText() != null
                || request.getValueNumber() != null
                || request.getValueDate() != null
                || request.getValueBoolean() != null;

        if (!hasValue) {
            throw new BadRequestException("Required attribute value missing for code: " + definition.getCode());
        }
    }

    private void applyDefinitionRequest(SectionAttributeDefinition definition, Section section, SectionAttributeDefinitionRequest request) {
        definition.setSection(section);
        definition.setCode(normalizeCode(request.getCode()));
        definition.setLabelPt(request.getLabelPt());
        definition.setLabelEn(request.getLabelEn());
        definition.setFieldType(request.getFieldType());
        definition.setIsRequired(Boolean.TRUE.equals(request.getIsRequired()));
        definition.setIsFilterable(Boolean.TRUE.equals(request.getIsFilterable()));
        definition.setIsDisplayedInCard(request.getIsDisplayedInCard() == null || request.getIsDisplayedInCard());
        definition.setIsDisplayedInDetails(request.getIsDisplayedInDetails() == null || request.getIsDisplayedInDetails());
        definition.setOptionsJson(request.getOptionsJson());
        definition.setIsActive(request.getIsActive() == null || request.getIsActive());
        definition.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
    }

    private void validateCodeUniqueness(Long sectionId, String code, Long currentDefinitionId) {
        String normalizedCode = normalizeCode(code);
        definitionRepository.findBySection_IdAndCode(sectionId, normalizedCode).ifPresent(existing -> {
            if (currentDefinitionId == null || !existing.getId().equals(currentDefinitionId)) {
                throw new ConflictException("Attribute code already exists in section: " + code);
            }
        });
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toLowerCase(Locale.ROOT);
    }

    private Section getSection(Long sectionId) {
        return sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
    }

    private SectionAttributeDefinition getDefinition(Long id) {
        return definitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section attribute definition not found with id: " + id));
    }

    private SectionAttributeDefinitionResponse mapDefinitionResponse(SectionAttributeDefinition definition) {
        return SectionAttributeDefinitionResponse.builder()
                .id(definition.getId())
                .sectionId(definition.getSection().getId())
                .code(definition.getCode())
                .labelPt(definition.getLabelPt())
                .labelEn(definition.getLabelEn())
                .fieldType(definition.getFieldType())
                .isRequired(definition.getIsRequired())
                .isFilterable(definition.getIsFilterable())
                .isDisplayedInCard(definition.getIsDisplayedInCard())
                .isDisplayedInDetails(definition.getIsDisplayedInDetails())
                .optionsJson(definition.getOptionsJson())
                .isActive(definition.getIsActive())
                .sortOrder(definition.getSortOrder())
                .createdAt(definition.getCreatedAt())
                .updatedAt(definition.getUpdatedAt())
                .build();
    }
}
