package cl.duoc.gamehub.config;

import cl.duoc.gamehub.model.Usuario;
import cl.duoc.gamehub.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatosIniciales {
    @Bean
    CommandLineRunner usuariosDePrueba(UsuarioRepository usuarios, PasswordEncoder encoder) {
        return args -> {
            if (usuarios.count() > 0) return;
            usuarios.save(new Usuario("Admin GameHub", "admin@gmail.com", encoder.encode("admin123"), "ADMIN"));
            usuarios.save(new Usuario("Cliente Gamer", "user@gmail.com", encoder.encode("user123"), "USER"));
        };
    }
}