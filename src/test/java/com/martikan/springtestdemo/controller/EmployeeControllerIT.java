package com.martikan.springtestdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.martikan.springtestdemo.SpringTestDemoApplicationIT;
import com.martikan.springtestdemo.apiConstant.Routes;
import com.martikan.springtestdemo.domain.Employee;
import com.martikan.springtestdemo.dto.EmployeeDTO;
import com.martikan.springtestdemo.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.oneOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeControllerIT extends SpringTestDemoApplicationIT {

    private final Faker faker = new Faker();

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee1;

    private Employee employee2;

    private EmployeeDTO employeeDTO1;

    private EmployeeDTO employeeDTO2;

    @BeforeEach
    void setup() {
        employee1 = new Employee();
        employee1.setFirstName(faker.name().firstName());
        employee1.setLastName(faker.name().lastName());
        employee1.setEmail(employee1.getLastName().toLowerCase().trim() + "@gmail.com");

        employee2 = new Employee();
        employee2.setFirstName(faker.name().firstName());
        employee2.setLastName(faker.name().lastName());
        employee2.setEmail(employee2.getLastName().toLowerCase().trim() + "@gmail.com");

        employeeDTO1 = new EmployeeDTO();
        employeeDTO1.setFirstName(employee1.getFirstName());
        employeeDTO1.setLastName(employee1.getLastName());
        employeeDTO1.setEmail(employee1.getEmail());

        employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setFirstName(employee2.getFirstName());
        employeeDTO2.setLastName(employee2.getLastName());
        employeeDTO2.setEmail(employee2.getEmail());
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
        employeeRepository.flush();
    }

    @Test
    void whenGetEmployees_thenReturnsListOfEmployeesWithStatusOK() throws Exception {
        // Arrange
        final var employeesForSave = new ArrayList<Employee>();
        employeesForSave.add(employee1);
        employeesForSave.add(employee2);
        employeeRepository.saveAllAndFlush(employeesForSave);

        // Act
        final var res = mockMvc.perform(get(Routes.EMPLOYEE_V1_PATH)
                .contentType(APPLICATION_JSON));

        // Assert
        res.andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email",
                        everyItem(oneOf(employeeDTO1.getEmail(), employeeDTO2.getEmail()))))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenGetEmployeeById_thenReturnsEmployeeWithStatusOK() throws Exception {
        // Arrange
        final var savedEmployee = employeeRepository.saveAndFlush(employee1);
        final var employeeId = savedEmployee.getId();

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

        // Act
        final var res = mockMvc.perform(get(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId));

        // Assert
        res.andExpect(status().isNotFound());
    }

    @Test
    void whenUpdateEmployeeById_thenReturnsUpdatedEmployeeWithStatusOK() throws Exception {
        // Arrange
        final var savedEmployee = employeeRepository.saveAndFlush(employee1);
        final var employeeId = savedEmployee.getId();
        final var updatedEmployee = employeeDTO2;
        updatedEmployee.setId(employeeId);

        // Act
        final var res = mockMvc.perform(put(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // Assert
        res.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeeId.intValue())))
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

        // Act
        final var res = mockMvc.perform(put(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        // Assert
        res.andExpect(status().isNotFound());
    }

    @Test
    void whenDeleteEmployeeById_thenReturnsStatusNO_CONTENT() throws Exception {
        // Arrange
        final var savedEmployee = employeeRepository.saveAndFlush(employee1);
        final var employeeId = savedEmployee.getId();

        // Act
        final var res = mockMvc.perform(delete(Routes.EMPLOYEE_V1_PATH + "/{id}", employeeId));

        // Assert
        res.andExpect(status().isNoContent());
    }

    @Test
    void whenSaveEmployee_thenReturnsSavedEmployeeWithStatusCREATED() throws Exception {
        // Arrange
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

}
