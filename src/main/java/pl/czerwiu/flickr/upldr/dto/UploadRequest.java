package pl.czerwiu.flickr.upldr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for photo upload.
 * Contains all parameters for uploading a photo to Flickr.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {

    /**
     * Album name (required).
     * If album doesn't exist, it will be created.
     */
    private String album;

    /**
     * Photo title (optional).
     */
    private String title;

    /**
     * Photo description (optional).
     */
    private String description;

    /**
     * Comma-separated tags (optional).
     * Example: "beach,sunset,california"
     */
    private String tags;

    /**
     * Duplicate photo check mode (optional).
     * 1 = check all user's photos, 2 = check recent uploads only, null = no check.
     */
    private Integer dedupCheck;
}
