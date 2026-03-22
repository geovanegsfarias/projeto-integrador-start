package br.senac.sp.projeto_integrador.mapper;

import br.senac.sp.projeto_integrador.dto.request.ReadingRequest;
import br.senac.sp.projeto_integrador.dto.response.ReadingResponse;
import br.senac.sp.projeto_integrador.model.Reading;

public class ReadingMapper {

    public static Reading toReading(ReadingRequest request) {
        return new Reading(
                request.ambientTemp(),
                request.liquidTemp(),
                request.humidity(),
                request.stage(),
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
