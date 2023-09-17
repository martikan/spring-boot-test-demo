package com.martikan.springtestdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.martikan.springtestdemo.apiConstant.Routes;
import com.martikan.springtestdemo.dto.EmployeeDTO;
import com.martikan.springtestdemo.exception.ResourceNotFoundException;
import com.martikan.springtestdemo.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.oneOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class EmployeeControllerTest {

    private final Faker faker = new Faker();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO employeeDTO1;

    private EmployeeDTO employeeDTO2;

    @BeforeEach
    public void setup() {
        employeeDTO1 = new EmployeeDTO();
        employeeDTO1.setId(1L);
        employeeDTO1.setFirstName(faker.name().firstName());
        employeeDTO1.setLastName(faker.name().lastName());
        employeeDTO1.setEmail(employeeDTO1.getLastName().toLowerCase().trim() + "@gmail.com");

        employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setId(2L);
        employeeDTO2.setFirstName(faker.name().firstName());
        employeeDTO2.setLastName(faker.name().lastName());
        employeeDTO2.setEmail(employeeDTO2.getLastName().toLowerCase().trim() + "@gmail.com");
    }

    @Test
    void whenSaveEmployee_thenReturnsSavedEmployeeWithStatusCREATED() throws Exception {
        // Arrange
        when(employeeService.saveEmployee(employeeDTO1)).thenAnswer((invocation -> invocation.getArgument(0)));

        // Act
        final var res = mockMvc.perform(post(Routes.EMPLOYEE_V1_PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDTO1)));

        // Assert
        res.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(employeeDTO1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employeeDTO1.getLastName())))
                .andExpect(jsonPath("$.email", is(employeeDTO1.getEmail())));
    }

    @Test
    void whenGetEmployees_thenReturnsListOfEmployeesWithStatusOK() throws Exception {
        // Arrange
        final var employeesList = new ArrayList<EmployeeDTO>();
        employeesList.add(employeeDTO1);
        employeesList.add(employeeDTO2);
        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(employeesList);

        // Act
        final var res = mockMvc.perform(get(Routes.EMPLOYEE_V1_PATH)
                .contentType(APPLICATION_JSON));

        // Assert
        res.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id",
                    everyItem(oneOf(employeeDTO1.getId().intValue(), employeeDTO2.getId().intValue()))))
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @DisplayName("Get employee by id API call - Happy flow")
    @Test
    void whenGetEmployeeById_thenReturnsEmployeeWithStatusOK() throws Exception {
        // Arrange
        final var employeeId = employeeDTO1.getId();
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeDTO1);

        // Act
        final var res = mockMvc.perform(get(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId));

        // Assert
        res.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employeeDTO1.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employeeDTO1.getLastName())))
                .andExpect(jsonPath("$.email", is(employeeDTO1.getEmail())));
    }

    @Test
    void whenGetEmployeeByIdWhenIdNotExists_thenThrowsResourceNotFoundException() throws Exception {
        // Arrange
        final var employeeId = 111L;
        doThrow(ResourceNotFoundException.class).when(employeeService).getEmployeeById(employeeId);

        // Act
        final var res = mockMvc.perform(get(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId));

        // Assert
        res.andExpect(status().isNotFound());
    }

    @DisplayName("Update employee by id API call - Happy flow")
    @Test
    void whenUpdateEmployeeById_thenReturnsUpdatedEmployeeWithStatusOK() throws Exception {
        // Arrange
        final var employeeId = employeeDTO1.getId();
        final var updatedEmployee = employeeDTO2;
        updatedEmployee.setId(employeeId);
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employeeDTO1);
        when(employeeService.updateEmployee(any(EmployeeDTO.class)))
                .thenAnswer((invocation -> invocation.getArgument(0)));

        // Act
        final var res = mockMvc.perform(put(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // Assert
        res.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedEmployee.getId().intValue())))
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

    @Test
    void whenUpdateEmployeeByIdWhenIdNotExists_thenThrowsResourceNotFoundException() throws Exception {
        // Arrange
        final var employeeId = employeeDTO1.getId();
        final var updatedEmployee = employeeDTO2;
        updatedEmployee.setId(employeeId);
        doThrow(ResourceNotFoundException.class).when(employeeService).updateEmployee(updatedEmployee);

        // Act
        final var res = mockMvc.perform(put(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // Assert
        res.andExpect(status().isNotFound());
    }

    @DisplayName("Delete employee by id API call - Happy flow")
    @Test
    void whenDeleteEmployeeById_thenReturnsStatusNO_CONTENT() throws Exception {
        // Arrange
        final var employeeId = 1L;
        doNothing().when(employeeService).deleteEmployee(employeeId);

        // Act
        final var res = mockMvc.perform(delete(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId));

        // Assert
        res.andExpect(status().isNoContent());
    }

}
