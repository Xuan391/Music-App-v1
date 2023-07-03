package com.app.music_application.services;

import com.app.music_application.models.UploadProgress;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class SongStorageService implements IStorageService {

    private final Path storageFolder = Paths.get("uploads/songs");

    private SseEmitter uploadProgressEmitter;


    public SseEmitter getUploadProgressEmitter() {
        return uploadProgressEmitter;
    }


    public SongStorageService() {
        try {
            Files.createDirectories(storageFolder);
        }
        catch (IOException exception) {
            throw new RuntimeException("Cannot initialize storage", exception);
        }
    }
    private boolean isAudioFile(MultipartFile file) {
        //let install FileNameUtils
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()); // dùng hàm getExtension để lấy đuôi file
        return Arrays.asList(new String[] {"mp3","midi","wav","ogg","flac"})
                .contains(fileExtension.trim().toLowerCase());
    }
    @Override
    public String storeFile(MultipartFile file) {
        try{
            System.out.println("haha");
            if (file.isEmpty()){
                throw new RuntimeException("Failed to store empty file.");
            }
            if (!isAudioFile(file)) {
                throw new RuntimeException("You can only upload image file.");
            }
            // store file
            // rename filename before store
            String generatedFilename1 = file.getOriginalFilename();
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String generatedFileName2 = UUID.randomUUID().toString().replace("-", "");
            generatedFileName2 = generatedFilename1 + generatedFileName2+"."+ fileExtension;
            Path destinationFilePath = this.storageFolder.resolve(Paths.get(generatedFileName2))
                    .normalize().toAbsolutePath();
            if (!destinationFilePath.getParent().equals(this.storageFolder.toAbsolutePath())){
                //this is a security check
                throw new RuntimeException(
                        "cannot store file outside current directory");
            }
            try(InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
            }

            uploadProgressEmitter = new SseEmitter();
            long fileSize = file.getSize();
            long uploadedBytes = 0;

            try (InputStream inputStream = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    // Lưu trữ file
                    uploadedBytes += bytesRead;
                    double progress = (double) uploadedBytes / fileSize * 100;
                    UploadProgress uploadProgress = new UploadProgress(uploadedBytes,fileSize,progress);
                    uploadProgressEmitter.send(uploadProgress);
                }
            }catch (IOException e) {
                throw new RuntimeException("Failed to send upload progress.", e);
            }

            return generatedFileName2;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to store file.", exception);
        }
    }



    @Override
    public Stream<Path> loadAll() {
        try {
            // sử dụng hàm walk duyệt bên trong folder với maxDepth = 1 (duyệt con gần nhất)
            return Files.walk(this.storageFolder, 1)
                    .filter(path -> /*!path.equals(this.storageFoler)*/ !path.equals(this.storageFolder) && !path.toString().contains("._"))
                    .map(this.storageFolder::relativize);
        } // list danh sách các ảnh
        catch (IOException exception) {
            throw new RuntimeException("Failed to load stored files", exception);
        }
    }

    @Override
    public byte[] readFileContent(String fileName) {
        try {
            Path file = storageFolder.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                return bytes;
            }
            else {
                throw new RuntimeException("could not read file: " + fileName);
            }
        }
        catch (IOException exception) {
            throw new RuntimeException("could not read file: "+fileName, exception);
        }
    }

    @Override
    public void deleteAllFiles() {

    }
}
