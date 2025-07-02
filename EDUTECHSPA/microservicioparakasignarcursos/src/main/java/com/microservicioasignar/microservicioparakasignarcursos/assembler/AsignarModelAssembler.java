package com.microservicioasignar.microservicioparakasignarcursos.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microservicioasignar.microservicioparakasignarcursos.controller.AsignarController;
import com.microservicioasignar.microservicioparakasignarcursos.model.AsignarModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AsignarModelAssembler implements RepresentationModelAssembler<AsignarModel, EntityModel<AsignarModel>> {
    @Override
    @NonNull
    public EntityModel<AsignarModel> toModel(@NonNull AsignarModel asignacion) {
        return EntityModel.of(
            asignacion,
            linkTo(methodOn(AsignarController.class).obtenerAsignarPorId(asignacion.getIdAsignacion())).withSelfRel(),
            linkTo(methodOn(AsignarController.class).listarAsignacionesHateoas()).withRel("todas-las-asignaciones")
        );
    }
}
