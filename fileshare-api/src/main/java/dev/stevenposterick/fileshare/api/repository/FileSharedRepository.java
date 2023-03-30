package dev.stevenposterick.fileshare.api.repository;

import dev.stevenposterick.fileshare.api.model.FileShared;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSharedRepository extends MongoRepository<FileShared, ObjectId> {
}
