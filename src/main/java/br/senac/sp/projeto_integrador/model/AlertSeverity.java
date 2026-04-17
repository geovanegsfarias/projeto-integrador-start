package br.senac.sp.projeto_integrador.model;

public enum AlertSeverity {
    INFO("INFO"),
    WARNING("AVISO"),
    CRITICAL("CRÍTICO"),
    SENSOR_FAIL("ERRO NO SENSOR");

    private String severity;

    AlertSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }

}
