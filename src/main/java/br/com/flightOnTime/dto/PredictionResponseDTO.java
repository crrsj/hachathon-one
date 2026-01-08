package br.com.flightOnTime.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PredictionResponseDTO {
    private String previsao;
    private double probabilidade;
}
