package com.openclassroom.safteynetalertsrefactor.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class JSONFileReaderTest {

    private JSONFileReaderRepository jsonFileReaderRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        Path jsonFile = tempDir.resolve("data.json");

        String content = """
                {
                    "persons": [
                        {
                            "firstName": "John",
                            "lastName": "Doe"
                            }],
                    "firestations": [  ],
                    "medicalrecords": [  ]
                }
                """;
        Files.writeString(jsonFile, content);

        jsonFileReaderRepository = new JSONFileReaderRepository("update-data.json", jsonFile.toString());
        System.out.println("tempDir = " + tempDir.toAbsolutePath());
    }

    @Test
    void testReadFromClasspath() {
        var classpathReader = new JSONFileReaderRepository("data.json", tempDir.resolve("nonexistent.json").toString());
        var rootNode = classpathReader.readJson();
        System.out.println("rootNode (classpath) = " + rootNode.toPrettyString());
        assert rootNode.get("persons").isArray();
        assert rootNode.get("persons").get(0).get("firstName").asText().equals("John");
    }
    @Test
    void testReadFromExternalFile() {
        var rootNode = jsonFileReaderRepository.readJson();
        System.out.println("rootNode (external) = " + rootNode.toPrettyString());
        assert rootNode.get("persons").isArray();
        assert rootNode.get("persons").get(0).get("firstName").asText().equals("John");
    }

    @Test
    void testReadJson() {
        var rootNode = jsonFileReaderRepository.readJson();
        System.out.println("rootNode (read) = " + rootNode.toPrettyString());
        assert rootNode.get("persons").isArray();
        assert rootNode.get("persons").get(0).get("firstName").asText().equals("John");
    }

    @Test
    void testReadList() {
        var persons = jsonFileReaderRepository.readList("persons", com.openclassroom.safteynetalertsrefactor.model.Person.class);
        System.out.println("rootNode (readList) = " + jsonFileReaderRepository.readJson().toPrettyString());
        assert persons != null;
        assert persons.size() == 1;
        assert persons.get(0).getFirstName().equals("John");
    }

    @Test
    void testWriteList() {
        var newPerson = new com.openclassroom.safteynetalertsrefactor.model.Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("Smith");

        var persons = jsonFileReaderRepository.readList("persons", com.openclassroom.safteynetalertsrefactor.model.Person.class);
        persons.add(newPerson);

        jsonFileReaderRepository.writeList("persons", persons);

        var afterWriteRoot = jsonFileReaderRepository.readJson();
        System.out.println("rootNode (after writeList) = " + afterWriteRoot.toPrettyString());

        var updatedPersons = jsonFileReaderRepository.readList("persons", com.openclassroom.safteynetalertsrefactor.model.Person.class);
        assert updatedPersons.size() == 2;
        assert updatedPersons.get(1).getFirstName().equals("Jane");
    }

}
