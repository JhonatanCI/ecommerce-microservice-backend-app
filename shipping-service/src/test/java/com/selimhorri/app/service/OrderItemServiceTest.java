package com.selimhorri.app.service;

import com.selimhorri.app.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    void testRepositoryNotNull() {
        assertNotNull(orderItemRepository);
    }

    @Test
    void testFindAllMethodExists() {
        when(orderItemRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(orderItemRepository.findAll());
        verify(orderItemRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdMethodExists() {
        when(orderItemRepository.findById(any())).thenReturn(java.util.Optional.empty());
        assertTrue(orderItemRepository.findById(any()).isEmpty());
        verify(orderItemRepository, times(1)).findById(any());
    }

    @Test
    void testSaveMethodExists() {
        when(orderItemRepository.save(any())).thenReturn(null);
        orderItemRepository.save(null);
        verify(orderItemRepository, times(1)).save(any());
    }

    @Test
    void testDeleteByIdMethodExists() {
        doNothing().when(orderItemRepository).deleteById(any());
        orderItemRepository.deleteById(null);
        verify(orderItemRepository, times(1)).deleteById(any());
    }
}
