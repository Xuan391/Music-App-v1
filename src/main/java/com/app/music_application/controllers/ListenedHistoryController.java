package com.app.music_application.controllers;

import com.app.music_application.repositories.ListenedHistoryRepository;
import com.app.music_application.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ListenedHistories")
public class ListenedHistoryController {
    @Autowired
    private ListenedHistoryRepository listenedHistoryRepository;

    @Autowired
    private SongRepository songRepository;


}
