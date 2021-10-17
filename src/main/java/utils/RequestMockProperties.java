package utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestMockProperties {
    private final Map<String, String> properties = new HashMap<>();

    public String getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public void setProperty(String propertyName, String propertyValue) {
        properties.put(propertyName, propertyValue);
    }
}
