package cl.duoc.gamehub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarValidacion(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining(". "));
        return ResponseEntity.badRequest().body(Map.of("mensaje", errores));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> manejarCredenciales(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("mensaje", "Correo o contraseña incorrectos"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("mensaje", "No tienes permisos para realizar esta acción"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor: " + ex.getMessage()));
    }
}