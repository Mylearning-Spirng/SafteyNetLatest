package com.openclassroom.safteynetalertsrefactor.repository;

import com.openclassroom.safteynetalertsrefactor.model.MedicalRecord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicalRecordsRepository {
    private static final String records = "medicalrecords";
    private final JSONFileReaderRepository JSONFileReaderRepository;

    private final List<MedicalRecord> medicalRecords = new ArrayList<>();

    public MedicalRecordsRepository(JSONFileReaderRepository JSONFileReaderRepository) {
        this.JSONFileReaderRepository = JSONFileReaderRepository;
    }

    @PostConstruct
    void init() {
        List<MedicalRecord> loaded = JSONFileReaderRepository.readList(records, MedicalRecord.class);
        if (loaded != null) {
            medicalRecords.addAll(loaded);
        }
    }

    public List<MedicalRecord> findAll() {
        return new ArrayList<>(medicalRecords);
    }

    public void add(MedicalRecord newMedicalRecords) {
        medicalRecords.add(0, newMedicalRecords);
        persist();
    }

    public Optional<MedicalRecord> findByName(String firstName, String lastName) {
        for (MedicalRecord mr : medicalRecords) {
            if (mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName)) {
                return Optional.of(mr);
            }
        }
        return Optional.empty();
    }

    public boolean updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedMedicalRecord) {
        Optional<MedicalRecord> medicalRecordToUpdate = findByName(firstName, lastName);
        if (medicalRecordToUpdate.isEmpty()) {
            return false;
        }
        MedicalRecord existingRecord = medicalRecordToUpdate.get();
        existingRecord.setBirthdate(updatedMedicalRecord.getBirthdate());
        existingRecord.setMedications(updatedMedicalRecord.getMedications());
        existingRecord.setAllergies(updatedMedicalRecord.getAllergies());
        persist();
        return true;
    }

    public boolean deleteByName(String firstName, String lastName) {
        Optional<MedicalRecord> medicalRecordToDelete = findByName(firstName, lastName);
        if (medicalRecordToDelete.isEmpty()) {
            return false;
        }
        medicalRecords.remove(medicalRecordToDelete.get());
        persist();
        return true;
    }

    public void persist() {
        JSONFileReaderRepository.writeList(records, medicalRecords);
    }
}