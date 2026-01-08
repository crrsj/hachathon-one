package br.com.flightOnTime.controller;

import br.com.flightOnTime.dto.PredictionHistoryDTO;
import br.com.flightOnTime.dto.PredictionRequestDTO;
import br.com.flightOnTime.dto.PredictionResponseDTO;
import br.com.flightOnTime.dto.StatusResponseDTO;
import br.com.flightOnTime.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/predict")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PredictionController {
    private final PredictionService predictionService;



    @PostMapping
    @Operation(summary = "endpoint responsável pela previsão de vôo, histórico de atrasos e cadastro das consultas.")
    @ApiResponse(responseCode = "200", description = " success", content = {
            @Content(mediaType = "application.json", schema = @Schema(implementation = ResponseEntity.class))
    })
    public ResponseEntity<PredictionResponseDTO> predict(@Valid @RequestBody PredictionRequestDTO request) {
        var response = predictionService.getPrediction(request);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/stats")
    @Operation(
            summary = "Obter estatísticas consolidadas do dia",
            description = "Retorna o volume total de consultas, a quantidade de atrasos previstos e a taxa percentual de ocorrência para o dia atual (desde 00:00)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas recuperadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar os dados do banco")
    })
    public ResponseEntity<StatusResponseDTO>showStatus(){
        return ResponseEntity.ok(predictionService.getStatus());
    }

}

