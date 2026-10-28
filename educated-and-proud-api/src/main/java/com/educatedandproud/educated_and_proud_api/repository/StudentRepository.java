package com.educatedandproud.educated_and_proud_api.repository;

import com.educatedandproud.educated_and_proud_api.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
