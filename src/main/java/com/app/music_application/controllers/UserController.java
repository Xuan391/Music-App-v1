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
import java.time.LocalDateTime;
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

    @GetMapping("/show/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Query product successfully",foundUser)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("false","Cannot find user with id = "+id, "")
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
        User user = new User();
        if (imagefile != null && !imagefile.isEmpty()) {
            // Xử lý và lưu trữ file ảnh mới (nếu có)
            String imageFileName = imageStorageService.storeFile(imagefile);
            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                    "readDetailImageFile", imageFileName).build().toUri().toString();
            user.setAvatarUrl(urlImage);
        } else {
            // Nếu không có file ảnh mới, đặt giá trị avatarUrl là null
            user.setAvatarUrl(null);
        }

        user.setName(name);
        user.setUserName(username);
        user.setPassword(password);
        user.setFollowers(new HashSet<>());
        user.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK","Insert user successfully", userRepository.save(user))
        );
    }
//    @PutMapping("/update/{id}") ////////////???????????????????????????
//    public ResponseEntity<ResponseObject>  updateUser(@RequestParam("image") MultipartFile fileImage,
//                                                      @RequestBody User newUser, @PathVariable Long id) {
//        User updateUser = userRepository.findById(id)
//                .map(user -> {
//                    if (newUser.getAvatarUrl() != null || fileImage != null) {
//                        // Xử lý và lưu trữ file ảnh mới (nếu có)
//                        if (fileImage != null) {
//                            String imageFileName = imageStorageService.storeFile(fileImage);
//                            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
//                                    "readDetailImageFile", imageFileName).build().toUri().toString();
//                            user.setAvatarUrl(urlImage);
//                        } else {
//                            // Nếu không có file ảnh mới, sử dụng avatarUrl từ newUser
//                            user.setAvatarUrl(newUser.getAvatarUrl());
//                        }
//                    }
//                    user.setName(newUser.getName());
//                    user.setUserName(newUser.getUserName());
//                    user.setPassword(newUser.getPassword());
//                    return userRepository.save(user);
//                }).orElseGet(() ->{
//                    newUser.setId(id);
//                    return userRepository.save(newUser);
//                });
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseObject("OK","Update user successfully", updateUser)
//        );
//    }
    @PutMapping("/update/{id}") // chỉnh sửa thông tin user , form newuser gửi xuống là một json
    public ResponseEntity<ResponseObject>  updateUser(@RequestBody User newUser, @PathVariable Long id) {
        User updateUser = userRepository.findById(id)
            .map(user -> {
                user.setName(newUser.getName());
                user.setUserName(newUser.getUserName());
                user.setPassword(newUser.getPassword());
                return userRepository.save(user);
            }).orElse(null);
        if (updateUser != null) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Update user successfully", updateUser)
            );
        }  else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("false", "cannot find user with id="+id, "")
            );
        }
    }

    @PutMapping("/changeAvatar/{id}")
    public ResponseEntity<ResponseObject> changeAvatarUser(@RequestParam ("image") MultipartFile imagefile,
                                                           @PathVariable Long id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            String imageFileName = imageStorageService.storeFile(imagefile);
            String urlImage = MvcUriComponentsBuilder.fromMethodName(SongController.class,
                    "readDetailImageFile", imageFileName).build().toUri().toString();
            user.setAvatarUrl(urlImage);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Change avatar successfully", userRepository.save(user))
            );
        }
        catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("false","Cannot find image file ", "")
            );
        }
    }

    @PutMapping("/{id}/follow")
    public ResponseEntity<ResponseObject> followUser(@PathVariable("id") Long userId,
                                                     @RequestParam("followerId") Long followerId) {
        // Lấy thông tin User hiện tại từ cơ sở dữ liệu
        User user = userRepository.findById(userId).orElse(null);

        // Lấy thông tin người theo dõi từ cơ sở dữ liệu
        User follower = userRepository.findById(followerId).orElse(null);

        if (user != null && follower != null) {
            Set<User> followers = user.getFollowers(); // Lấy danh sách followers hiện tại của user
            followers.add(follower); // Thêm người dùng theo dõi vào danh sách followers

            user.setFollowers(followers); // Cập nhật danh sách followers mới
            user.setFollowersCount(followers.size()); // Cập nhật giá trị followersCount

            // Lưu các thay đổi vào cơ sở dữ liệu
            userRepository.save(user);
            return ResponseEntity.ok().body(new ResponseObject("OK", "User followed successfully", user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{userId}/unfollow/{followerId}")
    public ResponseEntity<ResponseObject> unfollowUser(@PathVariable Long userId, @PathVariable Long followerId) {
        User user = userRepository.findById(userId).orElse(null);
        User follower = userRepository.findById(followerId).orElse(null);
        if (user == null || follower == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("ERROR", "User or follower not found", null)
            );
        }
        user.getFollowers().remove(follower);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Unfollow user successfully", null)
        );
    }
    @DeleteMapping("/delete/{id}")
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
