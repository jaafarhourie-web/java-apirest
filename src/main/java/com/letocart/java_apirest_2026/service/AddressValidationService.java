package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.dto.AddressValidationResponse;
import com.letocart.java_apirest_2026.model.Address;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Service de validation d'adresse via l'API de géocodage
 * 
 * Utilise HttpClient natif Java 11+ avec support HTTP/2
 */
@Service
public class AddressValidationService {

    private final HttpClient httpClient;

    // API du labo RIOC (remote)
    private static final String API_BASE_URL = "https://api-gouv.lab.rioc.fr/search";
    private static final double SCORE_THRESHOLD = 0.5;

    public AddressValidationService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    /**
     * Valide une adresse en appelant l'API de géocodage
     * 
     * @param address L'adresse à valider
     * @return true si l'adresse existe et a un score de confiance > 0.5
     */
    public boolean validateAddress(Address address) {
        try {
            // 1. Construction de la requête complète d'adresse
            String fullAddress = buildFullAddress(address);

            // 2. Appel à l'API externe
            AddressValidationResponse response = callGeocodingApi(fullAddress);
            
            // 3. Vérification de la réponse
            if (response == null || response.getFeatures() == null || response.getFeatures().isEmpty()) {
                return false;
            }

            // 4. Traitement avec STREAM (comme demandé dans le TD)
            return response.getFeatures().stream()
                    .findFirst()
                    .map(feature -> feature.getProperties().getScore())
                    .filter(score -> score != null && score > SCORE_THRESHOLD)
                    .isPresent();

        } catch (Exception e) {
            System.err.println("Erreur validation adresse: " + e.getMessage());
            return false;
        }
    }

    /**
     * Construit l'adresse complète à partir des champs
     */
    private String buildFullAddress(Address address) {
        return String.format("%s %s %s",
                address.getStreet(),
                address.getPostalCode(),
                address.getCity()
        );
    }

    /**
     * Appelle l'API de géocodage avec HttpClient natif (HTTP/2)
     */
    private AddressValidationResponse callGeocodingApi(String fullAddress) {
        String url = UriComponentsBuilder
                .fromUriString(API_BASE_URL)
                .queryParam("q", fullAddress)
                .queryParam("limit", "1")
                .toUriString();

        try {
            // Construction de la requête HTTP avec headers
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (compatible; JavaApp/1.0)")
                    .header("Accept", "*/*")
                    .GET()
                    .build();
            
            // Envoi de la requête et récupération de la réponse
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            
            if (json == null || json.isBlank()) {
                return null;
            }

            // Convertir avec Jackson
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, AddressValidationResponse.class);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel à l'API de géocodage: " + e.getMessage());
            return null;
        }
    }
}
