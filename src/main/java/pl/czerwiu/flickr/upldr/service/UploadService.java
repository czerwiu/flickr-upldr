package pl.czerwiu.flickr.upldr.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.czerwiu.flickr.upldr.config.FlickrProperties;
import pl.czerwiu.flickr.upldr.dto.UploadRequest;
import pl.czerwiu.flickr.upldr.dto.UploadResponse;

import java.time.Instant;

/**
 * Main service for orchestrating photo upload workflow.
 * Coordinates FlickrService and AlbumService, tracks metrics and logs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final FlickrService flickrService;
    private final AlbumService albumService;
    private final FlickrProperties flickrProperties;
    private final MeterRegistry meterRegistry;

    /**
     * Uploads photo to Flickr with album management.
     * Complete workflow:
     * 1. Upload photo to Flickr
     * 2. Ensure album exists (create if needed)
     * 3. Add photo to album
     * 4. Track metrics and log results
     *
     * @param file    multipart file to upload
     * @param request upload request with metadata and album name
     * @return upload response with photo details
     */
    public UploadResponse upload(MultipartFile file, UploadRequest request) {
        // Start timer for metrics
        Timer.Sample sample = Timer.start(meterRegistry);

        log.info("Upload request received: filename={}, size={}, album={}, user={}",
            file.getOriginalFilename(),
            file.getSize(),
            request.getAlbum(),
            flickrProperties.getUser().getName());

        try {
            // Step 1: Upload photo to Flickr
            log.debug("Step 1: Uploading photo to Flickr");
            String photoId = flickrService.uploadPhoto(file, request);

            // Step 2: Ensure album exists (use photoId as primary if creating new)
            log.debug("Step 2: Ensuring album exists: {}", request.getAlbum());
            String albumId = albumService.ensureAlbum(request.getAlbum(), photoId);

            // Step 3: Add photo to album
            log.debug("Step 3: Adding photo to album");
            albumService.addPhotoToAlbum(photoId, albumId);

            // Build photo URL
            String photoUrl = flickrService.buildPhotoUrl(
                photoId,
                flickrProperties.getUser().getNsid()
            );

            // Record success metrics
            meterRegistry.counter("upload.success").increment();
            meterRegistry.summary("upload.file.size").record(file.getSize());

            // Build response
            UploadResponse response = UploadResponse.builder()
                .photoId(photoId)
                .album(request.getAlbum())
                .albumId(albumId)
                .uploadedAt(Instant.now())
                .status("SUCCESS")
                .url(photoUrl)
                .build();

            log.info("Photo uploaded successfully: photoId={}, album={}, albumId={}, duration={}ms, size={}, user={}",
                photoId,
                request.getAlbum(),
                albumId,
                sample.stop(meterRegistry.timer("upload.duration")),
                file.getSize(),
                flickrProperties.getUser().getName());

            return response;

        } catch (Exception e) {
            // Record failure metric
            meterRegistry.counter("upload.failure").increment();

            log.error("Upload failed: filename={}, album={}, error={}",
                file.getOriginalFilename(),
                request.getAlbum(),
                e.getMessage(),
                e);

            // Re-throw exception to be handled by GlobalExceptionHandler
            throw e;

        } finally {
            // Record total uploads and duration
            meterRegistry.counter("upload.total").increment();
        }
    }
}
