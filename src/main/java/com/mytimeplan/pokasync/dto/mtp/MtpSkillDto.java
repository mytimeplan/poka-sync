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
    private Long categoryId;
    private String unitPosition;


    public MtpSkillDto(PokaSkillResponseDto.Skill pokaSkill) {
        this.name = pokaSkill.getName();
        this.externalId = pokaSkill.getId();
        this.categoryId = pokaSkill.getCategory().getId();
        this.unitPosition = pokaSkill.getCategory().getUnitPosition();
    }
}