package dev.stevenposterick.fileshare.api.repository;

import dev.stevenposterick.fileshare.api.model.FileReadExpiration;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FileReadExpirationRepository extends MongoRepository<FileReadExpiration, ObjectId> {

    @Query("{'fileId': {$eq: ?0}}")
    List<FileReadExpiration> findByFileId(ObjectId fileId);
}
