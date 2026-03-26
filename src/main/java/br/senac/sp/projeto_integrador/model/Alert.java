package br.senac.sp.projeto_integrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reading_id")
    private Reading reading;

    @Column(length = 20, nullable = false)
    private String type;

    @Enumerated(value = EnumType.STRING)
    private AlertSeverity severity;

    @Column(length = 100, nullable = false)
    private String message;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private double threshold;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    public Alert() {
    }

    public Alert(Reading reading, String type, AlertSeverity severity, String message, double value, double threshold) {
        this.reading = reading;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.value = value;
        this.threshold = threshold;
    }

    public Alert(String type, AlertSeverity severity, String message) {
        this.type = type;
        this.severity = severity;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reading getReading() {
        return reading;
    }

    public void setReading(Reading reading) {
        this.reading = reading;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

}