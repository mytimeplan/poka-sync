package com.mytimeplan.pokasync.dto.mtp;

import com.mytimeplan.pokasync.dto.poka.PokaSkillResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class MtpSkillDto {
    private String name;
    private Long externalId;


    public MtpSkillDto(PokaSkillResponseDto.Skill pokaSkill) {
        this.name = pokaSkill.getName();
        this.externalId = pokaSkill.getId();
    }
}