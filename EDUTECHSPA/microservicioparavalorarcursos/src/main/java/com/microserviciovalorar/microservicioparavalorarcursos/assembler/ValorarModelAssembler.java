package com.microserviciovalorar.microservicioparavalorarcursos.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microserviciovalorar.microservicioparavalorarcursos.controller.ValorarController;
import com.microserviciovalorar.microservicioparavalorarcursos.model.ValorarModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ValorarModelAssembler implements RepresentationModelAssembler<ValorarModel, EntityModel<ValorarModel>> {
    @Override
    @NonNull
    public EntityModel<ValorarModel> toModel(@NonNull ValorarModel valoracion) {
        return EntityModel.of(
            valoracion,
            linkTo(methodOn(ValorarController.class).obtenerValoracionPorId(valoracion.getIdValoracion())).withSelfRel(),
            linkTo(methodOn(ValorarController.class).obtenerValoraciones()).withRel("todas-las-valoraciones")
        );
    }
}
