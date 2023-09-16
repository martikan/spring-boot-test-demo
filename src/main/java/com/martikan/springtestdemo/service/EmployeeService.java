package com.martikan.springtestdemo.service;

import com.martikan.springtestdemo.dto.EmployeeDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees(final Pageable pageable);

    EmployeeDTO getEmployeeById(final Long id);

    EmployeeDTO updateEmployee(final EmployeeDTO dto);

    EmployeeDTO saveEmployee(final EmployeeDTO dto);

    void deleteEmployee(final Long id);
}
