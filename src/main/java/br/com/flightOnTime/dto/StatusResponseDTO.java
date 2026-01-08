package br.com.flightOnTime.dto;

public record StatusResponseDTO(
        long totalVoos,
        long voosAtrasados,
        double percentualAtraso

) {
}


