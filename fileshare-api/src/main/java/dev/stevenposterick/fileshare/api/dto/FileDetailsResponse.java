package dev.stevenposterick.fileshare.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileDetailsResponse {
    private String fileName;
    private LocalDateTime expirationDate;
    private Integer remainingReads;
}
