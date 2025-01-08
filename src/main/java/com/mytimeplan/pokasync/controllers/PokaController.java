package com.mytimeplan.pokasync.controllers;

import com.mytimeplan.pokasync.exceptions.DefaultException;
import com.mytimeplan.pokasync.services.poka.SkillService;
import com.mytimeplan.pokasync.services.poka.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/poca-sync")
@RequiredArgsConstructor
public class PokaController {

    private final SkillService skillService;
    private final UserService userService;

    @GetMapping("/skills")
    public ResponseEntity<?> getSkills() throws DefaultException {
        return new ResponseEntity<>(skillService.getSkills(), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() throws DefaultException {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }
}