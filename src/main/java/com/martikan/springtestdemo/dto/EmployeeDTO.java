package com.martikan.springtestdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class EmployeeDTO {

    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Length(max = 50)
    private String firstName;

    @NotBlank
    @Length(max = 100)
    private String lastName;

    @EqualsAndHashCode.Include
    @NotNull
    @Length(max = 255)
    @Email
    private String email;

    private boolean active;
}
