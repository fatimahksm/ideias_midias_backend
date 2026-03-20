package com.ideiasmidias.contact.repository;

import com.ideiasmidias.contact.entity.ContactMethod;
import com.ideiasmidias.common.enums.ContactMethodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMethodRepository extends JpaRepository<ContactMethod, Long> {

    List<ContactMethod> findAllByOrderBySortOrderAscIdAsc();

    List<ContactMethod> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    List<ContactMethod> findAllByTypeOrderBySortOrderAscIdAsc(ContactMethodType type);

    List<ContactMethod> findAllByTypeAndIsActiveTrueOrderBySortOrderAscIdAsc(ContactMethodType type);
}