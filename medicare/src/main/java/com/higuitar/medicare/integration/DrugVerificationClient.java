package com.higuitar.medicare.integration;


import com.higuitar.medicare.dto.response.DrugResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class DrugVerificationClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    RestClient restClient = RestClient.create
            ("https://api.fda.gov");

    @CircuitBreaker(name="drugVerification", fallbackMethod = "verificationFallback")
    public DrugResponse verifyDrug(String drugName){
        String json = restClient.get()
                .uri("/drug/drugsfda.json?search=active_ingredients.name:{drugName}&limit=1", drugName)
                .retrieve()
                .body(String.class);
        JsonNode root = objectMapper.readTree(json);
        JsonNode product = root.path("results").get(0).path("products").get(0);
        String brandName = product.path("brand_name").asString();
        String activeIngredients = product.path("active_ingredients").get(0)
                .path("name").asString();

        return new DrugResponse(brandName, activeIngredients);

    }

    public DrugResponse verificationFallback (String drugName, Exception ex){
        return new DrugResponse(drugName,"Medicament Verification Pending!");
    }
}
