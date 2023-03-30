package dev.stevenposterick.fileshare.api.scheduler;


import dev.stevenposterick.fileshare.api.model.FileDateExpiration;
import dev.stevenposterick.fileshare.api.service.FileShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileCleanupScheduler {

    private final FileShareService fileShareService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public FileCleanupScheduler(FileShareService fileShareService) {
        this.fileShareService = fileShareService;
    }

    @Scheduled(cron = "0 0 * * * *") // Run the task every hour
    private void cleanUpExpiredFiles(){
        for (FileDateExpiration expiredFile : fileShareService.getExpiredFiles()) {
            try {
                fileShareService.deleteFile(expiredFile.getFileId());
            } catch (Exception ex){
                logger.error("Clean Up File Exception", ex);
            }
        }
    }
}
