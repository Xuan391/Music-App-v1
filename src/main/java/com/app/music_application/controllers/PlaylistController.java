package com.app.music_application.controllers;

import com.app.music_application.models.Category;
import com.app.music_application.models.Playlist;
import com.app.music_application.models.ResponseObject;
import com.app.music_application.repositories.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Playlists")
public class PlaylistController {
    @Autowired
    private PlaylistRepository playlistRepository;

    @GetMapping("/ShowAll")
    List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id){
        Optional<Playlist> foundPlaylist = playlistRepository.findById(id);
        return foundPlaylist.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Query category successfully",foundPlaylist)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Cannot find category with id = "+id, "")
                );
    }
}

