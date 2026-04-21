package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.agri.federation.model.Activite;
import com.agri.federation.service.ActiviteService;

@RestController
@RequestMapping("/api/activites")
@RequiredArgsConstructor
public class ActiviteController {

    private final ActiviteService service;

    @GetMapping
    public List<Activite> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Activite create(@RequestBody Activite a) {
        return service.save(a);
    }
}