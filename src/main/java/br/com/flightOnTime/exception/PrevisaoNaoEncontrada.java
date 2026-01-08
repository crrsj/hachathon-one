package br.com.flightOnTime.exception;

public class PrevisaoNaoEncontrada extends RuntimeException {
    public PrevisaoNaoEncontrada(String message) {
        super(message);
    }
}
