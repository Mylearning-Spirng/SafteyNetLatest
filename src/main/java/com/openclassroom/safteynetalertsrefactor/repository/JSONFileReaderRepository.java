package com.openclassroom.safteynetalertsrefactor.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
/* Repository class for reading and writing JSON data from/to files.
 * It supports reading from a classpath resource and an external file, as well as
 * reading/writing lists of objects to/from JSON arrays.
 */
public class JSONFileReaderRepository {

    private static final Logger log = LoggerFactory.getLogger(JSONFileReaderRepository.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String classpathResource;
    private final Path writePath;

    public JSONFileReaderRepository(
            @Value("${app.data.classpath-resource:data.json}") String classpathResource,
            @Value("${app.data.write-path:test/updated.json}") String writePathStr

    ) {
        this.classpathResource = classpathResource;
        this.writePath = Path.of(writePathStr);
    }

    // Read JsonNode from external file if exists, otherwise from classpath
    public JsonNode readJson() {
        return readFromExternalFile().orElseGet(this::readFromClasspath);
    }


    private Optional<JsonNode> readFromExternalFile() {
        if (Files.exists(writePath)) {
            try (InputStream in = Files.newInputStream(writePath)) {
                return Optional.of(objectMapper.readTree(in));
            } catch (IOException e) {
                log.error("Error reading JSON from external file", e);
            }
        }
        return Optional.empty();
    }

    /* Read JsonNode from classpath resource */
    private JsonNode readFromClasspath() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(classpathResource)) {
            if (in == null) {
                log.warn("Classpath resource {} not found, returning empty object node", classpathResource);
                return objectMapper.createObjectNode();
            }
            return objectMapper.readTree(in);
        } catch (IOException e) {
            log.error("Error reading JSON from classpath resource, returning empty object node", e);
            return objectMapper.createObjectNode();
        }
    }

    // Write JsonNode to external file
    public void writeJson(JsonNode root) {
        try {
            Path parent = writePath.toAbsolutePath().getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writePath.toFile(), root);
        } catch (IOException e) {
            log.error("Error writing JSON to file: {}", writePath, e);
            throw new RuntimeException("Failed to write JSON file", e);
        }
    }

    // Read a list of objects from a JSON array and convert them to a List<T>
    public <T> List<T> readList(String arrayName, Class<T> elementType) {
        JsonNode arrayNode = readJson().path(arrayName);
        if (!arrayNode.isArray() || arrayNode.isEmpty()) {
            return new ArrayList<>();
        }
        return objectMapper.convertValue(
                arrayNode,
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
        );
    }

    //Convert a List<T> to a JSON array and write it to the JSON file
    public <T> void writeList(String arrayName, List<T> items) {
        JsonNode root = readJson();
        ObjectNode objectNode = root.isObject() ? (ObjectNode) root : objectMapper.createObjectNode();
        objectNode.set(arrayName, objectMapper.valueToTree(items));
        writeJson(objectNode);
    }
}
