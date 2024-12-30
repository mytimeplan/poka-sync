package com.mytimeplan.pokasync.controllers;

import com.mytimeplan.pokasync.exceptions.DefaultException;
import com.mytimeplan.pokasync.services.PokaService;
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

    private final PokaService pokaService;

    @GetMapping("/skills")
    public ResponseEntity<?> getSkills() throws DefaultException {
        return new ResponseEntity<>(pokaService.getSkills(), HttpStatus.OK);
    }
}