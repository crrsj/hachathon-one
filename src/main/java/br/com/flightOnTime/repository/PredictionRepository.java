package br.com.flightOnTime.repository;

import br.com.flightOnTime.entity.PredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PredictionRepository extends JpaRepository<PredictionEntity,Long> {
    long countByDataConsultaBetween(
            LocalDateTime inicio,
            LocalDateTime fim
    );

    long countByResultadoPrevisaoAndDataConsultaBetween(
            String resultadoPrevisao,
            LocalDateTime inicio,
            LocalDateTime fim
    );
}
