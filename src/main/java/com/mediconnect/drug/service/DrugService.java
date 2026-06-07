package com.mediconnect.drug.service;

import com.mediconnect.res.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/*
 Servizio per l'integrazione con l'API pubblica OpenFDA (https://api.fda.gov).
 A differenza degli altri Service del progetto, questa classe NON ha un'interfaccia separata
 né un Repository: non salva dati nel database, ma effettua chiamate HTTP esterne
 tramite RestTemplate per recuperare informazioni su farmaci, interazioni e avvertenze.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DrugService {

    // URL base dell'API OpenFDA per le etichette dei farmaci
    private static final String OPENFDA_BASE = "https://api.fda.gov/drug/label.json";

    // RestTemplate: client HTTP di Spring per effettuare chiamate GET all'API esterna
    // Viene iniettato da Spring (definito come @Bean in AppConfig)
    private final RestTemplate restTemplate;

    // Cerca farmaci per nome commerciale (brand_name) sull'API OpenFDA
    // Costruisce l'URL con UriComponentsBuilder, limita i risultati a max 10
    // In caso di errore (farmaco non trovato, API down), ritorna una lista vuota con status 404
    @SuppressWarnings("unchecked")
    public Response<List<Map<String, Object>>> searchDrug(String name, int limit) {
        String url = UriComponentsBuilder.fromUriString(OPENFDA_BASE)
                .queryParam("search", "openfda.brand_name:" + name)
                .queryParam("limit", Math.min(limit, 10))
                .toUriString();

        try {
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> results = result != null ? (List<Map<String, Object>>) result.get("results")
                    : List.of();

            return Response.<List<Map<String, Object>>>builder()
                    .statusCode(200)
                    .message("Drug information retrieved from OpenFDA")
                    .data(results)
                    .build();
        } catch (Exception e) {
            log.error("OpenFDA API error: {}", e.getMessage());
            return Response.<List<Map<String, Object>>>builder()
                    .statusCode(404)
                    .message("No drug information found for: " + name)
                    .data(List.of())
                    .build();
        }
    }

    // Cerca le interazioni farmacologiche note per un dato farmaco
    // Interroga il campo "drug_interactions" dell'API OpenFDA
    @SuppressWarnings("unchecked")
    public Response<List<Map<String, Object>>> getDrugInteractions(String drugName) {
        String url = UriComponentsBuilder.fromUriString(OPENFDA_BASE)
                .queryParam("search", "drug_interactions:" + drugName)
                .queryParam("limit", 5)
                .toUriString();

        try {
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> results = result != null ? (List<Map<String, Object>>) result.get("results")
                    : List.of();

            return Response.<List<Map<String, Object>>>builder()
                    .statusCode(200)
                    .message("Drug interactions retrieved from OpenFDA")
                    .data(results)
                    .build();
        } catch (Exception e) {
            log.error("OpenFDA interaction API error: {}", e.getMessage());
            return Response.<List<Map<String, Object>>>builder()
                    .statusCode(404)
                    .message("No interaction data found for: " + drugName)
                    .data(List.of())
                    .build();
        }
    }

    // Cerca le avvertenze e controindicazioni per un dato farmaco
    // Interroga il campo "warnings" dell'API OpenFDA
    @SuppressWarnings("unchecked")
    public Response<List<Map<String, Object>>> getDrugWarnings(String drugName) {
        String url = UriComponentsBuilder.fromUriString(OPENFDA_BASE)
                .queryParam("search", "warnings:" + drugName)
                .queryParam("limit", 5)
                .toUriString();

        try {
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> results = result != null ? (List<Map<String, Object>>) result.get("results")
                    : List.of();

            return Response.<List<Map<String, Object>>>builder()
                    .statusCode(200)
                    .message("Drug warnings retrieved from OpenFDA")
                    .data(results)
                    .build();
        } catch (Exception e) {
            log.error("OpenFDA warnings API error: {}", e.getMessage());
            return Response.<List<Map<String, Object>>>builder()
                    .statusCode(404)
                    .message("No warning data found for: " + drugName)
                    .data(List.of())
                    .build();
        }
    }
}
