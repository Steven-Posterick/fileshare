package dev.stevenposterick.fileshare.api.controller;

import dev.stevenposterick.fileshare.api.service.SharedFileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileShareController {

    private final SharedFileService fileService;

    public FileShareController(SharedFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        try {
            var fileId = fileService.uploadFile(file);
            return ResponseEntity.ok(fileId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") String fileId){

        Resource resource;
        String fileName;
        try {
            var sharedFile = fileService.getSharedFile(fileId);
            if (sharedFile.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

            fileName = sharedFile.get().getFileName();
            resource = fileService.downloadFile(fileId);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
