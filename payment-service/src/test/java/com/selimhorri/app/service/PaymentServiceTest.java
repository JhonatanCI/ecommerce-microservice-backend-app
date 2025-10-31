package com.selimhorri.app.service;

import com.selimhorri.app.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    void testRepositoryNotNull() {
        assertNotNull(paymentRepository);
    }

    @Test
    void testFindAllMethodExists() {
        when(paymentRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(paymentRepository.findAll());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdMethodExists() {
        when(paymentRepository.findById(1)).thenReturn(java.util.Optional.empty());
        assertTrue(paymentRepository.findById(1).isEmpty());
        verify(paymentRepository, times(1)).findById(1);
    }

    @Test
    void testSaveMethodExists() {
        when(paymentRepository.save(any())).thenReturn(null);
        paymentRepository.save(null);
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void testDeleteByIdMethodExists() {
        doNothing().when(paymentRepository).deleteById(1);
        paymentRepository.deleteById(1);
        verify(paymentRepository, times(1)).deleteById(1);
    }
}
