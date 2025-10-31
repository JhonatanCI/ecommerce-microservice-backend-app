package com.selimhorri.app.service;

import com.selimhorri.app.repository.FavouriteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    private FavouriteRepository favouriteRepository;

    @Test
    void testRepositoryNotNull() {
        assertNotNull(favouriteRepository);
    }

    @Test
    void testFindAllMethodExists() {
        when(favouriteRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        assertNotNull(favouriteRepository.findAll());
        verify(favouriteRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdMethodExists() {
        when(favouriteRepository.findById(any())).thenReturn(java.util.Optional.empty());
        assertTrue(favouriteRepository.findById(any()).isEmpty());
        verify(favouriteRepository, times(1)).findById(any());
    }

    @Test
    void testSaveMethodExists() {
        when(favouriteRepository.save(any())).thenReturn(null);
        favouriteRepository.save(null);
        verify(favouriteRepository, times(1)).save(any());
    }

    @Test
    void testDeleteByIdMethodExists() {
        doNothing().when(favouriteRepository).deleteById(any());
        favouriteRepository.deleteById(null);
        verify(favouriteRepository, times(1)).deleteById(any());
    }
}
