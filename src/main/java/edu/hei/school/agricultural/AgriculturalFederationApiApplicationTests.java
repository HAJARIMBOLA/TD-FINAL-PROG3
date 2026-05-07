package edu.hei.school.agricultural;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class AgriculturalFederationApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test 1: Requête sans clé API - devrait retourner 401
     */
    @Test
    public void testGetCollectivityWithoutApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(get("/collectivities/test-id"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }

    /**
     * Test 2: Requête avec clé API incorrecte - devrait retourner 401
     */
    @Test
    public void testGetCollectivityWithInvalidApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(get("/collectivities/test-id")
                .header("x-api-key", "invalid-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }

    /**
     * Test 3: Requête avec clé API correcte - le filtre la laisse passer
     * (On s'attend à une 404 ou autre erreur métier, pas une 401)
     */
    @Test
    public void testGetCollectivityWithValidApiKey_shouldPassAuthFilter() throws Exception {
        mockMvc.perform(get("/collectivities/test-id")
                .header("x-api-key", "agri-secure-key"))
                .andExpect(status().isNotFound()); // 404 car le collectivity n'existe pas, pas 401
    }

    /**
     * Test 4: POST sans clé API - devrait retourner 401
     */
    @Test
    public void testPostCollectivitiesWithoutApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(post("/collectivities")
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }

    /**
     * Test 5: POST avec clé API invalide - devrait retourner 401
     */
    @Test
    public void testPostCollectivitiesWithInvalidApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(post("/collectivities")
                .header("x-api-key", "wrong-key")
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }

    /**
     * Test 6: POST avec clé API valide - le filtre la laisse passer
     */
    @Test
    public void testPostCollectivitiesWithValidApiKey_shouldPassAuthFilter() throws Exception {
        mockMvc.perform(post("/collectivities")
                .header("x-api-key", "agri-secure-key")
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isOk()); // Le contrôleur traite la requête
    }

    /**
     * Test 7: Header x-api-key vide - devrait retourner 401
     */
    @Test
    public void testGetCollectivityWithEmptyApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(get("/collectivities/test-id")
                .header("x-api-key", ""))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }

    /**
     * Test 8: Clé API avec espaces - devrait retourner 401
     */
    @Test
    public void testGetCollectivityWithWhitespaceInApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(get("/collectivities/test-id")
                .header("x-api-key", " agri-secure-key "))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }

    /**
     * Test 9: Clé API sensible à la casse - devrait retourner 401
     */
    @Test
    public void testGetCollectivityWithWrongCaseApiKey_shouldReturn401() throws Exception {
        mockMvc.perform(get("/collectivities/test-id")
                .header("x-api-key", "AGRI-SECURE-KEY"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\": \"Bad credentials\"}"));
    }
}
