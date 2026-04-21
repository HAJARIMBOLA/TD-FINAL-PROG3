package com.agri.federation.controller;

import com.agri.federation.repository.CotisationRepository;
import com.agri.federation.service.CotisationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cotisations")
public class CotisationController {

    private final CotisationService service;
    private final CotisationRepository repository;

    public CotisationController(CotisationService service,
                                CotisationRepository repository) {
        this.service = service;
        this.repository = repository;
    }
}