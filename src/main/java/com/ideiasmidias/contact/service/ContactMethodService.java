package com.ideiasmidias.contact.service;

import com.ideiasmidias.common.enums.ContactMethodType;
import com.ideiasmidias.contact.dto.ContactMethodRequest;
import com.ideiasmidias.contact.dto.ContactMethodResponse;

import java.util.List;

public interface ContactMethodService {

    ContactMethodResponse create(ContactMethodRequest request);

    ContactMethodResponse update(Long id, ContactMethodRequest request);

    ContactMethodResponse getById(Long id);

    List<ContactMethodResponse> getAll();

    List<ContactMethodResponse> getAllActive();

    List<ContactMethodResponse> getByType(ContactMethodType type);

    List<ContactMethodResponse> getActiveByType(ContactMethodType type);

    void delete(Long id);
}