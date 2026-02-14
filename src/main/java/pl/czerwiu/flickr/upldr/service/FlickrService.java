package pl.czerwiu.flickr.upldr.service;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.util.AuthStore;
import pl.czerwiu.flickr.upldr.exception.DuplicatePhotoException;
import pl.czerwiu.flickr.upldr.flickr.DedupUploadMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.czerwiu.flickr.upldr.config.FlickrConfig;
import pl.czerwiu.flickr.upldr.config.FlickrProperties;
import pl.czerwiu.flickr.upldr.dto.UploadRequest;
import pl.czerwiu.flickr.upldr.exception.FlickrUploadException;
import pl.czerwiu.flickr.upldr.exception.RetryExhaustedException;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * Service for Flickr API integration.
 * Wrapper around Flickr4Java client with retry logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlickrService {

    private final Flickr flickrClient;

    private final FlickrConfig flickrConfig;

    private final AuthStore authStore;

    /**
     * Uploads photo to Flickr with automatic retry on failures.
     * Retries 3 times with exponential backoff (1s, 2s, 4s).
     *
     * @param file    multipart file to upload
     * @param request upload request with metadata
     * @return Flickr photo ID
     * @throws FlickrUploadException if upload fails after retries
     */
    @Retryable(
        retryFor = {FlickrException.class, SocketTimeoutException.class},
        maxAttempts = 1,
        backoff = @Backoff(delay = 1000, multiplier = 2)
        // Delays: 1000ms (1s), 2000ms (2s), 4000ms (4s)
    )
    public String uploadPhoto(MultipartFile file, UploadRequest request) throws FlickrUploadException {
        log.debug("Uploading photo to Flickr: filename={}, size={}",
            file.getOriginalFilename(), file.getSize());

        RequestContext.getRequestContext().setAuth(flickrClient.getAuth());

//        if (this.authStore != null) {
//            Auth auth = this.authStore.retrieve(flickrProperties.getUser().getNsid());
//            if (auth == null) {
//                this.authorize();
//            } else {
//                rc.setAuth(auth);
//            }
//        }


        try {
            InputStream inputStream = file.getInputStream();

            // Prepare upload metadata (with optional dedup check)
            UploadMetaData metadata;
            if (request.getDedupCheck() != null) {
                metadata = new DedupUploadMetaData(request.getDedupCheck());
            } else {
                metadata = new UploadMetaData();
            }
            metadata.setTitle(request.getTitle());
            metadata.setDescription(request.getDescription());

            // Parse tags (comma-separated)
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                String[] tagsArray = request.getTags().split(",");
                metadata.setTags(Arrays.asList(tagsArray));
            }

            // Upload to Flickr
            String photoId = flickrClient.getUploader().upload(inputStream, metadata);

            log.info("Photo uploaded successfully to Flickr: photoId={}, filename={}",
                photoId, file.getOriginalFilename());

            return photoId;

        } catch (FlickrException e) {
            if ("9".equals(e.getErrorCode())) {
                log.warn("Duplicate photo detected: {}", e.getMessage());
                throw new DuplicatePhotoException(
                    "Duplicate photo detected", e.getErrorMessage(), e);
            }
            log.warn("Flickr API error during upload: {}", e.getMessage());
            throw new RuntimeException(e);  // Will trigger retry
        } catch (IOException e) {
            log.error("IO error reading file: {}", e.getMessage());
            throw new FlickrUploadException("Failed to read file", e.getMessage(), e);
        }
    }

    /**
     * Recovery method called when all retry attempts are exhausted.
     * Logs error and throws RetryExhaustedException.
     *
     * @param e       the exception that caused retries to fail
     * @param file    the file that was being uploaded
     * @param request the upload request
     * @return never returns, always throws exception
     * @throws RetryExhaustedException always
     */
    @Recover
    public String recoverFromUploadFailure(
            Exception e,
            MultipartFile file,
            UploadRequest request) {

        log.error("Upload failed after 3 retry attempts: filename={}, title={}, error={}",
            file.getOriginalFilename(), request.getTitle(), e.getMessage(), e);

        throw new RetryExhaustedException(
            "Failed to upload photo " + request.getTitle() + " after 3 retries: " + e.getMessage(), e);
    }

    /**
     * Builds Flickr photo URL from photo ID.
     *
     * @param photoId Flickr photo ID
     * @param userNsid Flickr user NSID
     * @return direct URL to photo on Flickr
     */
    public String buildPhotoUrl(String photoId, String userNsid) {
        // Extract username from NSID (part before @)
        String username = userNsid.split("@")[0];
        return String.format("https://www.flickr.com/photos/%s/%s/", username, photoId);
    }
}
