package com.letocart.java_apirest_2026.service;

import com.letocart.java_apirest_2026.model.Notice;
import com.letocart.java_apirest_2026.model.Account;
import com.letocart.java_apirest_2026.model.Product;
import com.letocart.java_apirest_2026.repository.NoticeRepository;
import com.letocart.java_apirest_2026.repository.AccountRepository;
import com.letocart.java_apirest_2026.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository,
                         AccountRepository accountRepository,
                         ProductRepository productRepository) {
        this.noticeRepository = noticeRepository;
        this.accountRepository = accountRepository;
        this.productRepository = productRepository;
    }

    public Notice createNotice(Long accountId, Long productId, Integer rating, String comment) throws Exception {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Exception("Compte non trouvé"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Produit non trouvé"));

        if (rating < 1 || rating > 5) {
            throw new Exception("La note doit être entre 1 et 5");
        }

        Notice notice = new Notice(account, product, rating, comment);
        return noticeRepository.save(notice);
    }

    public List<Notice> getAllNotices() {
        return (List<Notice>) noticeRepository.findAll();
    }

    public List<Notice> getNoticesByProduct(Long productId) {
        return noticeRepository.findByProductProductId(productId);
    }

    public List<Notice> getNoticesByAccount(Long accountId) {
        return noticeRepository.findByAccountAccountId(accountId);
    }

    public void deleteNotice(Long id) throws Exception {
        if (!noticeRepository.existsById(id)) {
            throw new Exception("Avis non trouvé");
        }
        noticeRepository.deleteById(id);
    }
}
