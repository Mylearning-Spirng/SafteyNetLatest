package com.openclassroom.safteynetalertsrefactor.controller;
import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.service.MedicalRecordsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecords")
public class MedicalRecordController {
    private final MedicalRecordsService medicalRecordsService;

    public MedicalRecordController(MedicalRecordsService medicalRecordsService) {
        this.medicalRecordsService = medicalRecordsService;
    }

    @GetMapping
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordsService.getAllMedicalRecords();
    }

    @PostMapping
    public MedicalRecord addMedicalRecords(@RequestBody MedicalRecord medicalRecord){
        return medicalRecordsService.addMedicalRecords(medicalRecord);
    }

    @PutMapping("/{lastName}/{firstName}")
    public ResponseEntity<Boolean> updateMedicalRecord(@PathVariable String firstName,
                                       @PathVariable String lastName,
                                       @RequestBody MedicalRecord updatedMedicalRecord) {
   boolean ok = medicalRecordsService.updateMedicalRecord(firstName, lastName, updatedMedicalRecord);
        return ok
        ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public ResponseEntity<Boolean> deleteMedicalRecord(@PathVariable String firstName,
                                              @PathVariable String lastName) {
        boolean deleted = medicalRecordsService.deleteMedicalRecord(firstName, lastName);
        return deleted
                ? ResponseEntity.ok(true) : ResponseEntity.notFound().build();
    }
}