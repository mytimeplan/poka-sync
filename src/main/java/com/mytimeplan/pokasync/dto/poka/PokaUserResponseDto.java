package com.mytimeplan.pokasync.dto.poka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PokaUserResponseDto extends PokaResultDto<PokaUserResponseDto.User>{

    @Getter
    @Setter
    @NoArgsConstructor
    public static class User {
        private Long id;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("is_employee")
        private boolean isEmployee;
        @JsonProperty("email")
        private String email;
    }
}