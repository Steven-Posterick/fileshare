package dev.stevenposterick.fileshare.api.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("file_read_expiration")
@Getter
@Setter
public class FileReadExpiration {
    @Id
    private ObjectId id;
    private ObjectId fileId;
    private int readLeft;
}
