package com.microserviciocursos.microservicioparajcrearcursos.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microserviciocursos.microservicioparajcrearcursos.controller.CursosController;
import com.microserviciocursos.microservicioparajcrearcursos.model.CursosModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CursosModelAssembler implements RepresentationModelAssembler<CursosModel, EntityModel<CursosModel>> {
    @Override
    @NonNull
    public EntityModel<CursosModel> toModel(@NonNull CursosModel curso) {
        return EntityModel.of(
            curso,
            linkTo(methodOn(CursosController.class).obtenerCursoPorId(curso.getIdCurso())).withSelfRel(),
            linkTo(methodOn(CursosController.class).obtenerCursos()).withRel("todos-los-cursos")
        );
    }
}
