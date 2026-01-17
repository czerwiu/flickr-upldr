package pl.czerwiu.flickr.upldr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for successful photo upload.
 * Contains all information about the uploaded photo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    /**
     * Flickr photo ID.
     */
    private String photoId;

    /**
     * Album name where photo was uploaded.
     */
    private String album;

    /**
     * Flickr album ID (photoset ID).
     */
    private String albumId;

    /**
     * Upload timestamp (ISO 8601 format).
     */
    private Instant uploadedAt;

    /**
     * Upload status (always "SUCCESS" for this response).
     */
    private String status;

    /**
     * Direct URL to photo on Flickr.
     */
    private String url;
}
