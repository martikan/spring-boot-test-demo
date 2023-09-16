package com.martikan.springtestdemo.service;

import com.github.javafaker.Faker;
import com.martikan.springtestdemo.domain.Employee;
import com.martikan.springtestdemo.dto.EmployeeDTO;
import com.martikan.springtestdemo.exception.BadRequestException;
import com.martikan.springtestdemo.exception.ResourceNotFoundException;
import com.martikan.springtestdemo.mapper.EmployeeMapper;
import com.martikan.springtestdemo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper mapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;

    private EmployeeDTO employee1DTO;

    @BeforeEach
    public void setup() {
        employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName(faker.name().firstName());
        employee1.setLastName(faker.name().lastName());
        employee1.setEmail(employee1.getLastName() + "@gmail.com");

        employee1DTO = EmployeeDTO.builder()
                .id(employee1.getId())
                .firstName(employee1.getFirstName())
                .lastName(employee1.getLastName())
                .email(employee1.getEmail())
                .build();
    }

    @Test
    void whenGetAllEmployees_thenReturnsListOfEmployeeDTOs() {
        // Arrange
        final var pageable = Pageable.ofSize(20);
        final var employeesList = Collections.singletonList(employee1);
        final var employeesPage = new PageImpl<>(employeesList);
        when(employeeRepository.findAll(pageable)).thenReturn(employeesPage);
        when(mapper.toDTO(any(Employee.class))).thenReturn(employee1DTO);

        // Act
        final var actualEmployeesList = employeeService.getAllEmployees(pageable);

        // Assert
        assertEquals(1, actualEmployeesList.size());
        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
        verify(mapper, times(1)).toDTO(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository, mapper);
    }

    @DisplayName("Saving employee service call - Happy flow")
    @Test
    void whenSaveEmployee_thenReturnsEmployeeDTO() {
        // Arrange
        when(employeeRepository.existsEmployeeByEmail(employee1.getEmail())).thenReturn(false);
        when(mapper.toEntity(employee1DTO)).thenReturn(employee1);
        when(employeeRepository.save(employee1)).thenReturn(employee1);
        when(mapper.toDTO(employee1)).thenReturn(employee1DTO);

        // Act
        final var savedEmployee = employeeService.saveEmployee(employee1DTO);

        // Assert
        assertNotNull(savedEmployee);
        verify(employeeRepository, times(1)).existsEmployeeByEmail(anyString());
        verify(mapper, times(1)).toEntity(any(EmployeeDTO.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(mapper, times(1)).toDTO(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository, mapper);
    }

    @Test
    void whenSaveEmployeeWhichAlreadyExists_thenThrowsBadRequestException() {
        // Arrange
        when(employeeRepository.existsEmployeeByEmail(employee1DTO.getEmail())).thenReturn(true);

        // Act
        assertThrows(BadRequestException.class, () -> employeeService.saveEmployee(employee1DTO));

        // Assert
        verify(employeeRepository, times(1)).existsEmployeeByEmail(anyString());
        verify(employeeRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository);
    }

    @DisplayName("Get employee by id service call - Happy flow")
    @Test
    void whenGetEmployeeById_thenReturnsEmployeeDTO() {
        // Arrange
        when(employeeRepository.findById(employee1DTO.getId())).thenReturn(Optional.of(employee1));
        when(mapper.toDTO(any(Employee.class))).thenReturn(employee1DTO);

        // Act
        final var actualEmployeeDTO = employeeService.getEmployeeById(employee1DTO.getId());

        // Assert
        assertNotNull(actualEmployeeDTO);
        verify(employeeRepository, times(1)).findById(any(Long.class));
        verify(mapper, times(1)).toDTO(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository, mapper);
    }

    @Test
    void whenGetEmployeeByIdWhichNotExists_thenThrowsResourceNotFoundException() {
        // Arrange
        when(employeeRepository.findById(employee1DTO.getId())).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(employee1DTO.getId()));

        // Assert
        verify(employeeRepository, times(1)).findById(any(Long.class));
        verify(mapper, never()).toDTO(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository, mapper);
    }

    @DisplayName("Update employee service call - Happy flow")
    @Test
    void whenUpdateEmployee_thenReturnsUpdatedEmployeeDTO() {
        // Arrange
        when(employeeRepository.findById(employee1DTO.getId())).thenReturn(Optional.of(employee1));
        when(mapper.toDTO(any(Employee.class))).thenReturn(employee1DTO);
        when(mapper.toEntity(employee1DTO)).thenReturn(employee1);
        final var updatedEmail = "test";
        employee1.setEmail(updatedEmail);
        employee1DTO.setEmail(updatedEmail);
        when(employeeRepository.save(employee1)).thenReturn(employee1);
        when(mapper.toDTO(employee1)).thenReturn(employee1DTO);

        // Act
        final var updatedEmployee = employeeService.updateEmployee(employee1DTO);

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(updatedEmail, updatedEmployee.getEmail());
        verify(employeeRepository, times(1)).findById(employee1DTO.getId());
        verify(mapper, times(1)).toEntity(any(EmployeeDTO.class));
        verify(mapper, times(2)).toDTO(any(Employee.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository, mapper);
    }

    @Test
    void whenUpdateEmployeeWhichNotExists_thenThrowsResourceNotFoundException() {
        // Arrange
        when(employeeRepository.findById(employee1DTO.getId())).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(employee1DTO));

        // Assert
        verify(employeeRepository, times(1)).findById(any(Long.class));
        verify(mapper, never()).toDTO(any(Employee.class));
        verify(employeeRepository, never()).save(any());
        verifyNoMoreInteractions(employeeRepository, mapper);
    }

    @DisplayName("Delete employee by id service call - Happy flow")
    @Test
    void whenDeleteEmployeeById_thenNothing() {
        // Arrange
        final var employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee1));
        when(mapper.toDTO(any(Employee.class))).thenReturn(employee1DTO);
        doNothing().when(employeeRepository).deleteById(employeeId);

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository, times(1)).deleteById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void whenDeleteEmployeeByIdAndEmployeeNotFound_thenThrowsResourceNotFoundException() {
        // Arrange
        final var employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act
        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(employeeId));

        // Assert
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).deleteById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
    }

}
