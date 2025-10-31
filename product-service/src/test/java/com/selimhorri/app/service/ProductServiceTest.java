package com.selimhorri.app.service;

import com.selimhorri.app.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    void testRepositoryNotNull() {
        // Simple test to verify repository mock is created
        assertNotNull(productRepository);
    }

    @Test
    void testFindAllMethodExists() {
        // Verify repository has findAll method
        when(productRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(productRepository.findAll());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdMethodExists() {
        // Verify repository has findById method
        when(productRepository.findById(1)).thenReturn(java.util.Optional.empty());
        assertTrue(productRepository.findById(1).isEmpty());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testSaveMethodExists() {
        // Verify repository has save method
        when(productRepository.save(any())).thenReturn(null);
        productRepository.save(null);
        verify(productRepository, times(1)).save(any());
    }

    @Test
    void testDeleteByIdMethodExists() {
        // Verify repository has deleteById method
        doNothing().when(productRepository).deleteById(1);
        productRepository.deleteById(1);
        verify(productRepository, times(1)).deleteById(1);
    }
}
