package pl.czerwiu.flickr.upldr.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.czerwiu.flickr.upldr.dto.ErrorResponse;
import pl.czerwiu.flickr.upldr.dto.UploadRequest;
import pl.czerwiu.flickr.upldr.dto.UploadResponse;
import pl.czerwiu.flickr.upldr.service.UploadService;

/**
 * REST controller for photo upload operations.
 * Provides endpoint for uploading photos to Flickr with album management.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Photo upload operations")
public class UploadController {

    private final UploadService uploadService;

    /**
     * Upload photo to Flickr with metadata and album assignment.
     * If album doesn't exist, it will be created automatically.
     *
     * @param file        image file to upload (required, max 200MB)
     * @param album       album name (required, max 255 chars)
     * @param title       photo title (optional, max 255 chars)
     * @param description photo description (optional, max 2000 chars)
     * @param tags        comma-separated tags (optional, max 500 chars)
     * @return upload response with photo details
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload photo to Flickr",
        description = "Uploads a photo to Flickr and adds it to specified album. " +
                     "Creates album if it doesn't exist. " +
                     "Supports automatic retry on transient failures.",
        security = @SecurityRequirement(name = "basicAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Photo uploaded successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UploadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - missing required parameters or invalid format",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict - duplicate photo detected (only when dedupCheck is enabled)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error - upload failed after retries",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    public ResponseEntity<UploadResponse> uploadPhoto(
            @Parameter(
                description = "Image file to upload (JPG, PNG, GIF, WEBP, etc.)",
                required = true
            )
            @RequestParam("file") MultipartFile file,

            @Parameter(
                description = "Album name (created if doesn't exist)",
                required = true,
                example = "Summer Vacation 2024"
            )
            @RequestParam("album") String album,

            @Parameter(
                description = "Photo title",
                example = "Beach Sunset"
            )
            @RequestParam(value = "title", required = false) String title,

            @Parameter(
                description = "Photo description",
                example = "Beautiful sunset at Malibu Beach, California"
            )
            @RequestParam(value = "description", required = false) String description,

            @Parameter(
                description = "Comma-separated tags",
                example = "beach,sunset,california,malibu,2024"
            )
            @RequestParam(value = "tags", required = false) String tags,

            @Parameter(
                description = "Duplicate check mode: 1 = check all photos, 2 = check recent uploads only",
                example = "1"
            )
            @RequestParam(value = "dedupCheck", required = false) Integer dedupCheck
    ) {
        log.debug("Upload endpoint called: filename={}, album={}",
            file.getOriginalFilename(), album);

        // Validate required parameters
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (album == null || album.trim().isEmpty()) {
            throw new IllegalArgumentException("Album name is required");
        }

        // Build upload request
        UploadRequest request = UploadRequest.builder()
            .album(album.trim())
            .title(title != null ? title.trim() : null)
            .description(description != null ? description.trim() : null)
            .tags(tags != null ? tags.trim() : null)
            .dedupCheck(dedupCheck)
            .build();

        // Delegate to service
        UploadResponse response = uploadService.upload(file, request);

        return ResponseEntity.ok(response);
    }
}
