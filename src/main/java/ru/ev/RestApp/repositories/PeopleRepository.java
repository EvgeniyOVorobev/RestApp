package ru.ev.RestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ev.RestApp.models.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

}