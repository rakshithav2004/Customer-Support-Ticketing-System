package com.csts.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "opaque_tokens")
public class OpaqueToken {
    @Id
    private String token;
    private String userId;
    private String role;
    private LocalDateTime expiresAt;

}