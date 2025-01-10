package com.mytimeplan.pokasync.controllers;

import com.mytimeplan.pokasync.exceptions.DefaultException;
import com.mytimeplan.pokasync.services.poka.SkillService;
import com.mytimeplan.pokasync.services.poka.UserService;
import com.mytimeplan.pokasync.services.poka.UserSkillsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api/poca-sync")
@RequiredArgsConstructor
public class PokaController {

    private final SkillService skillService;
    private final UserService userService;
    private final UserSkillsService userSkillsService;

    @GetMapping("/skills")
    public ResponseEntity<?> getSkills() throws DefaultException {
        return new ResponseEntity<>(skillService.getSkills(), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() throws DefaultException {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/users-with-skills")
    public ResponseEntity<?> getSkillsPerUser() throws DefaultException {
//        return new ResponseEntity<>(userSkillsService.comparingUsersWithSkills(), HttpStatus.OK);
        try {
            ClassPathResource resource = new ClassPathResource("static/test.json");

            String fileContent = Files.readString(Path.of(resource.getURI()));


            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}