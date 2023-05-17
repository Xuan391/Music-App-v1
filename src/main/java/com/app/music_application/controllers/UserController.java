package com.app.music_application.controllers;

import com.app.music_application.models.ResponseObject;
import com.app.music_application.models.Song;
import com.app.music_application.models.User;
import com.app.music_application.repositories.UserRepository;
import com.app.music_application.services.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/Users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageStorageService imageStorageService;


    @GetMapping("/ShowAll")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Query product successfully",foundUser)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Cannot find product with id = "+id, "")
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

        }catch (Exception exception){
            return ResponseEntity.noContent().build(); // ko tìm thấy image trả về nocontent
        }
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertUser(@RequestParam("name") String name,
                                              @RequestParam("image") MultipartFile imagefile,
                                              @RequestParam("username") String username,
                                              @RequestParam("password") String password) {
        String imageFileName = imageStorageService.storeFile(imagefile);
        String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                "readDetailImageFile", imageFileName).build().toUri().toString();

        User user = new User();
        user.setName(name);
        user.setAvatarUrl(urlImage);
        user.setUserName(username);
        user.setPassword(password);
        user.setFollowers(new HashSet<>());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK","Insert user successfully", userRepository.save(user))
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject>  updateUser(@RequestParam("image") MultipartFile fileImage,
                                                      @RequestBody User newUser, @PathVariable Long id) {
        User updateUser = userRepository.findById(id)
                .map(user -> {
                    if (newUser.getAvatarUrl() != null || fileImage != null) {
                        // Xử lý và lưu trữ file ảnh mới (nếu có)
                        if (fileImage != null) {
                            String imageFileName = imageStorageService.storeFile(fileImage);
                            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                                    "readDetailImageFile", imageFileName).build().toUri().toString();
                            user.setAvatarUrl(urlImage);
                        } else {
                            // Nếu không có file ảnh mới, sử dụng avatarUrl từ newUser
                            user.setAvatarUrl(newUser.getAvatarUrl());
                        }
                    }
                    user.setName(newUser.getName());
                    user.setUserName(newUser.getUserName());
                    user.setPassword(newUser.getPassword());
                    return userRepository.save(user);
                }).orElseGet(() ->{
                    newUser.setId(id);
                    return userRepository.save(newUser);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK","Update user successfully", updateUser)
        );
    }
    @PutMapping("/{id}/follow")
    public ResponseEntity<ResponseObject> followUser(@PathVariable("id") Long userId,
                                                     @RequestParam("followerId") Long followerId) {
        // Lấy thông tin User hiện tại từ cơ sở dữ liệu
        User user = userRepository.findById(userId).orElse(null);

        // Lấy thông tin người theo dõi từ cơ sở dữ liệu
        User follower = userRepository.findById(followerId).orElse(null);

        // Kiểm tra và thực hiện việc thêm người theo dõi
        if (user != null && follower != null) {
            user.setFollowers((Set<User>) follower); // Thêm người dùng theo dõi vào danh sách followers
            user.setFollowersCount(user.getFollowersCount()); // Cập nhật giá trị followersCount

            // Lưu các thay đổi vào cơ sở dữ liệu
            userRepository.save(user);

            return ResponseEntity.ok().body(new ResponseObject("OK", "User followed successfully", user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteUser(@PathVariable Long id) {
        boolean exists = userRepository.existsById(id);
        if(exists){
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "delete user successfully","")
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed", "cannot find user to delete","")
        );
    }

}
