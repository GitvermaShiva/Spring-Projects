package com.shivam.MyWeb;

import com.shivam.MyWeb.Model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for integration tests
 */
public class TestUtils {

    /**
     * Creates test users for testing purposes
     */
    public static List<User> createTestUsers() {
        return Arrays.asList(
            new User(100, "TestUser1", "test1@example.com"),
            new User(101, "TestUser2", "test2@example.com"),
            new User(102, "TestUser3", "test3@example.com"),
            new User(103, "TestUser4", "test4@example.com"),
            new User(104, "TestUser5", "test5@example.com")
        );
    }

    /**
     * Creates a single test user
     */
    public static User createTestUser(int id, String name, String email) {
        return new User(id, name, email);
    }

    /**
     * Creates HTTP headers with JSON content type
     */
    public static HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates an HTTP entity with JSON headers
     */
    public static <T> HttpEntity<T> createJsonEntity(T body) {
        return new HttpEntity<>(body, createJsonHeaders());
    }

    /**
     * Validates that a user has the expected properties
     */
    public static void assertUserProperties(User user, int expectedId, String expectedName, String expectedEmail) {
        assert user != null : "User should not be null";
        assert user.getId() == expectedId : "User ID should be " + expectedId + " but was " + user.getId();
        assert expectedName.equals(user.getName()) : "User name should be '" + expectedName + "' but was '" + user.getName() + "'";
        assert expectedEmail.equals(user.getEmail()) : "User email should be '" + expectedEmail + "' but was '" + user.getEmail() + "'";
    }

    /**
     * Creates a user with random data for testing
     */
    public static User createRandomTestUser() {
        int randomId = (int) (Math.random() * 10000) + 1000;
        return new User(randomId, "RandomUser" + randomId, "random" + randomId + "@test.com");
    }
}
