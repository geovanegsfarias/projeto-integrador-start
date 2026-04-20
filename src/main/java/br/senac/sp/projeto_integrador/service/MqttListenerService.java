package br.senac.sp.projeto_integrador.service;

import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MqttListenerService {

    private static final Logger logger = LoggerFactory.getLogger(MqttListenerService.class);

    private final ReadingService readingService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MqttListenerService(ReadingService readingService, ObjectMapper objectMapper) {
        this.readingService = readingService;
        this.objectMapper = objectMapper;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void onMessage(Message<String> message) {
        String payload = message.getPayload();
        logger.info("Mensagem MQTT recebida: {}", payload);

        try {
            ReadingRequest request = objectMapper.readValue(payload, ReadingRequest.class);
            readingService.save(request);
            logger.info("Leitura salva via MQTT com sucesso.");
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem MQTT: {} | payload: {}", e.getMessage(), payload);
        }
    }
}