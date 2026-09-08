package com.earthquake.core.web;

import com.earthquake.core.dto.BackfillRequest;
import com.earthquake.core.dto.IngestionJobResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ingestion")
public class AdminIngestionController {

    @PostMapping("/backfill")
    public ResponseEntity<IngestionJobResponse> backfill(@RequestBody @Valid BackfillRequest request) {
        throw new UnsupportedOperationException("TODO: implement trigger backfill ingestion job");
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<IngestionJobResponse>> jobs() {
        throw new UnsupportedOperationException("TODO: implement list ingestion jobs");
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<IngestionJobResponse> getJob(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implement get ingestion job by id");
    }
}
