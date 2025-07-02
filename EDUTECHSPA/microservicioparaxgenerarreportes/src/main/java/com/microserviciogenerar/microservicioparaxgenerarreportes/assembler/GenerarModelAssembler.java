package com.microserviciogenerar.microservicioparaxgenerarreportes.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

import com.microserviciogenerar.microservicioparaxgenerarreportes.controller.GenerarController;
import com.microserviciogenerar.microservicioparaxgenerarreportes.model.GenerarModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class GenerarModelAssembler implements RepresentationModelAssembler<GenerarModel, EntityModel<GenerarModel>> {
    @Override
    @NonNull
    public EntityModel<GenerarModel> toModel(@NonNull GenerarModel reporte) {
        return EntityModel.of(
            reporte,
            linkTo(methodOn(GenerarController.class).obtenerReportePorId(reporte.getIdReporte())).withSelfRel(),
            linkTo(methodOn(GenerarController.class).obtenerTodosLosReportes()).withRel("todos-los-reportes")
        );
    }
}
