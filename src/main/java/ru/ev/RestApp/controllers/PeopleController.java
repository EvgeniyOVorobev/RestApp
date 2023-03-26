package ru.ev.RestApp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.ev.RestApp.dto.PersonDTO;
import ru.ev.RestApp.models.Person;
import ru.ev.RestApp.services.PeopleService;
import ru.ev.RestApp.util.PersonErrorResponse;
import ru.ev.RestApp.util.PersonNotCreatetExc;
import ru.ev.RestApp.util.PersonNotFoundExeption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService,
                            ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper=modelMapper;
    }
    @GetMapping("/getall")
    public List<PersonDTO> getAllPerson(){
        return peopleService.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id){
        return convertToPersonDTO(peopleService.findOne(id));

    }
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundExeption exeption){
        PersonErrorResponse response=new PersonErrorResponse("person with this id wasn't found",System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatetExc exeption){
        PersonErrorResponse response=new PersonErrorResponse(exeption.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder error=new StringBuilder();
            List<FieldError> errors =bindingResult.getFieldErrors();
            for (FieldError error1:errors){
                error.append(error1.getField())
                        .append("-").append(error1.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotCreatetExc(error.toString());
        }
        peopleService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }
    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person,PersonDTO.class);
    }


}
