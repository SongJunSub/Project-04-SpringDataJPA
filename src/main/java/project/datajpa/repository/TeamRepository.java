package project.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.datajpa.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {



}