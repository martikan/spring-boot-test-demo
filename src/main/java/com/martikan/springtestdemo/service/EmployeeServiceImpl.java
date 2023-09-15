package com.martikan.springtestdemo.service;

import com.martikan.springtestdemo.dto.EmployeeDTO;
import com.martikan.springtestdemo.exception.BadRequestException;
import com.martikan.springtestdemo.exception.ResourceNotFoundException;
import com.martikan.springtestdemo.mapper.EmployeeMapper;
import com.martikan.springtestdemo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper mapper;

    @Override
    public List<EmployeeDTO> getAllEmployees(final Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public EmployeeDTO getEmployeeById(final Long id) {
        return employeeRepository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with the given ID"));
    }

    @Override
    public EmployeeDTO updateEmployee(final EmployeeDTO dto) {
        // Check is employee exist
        getEmployeeById(dto.getId());
        return mapper.toDTO(employeeRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public EmployeeDTO saveEmployee(final EmployeeDTO dto) {
        if (employeeRepository.existsEmployeeByEmail(dto.getEmail())) {
            throw new BadRequestException("Employee already exist with the given email");
        }

        dto.setId(null);
        return mapper.toDTO(employeeRepository.save(mapper.toEntity(dto)));
    }
}
