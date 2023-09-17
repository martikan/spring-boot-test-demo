package com.martikan.springtestdemo.repository;

import com.github.javafaker.Faker;
import com.martikan.springtestdemo.SpringTestDemoApplicationIT;
import com.martikan.springtestdemo.domain.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeRepositoryIT extends SpringTestDemoApplicationIT {

    @Autowired
    private EmployeeRepository employeeRepository;

    private final Faker faker = new Faker();

    private Employee employee1;

    private Employee employee2;

    @BeforeEach
    void setup() {
        employee1 = new Employee();
        employee1.setFirstName(faker.name().firstName());
        employee1.setLastName(faker.name().lastName());
        employee1.setEmail(employee1.getLastName() + "@gmail.com");

        employee2 = new Employee();
        employee2.setFirstName(faker.name().firstName());
        employee2.setLastName(faker.name().lastName());
        employee2.setEmail(employee2.getLastName() + "@gmail.com");
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void whenSaveEmployee_thenReturnsEmployee() {
        // Arrange
        employee1.setActive(false);

        // Act
        final var savedEmployee = employeeRepository.save(employee1);

        // Assert
        assertNotNull(savedEmployee);
        assertEquals(employee1.getEmail(), savedEmployee.getEmail());
        assertEquals(employee1.getFirstName(), savedEmployee.getFirstName());
        assertEquals(employee1.getLastName(), savedEmployee.getLastName());
        assertEquals(employee1.getActive(), savedEmployee.getActive());
    }

    @Test
    void whenSaveEmployeeWithMissingActiveField_thenReturnsEmployeeWithDefaultActive() {
        // Arrange
        // Act
        final var savedEmployee = employeeRepository.save(employee1);

        // Assert
        assertNotNull(savedEmployee);
        assertTrue(employee1.getId() > 0);
        assertEquals(employee1.getEmail(), savedEmployee.getEmail());
        assertEquals(employee1.getFirstName(), savedEmployee.getFirstName());
        assertEquals(employee1.getLastName(), savedEmployee.getLastName());
        assertTrue(savedEmployee.getActive());
    }

    @Test
    void whenFindAllEmployees_thenReturnsEmployeesList() {
        // Arrange
        final var expectedList = new ArrayList<Employee>();
        expectedList.add(employee1);
        expectedList.add(employee2);
        employeeRepository.saveAllAndFlush(expectedList);

        // Act
        final var actualList = employeeRepository.findAll();

        // Assert
        assertEquals(2, actualList.size());
        assertEquals(expectedList.get(0).getEmail(), actualList.get(0).getEmail());
        assertEquals(expectedList.get(0).getLastName(), actualList.get(0).getLastName());
        assertEquals(expectedList.get(0).getFirstName(), actualList.get(0).getFirstName());
        assertEquals(expectedList.get(0).getActive(), actualList.get(0).getActive());
        assertEquals(expectedList.get(1).getEmail(), actualList.get(1).getEmail());
        assertEquals(expectedList.get(1).getLastName(), actualList.get(1).getLastName());
        assertEquals(expectedList.get(1).getFirstName(), actualList.get(1).getFirstName());
        assertEquals(expectedList.get(1).getActive(), actualList.get(1).getActive());
    }

    @Test
    void whenFindEmployeeById_thenReturnsEmployee() {
        // Arrange
        employeeRepository.save(employee1);

        // Act
        final var actualEmployee = employeeRepository.findById(employee1.getId());

        // Assert
        assertTrue(actualEmployee.isPresent());
        assertEquals(employee1.getEmail(), actualEmployee.get().getEmail());
        assertEquals(employee1.getFirstName(), actualEmployee.get().getFirstName());
        assertEquals(employee1.getLastName(), actualEmployee.get().getLastName());
        assertTrue(actualEmployee.get().getActive());
    }

    @Test
    void whenFindEmployeeByEmail_thenReturnsEmployee() {
        // Arrange
        employeeRepository.save(employee1);

        // Act
        final var actualEmployee = employeeRepository.findByEmail(employee1.getEmail());

        // Assert
        assertTrue(actualEmployee.isPresent());
        assertEquals(employee1.getEmail(), actualEmployee.get().getEmail());
        assertEquals(employee1.getFirstName(), actualEmployee.get().getFirstName());
        assertEquals(employee1.getLastName(), actualEmployee.get().getLastName());
        assertTrue(actualEmployee.get().getActive());
    }

    @Test
    void whenExistsEmployeeByEmail_thenReturnsBoolean() {
        // Arrange
        employeeRepository.save(employee1);

        // Act
        final var actualEmployee = employeeRepository.existsEmployeeByEmail(employee1.getEmail());

        // Assert
        assertTrue(actualEmployee);
    }

    @Test
    void whenUpdateEmployee_thenReturnsUpdatedEmployee() {
        // Arrange
        employeeRepository.save(employee1);
        final var expectedUpdatedFirstName = "first";
        final var expectedUpdatedLastName = "last";

        // Act
        final var savedEmployee = employeeRepository.findById(employee1.getId());
        savedEmployee.map(e -> { // TODO: Add checking for updated_at field as well
            e.setFirstName(expectedUpdatedFirstName);
            e.setLastName(expectedUpdatedLastName);
            return e;
        });
        final var updatedEmployee = employeeRepository.save(savedEmployee.orElseThrow(() -> new RuntimeException("Saved employee must be exists")));

        // Assert
        assertNotNull(updatedEmployee);
        assertEquals(employee1.getEmail(), updatedEmployee.getEmail());
        assertEquals(expectedUpdatedFirstName, updatedEmployee.getFirstName());
        assertEquals(expectedUpdatedLastName, updatedEmployee.getLastName());
        assertTrue(updatedEmployee.getActive());
    }

    @Test
    void whenDeleteEmployeeById_thenDeleteIt() {
        // Arrange
        employeeRepository.save(employee1);

        // Act
        employeeRepository.deleteById(employee1.getId());
        final var actualEmployee = employeeRepository.findById(employee1.getId());

        // Assert
        assertTrue(actualEmployee.isEmpty());
    }

    @Test
    void whenFindEmployeeByFirstNameAndLastName_thenReturnsEmployee() {
        // Arrange
        final var expectedFirstName = employee1.getFirstName();
        final var expectedLastName = employee1.getLastName();
        employeeRepository.save(employee1);

        // Act
        final var actualEmployee = employeeRepository.findEmployeeByFirstNameAndLastName(expectedFirstName, expectedLastName);

        // Assert
        assertTrue(actualEmployee.isPresent());
        assertEquals(employee1.getEmail(), actualEmployee.get().getEmail());
        assertEquals(expectedFirstName, actualEmployee.get().getFirstName());
        assertEquals(expectedLastName, actualEmployee.get().getLastName());
        assertTrue(actualEmployee.get().getActive());
    }

}
