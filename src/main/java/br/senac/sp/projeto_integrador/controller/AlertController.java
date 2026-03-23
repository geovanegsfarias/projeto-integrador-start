package br.senac.sp.projeto_integrador.controller;

import br.senac.sp.projeto_integrador.dto.response.AlertCountResponse;
import br.senac.sp.projeto_integrador.dto.response.AlertResponse;
import br.senac.sp.projeto_integrador.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    private final AlertService alertService;

    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAll());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AlertResponse>> getRecentAlerts() {
        return ResponseEntity.ok(alertService.getRecent());
    }

    @GetMapping("/recent/count")
    public ResponseEntity<AlertCountResponse> getRecentAlertsCount() {
        return ResponseEntity.ok(alertService.getRecentCount());
    }

}