package org.library.thelibraryj.infrastructure.imageHandling;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${library.servlet.auth_free_mapping}${library.image.mapping}")
@Tag(name = "Image - Public", description = "Endpoints to request image resources from server.")
class ImageHandlerController {

    @Operation(
            summary = "Retrieve an image resource from server. Returns the asked for image as a byte[] array. " +
                    "If `fail_on_not_found is true, will fail if a non-existent image handler is specified or the request image is not found. " +
                    "Otherwise will never fail unless non-existent image handler is specified - returns a handler's default image is the asked for is not found.",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the image"),
            @ApiResponse(responseCode = "400", description = "Invalid image handler provided"),
            @ApiResponse(responseCode = "404", description = "Requested image not found"),
    })
    @GetMapping(value = "/{handler}/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageResourceOrFail(@PathVariable("handler") String requested_handler,
                                                         @PathVariable("imageId") String imageId,
                                                         @RequestParam(value = "fail_on_not_found", required = false, defaultValue = "false") boolean failOnNotFound) {
        if (!failOnNotFound) {
            return BaseImageHandler.getHandler(requested_handler)
                    .map(handler -> ResponseEntity.ok(handler.fetchImageOrDefaultAsBytes(imageId)))
                    .orElseGet(() -> ResponseEntity.badRequest().build());
        } else {
            return BaseImageHandler.getHandler(requested_handler)
                    .map(handler -> handler.fetchImageAsBytes(imageId)
                            .map(ResponseEntity::ok)
                            .orElseGet(ResponseEntity.notFound()::build)
                    )
                    .orElseGet(() -> ResponseEntity.badRequest().build());
        }
    }

}
