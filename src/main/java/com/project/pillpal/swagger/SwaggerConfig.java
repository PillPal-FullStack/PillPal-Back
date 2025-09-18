package com.project.pillpal.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("PillPal API")
                                                .description("API for managing medications and reminders")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("PillPal Team")
                                                                .email("support@pillpal.com"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Development server")))
                                .components(new Components()

                                                .addSchemas("ErrorResponse", new Schema<>()
                                                                .type("object")
                                                                .addProperty("status", new Schema<>().type("integer"))
                                                                .addProperty("message", new Schema<>().type("string"))
                                                                .addProperty("timestamp",
                                                                                new Schema<>().type("string")
                                                                                                .format("date-time")))

                                                .addResponses("BadRequest",
                                                                apiResponse(400, "Invalid input or malformed request"))
                                                .addResponses("Forbidden", apiResponse(403,
                                                                "You do not have permission to access this resource"))
                                                .addResponses("NotFound",
                                                                apiResponse(404, "Requested resource not found"))
                                                .addResponses("InternalServerError",
                                                                apiResponse(500, "Internal server error"))
                                                .addResponses("NoContent", new ApiResponse()
                                                                .description("Successfully processed request with no content")));
        }

        private ApiResponse apiResponse(int status, String message) {
                return new ApiResponse()
                                .description(message)
                                .content(jsonError(status, message));
        }

        private Content jsonError(int status, String message) {
                return new Content().addMediaType("application/json",
                                new MediaType()
                                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                                .example(Map.of(
                                                                "status", status,
                                                                "message", message,
                                                                "timestamp", "2024-01-01T12:00:00Z")));
        }
}
