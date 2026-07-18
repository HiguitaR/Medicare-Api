package com.higuitar.medicare.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;

public record DrugResponse(

        @JsonProperty("brand_name") String brandName,
        @JsonProperty("active_ingredients") String activeIngredients
) {
}
