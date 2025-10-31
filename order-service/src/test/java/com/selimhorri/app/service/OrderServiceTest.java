package com.selimhorri.app.service;

import com.selimhorri.app.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Test
    void testRepositoryNotNull() {
        assertNotNull(orderRepository);
    }

    @Test
    void testFindAllMethodExists() {
        when(orderRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(orderRepository.findAll());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdMethodExists() {
        when(orderRepository.findById(1)).thenReturn(java.util.Optional.empty());
        assertTrue(orderRepository.findById(1).isEmpty());
        verify(orderRepository, times(1)).findById(1);
    }

    @Test
    void testSaveMethodExists() {
        when(orderRepository.save(any())).thenReturn(null);
        orderRepository.save(null);
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void testDeleteByIdMethodExists() {
        doNothing().when(orderRepository).deleteById(1);
        orderRepository.deleteById(1);
        verify(orderRepository, times(1)).deleteById(1);
    }
}
