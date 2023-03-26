package dev.stevenposterick.fileshare.api.repository;

import dev.stevenposterick.fileshare.api.model.SharedFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedFileRepository extends MongoRepository<SharedFile, ObjectId> {
}
