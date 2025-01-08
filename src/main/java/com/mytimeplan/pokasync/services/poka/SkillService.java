package com.mytimeplan.pokasync.services.poka;

import com.mytimeplan.pokasync.dto.mtp.MtpSkillDto;
import com.mytimeplan.pokasync.dto.poka.PokaSkillResponseDto;
import com.mytimeplan.pokasync.exceptions.DefaultException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class SkillService extends PokaService<PokaSkillResponseDto> {

    private static final int LIMIT_OBJECTS = 20;

    public SkillService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public List<MtpSkillDto> getSkills() throws DefaultException {
        PokaSkillResponseDto skillsResponse = sendRequest(0);
        List<PokaSkillResponseDto.Skill> pokaSkills = new ArrayList<>(skillsResponse.getResult());

        for (int i = LIMIT_OBJECTS; i < skillsResponse.getCount(); i += LIMIT_OBJECTS) {
            skillsResponse = sendRequest(i);
            pokaSkills.addAll(skillsResponse.getResult());
        }

        return pokaSkills.stream()
                .filter(skill -> skill.getCategory() != null)
                .map(MtpSkillDto::new)
                .filter(mtpSkill -> !mtpSkill.getUnitPositions().isEmpty())
                .collect(Collectors.toList());
    }

    private PokaSkillResponseDto sendRequest(int offset) throws DefaultException {
        String skillsUrl = getSkillsUrl(offset);
        HttpHeaders headers = getHeaders();
        try {
            ResponseEntity<PokaSkillResponseDto> pokaSkillResponse = restTemplate.exchange(
                    skillsUrl, HttpMethod.GET, new HttpEntity<>(headers), PokaSkillResponseDto.class);
            if (!isCorrectResponse(pokaSkillResponse)) throw new DefaultException("Incorrect response");
            return pokaSkillResponse.getBody();
        } catch (HttpClientErrorException.Forbidden ex) {
            log.error("Forbidden error when getting skills from POKA. Check token in settings. URL:[{}] HEADERS:[{}]", skillsUrl, GSON.toJson(headers), ex);
            return null;
        }
    }

    protected List<Long> getSkillIds() throws DefaultException {
        return getSkills().stream().map(MtpSkillDto::getExternalId).toList();
    }

    @Override
    protected boolean isCorrectResponse(ResponseEntity<PokaSkillResponseDto> response) {
        return super.isCorrectResponse(response) && response.getBody().getCount() != 0;
    }

    private String getSkillsUrl(int offset) {
        return String.format("%s/lms/skills?limit=%s&offset=%s", pokaUrl, LIMIT_OBJECTS, offset);
    }
}