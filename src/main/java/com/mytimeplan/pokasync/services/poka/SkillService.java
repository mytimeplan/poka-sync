package com.mytimeplan.pokasync.services.poka;

import com.mytimeplan.pokasync.dto.mtp.MtpSkillDto;
import com.mytimeplan.pokasync.dto.poka.PokaSkillResponseDto;
import com.mytimeplan.pokasync.exceptions.DefaultException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class SkillService extends PokaService<PokaSkillResponseDto> {

    private static final int LIMIT_OBJECTS = 20;

    public SkillService(RestTemplate restTemplate) {
        super(restTemplate, PokaSkillResponseDto.class);
    }

    public List<MtpSkillDto> getSkills() throws DefaultException {
        PokaSkillResponseDto skillsResponse = sendRequest(getSkillsUrl(0));
        List<PokaSkillResponseDto.Skill> pokaSkills = new ArrayList<>(skillsResponse.getResult());

        for (int i = LIMIT_OBJECTS; i < skillsResponse.getCount(); i += LIMIT_OBJECTS) {
            skillsResponse = sendRequest(getSkillsUrl(i));
            pokaSkills.addAll(skillsResponse.getResult());
        }

        return pokaSkills.stream()
                .filter(skill -> skill.getCategory() != null)
                .map(MtpSkillDto::new)
                .filter(mtpSkill -> StringUtils.hasText(mtpSkill.getUnitPosition()))
                .collect(Collectors.toList());
    }

    protected List<Long> getSkillIds() throws DefaultException {
        return getSkills().stream().map(MtpSkillDto::getExternalId).toList();
    }

    @Override
    protected boolean isCorrectResponse(ResponseEntity<PokaSkillResponseDto> response) {
        return super.isCorrectResponse(response) && response.getBody().getCount() != 0;
    }

    private URI getSkillsUrl(int offset) {
        return getUri(String.format("%s/lms/skills?limit=%s&offset=%s", pokaUrl, LIMIT_OBJECTS, offset));
    }
}