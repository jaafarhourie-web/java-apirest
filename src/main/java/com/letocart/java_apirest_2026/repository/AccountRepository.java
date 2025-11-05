package com.letocart.java_apirest_2026.repository;

import com.letocart.java_apirest_2026.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    // Méthode personnalisée pour trouver un compte par email
    Optional<Account> findByEmail(String email);

    // Spring Data JPA génère automatiquement l'implémentation
    // en se basant sur le nom de la méthode
}
