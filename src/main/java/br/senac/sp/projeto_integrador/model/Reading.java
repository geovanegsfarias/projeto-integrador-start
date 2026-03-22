package br.senac.sp.projeto_integrador.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "readings")
public class Reading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private OffsetDateTime timestamp;

    @Column(nullable = false)
    private double ambientTemp;

    @Column(nullable = false)
    private double liquidTemp;

    @Column(nullable = false)
    private double humidity;

    @Enumerated(value = EnumType.STRING)
    private BeerStage stage;

    @Column(length = 50, nullable = false)
    private String deviceId;

    public Reading() {
    }

    public Reading(double ambientTemp, double liquidTemp, double humidity, BeerStage stage, String deviceId) {
        this.ambientTemp = ambientTemp;
        this.liquidTemp = liquidTemp;
        this.humidity = humidity;
        this.stage = stage;
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmbientTemp() {
        return ambientTemp;
    }

    public void setAmbientTemp(double ambientTemp) {
        this.ambientTemp = ambientTemp;
    }

    public double getLiquidTemp() {
        return liquidTemp;
    }

    public void setLiquidTemp(double liquidTemp) {
        this.liquidTemp = liquidTemp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public BeerStage getStage() {
        return stage;
    }

    public void setStage(BeerStage stage) {
        this.stage = stage;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
