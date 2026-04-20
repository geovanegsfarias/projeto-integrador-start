package br.senac.sp.projeto_integrador.mapper;

import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import br.senac.sp.projeto_integrador.dto.response.ReadingResponse;
import br.senac.sp.projeto_integrador.model.BeerStage;
import br.senac.sp.projeto_integrador.model.Reading;

public class ReadingMapper {

    public static Reading toReading(ReadingRequest request, BeerStage stage) {
        return new Reading(
                request.ambientTemp(),
                request.liquidTemp(),
                request.humidity(),
                stage,
                request.deviceId()
        );
    }

    public static ReadingResponse toReadingResponse(Reading reading) {
        return new ReadingResponse(
                reading.getId(),
                reading.getTimestamp(),
                reading.getAmbientTemp(),
                reading.getLiquidTemp(),
                reading.getHumidity(),
                reading.getStage(),
                reading.getDeviceId()
        );
    }
}