package dev.stevenposterick.fileshare.api.controller;

import dev.stevenposterick.fileshare.api.data.ExpirationDate;
import dev.stevenposterick.fileshare.api.dto.FileDetailsResponse;
import dev.stevenposterick.fileshare.api.service.FileShareService;
import dev.stevenposterick.fileshare.api.service.NumberService;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/files")
public class FileShareController {

    private final FileShareService fileService;
    private final NumberService numberService;

    public FileShareController(
            FileShareService fileService,
            NumberService numberService
    ) {
        this.fileService = fileService;
        this.numberService = numberService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "expiration", defaultValue = "WEEK") String expiration,
            @RequestParam(name = "burnAfter", defaultValue = "") String burnAfter
    ){
        try {
            var expirationType = ExpirationDate.valueOf(expiration);
            var burnAfterInteger = numberService.tryParse(burnAfter);

            var fileId = fileService.uploadFile(file, expirationType, burnAfterInteger);
            return ResponseEntity.ok(fileId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping("/info/{fileId}")
    public ResponseEntity<FileDetailsResponse> getFileDetails(@PathVariable("fileId") String fileId) {
        var fileObjectId = new ObjectId(fileId);
        var fileDetailsOptional = fileService.getFileDetails(fileObjectId);

        return fileDetailsOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") String fileId){

        Resource resource;
        String fileName;
        try {
            var sharedFile = fileService.getSharedFile(new ObjectId(fileId));
            if (sharedFile.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

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
