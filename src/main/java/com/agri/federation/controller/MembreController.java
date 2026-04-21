package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.agri.federation.model.Membre;
import com.agri.federation.service.MembreService;

@RestController
@RequestMapping("/api/membres")
@RequiredArgsConstructor
public class MembreController {

    private final MembreService service;

    @GetMapping
    public List<Membre> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Membre create(@RequestBody Membre m) {
        return service.save(m);
    }
}