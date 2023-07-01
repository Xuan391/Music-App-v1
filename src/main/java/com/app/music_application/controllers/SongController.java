package com.app.music_application.controllers;

import com.app.music_application.models.*;
import com.app.music_application.repositories.CategoryRepository;
import com.app.music_application.repositories.ListenedHistoryRepository;
import com.app.music_application.repositories.SongRepository;
import com.app.music_application.repositories.UserRepository;
import com.app.music_application.services.ImageStorageService;
import com.app.music_application.services.SongStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Songs")
public class SongController {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private SongStorageService songStorageService;
    @Autowired
    private ListenedHistoryRepository listenedHistoryRepository;


    @GetMapping("/ShowAll")
    List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @GetMapping("/GetAllSong")
    List<ShowSongDTO> getAllSongDTO() {
        List<ShowSongDTO> songDTOs = new ArrayList<>();
        List<Song> songs = songRepository.findAll();
        for (Song song : songs) {
            ShowSongDTO dto = new ShowSongDTO();

            dto.setSongId(song.getId());
            dto.setNameSong(song.getName());
            dto.setUrl(song.getUrl());
            dto.setThumbnail(song.getThumbnailUrl());

            User user = song.getCreator();
            dto.setUserId(user.getId());
            dto.setNameUser(user.getUserName());
            songDTOs.add(dto);
        }
        return songDTOs;
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Song> foundSong = songRepository.findById(id);
        return foundSong.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Find song successfully", foundSong)
                ) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("False", "Cannot find song with id =" + id, foundSong)
                );
    }

    @GetMapping("/top10MostListenedSongs")
    public ResponseEntity<ResponseObject> getTop10MostListenedSongs() {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<Song> top10Songs = songRepository.findTop10SongsByListenedCountAndDateRange(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Get ListenedCount Top 10 Songs For 1 Month", top10Songs)
        );
    }

    @GetMapping("/imageFiles/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailImageFile(@PathVariable String fileName) {
        try {
            byte[] bytes = imageStorageService.readFileContent(fileName);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);

        } catch (Exception exception) {
            return ResponseEntity.noContent().build(); // ko tìm thấy image trả về nocontent
        }
    }

    @GetMapping("/musicFiles/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailSongFile(@PathVariable String fileName) {
        // Xử lý logic để đọc file chi tiết dựa trên fileName
        // ...
        try {
            byte[] bytes = songStorageService.readFileContent(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(bytes);
        } catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/insertbyuser")
    ResponseEntity<ResponseObject> insertSongByUser(@RequestParam("name") String name,
                                                    @RequestParam("image") MultipartFile imagefile,
                                                    @RequestParam("song") MultipartFile songfile,
                                                    @RequestParam("creator") Long userId) {
        Song song = new Song();
        // lưu trữ file
        String songFileName = songStorageService.storeFile(songfile);
        //lấy đường dẫn Url
        String songUrl = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                "readDetailSongFile", songFileName).build().toUri().toString();

        if (imagefile != null && !imagefile.isEmpty()) {
            // Xử lý và lưu trữ file ảnh mới (nếu có)
            String imageFileName = imageStorageService.storeFile(imagefile);
            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                    "readDetailImageFile", imageFileName).build().toUri().toString();
            song.setThumbnailUrl(urlImage);
        } else {
            // Nếu không có file ảnh mới, đặt giá trị avatarUrl là null
            song.setThumbnailUrl(null);
        }

        User creator = userRepository.findById(userId).orElse(null);

        song.setName(name);
        song.setUrl(songUrl);
        song.setCreator(creator);
        song.setDownloadCount(0);
        song.setListenedCount(0);
        song.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Insert song successfully", songRepository.save(song))
        );
    }

    @PostMapping("/insertbyadmin")
    ResponseEntity<ResponseObject> insertSongByAdmin(@RequestParam("name") String name,
                                                     @RequestParam("image") MultipartFile imagefile,
                                                     @RequestParam("song") MultipartFile songfile,
                                                     @RequestParam("category") Long categoryId,
                                                     @RequestParam("creator") Long userId) {
        // Tạo SSE emitter để gửi thông tin tiến trình tải lên cho client
        SseEmitter uploadProgressEmitter = songStorageService.getUploadProgressEmitter();
        Song song = new Song();
        // lưu trữ file
        String songFileName = songStorageService.storeFile(songfile);
        // Khi hoàn thành việc tải lên, đảm bảo đóng SSE emitter
        uploadProgressEmitter.complete();
        //lấy đường dẫn Url
        String songUrl = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                "readDetailSongFile", songFileName).build().toUri().toString();

        if (imagefile != null && !imagefile.isEmpty()) {
            // Xử lý và lưu trữ file ảnh mới (nếu có)
            String imageFileName = imageStorageService.storeFile(imagefile);
            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                    "readDetailImageFile", imageFileName).build().toUri().toString();
            song.setThumbnailUrl(urlImage);
        } else {
            // Nếu không có file ảnh mới, đặt giá trị avatarUrl là null
            song.setThumbnailUrl(null);
        }

//        Optional<User> optionalUser = userRepository.findById(userId);
//        if (optionalUser.isEmpty()) {
//            // Xử lý khi không tìm thấy user
//            return ResponseEntity.notFound().build();
//        }
//        User creator = optionalUser.get();
//
//        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
//        if (optionalCategory.isEmpty()) {
//            // Xử lý khi không tìm thấy category
//            return ResponseEntity.notFound().build();
//        }
//        Category category = optionalCategory.get();
        User creator = userRepository.findById(userId).orElse(null);
        Category category = categoryRepository.findById(categoryId).orElse(null);

        song.setName(name);
        song.setUrl(songUrl);
        song.setCategory(category);
        song.setCreator(creator);
        song.setDownloadCount(0);
        song.setListenedCount(0);
        song.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Insert song successfully", songRepository.save(song))
        );
    }

    @GetMapping("/progress")
    public SseEmitter uploadProgress() {
        // Trả về SSE emitter để client có thể theo dõi tiến trình tải lên
        return songStorageService.getUploadProgressEmitter();
    }

    // update, upsert = update if found, otherwise insert
    @PutMapping("/update/{id}") // up date tên bài hát, thể loại của bài hát
    public ResponseEntity<ResponseObject> updateSong(@RequestBody Song newSong, @PathVariable Long id) {
        Song updateSong = songRepository.findById(id)
                .map(song -> {
                    song.setName(newSong.getName());
                    song.setCategory(newSong.getCategory());
                    return songRepository.save(song);
                }).orElse(null);

        if (updateSong != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Update song successfully", updateSong)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("false", "Cannot find song with id=" + id, "")
            );
        }
    }

    @PutMapping("/update") // up date tên bài hát, thể loại của bài hát
    public ResponseEntity<ResponseObject> updateSong(@RequestParam("name") String name,
                                                     @RequestParam("categoryId") Long categoryId,
                                                     @RequestParam("songId") Long songId) {
        Category updateCategory = categoryRepository.findById(categoryId).orElse(null);
        Song updateSong = songRepository.findById(songId)
                .map(song -> {
                    song.setName(name);
                    song.setCategory(updateCategory);
                    return songRepository.save(song);
                }).orElse(null);

        if (updateSong != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Update song successfully", updateSong)
            );
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("false", "Cannot find song with id=" + songId, "")
            );
        }
    }

    @PutMapping("/changeImage")
    ResponseEntity<ResponseObject> updateImageSong(@RequestParam("image") MultipartFile file,
                                                   @RequestParam Long id) {
        try {
            Song song = songRepository.findById(id).orElse(null);
            String imageFileName = imageStorageService.storeFile(file);
            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                    "readDetailImageFile", imageFileName).build().toUri().toString();

            song.setThumbnailUrl(urlImage);
            songRepository.save(song);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "changed song's image successfully", song)
            );
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("false", "Cannot find image file ", "")
            );
        }
    }

    @PutMapping("/download")
    public ResponseEntity<ResponseObject> updateDownloadCount(@RequestParam Long id) {
        // Tìm bài hát theo songId trong cơ sở dữ liệu
        Optional<Song> optionalSong = songRepository.findById(id);
        if (optionalSong.isPresent()) {
            Song song = optionalSong.get();
            // Tăng giá trị downloadCount
            song.setDownloadCount(song.getDownloadCount() + 1);
            // Lưu đối tượng Song đã được cập nhật
            Song updatedSong = songRepository.save(song);
            return ResponseEntity.ok().body(new ResponseObject("ok", "Download count updated", updatedSong));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//    @PutMapping ("/{id}/listened")
//    public ResponseEntity<ResponseObject> updateListenedCount(@PathVariable Long id) {
//        // Tìm bài hát theo songId trong cơ sở dữ liệu
//        Optional<Song> optionalSong = songRepository.findById(id);
//        if (optionalSong.isPresent()) {
//            Song song = optionalSong.get();
//            // Tăng giá trị downloadCount
//            song.setListenedCount(song.getListenedCount() + 1);
//            // Lưu đối tượng Song đã được cập nhật
//            Song updatedSong = songRepository.save(song);
//            return ResponseEntity.ok().body(new ResponseObject("ok", "Download count updated", updatedSong));
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    //Delete a product -> DELETE method
    @DeleteMapping("/delete")
    ResponseEntity<ResponseObject> deleteSong(@RequestParam(name = "id") Long id) {
        boolean exists = songRepository.existsById(id);
        if (exists) {
//            Song song = songRepository.findById(id).orElse(null);
//            List<ListenedHistory> listenedHistories = listenedHistoryRepository.getListenedHistoriesBySongId(song);
//            for (ListenedHistory listenedHistory : listenedHistories) {
//                listenedHistoryRepository.deleteById(listenedHistory.getId());
//            }
            songRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "delete song successfully", "")
            );
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("failed", "cannot find song to delete", "")
            );
        }
    }

}
