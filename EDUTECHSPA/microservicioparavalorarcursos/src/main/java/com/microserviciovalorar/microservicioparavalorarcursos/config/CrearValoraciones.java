package com.microserviciovalorar.microservicioparavalorarcursos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microserviciovalorar.microservicioparavalorarcursos.model.ValorarModel;
import com.microserviciovalorar.microservicioparavalorarcursos.repository.ValorarRepository;
import com.microserviciovalorar.microservicioparavalorarcursos.service.ValorarService;

@Configuration
public class CrearValoraciones {

    @Autowired
    private ValorarService valorarService;

    @Bean
    CommandLineRunner initDataBase(ValorarRepository valorarRepository) {
        return args -> {
            if (valorarRepository.count() == 0) {
                ValorarModel valoracion1 = new ValorarModel();
                valoracion1.setReseñaUsuario("Excelente curso, muy bien explicado");
                valoracion1.setEstadoCompra("Carrito");
                valoracion1.setNombreCurso("Mecanica");
                valoracion1.setNombreProfesor("Juan");
                valorarService.guardarValoracion(valoracion1);
                
                ValorarModel valoracion2 = new ValorarModel();
                valoracion2.setReseñaUsuario("Buen contenido pero podría mejorar la práctica");
                valoracion2.setEstadoCompra("Comprado");
                valoracion2.setNombreCurso("Programacion Web");
                valoracion2.setNombreProfesor("Maria");
                valorarService.guardarValoracion(valoracion2);
            }
        };
    }
}
