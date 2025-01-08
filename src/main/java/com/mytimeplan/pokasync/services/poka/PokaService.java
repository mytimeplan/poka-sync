package com.mytimeplan.pokasync.services.poka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mytimeplan.pokasync.dto.poka.PokaResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@Service
@RequiredArgsConstructor
public abstract class PokaService<T extends PokaResultDto<?>> {

    @Value("${poka.url}")
    protected String pokaUrl;
    @Value("${poka.token}")
    protected String pokaToken;

    protected final RestTemplate restTemplate;
    protected static final Gson GSON = new GsonBuilder().create();

    protected boolean isCorrectResponse(ResponseEntity<T> response) {
        return response != null && response.getStatusCode().equals(HttpStatus.OK)
                && response.getBody() != null
                && !CollectionUtils.isEmpty(response.getBody().getResult());
    }

    protected HttpHeaders getHeaders() {
        return new HttpHeaders() {{
            set("Accept", MediaType.APPLICATION_JSON_VALUE);
            set("Authorization", pokaToken);
        }};
    }

    protected URI getUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            log.error("Cannot create URI for url: {}", url);
            return null;
        }
    }
}