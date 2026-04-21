package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.agri.federation.model.Collectivite;
import com.agri.federation.service.CollectiviteService;

@RestController
@RequestMapping("/api/collectivites")
@RequiredArgsConstructor
public class CollectiviteController {

    private final CollectiviteService service;

    @GetMapping
    public List<Collectivite> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Collectivite create(@RequestBody Collectivite c) {
        return service.create(c);
    }
}