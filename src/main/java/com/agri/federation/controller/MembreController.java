package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.agri.federation.dto.CreateMemberRequest;
import com.agri.federation.model.Membre;
import com.agri.federation.service.MembreService;

@RestController
@RequiredArgsConstructor
public class MembreController {

    private final MembreService service;

    // ✅ POST /members — fonctionnalité B-2 (OAS bulk)
    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Membre> createBulk(@RequestBody List<CreateMemberRequest> requests) {
        return service.createAll(requests);
    }
}