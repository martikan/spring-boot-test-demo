package com.martikan.springtestdemo.repository;

import com.martikan.springtestdemo.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(final String email);

    boolean existsEmployeeByEmail(final String email);

    @Query("from Employee e where e.firstName = :firstName and e.lastName = :lastName")
    Optional<Employee> findEmployeeByFirstNameAndLastName(@Param("firstName") final String firstName,
                                                          @Param("lastName") final String lastName);
}
