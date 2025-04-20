package org.library.thelibraryj.infrastructure.imageHandling;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.library.thelibraryj.book.domain.BookImageHandler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${library.servlet.auth_free_mapping}${library.image.mapping}")
@Tag(name = "Image - Public", description = "Endpoints to request image resources from server.")
class ImageHandlerController {
    private final BookImageHandler bookImageHandler;


    @Operation(
            summary = "Retrieve an image resource from server. Returns the asked for image as multipart-file",
            tags = {"book", "no auth required"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the image"),
            @ApiResponse(responseCode = "400", description = "Invalid image path provided"),
            @ApiResponse(responseCode = "404", description = "Requested image not found")
    })
    @GetMapping(value = "/{imagePath}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageResource(String imagePath){

    }

}
