package com.openclassroom.safteynetalertsrefactor.service;

import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import com.openclassroom.safteynetalertsrefactor.repository.MedicalRecordsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MedicalRecordsService {
    private final MedicalRecordsRepository medicalRecordsRepository;

    public MedicalRecordsService(MedicalRecordsRepository medicalRecordsRepository) {
        this.medicalRecordsRepository = medicalRecordsRepository;
    }

    /**
     * Retrieve all medical records.
     *
     * @return List of all MedicalRecord entities.
     */
    public List<MedicalRecord> getAllMedicalRecords() {
        log.info("GET /medicalRecords - request received");
        try {
            List<MedicalRecord> records = medicalRecordsRepository.findAll();
            log.debug("GET /medicalRecords - returning {} records", records == null ? 0 : records.size());
            return records;
        } catch (Exception e) {
            log.error("Error fetching medical records", e);
            throw e;
        }
    }

    /**
     * Add a new medical record.
     *
     * @param medicalRecord MedicalRecord entity to add.
     * @return The added MedicalRecord entity.
     */
    public MedicalRecord addMedicalRecords(MedicalRecord medicalRecord) {
        log.info("POST /medicalRecords - add requested for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
        try {
            medicalRecordsRepository.add(medicalRecord);
            log.debug("POST /medicalRecords - added record for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return medicalRecord;
        } catch (Exception e) {
            log.error("Error adding medical record for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName(), e);
            throw e;
        }
    }

    /**
     * Update an existing medical record.
     *
     * @param firstName            First name of the medical record to update.
     * @param lastName             Last name of the medical record to update.
     * @param updatedMedicalRecord MedicalRecord entity with updated data.
     * @return true if update was successful, false if not found.
     */
    public boolean updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord) {
        log.info("PUT /medicalRecords - update requested for {} {}", firstName, lastName);
        try {
            boolean updated = medicalRecordsRepository.updateMedicalRecord(firstName, lastName, updatedMedicalRecord);
            if (updated) {
                log.info("PUT /medicalRecords - update successful for {} {}", firstName, lastName);
            } else {
                log.warn("PUT /medicalRecords - not found for {} {}", firstName, lastName);
            }
            return updated;
        } catch (Exception e) {
            log.error("Error updating medical record for {} {}", firstName, lastName, e);
            throw e;
        }
    }

    /**
     * Delete a medical record by first and last name.
     *
     * @param firstName First name of the medical record to delete.
     * @param lastName  Last name of the medical record to delete.
     * @return true if deletion was successful, false if not found.
     */
    public boolean deleteMedicalRecord(String firstName, String lastName) {
        log.info("DELETE /medicalRecords - delete requested for {} {}", firstName, lastName);
        try {
            boolean deleted = medicalRecordsRepository.deleteByName(firstName, lastName);
            if (deleted) {
                log.info("DELETE /medicalRecords - delete successful for {} {}", firstName, lastName);
            } else {
                log.warn("DELETE /medicalRecords - not found for {} {}", firstName, lastName);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Error deleting medical record for {} {}", firstName, lastName, e);
            throw e;
        }
    }
}
