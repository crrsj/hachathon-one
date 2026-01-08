package br.com.flightOnTime.infra;

import br.com.flightOnTime.dto.ErroResponseDTO;
import br.com.flightOnTime.dto.ValidandoCampos;
import br.com.flightOnTime.exception.PrevisaoNaoEncontrada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.channels.ClosedChannelException;

@RestControllerAdvice
public class ExcecoesGlobais {

       @ExceptionHandler(PrevisaoNaoEncontrada.class)
       public ResponseEntity<ErroResponseDTO> tratarPrevisaoNaoEncontrada(PrevisaoNaoEncontrada ex) {
            ErroResponseDTO erro = new ErroResponseDTO(ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(erro);
        }

    @ExceptionHandler(ClosedChannelException.class)
    public ResponseEntity<ErroResponseDTO> tratarErroGenerico(Exception ex) {

        ErroResponseDTO erro =
                new ErroResponseDTO("Erro interno no servidor:API pythhon offline");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(erro);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?>validandoCampos(MethodArgumentNotValidException ex){
        var erros = ex.getFieldErrors();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros.stream().map(ValidandoCampos::new).toList());
    }
}


