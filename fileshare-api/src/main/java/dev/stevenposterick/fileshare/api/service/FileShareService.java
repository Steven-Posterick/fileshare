package dev.stevenposterick.fileshare.api.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import dev.stevenposterick.fileshare.api.data.ExpirationDate;
import dev.stevenposterick.fileshare.api.model.FileDateExpiration;
import dev.stevenposterick.fileshare.api.model.FileReadExpiration;
import dev.stevenposterick.fileshare.api.model.FileShared;
import dev.stevenposterick.fileshare.api.repository.FileDateExpirationRepository;
import dev.stevenposterick.fileshare.api.repository.FileReadExpirationRepository;
import dev.stevenposterick.fileshare.api.repository.FileSharedRepository;
import org.bson.types.ObjectId;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileShareService {
    private final FileSharedRepository fileSharedRepository;
    private final FileDateExpirationRepository fileDateExpirationRepository;
    private final FileReadExpirationRepository fileReadExpirationRepository;
    private final GridFsOperations gridFsOperations;
    private final MongoDatabaseFactory mongoDatabaseFactory;

    public FileShareService(FileSharedRepository sharedFileRepository, FileDateExpirationRepository fileDateExpirationRepository, FileReadExpirationRepository fileReadExpirationRepository, GridFsOperations gridFsOperations, MongoDatabaseFactory mongoDatabaseFactory) {
        this.fileSharedRepository = sharedFileRepository;
        this.fileDateExpirationRepository = fileDateExpirationRepository;
        this.fileReadExpirationRepository = fileReadExpirationRepository;
        this.gridFsOperations = gridFsOperations;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    public String uploadFile(MultipartFile file,
                             ExpirationDate expiration,
                             Integer burnAfter
    ) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            GridFSUploadOptions options = new GridFSUploadOptions().metadata(null);

            ObjectId fileId = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase())
                    .uploadFromStream(Objects.requireNonNull(file.getOriginalFilename()), inputStream, options);

            FileShared sharedFile = new FileShared();
            sharedFile.setId(fileId);
            sharedFile.setFileName(file.getOriginalFilename());
            fileSharedRepository.save(sharedFile);

            var expirationDate = expiration.getExpirationFunction().apply(LocalDateTime.now());

            // Delete file after a duration.
            FileDateExpiration fileDateExpiration = new FileDateExpiration();
            fileDateExpiration.setFileId(fileId);
            fileDateExpiration.setExpiration(expirationDate);
            fileDateExpirationRepository.save(fileDateExpiration);

            if (burnAfter != null && burnAfter > 0){
                FileReadExpiration fileReadExpiration = new FileReadExpiration();
                fileReadExpiration.setFileId(fileId);
                fileReadExpiration.setReadLeft(burnAfter);
                fileReadExpirationRepository.save(fileReadExpiration);
            }

            return fileId.toString();
        }
    }

    public Optional<FileShared> getSharedFile(String fileId) {
        return fileSharedRepository.findById(new ObjectId(fileId));
    }

    public Resource downloadFile(String fileId) throws IOException {
        ObjectId objectId = new ObjectId(fileId);
        Optional<FileShared> sharedFileOptional = fileSharedRepository.findById(objectId);

        if (sharedFileOptional.isEmpty())
            throw new IOException("File not found");

        GridFSFile gridFSFile = gridFsOperations.findOne(BasicQuery.query(Criteria.where("_id").is(objectId)));
        if (gridFSFile == null)
            throw new IOException("File not found");

        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
        Resource resource;
        try (var stream = gridFSBucket.openDownloadStream(objectId)){
            resource = new ByteArrayResource(stream.readAllBytes());
        }

        // Delete if burn on read.
        var list = fileReadExpirationRepository.findByFileId(objectId).stream().toList();
        if (!list.isEmpty()){
            var expiration = list.stream().findFirst().get();

            expiration.setReadLeft(expiration.getReadLeft() - 1);

            if (expiration.getReadLeft() <= 0){
                deleteFile(objectId);
            } else {
                fileReadExpirationRepository.save(expiration);
            }
        }

        return resource;
    }

    public List<FileDateExpiration> getExpiredFiles(){
        return fileDateExpirationRepository.findByExpirationBefore(LocalDateTime.now());
    }

    public void deleteFile(ObjectId objectId){
        // Delete the bucket file.
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
        gridFSBucket.delete(objectId);

        // Delete file reference
        fileSharedRepository.deleteById(objectId);

        // Delete the expiration data.
        fileReadExpirationRepository.deleteAll(fileReadExpirationRepository.findByFileId(objectId));
        fileDateExpirationRepository.deleteAll(fileDateExpirationRepository.findByFileId(objectId));
    }
}
