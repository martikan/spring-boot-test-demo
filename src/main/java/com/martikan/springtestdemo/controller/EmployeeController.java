package com.martikan.springtestdemo.controller;

import com.martikan.springtestdemo.apiConstant.Routes;
import com.martikan.springtestdemo.dto.EmployeeDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping(Routes.EMPLOYEE_V1_PATH)
@RestController
public class EmployeeController {

    @PostMapping
    public ResponseEntity<EmployeeDTO> saveEmployee(@Valid @RequestBody EmployeeDTO dto) {
        return null;
    }

}
