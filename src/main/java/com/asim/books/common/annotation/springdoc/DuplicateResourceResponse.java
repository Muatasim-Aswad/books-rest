package com.asim.books.common.annotation.springdoc;

import com.asim.books.common.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

/**
 * Common response for duplicate resource (409).
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
        @ApiResponse(responseCode = "409", description = "Resource already exists",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
})
public @interface DuplicateResourceResponse {
}