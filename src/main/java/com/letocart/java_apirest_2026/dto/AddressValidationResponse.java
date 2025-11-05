package com.letocart.java_apirest_2026.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressValidationResponse {

    private String type;
    private List<Feature> features;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<Feature> getFeatures() { return features; }
    public void setFeatures(List<Feature> features) { this.features = features; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private Properties properties;

        public Properties getProperties() { return properties; }
        public void setProperties(Properties properties) { this.properties = properties; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private String label;
        private Double score;
        private String postcode;
        private String city;

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
