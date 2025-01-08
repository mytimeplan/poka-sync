package com.mytimeplan.pokasync.dto.poka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class PokaUserSkillResponseDto extends PokaResultDto<PokaUserSkillResponseDto.UserSkill> {

    @JsonProperty("next")
    private String nextUrl;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserSkill {
        private Endorsement endorsement;
        @JsonProperty("skill_id")
        private Long skillId;
        @JsonProperty("user_id")
        private Long userId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Endorsement {
        @JsonProperty("expires_at")
        private Date expiresAt;
        @JsonProperty("endorsement_date")
        private Date endorsementDate;
        @JsonProperty("endorsement_level")
        private Level level;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Level {
            private int value;
        }
    }
}