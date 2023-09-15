package com.martikan.springtestdemo.mapper;

import com.martikan.springtestdemo.domain.Employee;
import com.martikan.springtestdemo.dto.EmployeeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeDTO toDTO(Employee entity);

    Employee toEntity(EmployeeDTO dto);
}
