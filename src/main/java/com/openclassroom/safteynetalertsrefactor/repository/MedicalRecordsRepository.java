package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
/* Repository class for managing MedicalRecord data.
 * It provides methods to load, add, update, delete, and persist medical records.
 */
public class MedicalRecordsRepository {
    private static final String records = "medicalrecords";
    private final JSONFileReaderRepository JSONFileReaderRepository;

    private final List<MedicalRecord> medicalRecords = new ArrayList<>();

    public MedicalRecordsRepository(JSONFileReaderRepository JSONFileReaderRepository) {
        this.JSONFileReaderRepository = JSONFileReaderRepository;
        log.info("MedicalRecordsRepository created for resource: {}", records);
    }

    @PostConstruct
    void init() {
        log.info("Initializing MedicalRecordsRepository from resource: {}", records);
        List<MedicalRecord> loaded = JSONFileReaderRepository.readList(records, MedicalRecord.class);
        if (loaded != null) {
            medicalRecords.addAll(loaded);
            log.info("Loaded {} medical records", loaded.size());
        } else {
            log.warn("No medical records loaded from resource: {}", records);
        }
    }

    /* Retrieves all medical record records. */
    public List<MedicalRecord> findAll() {
        log.debug("findAll called, returning {} records", medicalRecords.size());
        return new ArrayList<>(medicalRecords);
    }

    /* Adds a new medical record and persists the change. */
    public void add(MedicalRecord newMedicalRecords) {
        log.info("Adding medical record for {} {}", newMedicalRecords.getFirstName(), newMedicalRecords.getLastName());
        medicalRecords.add(0, newMedicalRecords);
        persist();
    }

    /* Finds a medical record by first and last name. */
    public Optional<MedicalRecord> findByName(String firstName, String lastName) {
        log.debug("Searching for medical record: {} {}", firstName, lastName);
        for (MedicalRecord mr : medicalRecords) {
            if (mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName)) {
                log.debug("Found medical record for {} {}", firstName, lastName);
                return Optional.of(mr);
            }
        }
        log.debug("No medical record found for {} {}", firstName, lastName);
        return Optional.empty();
    }

    /* Updates an existing medical record identified by first and last name. */
    public boolean updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord) {
        log.info("Updating medical record for {} {}", firstName, lastName);
        Optional<MedicalRecord> medicalRecordToUpdate = findByName(firstName, lastName);
        if (medicalRecordToUpdate.isEmpty()) {
            log.warn("Cannot update - medical record not found for {} {}", firstName, lastName);
            return false;
        }
        MedicalRecord existingRecord = medicalRecordToUpdate.get();
        existingRecord.setBirthdate(updatedMedicalRecord.getBirthdate());
        existingRecord.setMedications(updatedMedicalRecord.getMedications());
        existingRecord.setAllergies(updatedMedicalRecord.getAllergies());
        persist();
        log.info("Updated medical record for {} {}", firstName, lastName);
        return true;
    }

    /* Deletes a medical record identified by first and last name. */
    public boolean deleteByName(String firstName, String lastName) {
        log.info("Deleting medical record for {} {}", firstName, lastName);
        Optional<MedicalRecord> medicalRecordToDelete = findByName(firstName, lastName);
        if (medicalRecordToDelete.isEmpty()) {
            log.warn("Cannot delete - medical record not found for {} {}", firstName, lastName);
            return false;
        }
        medicalRecords.remove(medicalRecordToDelete.get());
        persist();
        log.info("Deleted medical record for {} {}", firstName, lastName);
        return true;
    }

    public void persist() {
        log.debug("Persisting {} medical records to resource: {}", medicalRecords.size(), records);
        JSONFileReaderRepository.writeList(records, medicalRecords);
    }
}
