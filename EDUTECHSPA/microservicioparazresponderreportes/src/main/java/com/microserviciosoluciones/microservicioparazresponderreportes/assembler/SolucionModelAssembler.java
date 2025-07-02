package com.microserviciosoluciones.microservicioparazresponderreportes.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microserviciosoluciones.microservicioparazresponderreportes.controller.SolucionController;
import com.microserviciosoluciones.microservicioparazresponderreportes.model.SolucionModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SolucionModelAssembler implements RepresentationModelAssembler<SolucionModel, EntityModel<SolucionModel>> {
    @Override
    @NonNull
    public EntityModel<SolucionModel> toModel(@NonNull SolucionModel solucion) {
        return EntityModel.of(
            solucion,
            linkTo(methodOn(SolucionController.class).obtenerSolucionPorId(solucion.getIdSolucion())).withSelfRel(),
            linkTo(methodOn(SolucionController.class).obtenerTodasLasSoluciones()).withRel("todas-las-soluciones")
        );
    }
}
