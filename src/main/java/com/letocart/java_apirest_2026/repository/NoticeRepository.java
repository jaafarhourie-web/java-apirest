package com.letocart.java_apirest_2026.repository;

import com.letocart.java_apirest_2026.model.Notice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticeRepository extends CrudRepository<Notice, Long> {

    // Trouver les avis d'un produit
    List<Notice> findByProductProductId(Long productId);

    // Trouver les avis d'un utilisateur
    List<Notice> findByAccountAccountId(Long accountId);
}
