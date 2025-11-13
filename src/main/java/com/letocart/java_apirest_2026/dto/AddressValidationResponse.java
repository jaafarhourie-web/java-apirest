package com.letocart.java_apirest_2026.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO pour la réponse de l'API de géocodage IGN
 * 
 * Structure de la réponse JSON :
 * {
 *   "type": "FeatureCollection",
 *   "features": [
 *     {
 *       "properties": {
 *         "label": "8 Boulevard du Port 80000 Amiens",
 *         "score": 0.49159121588068583,
 *         "postcode": "80000",
 *         "city": "Amiens"
 *       }
 *     }
 *   ]
 * }
 * 
 * @JsonIgnoreProperties permet d'ignorer les champs non mappés dans le JSON
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressValidationResponse {

    private String type;
    private List<Feature> features = new ArrayList<>(); // Liste d'objets (comme demandé dans le TD)

    // Constructeur par défaut requis pour Jackson
    public AddressValidationResponse() {}

    // Getters et Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Feature> getFeatures() { return features; }
    public void setFeatures(List<Feature> features) { this.features = features; }

    /**
     * Classe interne représentant un Feature de la réponse
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private Properties properties;

        // Constructeur par défaut requis pour Jackson
        public Feature() {}

        public Properties getProperties() { return properties; }
        public void setProperties(Properties properties) { this.properties = properties; }
    }

    /**
     * Classe interne représentant les propriétés d'un Feature
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private String label;      // Adresse complète formatée
        private Double score;       // Score de confiance (0.0 à 1.0)
        private String postcode;    // Code postal
        private String city;        // Ville

        // Constructeur par défaut requis pour Jackson
        public Properties() {}

        // Getters et Setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public String getPostcode() { return postcode; }
        public void setPostcode(String postcode) { this.postcode = postcode; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}
