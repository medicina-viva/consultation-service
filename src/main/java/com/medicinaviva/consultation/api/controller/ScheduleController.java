package com.medicinaviva.consultation.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.medicinaviva.consultation.api.dto.CreateScheduleRequest;
import com.medicinaviva.consultation.api.dto.ReadScheduleResponse;
import com.medicinaviva.consultation.api.dto.Response;
import com.medicinaviva.consultation.api.dto.UpdateScheduleRequest;
import com.medicinaviva.consultation.model.exception.BusinessException;
import com.medicinaviva.consultation.model.exception.ConflictException;
import com.medicinaviva.consultation.model.exception.NotFoundException;
import com.medicinaviva.consultation.persistence.entity.Schedule;
import com.medicinaviva.consultation.service.contract.ScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Schedules")
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ModelMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Create Schedule")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Returns successful message."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "409", description = "Conflict."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> create(@RequestBody CreateScheduleRequest request) {
        Response response;
        String error = ScheduleControllerValidators.createValidator(request);
        if (error != null) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }

        try {
            Schedule schedule = this.mapper.map(request, Schedule.class);
            this.scheduleService.create(schedule);
            response = Response.builder().code(HttpStatus.CREATED.value()).message("Schedule Created.").build();
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
    @Operation(summary = "Get Schedule")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns schedule."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> read(@PathVariable("id") long id) {
        Response response;
        try {
            Schedule schedule = this.scheduleService.read(id);
            ReadScheduleResponse readScheduleResponse = this.mapper.map(schedule, ReadScheduleResponse.class);
            response = Response.builder().code(HttpStatus.OK.value()).body(readScheduleResponse).build();
        } catch (NotFoundException ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(ex.getMessage())
                    .build();
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

    @GetMapping("{doctorId}/list")
    @Operation(summary = "Get Schedules by doctor id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns schedules."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> readByDoctorId(@PathVariable("doctorId") String doctorId) {
        Response response;
        try {
            List<Schedule> schedules = this.scheduleService.readByDoctorId(doctorId);
            List<ReadScheduleResponse> readScheduleResponses = new ArrayList<>();
            for (Schedule schedule : schedules) {
                ReadScheduleResponse readScheduleResponse = this.mapper.map(schedule, ReadScheduleResponse.class);
                readScheduleResponses.add(readScheduleResponse);
            }
            response = Response.builder().code(HttpStatus.OK.value()).body(readScheduleResponses).build();
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

    @GetMapping
    @Operation(summary = "Get Schedules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns schedules."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> readAll() {
        Response response;
        try {
            List<Schedule> schedules = this.scheduleService.readAll();
            List<ReadScheduleResponse> readScheduleResponses = new ArrayList<>();
            for (Schedule schedule : schedules) {
                ReadScheduleResponse readScheduleResponse = this.mapper.map(schedule, ReadScheduleResponse.class);
                readScheduleResponses.add(readScheduleResponse);
            }
            response = Response.builder().code(HttpStatus.OK.value()).body(readScheduleResponses).build();
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
    @Operation(summary = "Update Schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns schedule."),
            @ApiResponse(responseCode = "400", description = "Bad request happened."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "409", description = "Conflict."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> update(@PathVariable("id") long id, @RequestBody UpdateScheduleRequest request) {
        Response response;
        String error = ScheduleControllerValidators.updateValidator(request);
        if (error != null) {
            response = Response.builder().code(HttpStatus.BAD_REQUEST.value()).message(error).build();
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
        }

        try {
            Schedule schedule = this.mapper.map(request, Schedule.class);
            schedule.setId(id);
            this.scheduleService.update(schedule);
            response = Response.builder().code(HttpStatus.OK.value()).body("OK").build();
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
    @Operation(summary = "Delete Schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns schedule."),
            @ApiResponse(responseCode = "404", description = "Resource not found."),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred."),
    })
    public ResponseEntity<Response> delete(@PathVariable("id") long id) {
        Response response;
        try {
            this.scheduleService.delete(id);
            response = Response.builder().code(HttpStatus.OK.value()).message("OK").build();
        } catch (NotFoundException ex) {
            response = Response
                    .builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(ex.getMessage())
                    .build();
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
