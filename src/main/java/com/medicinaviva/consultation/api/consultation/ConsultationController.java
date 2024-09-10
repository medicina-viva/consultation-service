package com.medicinaviva.consultation.api.consultation;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medicinaviva.consultation.api.common.dto.Response;
import com.medicinaviva.consultation.api.consultation.dto.ScheduleConsultationRequest;
import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.model.exception.NotFoundException;
import com.medicinaviva.consultation.persistence.entity.Consultation;
import com.medicinaviva.consultation.service.contract.ConsultationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Consultations")
@RequestMapping("/consultations")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;
    private final ModelMapper mapper;

    @PostMapping
    @Operation(summary = "Schedule consultation")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns successful message."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "409", description = "Conflict."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> create(@RequestBody ScheduleConsultationRequest request) {
        Response response;
        String error = Validators.createValidator(request);
        if (error != null) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }

        try {
            Consultation consultation = this.mapper.map(request, Consultation.class);
            this.consultationService.create(consultation);
            response = Response.builder().code(HttpStatus.CREATED.value()).message("Consultation scheduled.").build();
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

    @PatchMapping("confirm/{id}")
    @Operation(summary = "Confirm consultation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "404", description = "Not found."),
            @ApiResponse(responseCode = "409", description = "Confict."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> confirm(@PathVariable("id") Long id) {
        Response response;
        try {
            this.consultationService.confirm(id);
            response = Response.builder().code(HttpStatus.CREATED.value()).message("Consultation confirmed.").build();
        } catch (BusinessException ex) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).build();
        } catch (NotFoundException ex) {
            response = Response.builder().code(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).build();
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
}
