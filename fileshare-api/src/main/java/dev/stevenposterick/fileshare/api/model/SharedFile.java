package dev.stevenposterick.fileshare.api.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "shared_files")
@Getter
@Setter
public class SharedFile {

    public SharedFile() {
        expiration = new Date();
    }

    @Id
    private ObjectId id;

    private String fileName;


    @Indexed(expireAfterSeconds = 60) // 60 * 60 * 24 * 7 -> 7 days till it should be deleted
    private Date expiration;
}