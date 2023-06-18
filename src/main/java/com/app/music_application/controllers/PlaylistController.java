package com.app.music_application.controllers;

import com.app.music_application.models.*;
import com.app.music_application.repositories.PlaylistRepository;
import com.app.music_application.repositories.SongRepository;
import com.app.music_application.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/Playlists")
public class PlaylistController {
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/ShowAll")
    List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }
    @GetMapping("/show/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id){
        Optional<Playlist> foundPlaylist = playlistRepository.findById(id);
        return foundPlaylist.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Query playlist successfully",foundPlaylist)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Cannot find playlist with id = "+id, "")
                );
    }

    @GetMapping ("/ListPlaylistByUser")
    ResponseEntity<ResponseObject> listPlaylistByUser(@RequestParam("userId") Long userId) {
        List<Playlist> foundPlaylist = playlistRepository.searchPlaylistByCreatorId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "get all playlist sucessfully",foundPlaylist)
        );
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertUser(@RequestParam("name") String name,
                                              @RequestParam("creator") Long userId ) {

        User creator = userRepository.findById(userId).orElse(null);
        if(creator !=null) {
            Playlist playlist = new Playlist();

            playlist.setName(name);
            playlist.setSongs(new HashSet<>());
            playlist.setCreatedAt(LocalDateTime.now());
            playlist.setFavorite(false);
            playlist.setCreatorId(creator);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Insert playlist successfully", playlistRepository.save(playlist))
            );
        } else
        {
            return ResponseEntity.status((HttpStatus.OK)).body(
                    new ResponseObject("false","Can't add playlist due to userId not found","")
            );
        }
    }
//    @PutMapping("/update/{id}")
//    public ResponseEntity<ResponseObject>  updateUser(@RequestBody Playlist newPlaylist, @PathVariable Long id) {
//        Playlist updatePlaylist = playlistRepository.findById(id)
//                .map(playlist -> {
//                    playlist.setName(newPlaylist.getName());
//                    playlist.setSongs(newPlaylist.getSongs());
//                    return playlistRepository.save(playlist);
//                }).orElse(null);
//        if (updatePlaylist != null) {
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseObject("OK", "Update playlist successfully", updatePlaylist)
//            );
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseObject("false", "cannot find playlist with id="+id, "")
//            );
//        }
//    }
    @PutMapping("/update") // thay doi ten cua playlist
    public ResponseEntity<ResponseObject>  updateUser(@RequestParam ("name") String name,
                                                      @RequestParam("playlist") Long id) {
        Playlist updatePlaylist = playlistRepository.findById(id)
                .map(playlist -> {
                    playlist.setName(name);
                    return playlistRepository.save(playlist);
                }).orElse(null);
        if (updatePlaylist != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Update playlist successfully", updatePlaylist)
            );
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", "cannot find playlist with id="+id, "")
            );
        }
    }

    @PutMapping("/addsongtoplaylist")
    public ResponseEntity<ResponseObject> followUser(@RequestParam("playlist") Long playlistId,
                                                     @RequestParam("song") Long songId) {
        // Lấy thông tin Playlist hiện tại từ cơ sở dữ liệu
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);

        // Lấy thông tin song từ cơ sở dữ liệu
        Song song = songRepository.findById(songId).orElse(null);

        // Kiểm tra và thực hiện việc thêm song vào playlist
        if (playlist != null && song != null) {
            Set<Song> songs = playlist.getSongs();
            songs.add(song);
            playlist.setSongs(songs);
            // Lưu các thay đổi vào cơ sở dữ liệu
            playlistRepository.save(playlist);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "add song to playlist successfully", playlist));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("false", "cannot found song or playlist by id", playlist));
        }
    }

    @DeleteMapping("/deletesongfromplaylist")
    public ResponseEntity<ResponseObject> removeSongFromPlaylist(@RequestParam("playlist") Long playlistId,
                                                                 @RequestParam("song") Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        Song song = songRepository.findById(songId).orElse(null);

        if (playlist == null || song == null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ERROR", "Playlist or song not found", null)
            );
        }

        playlist.getSongs().remove(song);
        playlistRepository.save(playlist);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Song removed from playlist successfully", playlist)
        );
    }
    @DeleteMapping("/delete")
    ResponseEntity<ResponseObject> deleteUser(@RequestParam (name = "id") Long id) {
        boolean exists = playlistRepository.existsById(id);
        if(exists){
            playlistRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "delete playlist successfully","")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed", "cannot find playlist to delete","")
        );
    }
}

