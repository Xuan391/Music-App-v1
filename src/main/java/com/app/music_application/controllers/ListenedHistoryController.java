package com.app.music_application.controllers;

import com.app.music_application.models.ListenedHistory;
import com.app.music_application.models.ResponseObject;
import com.app.music_application.models.Song;
import com.app.music_application.models.User;
import com.app.music_application.repositories.ListenedHistoryRepository;
import com.app.music_application.repositories.SongRepository;
import com.app.music_application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ListenedHistories")
public class ListenedHistoryController {
    @Autowired
    private ListenedHistoryRepository listenedHistoryRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;

    @GetMapping("/ShowAll")
    List<ListenedHistory> getAllHistory() {return listenedHistoryRepository.findAll();}

    @GetMapping("/showbyuserid")
    ResponseEntity<ResponseObject> getListendHistory(@RequestParam ("userId") Long userId){
        User user = userRepository.findById(userId).orElse(null);
        List<ListenedHistory> listenedHistories = listenedHistoryRepository.findByUserId(user);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("Ok","get listenedHistories with userId successfully", listenedHistories)
        );
    }

    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertListiendHistory(@RequestParam("userId") Long userId, @RequestParam("songId") Long songId) {
        User user = userRepository.findById(userId).orElse(null);
        Song song = songRepository.findById(songId).orElse(null);
        song.setListenedCount(song.getListenedCount()+1);

        ListenedHistory listenedHistory = new ListenedHistory();
        listenedHistory.setUserId(user);
        listenedHistory.setSongId(song);
        listenedHistory.setListenedTime(LocalDateTime.now());
        listenedHistoryRepository.save(listenedHistory);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK","Insert ListenedHistory successfully", listenedHistory)
        );
    }
}
