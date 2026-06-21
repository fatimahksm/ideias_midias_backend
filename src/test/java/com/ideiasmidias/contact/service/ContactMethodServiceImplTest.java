package com.ideiasmidias.contact.service;

import com.ideiasmidias.common.enums.ContactMethodType;
import com.ideiasmidias.common.exception.ResourceNotFoundException;
import com.ideiasmidias.contact.dto.ContactMethodRequest;
import com.ideiasmidias.contact.dto.ContactMethodResponse;
import com.ideiasmidias.contact.entity.ContactMethod;
import com.ideiasmidias.contact.repository.ContactMethodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactMethodServiceImplTest {

    @Mock
    private ContactMethodRepository repository;

    @InjectMocks
    private ContactMethodServiceImpl service;

    private ContactMethodRequest validRequest() {
        ContactMethodRequest request = new ContactMethodRequest();
        request.setType(ContactMethodType.WHATSAPP);
        request.setLabelPt("Fale conosco");
        request.setLabelEn("Talk to us");
        request.setValue("+5511999999999");
        request.setIconName("whatsapp");
        return request;
    }

    @Test
    void createMapsRequestAndReturnsResponse() {
        when(repository.save(any(ContactMethod.class))).thenAnswer(inv -> inv.getArgument(0));

        ContactMethodResponse response = service.create(validRequest());

        assertThat(response.getType()).isEqualTo(ContactMethodType.WHATSAPP);
        assertThat(response.getLabelPt()).isEqualTo("Fale conosco");
        assertThat(response.getLabelEn()).isEqualTo("Talk to us");
        assertThat(response.getValue()).isEqualTo("+5511999999999");
        assertThat(response.getIconName()).isEqualTo("whatsapp");
    }

    @Test
    void createAppliesDefaultsForNullFlags() {
        ContactMethodRequest request = validRequest();
        request.setIsActive(null);
        request.setSortOrder(null);

        ArgumentCaptor<ContactMethod> captor = ArgumentCaptor.forClass(ContactMethod.class);
        when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.create(request);

        ContactMethod saved = captor.getValue();
        assertThat(saved.getIsActive()).isTrue();
        assertThat(saved.getSortOrder()).isZero();
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteThrowsWhenMissingAndDoesNotCallDelete() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(5L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    void deleteRemovesExistingEntity() {
        ContactMethod entity = new ContactMethod();
        entity.setId(5L);
        entity.setType(ContactMethodType.EMAIL);
        entity.setValue("hello@example.com");
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        service.delete(5L);

        verify(repository).delete(entity);
    }
}
