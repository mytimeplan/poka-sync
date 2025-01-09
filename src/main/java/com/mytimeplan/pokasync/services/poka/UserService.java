package com.mytimeplan.pokasync.services.poka;

import com.mytimeplan.pokasync.dto.mtp.MtpUserDto;
import com.mytimeplan.pokasync.dto.poka.PokaUserResponseDto;
import com.mytimeplan.pokasync.exceptions.DefaultException;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserService extends PokaService<PokaUserResponseDto> {

    private static final int LIMIT_OBJECTS = 50;

    public UserService(RestTemplate restTemplate) {
        super(restTemplate, PokaUserResponseDto.class);
    }

    public List<MtpUserDto> getUsers() throws DefaultException {
        PokaUserResponseDto usersResponse = sendRequest(0);
        List<PokaUserResponseDto.User> pokaUsers = new ArrayList<>(usersResponse.getResult());

        for (int i = LIMIT_OBJECTS; i < usersResponse.getCount(); i += LIMIT_OBJECTS) {
            usersResponse = sendRequest(i);
            pokaUsers.addAll(usersResponse.getResult());
        }

        return pokaUsers.stream()
                .filter(u -> u.isEmployee() && !StringUtils.isEmpty(u.getEmail()))
                .map(MtpUserDto::new)
                .collect(Collectors.toList());
    }

    private PokaUserResponseDto sendRequest(int offset) throws DefaultException {
        String usersUrl = getUsersUrl(offset);
        HttpHeaders headers = getHeaders();
        try {
            ResponseEntity<PokaUserResponseDto> pokaUserResponse = restTemplate.exchange(
                    usersUrl, HttpMethod.GET, new HttpEntity<>(headers), PokaUserResponseDto.class);
            if (!isCorrectResponse(pokaUserResponse)) throw new DefaultException("Incorrect response");
            return pokaUserResponse.getBody();
        } catch (HttpClientErrorException.Forbidden ex) {
            log.error("Forbidden error when getting users from POKA. Check token in settings. URL:[{}] HEADERS:[{}]", usersUrl, GSON.toJson(headers), ex);
            return null;
        }
    }

    @Override
    protected boolean isCorrectResponse(ResponseEntity<PokaUserResponseDto> response) {
        return super.isCorrectResponse(response) && response.getBody().getCount() != 0;
    }

    private String getUsersUrl(int offset) {
        return String.format("%s/users?optional_fields=email&limit=%s&offset=%s", pokaUrl, LIMIT_OBJECTS, offset);
    }
}