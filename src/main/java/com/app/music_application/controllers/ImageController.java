package com.app.music_application.controllers;

import com.app.music_application.models.ImageReponse;
import com.app.music_application.models.ResponseObject;
import com.app.music_application.services.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageReponse> uploadImage(@RequestParam("file") MultipartFile file){
        try {
        String generatedFileName = imageStorageService.storeFile(file);
        String urlImage = MvcUriComponentsBuilder.fromMethodName(ImageController.class,
                    "readDetailFile", generatedFileName).build().toUri().toString();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ImageReponse(""+urlImage, "multipart/form-data")
        );
    }catch (Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                new ImageReponse("","multipart/form-data")
        );
    }
    }

    @GetMapping("/files/{fileName:.+}")
    // /files/06a290064eb94a02a58bfeef36002483.png
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName) {
        try {
            byte[] bytes = imageStorageService.readFileContent(fileName);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        }catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }
    //How to load all uploaded files ?
    @GetMapping("")
    public ResponseEntity<ResponseObject> getUploadedFiles() {
        try {
            List<String> urls = imageStorageService.loadAll()
                    .map(path -> {
                        //convert fileName to url(send request "readDetailFile")
                        String urlPath = MvcUriComponentsBuilder.fromMethodName(ImageController.class,
                                "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                        return urlPath;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseObject("ok", "List files successfully", urls));
        }catch (Exception exception) {
            return ResponseEntity.ok(new
                    ResponseObject("failed", "List files failed", new String[] {}));
        }
    }

}
