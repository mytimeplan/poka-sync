package com.mytimeplan.pokasync.dto.mtp;

import com.mytimeplan.pokasync.dto.poka.PokaSkillResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class MtpSkillDto {
    private String name;
    private Long externalId;
    private Long categoryId;
    private Set<Integer> unitPositions;


    public MtpSkillDto(PokaSkillResponseDto.Skill pokaSkill) {
        this.name = pokaSkill.getName();
        this.externalId = pokaSkill.getId();
        this.categoryId = pokaSkill.getCategory().getId();
        this.unitPositions = pokaSkill.getCategory().getCategoryIds();
    }
}