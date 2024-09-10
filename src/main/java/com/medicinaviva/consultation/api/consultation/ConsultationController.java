package com.medicinaviva.consultation.api.consultation;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medicinaviva.consultation.api.common.dto.Response;
import com.medicinaviva.consultation.api.consultation.dto.CancelConsultationRequest;
import com.medicinaviva.consultation.api.consultation.dto.ReadConsultationResponse;
import com.medicinaviva.consultation.api.consultation.dto.ScheduleConsultationRequest;
import com.medicinaviva.consultation.api.consultation.dto.UpdateConsultationRequest;
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
    @PreAuthorize("hasRole('PATIENT')")
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
            Consultation consultation = Consultation
                    .builder()
                    .doctorId(request.getDoctorId())
                    .patientId(request.getPatientId())
                    .consultationDate(request.getConsultationDate())
                    .consultationTime(request.getConsultationTime())
                    .isTeleConsultation(request.isTeleConsultation())
                    .build();
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


    @GetMapping("/{id}")
    @Operation(summary = "Read Consultation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> read(@PathVariable("id") long id) {
        Response response;
        try {
            Consultation consultation = this.consultationService.read(id);
            ReadConsultationResponse readConsultationResponse = this.mapper.map(consultation, ReadConsultationResponse.class);
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").body(readConsultationResponse).build();
        } catch (NotFoundException ex) {
            response = Response.builder().code(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).build();
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

    @GetMapping("/doctors/{doctorId}")
    @Operation(summary = "Read doctor Consultations")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> readByDoctorId(@PathVariable("doctorId") String doctorId) {
        Response response;
        try {
            List<Consultation> consultations = this.consultationService.readByDoctorId(doctorId);
            List<ReadConsultationResponse> readConsultationResponses = new ArrayList<>();
            for (Consultation consultation : consultations) {
                ReadConsultationResponse readConsultationResponse = this.mapper.map(consultation, ReadConsultationResponse.class);
                readConsultationResponses.add(readConsultationResponse);
            }
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").body(readConsultationResponses).build();
        }  catch (Exception ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected Error occurred")
                    .body(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping("/patients/{patientId}")
    @Operation(summary = "Read patient Consultations")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> readByPatientId(@PathVariable("patientId") String patientId) {
        Response response;
        try {
            List<Consultation> consultations = this.consultationService.readByPatientId(patientId);
            List<ReadConsultationResponse> readConsultationResponses = new ArrayList<>();
            for (Consultation consultation : consultations) {
                ReadConsultationResponse readConsultationResponse = this.mapper.map(consultation, ReadConsultationResponse.class);
                readConsultationResponses.add(readConsultationResponse);
            }
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").body(readConsultationResponses).build();
        }  catch (Exception ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected Error occurred")
                    .body(ex.getMessage())
                    .build();
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm Consultation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> confirm(@PathVariable("id") long id) {
        Response response;
        try {
            this.consultationService.confirm(id);
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").build();
        } catch (NotFoundException ex) {
            response = Response.builder().code(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).build();
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

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel consultation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> cancel(@PathVariable("id") Long id, CancelConsultationRequest request) {
        Response response;
        String error = Validators.cancelConsultation(request);
        if (error != null) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }

        try {
            this.consultationService.cancel(id, request.getUserId(), request.getMotive());
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").build();
        } catch (BusinessException ex) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).build();
        } catch (NotFoundException ex) {
            response = Response.builder().code(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).build();
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

    @PutMapping("/{id}")
    @Operation(summary = "Update consultation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "409", description = "Conflict."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> update(@PathVariable("id") Long id, @RequestBody UpdateConsultationRequest request) {
        Response response;
        String error = Validators.updateConsultation(request);
        if (error != null) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }

        try {
            Consultation consultation = this.mapper.map(request, Consultation.class);
            consultation.setId(id);
            this.consultationService.update(consultation);
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").build();
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete consultation")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns successful message."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        Response response;
        try {
            this.consultationService.delete(id);
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").build();
        } catch (BusinessException ex) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).build();
        } catch (NotFoundException ex) {
            response = Response.builder().code(HttpStatus.NOT_FOUND.value()).message(ex.getMessage()).build();
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
