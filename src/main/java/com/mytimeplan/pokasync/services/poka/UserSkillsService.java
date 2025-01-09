package com.mytimeplan.pokasync.services.poka;

import com.mytimeplan.pokasync.dto.mtp.MtpUserDto;
import com.mytimeplan.pokasync.dto.mtp.MtpUserSkillsDto;
import com.mytimeplan.pokasync.dto.poka.PokaUserSkillResponseDto;
import com.mytimeplan.pokasync.exceptions.DefaultException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Log4j2
@Service
public class UserSkillsService extends PokaService<PokaUserSkillResponseDto> {

    private final UserService userService;
    private final SkillService skillService;
    private static Date actualDate = new Date();

    public UserSkillsService(RestTemplate restTemplate, UserService userService, SkillService skillService) {
        super(restTemplate, PokaUserSkillResponseDto.class);
        this.userService = userService;
        this.skillService = skillService;
    }

    public List<MtpUserSkillsDto> comparingUsersWithSkills() throws DefaultException {
        List<MtpUserDto> users = userService.getUsers();
        List<Long> validSkillIds = skillService.getSkillIds();
        Map<Long, List<PokaUserSkillResponseDto.UserSkill>> skillsPerUserId = fetchUserSkills().stream()
                .collect(groupingBy(PokaUserSkillResponseDto.UserSkill::getUserId));
        skillsPerUserId.forEach((idUser, skills) ->
                skills.removeIf(s -> !validSkillIds.contains(s.getSkillId())));
        return users.stream()
                .map(user -> new MtpUserSkillsDto(user.getId(), user.getEmail(), skillsPerUserId.get(user.getId())))
                .toList();
    }

    private List<PokaUserSkillResponseDto.UserSkill> fetchUserSkills() throws DefaultException {
        List<PokaUserSkillResponseDto.UserSkill> result = new ArrayList<>();
        URI url = getUserSkillsUrl();
        actualDate = new Date();
        do {
            PokaUserSkillResponseDto pokaUsersSkillsResponse = sendRequest(url);
            List<PokaUserSkillResponseDto.UserSkill> usersSkills = pokaUsersSkillsResponse.getResult().stream()
                    .filter(UserSkillsService::isCorrectEndorsement)
                    .toList();
            result.addAll(usersSkills);
            url = getUri(pokaUsersSkillsResponse.getNextUrl());
        } while (url != null && StringUtils.hasText(url.toString()));
        return result;
    }

    protected PokaUserSkillResponseDto sendRequest(URI url) throws DefaultException {
        HttpHeaders headers = getHeaders();
        try {
            ResponseEntity<PokaUserSkillResponseDto> pokaUserResponse = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), PokaUserSkillResponseDto.class);
            if (!isCorrectResponse(pokaUserResponse)) throw new DefaultException("Incorrect response");
            return pokaUserResponse.getBody();
        } catch (HttpClientErrorException.Forbidden ex) {
            log.error("Forbidden error when getting users skills from POKA. Check token in settings. URL:[{}] HEADERS:[{}]", url, GSON.toJson(headers), ex);
            return null;
        }
    }

    private static boolean isCorrectEndorsement(PokaUserSkillResponseDto.UserSkill userSkill) {
        return userSkill.getEndorsement() != null
                && userSkill.getEndorsement().getLevel() != null
                && userSkill.getEndorsement().getLevel().getValue() >= 75
                && (userSkill.getEndorsement().getExpiresAt() == null || userSkill.getEndorsement().getExpiresAt().after(actualDate));
    }

    private URI getUserSkillsUrl() {
        return getUri(pokaUrl + "/user-skills-endorsement?endorsed_from=2000-01-01");
    }
}