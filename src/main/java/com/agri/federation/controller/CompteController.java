package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.agri.federation.model.Compte;
import com.agri.federation.service.CompteService;

@RestController
@RequestMapping("/api/comptes")
@RequiredArgsConstructor
public class CompteController {

    private CompteService service;

    @PostMapping
    public Compte create(@RequestBody Compte c) {
        return service.save(c);
    }
}