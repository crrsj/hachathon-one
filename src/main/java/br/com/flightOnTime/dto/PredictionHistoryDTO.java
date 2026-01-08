package br.com.flightOnTime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PredictionHistoryDTO {
    private Long id;
    private String origem;
    private String dataPartida;
    private String previsao;
    private double probabilidade;
    private LocalDateTime dataConsulta;
}
