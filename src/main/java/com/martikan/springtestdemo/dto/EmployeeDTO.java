package com.martikan.springtestdemo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EmployeeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8065406167659624095L;

    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Length(max = 50)
    private String firstName;

    @NotNull
    @Length(max = 100)
    private String lastName;

    @EqualsAndHashCode.Include
    @NotNull
    @Length(max = 255)
    @Email
    private String email;

    private boolean active;
}
