package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.agri.federation.model.Mandat;
import com.agri.federation.service.MandatService;

@RestController
@RequestMapping("/api/mandats")
@RequiredArgsConstructor
public class MandatController {

    private final MandatService service;

    @PostMapping
    public Mandat create(@RequestBody Mandat m) {
        return service.save(m);
    }
}