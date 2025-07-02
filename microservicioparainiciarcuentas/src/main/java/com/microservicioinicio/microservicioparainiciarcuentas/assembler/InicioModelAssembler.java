package com.microservicioinicio.microservicioparainiciarcuentas.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microservicioinicio.microservicioparainiciarcuentas.controller.InicioController;
import com.microservicioinicio.microservicioparainiciarcuentas.model.InicioModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class InicioModelAssembler implements RepresentationModelAssembler<InicioModel, EntityModel<InicioModel>> {
    @Override
    @NonNull
    public EntityModel<InicioModel> toModel(@NonNull InicioModel inicio) {
        return EntityModel.of(
            inicio,
            linkTo(methodOn(InicioController.class).obtenerInicioId(inicio.getId())).withSelfRel(),
            linkTo(methodOn(InicioController.class).obtenerInicio()).withRel("todos-los-usuarios-logueados")
        );
    }
} 
