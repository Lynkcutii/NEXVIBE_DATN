package com.example.datnspct.Controller.admin;

import com.example.datnspct.Model.ChatLieu;
import com.example.datnspct.Repository.ChatLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chatlieu")
public class ChatLieuController {

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @GetMapping
    public ResponseEntity<List<ChatLieu>> layTatCaChatLieu() {
        List<ChatLieu> chatLieus = chatLieuRepository.findAll();
        return ResponseEntity.ok(chatLieus);
    }
}