package br.com.flightOnTime.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="predict")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String origem;
    @Column(name = "data_partida", nullable = false)
    private String dataPartida;
    @Column(name = "distancia_km")
    private int distanciaKm;
    @Column(nullable = false)
    private String resultadoPrevisao;
    private Double probabilidade;
    @Column(name = "dataConsulta", nullable = false)
    private LocalDateTime dataConsulta;
}
