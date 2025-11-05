package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.dto.AddressValidationResponse;
import com.letocart.java_apirest_2026.model.Address;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AddressValidationService {

    private final RestTemplate restTemplate;

    // L'API du labo pour éviter les limites de rate limiting
    private static final String API_BASE_URL = "https://api-gouv.lab.rioc.fr/geocodage/search";

    public AddressValidationService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Valide une adresse en appelant l'API de géocodage
     * @param address L'adresse à valider
     * @return true si l'adresse existe et a un score de confiance > 0.5
     */
    public boolean validateAddress(Address address) {
        try {
            // Construction de la requête complète d'adresse
            String fullAddress = String.format("%s %s %s",
                    address.getStreet(),
                    address.getPostalCode(),
                    address.getCity()
            );

            // Construction de l'URL avec les paramètres
            String url = UriComponentsBuilder
                    .fromHttpUrl(API_BASE_URL)
                    .queryParam("q", fullAddress)
                    .queryParam("limit", "1")
                    .toUriString();

            // Appel à l'API externe
            AddressValidationResponse response = restTemplate.getForObject(
                    url,
                    AddressValidationResponse.class
            );

            // Vérification de la réponse
            if (response != null && response.getFeatures() != null && !response.getFeatures().isEmpty()) {
                AddressValidationResponse.Feature feature = response.getFeatures().get(0);
                Double score = feature.getProperties().getScore();

                // Score de confiance > 0.5 = adresse valide
                return score != null && score > 0.5;
            }

            return false;

        } catch (Exception e) {
            // En cas d'erreur, on log et on retourne false
            System.err.println("Erreur lors de la validation de l'adresse: " + e.getMessage());
            return false;
        }
    }
}
