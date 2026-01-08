package br.com.flightOnTime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PredictionRequestDTO {
    @NotBlank(message = "O campo 'companhia' é obrigatório.")
    private String companhia;
    @NotBlank(message = "O campo 'origem' é obrigatório.")
    private String origem;
    @NotBlank(message = "O campo 'destino' é obrigatório.")
    private String destino;
    @NotNull(message = "A 'data_partida' é obrigatória.")
    private String data_partida;
    @Positive(message = "A distância deve ser um valor positivo.")
    private int distancia_km;

}

