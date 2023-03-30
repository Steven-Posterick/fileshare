package dev.stevenposterick.fileshare.api.repository;

import dev.stevenposterick.fileshare.api.model.FileDateExpiration;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FileDateExpirationRepository extends MongoRepository<FileDateExpiration, ObjectId> {
    @Query("{'expiration': {$lt: ?0}}")
    List<FileDateExpiration> findByExpirationBefore(LocalDateTime dateTime);

    @Query("{'fileId':  {$eq: ?0}}")
    List<FileDateExpiration> findByFileId(ObjectId fileId);
}
