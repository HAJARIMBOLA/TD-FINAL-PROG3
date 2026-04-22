package com.agri.federation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.agri.federation.dto.AssignFederationAttributesRequest;
import com.agri.federation.model.Collectivite;
import com.agri.federation.service.CollectiviteService;

@RestController
@RequiredArgsConstructor
public class CollectiviteController {

    private final CollectiviteService service;

    // ✅ POST /collectivities — fonctionnalité A (OAS bulk)
    @PostMapping("/collectivities")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Collectivite> createBulk(@RequestBody List<Collectivite> collectivites) {
        return service.createAll(collectivites);
    }

    // ✅ PUT /collectivities/{collectivityId}/federation-attributes — fonctionnalité J
    @PutMapping("/collectivities/{collectivityId}/federation-attributes")
    public Collectivite assignFederationAttributes(
            @PathVariable Long collectivityId,
            @RequestBody AssignFederationAttributesRequest request) {
        return service.assignFederationAttributes(collectivityId, request);
    }
}