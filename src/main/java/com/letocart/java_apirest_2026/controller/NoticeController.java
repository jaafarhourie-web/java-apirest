package com.letocart.java_apirest_2026.controller;

import com.letocart.java_apirest_2026.model.Notice;
import com.letocart.java_apirest_2026.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<?> createNotice(@RequestBody Map<String, Object> noticeRequest) {
        try {
            Long accountId = Long.valueOf(noticeRequest.get("accountId").toString());
            Long productId = Long.valueOf(noticeRequest.get("productId").toString());
            Integer rating = Integer.valueOf(noticeRequest.get("rating").toString());
            String comment = noticeRequest.get("comment").toString();

            Notice createdNotice = noticeService.createNotice(accountId, productId, rating, comment);
            return new ResponseEntity<>(createdNotice, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return new ResponseEntity<>(notices, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Notice>> getNoticesByProduct(@PathVariable Long productId) {
        List<Notice> notices = noticeService.getNoticesByProduct(productId);
        return new ResponseEntity<>(notices, HttpStatus.OK);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Notice>> getNoticesByAccount(@PathVariable Long accountId) {
        List<Notice> notices = noticeService.getNoticesByAccount(accountId);
        return new ResponseEntity<>(notices, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
