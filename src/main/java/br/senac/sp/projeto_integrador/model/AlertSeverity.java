package br.senac.sp.projeto_integrador.model;

public enum AlertSeverity {
    INFO("Info"),
    WARNING("Warning"),
    CRITICAL("Critical"),
    SENSOR_FAIL("Sensor Fail");

    private String severity;

    AlertSeverity(String severity) {
        this.severity = severity;
    }

    public String getSeverity() {
        return severity;
    }

}
