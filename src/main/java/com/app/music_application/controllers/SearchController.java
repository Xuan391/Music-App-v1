package com.app.music_application.controllers;

import com.app.music_application.models.*;
import com.app.music_application.repositories.PlaylistRepository;
import com.app.music_application.repositories.SearchHistoryRepository;
import com.app.music_application.repositories.SongRepository;
import com.app.music_application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @GetMapping("/searchAll")
    public List<SearchResultDTO> search (@RequestParam("searchText") String searchText, @RequestParam("userId") Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        SearchHistory existingSearchHistory = searchHistoryRepository.findByUserIdAndSearchQuery(user, searchText);
        //save history
        // Check if SearchHistory exists for the searchText

        if ((existingSearchHistory == null)) {
            // If no SearchHistory found, create a new one
            SearchHistory newSearchHistory = new SearchHistory();
            newSearchHistory.setUserId(user);
            newSearchHistory.setSearchQuery(searchText);
            newSearchHistory.setSearchTime(LocalDateTime.now());
            searchHistoryRepository.save(newSearchHistory);
        } else {
            // If SearchHistory already exists, update it
            existingSearchHistory.setSearchTime(LocalDateTime.now());
            searchHistoryRepository.save(existingSearchHistory);
        }
        //results search
        List<User> foundUsers = userRepository.searchUsersByName(searchText);
        List<Song> songs = songRepository.searchSongsByName(searchText);
        List<Playlist> playlists = playlistRepository.searchPlaylistByName(searchText);

        List<SearchResultDTO> results = new ArrayList<>();

        // Add found users to results
        for (User foundUser : foundUsers) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setType("user");
            dto.setId(foundUser.getId());
            dto.setName(foundUser.getName());
            // Set other properties of dto if needed
            results.add(dto);
        }

        // Add songs to results
        for (Song song : songs) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setType("song");
            dto.setId(song.getId());
            dto.setName(song.getName());
            // Set other properties of dto if needed
            results.add(dto);
        }

        // Add playlists to results
        for (Playlist playlist : playlists) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setType("playlist");
            dto.setId(playlist.getId());
            dto.setName(playlist.getName());
            // Set other properties of dto if needed
            results.add(dto);
        }

        Collections.shuffle(results); // Shuffle the results
        return results;

    }

    @GetMapping("/searchSong")
    public List<ShowSongDTO> searchSong (@RequestParam ("searchText") String searchText, @RequestParam ("userId") Long UserId){
        List<Song> songs = songRepository.searchSongsByName(searchText);
        List<ShowSongDTO> songDTOS = new ArrayList<>();
        for (Song song: songs){
            ShowSongDTO songDTO = new ShowSongDTO();
            songDTO.setSongId(song.getId());
            songDTO.setNameSong(song.getName());
            songDTO.setNameUser(song.getCreator().getUserName());
            songDTO.setThumbnail(song.getThumbnailUrl());
            songDTO.setUrl(song.getUrl());
            songDTO.setUserId(song.getCreator().getId());
            songDTOS.add(songDTO);
        }
        return songDTOS;
    }
}
