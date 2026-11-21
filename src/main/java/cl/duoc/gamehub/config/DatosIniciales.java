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

    @Bean
    CommandLineRunner cargarProductosIniciales(ProductoRepositorio productoRepositorio) {
        return args -> {
            if (productoRepositorio.count() > 0) {
                return;
            }

            List<Producto> iniciales = List.of(
                    crear("Procesador AMD Ryzen 5 5600G", 129990L, 8, "Procesadores", "AMD", "/amd5600g.png"),
                    crear("Laptop Gamer ASUS TUF Gaming", 799990L, 3, "Notebooks", "ASUS", "/Asus_Laptop.png"),
                    crear("Tarjeta de Video RTX 4060 Edición Miku", 459990L, 4, "Tarjetas Gráficas", "NVIDIA", "/grafica_miku.png"),
                    crear("Procesador AMD Ryzen 7 5700X", 189990L, 6, "Procesadores", "AMD", "/Ryzen7.jpg"),
                    crear("Procesador AMD Ryzen 9 9900X", 499990L, 0, "Procesadores", "AMD", "/Ryzen9.jpg")
            );

            productoRepositorio.saveAll(iniciales);
        };
    }

    private Producto crear(String nombre, Long precio, Integer stock, String categoria, String marca, String imagen) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setCategoria(categoria);
        p.setMarca(marca);
        p.setImagen(imagen);
        return p;
    }
}