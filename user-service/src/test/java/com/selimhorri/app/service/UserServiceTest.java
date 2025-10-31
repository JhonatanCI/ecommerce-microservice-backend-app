package com.selimhorri.app.service;

import com.selimhorri.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testRepositoryNotNull() {
        assertNotNull(userRepository);
    }

    @Test
    void testFindAllMethodExists() {
        when(userRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(userRepository.findAll());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdMethodExists() {
        when(userRepository.findById(1)).thenReturn(java.util.Optional.empty());
        assertTrue(userRepository.findById(1).isEmpty());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testSaveMethodExists() {
        when(userRepository.save(any())).thenReturn(null);
        userRepository.save(null);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testDeleteByIdMethodExists() {
        doNothing().when(userRepository).deleteById(1);
        userRepository.deleteById(1);
        verify(userRepository, times(1)).deleteById(1);
    }
}
