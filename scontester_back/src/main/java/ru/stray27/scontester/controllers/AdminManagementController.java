package ru.stray27.scontester.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stray27.scontester.dto.AttemptDto;
import ru.stray27.scontester.dto.SenderDto;
import ru.stray27.scontester.dto.TaskDto;
import ru.stray27.scontester.entities.Attempt;
import ru.stray27.scontester.entities.Sender;
import ru.stray27.scontester.entities.Task;
import ru.stray27.scontester.entities.Test;
import ru.stray27.scontester.repositories.AttemptRepository;
import ru.stray27.scontester.repositories.SenderRepository;
import ru.stray27.scontester.repositories.TaskRepository;
import ru.stray27.scontester.repositories.TestRepository;
import ru.stray27.scontester.services.mappers.EntityConverter;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/api/")
public class AdminManagementController {

    @Autowired
    private SenderRepository senderRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AttemptRepository attemptRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    @Qualifier("taskConverter")
    private EntityConverter<Task, TaskDto> taskConverter;
    @Autowired
    @Qualifier("senderConverter")
    private EntityConverter<Sender, SenderDto> senderConverter;
    @Autowired
    @Qualifier("attemptConverter")
    private EntityConverter<Attempt, AttemptDto> attemptConverter;

    @GetMapping(value = "login")
    public ResponseEntity<?> login() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "deleteSender")
    public ResponseEntity<?> deleteSender(@RequestBody SenderDto dto) {
        try {
            Sender sender = senderRepository.findByUID(dto.getUID()).orElseThrow();
            senderRepository.deleteByUID(sender.getUID());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "getSenders")
    public ResponseEntity<?> getSenders() {
        try {
            Iterable<Sender> senders = senderRepository.findAll();
            List<SenderDto> dtos = new ArrayList<>();
            for (Sender sender: senders) {
                dtos.add(senderConverter.convertToDto(sender));
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "createSender")
    public ResponseEntity<?> createSender(@RequestBody SenderDto senderDto) {
        try {
            senderRepository.save(senderConverter.convertToEntity(senderDto));
            return new ResponseEntity<>(senderDto, HttpStatus.OK);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }


    @PostMapping(value = "createTask")
    public ResponseEntity<?> createTask(@RequestBody TaskDto taskInput) {
        try {
            Task task = new Task();
            task.setTitle(taskInput.getTitle());
            task.setDescription(taskInput.getDescription());
            task.setInputType(taskInput.getInputType());
            task.setTimeLimit(taskInput.getTimeLimit());
            task.setMemoryLimit(taskInput.getMemoryLimit());
            taskRepository.save(task);
            for (Test inputTest : taskInput.getTests()) {
                Test test = new Test();
                test.setTask(task);
                test.setInput(inputTest.getInput());
                test.setOutput(inputTest.getOutput());
                testRepository.save(test);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "deleteTask")
    public ResponseEntity<TaskDto> deleteTask(@RequestBody TaskDto dto) {
        try {
            Long id = dto.getId();
            taskRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "getAttempts")
    public ResponseEntity<Iterable<AttemptDto>> getAllAttempts() {
        List<Attempt> attempts = (List<Attempt>) attemptRepository.findAllOrderById().orElseThrow();
        List<AttemptDto> outputAttempts = new ArrayList<>();
        for (Attempt attempt : attempts) {
            outputAttempts.add(attemptConverter.convertToDto(attempt));
        }
        return new ResponseEntity<>(outputAttempts, HttpStatus.OK);
    }
}
