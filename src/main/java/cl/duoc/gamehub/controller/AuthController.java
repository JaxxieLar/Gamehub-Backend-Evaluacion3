package cl.duoc.gamehub.controller;

import cl.duoc.gamehub.model.Usuario;
import cl.duoc.gamehub.repository.UsuarioRepository;
import cl.duoc.gamehub.security.JwtServicio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtServicio jwt;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtServicio jwt) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public LoginRespuesta login(@Valid @RequestBody LoginSolicitud datos) {
        Usuario u = usuarios.findByEmail(datos.email())
                .filter(x -> encoder.matches(datos.password(), x.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos"));
        String token = jwt.generar(u.getEmail(), u.getRol());
        return new LoginRespuesta(token, new UsuarioDTO(u.getNombre(), u.getEmail(), u.getRol()));
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginRespuesta registro(@Valid @RequestBody RegistroSolicitud datos) {
        if (usuarios.findByEmail(datos.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya está registrado");
        }
        Usuario nuevo = new Usuario(datos.nombre(), datos.email(), encoder.encode(datos.password()), "USER");
        usuarios.save(nuevo);
        String token = jwt.generar(nuevo.getEmail(), nuevo.getRol());
        return new LoginRespuesta(token, new UsuarioDTO(nuevo.getNombre(), nuevo.getEmail(), nuevo.getRol()));
    }

    public record LoginSolicitud(@NotBlank String email, @NotBlank String password) {}
    public record RegistroSolicitud(@NotBlank String nombre, @NotBlank @Email String email, @NotBlank String password) {}
    public record UsuarioDTO(String nombre, String email, String rol) {}
    public record LoginRespuesta(String token, UsuarioDTO usuario) {}
}