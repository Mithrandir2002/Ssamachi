package com.earthquake.core.web;

import com.earthquake.core.dto.CreateReportRequest;
import com.earthquake.core.dto.ReportResponse;
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
@RequestMapping("/api/reports")
public class ReportController {

    @PostMapping
    public ResponseEntity<ReportResponse> create(@RequestBody @Valid CreateReportRequest request) {
        throw new UnsupportedOperationException("TODO: implement create report request");
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> list() {
        throw new UnsupportedOperationException("TODO: implement list report requests");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getById(@PathVariable Long id) {
        throw new UnsupportedOperationException("TODO: implement get report request by id");
    }
}
