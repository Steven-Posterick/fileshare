package dev.stevenposterick.fileshare.api.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "file_shared")
@Getter
@Setter
public class FileShared {
    @Id
    private ObjectId id;

    private String fileName;
}