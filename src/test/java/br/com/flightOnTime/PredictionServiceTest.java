package br.com.flightOnTime;

import br.com.flightOnTime.dto.PredictionRequestDTO;
import br.com.flightOnTime.dto.PredictionResponseDTO;
import br.com.flightOnTime.dto.StatusResponseDTO;
import br.com.flightOnTime.entity.PredictionEntity;
import br.com.flightOnTime.exception.PrevisaoNaoEncontrada;
import br.com.flightOnTime.repository.PredictionRepository;
import br.com.flightOnTime.service.PredictionService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class PredictionServiceTest {

	@Mock
	private PredictionRepository predictionRepository;

	private static MockWebServer mockWebServer;
	private PredictionService predictionService;

	@BeforeAll
	static void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@BeforeEach
	void initialize() {
		MockitoAnnotations.openMocks(this);
		String baseUrl = mockWebServer.url("/").toString();
		WebClient.Builder webClientBuilder = WebClient.builder();
		this.predictionService = new PredictionService(webClientBuilder,predictionRepository, baseUrl);
		ReflectionTestUtils.setField(predictionService, "predictEndpoint", "/predict_internal");
	}

@Test
@DisplayName("Deve retornar a previsão com sucesso e salvar no banco em letras maiúsculas")
void getPrediction_Success() {
	mockWebServer.enqueue(new MockResponse()
			.setResponseCode(200)
			.setBody("{\"previsao\": \"Atrasado\", \"probabilidade\": 0.65}")
			.addHeader("Content-Type", "application/json"));


	Mockito.when(predictionRepository.save(any(PredictionEntity.class)))
			.thenAnswer(i -> i.getArguments()[0]);

	PredictionRequestDTO request = new PredictionRequestDTO();
	request.setOrigem("GRU");
	request.setData_partida("2025-12-25T10:00:00");
	request.setDistancia_km(450);
	PredictionResponseDTO response = predictionService.getPrediction(request);
	assertNotNull(response);
	assertEquals("Atrasado", response.getPrevisao());
	ArgumentCaptor<PredictionEntity> entityCaptor = ArgumentCaptor.forClass(PredictionEntity.class);
	Mockito.verify(predictionRepository, Mockito.times(1)).save(entityCaptor.capture());
	PredictionEntity entitySalva = entityCaptor.getValue();
	assertEquals(PredictionService.STATUS_ATRASADO, entitySalva.getResultadoPrevisao());
	assertEquals(0.65, entitySalva.getProbabilidade());
}

	@Test
	@DisplayName("Deve lançar PrevisaoNaoEncontrada quando o Python retorna erro 4xx ou 5xx")
	void getPrediction_ApiError() {
		mockWebServer.enqueue(new MockResponse().setResponseCode(404));
		PredictionRequestDTO request = new PredictionRequestDTO();
		request.setOrigem("GIG");
		assertThrows(PrevisaoNaoEncontrada.class, () -> {
			predictionService.getPrediction(request);
		});

		Mockito.verify(predictionRepository, Mockito.never()).save(any());
	}


	@Test
	@DisplayName("Deve calcular estatísticas corretamente quando houver dados hoje")
	void getStatus_ComDados() {

		long totalSimulado = 10;
		long atrasadosSimulados = 4; // Deve resultar em 40.0%

		Mockito.when(predictionRepository.countByDataConsultaBetween(any(), any()))
				.thenReturn(totalSimulado);

		Mockito.when(predictionRepository.countByResultadoPrevisaoAndDataConsultaBetween(
						eq(PredictionService.STATUS_ATRASADO), any(), any()))
				.thenReturn(atrasadosSimulados);
		StatusResponseDTO result = predictionService.getStatus();
		assertNotNull(result);
		assertEquals(10, result.totalVoos());
		assertEquals(4, result.voosAtrasados());
		assertEquals(40.0, result.percentualAtraso());
		Mockito.verify(predictionRepository).countByResultadoPrevisaoAndDataConsultaBetween(
				eq(PredictionService.STATUS_ATRASADO), any(), any());
	}

	@Test
	@DisplayName("Deve retornar percentual zero quando não houver voos hoje (evitar divisão por zero)")
	void getStatus_SemDados() {

		Mockito.when(predictionRepository.countByDataConsultaBetween(any(), any()))
				.thenReturn(0L);
		Mockito.when(predictionRepository.countByResultadoPrevisaoAndDataConsultaBetween(any(), any(), any()))
				.thenReturn(0L);

		StatusResponseDTO result = predictionService.getStatus();

		assertEquals(0, result.totalVoos());
		assertEquals(0.0, result.percentualAtraso());
	}

	@Test
	@DisplayName("Deve garantir que o intervalo de tempo inicia no começo do dia atual")
	void getStatus_VerificaDatas() {

		ArgumentCaptor<LocalDateTime> dataInicioCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		predictionService.getStatus();
		Mockito.verify(predictionRepository).countByDataConsultaBetween(dataInicioCaptor.capture(), any());
		LocalDateTime dataInicioUsada = dataInicioCaptor.getValue();

		assertEquals(0, dataInicioUsada.getHour());
		assertEquals(0, dataInicioUsada.getMinute());
		assertEquals(0, dataInicioUsada.getSecond());
		assertEquals(LocalDate.now(), dataInicioUsada.toLocalDate());
	}

}
