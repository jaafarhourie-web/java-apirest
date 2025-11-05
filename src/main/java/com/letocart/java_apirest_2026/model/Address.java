package com.letocart.java_apirest_2026.model;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    // Relation OneToOne avec Account (bidirectionnelle)
    @OneToOne(mappedBy = "address")
    private Account account;

    // Constructeur par défaut (obligatoire pour JPA)
    public Address() {}

    // Constructeur avec paramètres
    public Address(String street, String city, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    // Getters et Setters
    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
