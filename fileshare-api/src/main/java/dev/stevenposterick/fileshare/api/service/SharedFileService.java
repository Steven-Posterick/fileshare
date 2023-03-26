package dev.stevenposterick.fileshare.api.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import dev.stevenposterick.fileshare.api.model.SharedFile;
import dev.stevenposterick.fileshare.api.repository.SharedFileRepository;
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
import java.util.Objects;
import java.util.Optional;

@Service
public class SharedFileService {
    private final SharedFileRepository sharedFileRepository;
    private final GridFsOperations gridFsOperations;
    private final MongoDatabaseFactory mongoDatabaseFactory;

    public SharedFileService(SharedFileRepository sharedFileRepository, GridFsOperations gridFsOperations, MongoDatabaseFactory mongoDatabaseFactory) {
        this.sharedFileRepository = sharedFileRepository;
        this.gridFsOperations = gridFsOperations;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            GridFSUploadOptions options = new GridFSUploadOptions().metadata(null);

            ObjectId fileId = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase())
                    .uploadFromStream(Objects.requireNonNull(file.getOriginalFilename()), inputStream, options);

            SharedFile sharedFile = new SharedFile();
            sharedFile.setId(fileId);
            sharedFile.setFileName(file.getOriginalFilename());

            sharedFileRepository.save(sharedFile);

            return fileId.toString();
        }
    }

    public Optional<SharedFile> getSharedFile(String fileId) {
        return sharedFileRepository.findById(new ObjectId(fileId));
    }

    public Resource downloadFile(String fileId) throws IOException {
        ObjectId objectId = new ObjectId(fileId);
        Optional<SharedFile> sharedFileOptional = sharedFileRepository.findById(objectId);

        if (sharedFileOptional.isEmpty())
            throw new IOException("File not found");

        GridFSFile gridFSFile = gridFsOperations.findOne(BasicQuery.query(Criteria.where("_id").is(objectId)));
        if (gridFSFile == null)
            throw new IOException("File not found");

        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
        try (var stream = gridFSBucket.openDownloadStream(objectId)){
            return new ByteArrayResource(stream.readAllBytes());
        }
    }

}
