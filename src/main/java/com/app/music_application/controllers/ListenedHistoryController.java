package com.app.music_application.controllers;

import com.app.music_application.models.ListenedHistory;
import com.app.music_application.models.ResponseObject;
import com.app.music_application.repositories.ListenedHistoryRepository;
import com.app.music_application.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ListenedHistories")
public class ListenedHistoryController {
    @Autowired
    private ListenedHistoryRepository listenedHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    @GetMapping("/ShowAll")
    List<ListenedHistory> getAllHistory() {return listenedHistoryRepository.findAll();}


}
