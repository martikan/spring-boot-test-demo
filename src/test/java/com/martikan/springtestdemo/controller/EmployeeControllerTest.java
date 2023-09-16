package com.martikan.springtestdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.martikan.springtestdemo.apiConstant.Routes;
import com.martikan.springtestdemo.dto.EmployeeDTO;
import com.martikan.springtestdemo.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.oneOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void whenSaveEmployee_thenReturnsSavedEmployeeWithStatusCREATED() throws Exception {
        // Arrange
        final var employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName(faker.name().firstName());
        employeeDTO.setLastName(faker.name().lastName());
        employeeDTO.setEmail(employeeDTO.getLastName().toLowerCase().trim() + "@gmail.com");
        when(employeeService.saveEmployee(employeeDTO)).thenAnswer((invocation -> invocation.getArgument(0)));

        // Act
        final var res = mockMvc.perform(post(Routes.EMPLOYEE_V1_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDTO)));

        // Assert
        res.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(employeeDTO.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employeeDTO.getLastName())))
                .andExpect(jsonPath("$.email", is(employeeDTO.getEmail())));
    }

    @Test
    void whenGetEmployees_thenReturnsListOfEmployeesWithStatusOK() throws Exception {
        // Arrange
        final var employeeDTO1 = new EmployeeDTO();
        employeeDTO1.setId(1L);
        employeeDTO1.setFirstName(faker.name().firstName());
        employeeDTO1.setLastName(faker.name().lastName());
        employeeDTO1.setEmail(employeeDTO1.getLastName().toLowerCase().trim() + "@gmail.com");
        final var employeeDTO2 = new EmployeeDTO();
        employeeDTO2.setId(2L);
        employeeDTO2.setFirstName(faker.name().firstName());
        employeeDTO2.setLastName(faker.name().lastName());
        employeeDTO2.setEmail(employeeDTO2.getLastName().toLowerCase().trim() + "@gmail.com");
        final var employeesList = new ArrayList<EmployeeDTO>();
        employeesList.add(employeeDTO1);
        employeesList.add(employeeDTO2);
        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(employeesList);

        // Act
        final var res = mockMvc.perform(get(Routes.EMPLOYEE_V1_PATH)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        res.andExpect(status().isOk())
            .andExpect(jsonPath("$[*].id",
                    everyItem(oneOf(employeeDTO1.getId().intValue(), employeeDTO2.getId().intValue()))))
            .andExpect(jsonPath("$", hasSize(2)));
    }

}
