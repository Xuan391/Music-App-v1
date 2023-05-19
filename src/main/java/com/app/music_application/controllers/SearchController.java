package com.app.music_application.controllers;

import com.app.music_application.repositories.PlaylistRepository;
import com.app.music_application.repositories.SongRepository;
import com.app.music_application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/Search")
public class SearchController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private PlaylistRepository playlistRepository;

    @GetMapping("")
    public List<Object[]> search (@RequestParam("searchText") String searchText) {
        List<Object[]> users = userRepository.searchUsersByName(searchText);
        List<Object[]> songs = songRepository.searchSongsByName(searchText);
        List<Object[]> playlists = playlistRepository.searchPlaylistByName(searchText);

        List<Object[]> results = new ArrayList<>();
        results.addAll(users);
        results.addAll(songs);
        results.addAll(playlists);

        Collections.shuffle(results); // cái này để random các phần tử trong results
         return results;
    }


}
