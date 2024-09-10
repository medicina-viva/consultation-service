package com.medicinaviva.consultation.api.specialty;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medicinaviva.consultation.api.common.dto.Response;
import com.medicinaviva.consultation.api.specialty.dto.CreateSpecialtyRequest;
import com.medicinaviva.consultation.api.specialty.dto.ReadSpecialtyResponse;
import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.persistence.entity.Specialty;
import com.medicinaviva.consultation.service.contract.SpecialtyService;
import com.medicinaviva.consultation.utils.FuncUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Specialties")
@RestController
@RequestMapping("/specialties")
@RequiredArgsConstructor
public class SpecialtyController {
    private final SpecialtyService service;
    private final ModelMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Specialty")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns successful message."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "409", description = "Conflict."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> create(@RequestBody CreateSpecialtyRequest request) {
        Response response;
        String error = Validators.createValidator(request);
        if (error != null) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }

        try {
            Specialty specialty = this.mapper.map(request, Specialty.class);
            this.service.create(specialty);
            response = Response.builder().code(HttpStatus.CREATED.value()).message("Specialty Created.").build();
        } catch (BusinessException ex) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).build();
        } catch (ConflictException ex) {
            response = Response.builder().code(HttpStatus.CONFLICT.value()).message(ex.getMessage()).build();
        } catch (Exception ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected Error occurred")
                    .body(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "Exists Specialty by id")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> existsById(@PathVariable Long id) {
        Response response;
        try {
            boolean exists = this.service.existsById(id);
            response = Response
                    .builder()
                    .code(HttpStatus.OK.value())
                    .message("OK")
                    .body(exists).build();
        } catch (Exception ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected Error occurred")
                    .body(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/exists/all/{ids}")
    @Operation(summary = "Exists Specialties by ids")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> existAllById(@PathVariable("ids") List<Long> ids) {
        Response response;
        try {
            boolean exists = this.service.existAllById(ids);
            response = Response
                    .builder()
                    .code(HttpStatus.OK.value())
                    .message("OK")
                    .body(exists).build();
        } catch (Exception ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected Error occurred")
                    .body(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("")
    @Operation(summary = "List Specialties")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> realAll() {
        Response response;
        try {
            List<Specialty> specialties = this.service.readAll();
            specialties = FuncUtils.convertToSet(specialties);
            List<ReadSpecialtyResponse> rSResponse = specialties
                    .stream()
                    .map(item -> this.mapper.map(item, ReadSpecialtyResponse.class))
                    .toList();
            response = Response
                    .builder()
                    .code(HttpStatus.OK.value())
                    .message("OK")
                    .body(rSResponse).build();
        } catch (Exception ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected Error occurred")
                    .body(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

}
