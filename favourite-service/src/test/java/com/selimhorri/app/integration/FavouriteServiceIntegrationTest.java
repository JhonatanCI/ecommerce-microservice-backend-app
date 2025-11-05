package com.selimhorri.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.FavouriteDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de Integración 3: Favourite Service - User-Product Relationship
 * Valida la comunicación entre User Service y Product Service a través de Favourites
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Test 3: Favourite Service - User-Product Integration")
class FavouriteServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should add product to user favourites")
    void shouldAddProductToUserFavourites() throws Exception {
        // Given - Usuario y Producto existen (IDs de prueba)
        Integer userId = 1;
        Integer productId = 1;

        FavouriteDto favourite = FavouriteDto.builder()
                .userId(userId)
                .productId(productId)
                .likeDate(LocalDateTime.now())
                .build();

        String favouriteJson = objectMapper.writeValueAsString(favourite);

        // When - Agregar a favoritos
        MvcResult result = mockMvc.perform(post("/api/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favouriteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productId").value(productId))
                .andReturn();

        // Then - Verificar que se agregó correctamente
        String responseContent = result.getResponse().getContentAsString();
        FavouriteDto createdFavourite = objectMapper.readValue(responseContent, FavouriteDto.class);
        
        mockMvc.perform(get("/api/favourites/{favouriteId}", createdFavourite.getFavouriteId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    @DisplayName("Should retrieve all favourites for a user")
    void shouldRetrieveAllFavouritesForUser() throws Exception {
        // Given - Usuario con múltiples productos favoritos
        Integer userId = 1;
        
        FavouriteDto favourite1 = FavouriteDto.builder()
                .userId(userId)
                .productId(1)
                .likeDate(LocalDateTime.now())
                .build();

        FavouriteDto favourite2 = FavouriteDto.builder()
                .userId(userId)
                .productId(2)
                .likeDate(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favourite1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favourite2)))
                .andExpect(status().isOk());

        // When & Then - Obtener favoritos del usuario
        mockMvc.perform(get("/api/favourites/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].userId", everyItem(is(userId))));
    }

    @Test
    @DisplayName("Should remove product from user favourites")
    void shouldRemoveProductFromUserFavourites() throws Exception {
        // Given - Agregar favorito
        Integer userId = 1;
        Integer productId = 3;

        FavouriteDto favourite = FavouriteDto.builder()
                .userId(userId)
                .productId(productId)
                .likeDate(LocalDateTime.now())
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(favourite)))
                .andExpect(status().isOk())
                .andReturn();

        FavouriteDto createdFavourite = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), FavouriteDto.class);
        Integer favouriteId = createdFavourite.getFavouriteId();

        // When - Eliminar favorito
        mockMvc.perform(delete("/api/favourites/{favouriteId}", favouriteId))
                .andExpect(status().isOk());

        // Then - Verificar que ya no existe
        mockMvc.perform(get("/api/favourites/{favouriteId}", favouriteId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should prevent duplicate favourites for same user-product")
    void shouldPreventDuplicateFavourites() throws Exception {
        // Given - Agregar favorito
        Integer userId = 1;
        Integer productId = 5;

        FavouriteDto favourite = FavouriteDto.builder()
                .userId(userId)
                .productId(productId)
                .likeDate(LocalDateTime.now())
                .build();

        String favouriteJson = objectMapper.writeValueAsString(favourite);

        // Primer favorito - debería funcionar
        mockMvc.perform(post("/api/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favouriteJson))
                .andExpect(status().isOk());

        // When & Then - Intentar agregar el mismo favorito nuevamente
        mockMvc.perform(post("/api/favourites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favouriteJson))
                .andExpect(status().isConflict()); // Debería fallar
    }

    @Test
    @DisplayName("Should count total favourites per product")
    void shouldCountTotalFavouritesPerProduct() throws Exception {
        // Given - Múltiples usuarios agregando el mismo producto a favoritos
        Integer productId = 10;

        for (int userId = 1; userId <= 5; userId++) {
            FavouriteDto favourite = FavouriteDto.builder()
                    .userId(userId)
                    .productId(productId)
                    .likeDate(LocalDateTime.now())
                    .build();

            mockMvc.perform(post("/api/favourites")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(favourite)))
                    .andExpect(status().isOk());
        }

        // When & Then - Obtener conteo de favoritos para el producto
        mockMvc.perform(get("/api/favourites/product/{productId}/count", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(greaterThanOrEqualTo(5)));
    }
}
