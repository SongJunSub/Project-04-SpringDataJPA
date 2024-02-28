package project.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.datajpa.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {



}