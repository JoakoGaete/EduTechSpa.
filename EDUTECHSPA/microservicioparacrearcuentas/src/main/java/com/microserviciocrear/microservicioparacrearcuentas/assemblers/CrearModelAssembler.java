package com.microserviciocrear.microservicioparacrearcuentas.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microserviciocrear.microservicioparacrearcuentas.controller.CrearController;
import com.microserviciocrear.microservicioparacrearcuentas.model.CrearModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CrearModelAssembler implements RepresentationModelAssembler<CrearModel, EntityModel<CrearModel>> {
    @Override
    @NonNull
    public EntityModel<CrearModel> toModel(@NonNull CrearModel crear) {
        return EntityModel.of(
            crear,
            linkTo(methodOn(CrearController.class).obtenerCrearPorId(crear.getIdUsuario())).withSelfRel(),
            linkTo(methodOn(CrearController.class).obtenerCrear()).withRel("todos-los-usuarios")
        );
    }
}

