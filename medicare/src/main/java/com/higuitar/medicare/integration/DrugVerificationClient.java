package com.higuitar.medicare.integration;


import com.higuitar.medicare.dto.response.DrugResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Verifies prescription drugs against the public OpenFDA API using a {@link RestClient}.
 * <p>
 * Guarded by the {@code drugVerification} Circuit Breaker (2s timeout): when the
 * API is down or slow, {@link #verificationFallback(String, Exception)} marks the
 * verification as pending instead of failing the clinical note creation.
 */
@Component
public class DrugVerificationClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    RestClient restClient = RestClient.create
            ("https://api.fda.gov");

    /**
     * Queries OpenFDA for the first product matching the given active ingredient
     * name and returns its brand name and active ingredient.
     *
     * @param drugName the active ingredient to search for
     * @return the brand name and active ingredient of the first matching product
     */
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

    /**
     * Fallback invoked by the Circuit Breaker when the OpenFDA call fails or
     * exceeds the configured timeout; reports the verification as pending.
     *
     * @param drugName the drug that could not be verified
     * @param ex       the failure that triggered the fallback
     * @return a response marking the verification as pending
     */
    public DrugResponse verificationFallback (String drugName, Exception ex){
        return new DrugResponse(drugName,"Medicament Verification Pending!");
    }
}
