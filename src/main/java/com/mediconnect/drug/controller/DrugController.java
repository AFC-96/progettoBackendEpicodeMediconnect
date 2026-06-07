package com.mediconnect.drug.controller;

import com.mediconnect.drug.service.DrugService;
import com.mediconnect.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 Controller REST che espone gli endpoint sotto /api/drugs per la ricerca farmaci.
 A differenza degli altri controller, non interagisce con il database ma delega
 al DrugService che effettua chiamate HTTP all'API pubblica OpenFDA.
 Tutti gli endpoint sono accessibili a qualsiasi utente autenticato (medici e pazienti).
 */
@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    // GET /api/drugs/search?name=aspirin&limit=3 — Cerca farmaci per nome commerciale
    // @RequestParam "name" è il nome del farmaco, "limit" il numero massimo di risultati (default 5, max 10)
    @GetMapping("/search")
    public ResponseEntity<Response<List<Map<String, Object>>>> searchDrug(
            @RequestParam String name,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(drugService.searchDrug(name, limit));
    }

    // GET /api/drugs/interactions?drugName=warfarin — Cerca le interazioni farmacologiche note
    @GetMapping("/interactions")
    public ResponseEntity<Response<List<Map<String, Object>>>> getDrugInteractions(
            @RequestParam String drugName) {
        return ResponseEntity.ok(drugService.getDrugInteractions(drugName));
    }

    // GET /api/drugs/warnings?drugName=ibuprofen — Cerca le avvertenze e controindicazioni
    @GetMapping("/warnings")
    public ResponseEntity<Response<List<Map<String, Object>>>> getDrugWarnings(
            @RequestParam String drugName) {
        return ResponseEntity.ok(drugService.getDrugWarnings(drugName));
    }
}
