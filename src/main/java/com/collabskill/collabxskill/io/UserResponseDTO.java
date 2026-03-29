
package com.collabskill.collabxskill.io;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String id;
    private String email;
    @JsonProperty("isAdmin")
    private boolean isAdmin;
    @JsonProperty("isVerified")
    private boolean isVerified;
    @JsonProperty("isBanned")
    private boolean isBanned;
}