package com.mytimeplan.pokasync.services.poka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mytimeplan.pokasync.dto.poka.PokaResultDto;
import com.mytimeplan.pokasync.exceptions.DefaultException;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.*;

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

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final Bucket bucket = Bucket.builder()
            .addLimit(limit -> limit.capacity(100).refillGreedy(100, Duration.ofMinutes(1)))
            .build();

    private final Class<T> responseType;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final int DEFAULT_DELAY_TIME = 60;
    private static final int DEFAULT_TIME_SHIT_IN_SECONDS = 5;
    private static final int MAX_RETRIES = 5;


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
        } catch (Exception e) {
            log.error("Cannot create URI for url: {}", url);
            return null;
        }
    }

    protected T sendRequest(URI url) throws DefaultException {
        CompletableFuture<T> resultFuture = new CompletableFuture<>();
        tryConsumeAndExecute(new SendRequest(url, getHeaders(), resultFuture));
        try {
            return resultFuture.get();
        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof DefaultException) {
                throw (DefaultException) ex.getCause();
            } else {
                log.error("Execution failed: {}", ex.getMessage(), ex);
                throw new DefaultException("Execution failed");
            }
        } catch (InterruptedException ex) {
            log.error("Request was interrupted: {}", ex.getMessage(), ex);
            throw new DefaultException("Request was interrupted");
        }
    }

    private void tryConsumeAndExecute(Callable<T> task) {
        if (bucket.tryConsume(1)) {
            scheduler.submit(task);
        } else {
            log.info("Rate limit exceeded. Retrying in {} {}.", DEFAULT_DELAY_TIME, DEFAULT_TIME_UNIT);
            scheduler.schedule(() -> tryConsumeAndExecute(task), DEFAULT_DELAY_TIME, DEFAULT_TIME_UNIT);
        }
    }


    private class SendRequest implements Callable<T> {
        private final URI url;
        private final HttpHeaders headers;
        private final int attempt;
        private final CompletableFuture<T> resultFuture;

        public SendRequest(URI url, HttpHeaders headers, CompletableFuture<T> resultFuture) {
            this.url = url;
            this.headers = headers;
            this.attempt = 0;
            this.resultFuture = resultFuture;
        }

        SendRequest(URI url, HttpHeaders headers, int attempt, CompletableFuture<T> resultFuture) {
            this.url = url;
            this.headers = headers;
            this.attempt = attempt;
            this.resultFuture = resultFuture;
        }

        @Override
        public T call() {
            try {
                ResponseEntity<T> response = restTemplate.exchange(
                        url, HttpMethod.GET, new HttpEntity<>(headers), responseType);
                if (!isCorrectResponse(response)) {
                    resultFuture.completeExceptionally(new DefaultException("Incorrect response"));
                    return null;
                }
                resultFuture.complete(response.getBody());
                return response.getBody();
            } catch (HttpClientErrorException.TooManyRequests ex) {
                long retryAfter = getRetryAfterDelay(ex.getResponseHeaders());
                log.warn("Received 429 Too Many Requests. Retrying in {} {}. Attempt {}/{}",
                        retryAfter, DEFAULT_TIME_UNIT, attempt + 1, MAX_RETRIES);
                if (attempt < MAX_RETRIES) {
                    scheduler.schedule(() -> {
                        Callable<T> retryTask = new SendRequest(url, headers, attempt + 1, resultFuture);
                        tryConsumeAndExecute(retryTask);
                    }, retryAfter, TimeUnit.SECONDS);
                } else {
                    log.error("Exceeded maximum retry attempts: {} for 429 Too Many Requests.", MAX_RETRIES);
                    resultFuture.completeExceptionally(
                            new DefaultException("Max retries [" + MAX_RETRIES + "] exceeded"));
                }
            } catch (Exception ex) {
                log.error("Error when sending request. URL:[{}] HEADERS:[{}]", url, GSON.toJson(headers), ex);
                resultFuture.completeExceptionally(
                        new DefaultException("Unexpected exception"));
            }
            return null;
        }

        private int getRetryAfterDelay(HttpHeaders headers) {
            if (headers != null && headers.containsKey("Retry-After")) {
                try {
                    String retryAfterValue = headers.getFirst("Retry-After");
                    return Integer.parseInt(retryAfterValue) + DEFAULT_TIME_SHIT_IN_SECONDS;
                } catch (NumberFormatException e) {
                    log.warn("Invalid Retry-After value in headers: {} , set default delay time.",
                            headers.getFirst("Retry-After"));
                }
            }
            return DEFAULT_DELAY_TIME;
        }
    }
}