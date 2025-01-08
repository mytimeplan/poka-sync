package com.mytimeplan.pokasync.dto.mtp;

import com.mytimeplan.pokasync.dto.poka.PokaUserSkillResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MtpUserSkillsDto {
    private MtpUserDto user;
    private List<Skill> skills;


    public MtpUserSkillsDto(Long userId, String userEmail, List<PokaUserSkillResponseDto.UserSkill> userSkills) {
        this.user = new MtpUserDto(userId, userEmail);
        if (!CollectionUtils.isEmpty(userSkills))
            this.skills = userSkills.stream()
                    .map(us -> new Skill(us.getSkillId(), us.getEndorsement().getEndorsementDate(), us.getEndorsement().getExpiresAt()))
                    .toList();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Skill {
        private Long id;
        private Date activeDate;
        private Date expirationDate;
    }
}