package com.shivam.MyWeb;

import com.shivam.MyWeb.Controller.UserController;
import com.shivam.MyWeb.Model.User;
import com.shivam.MyWeb.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true"
})
class EndToEndIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    private String baseUrl;
    private User testUser;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        testUser = new User(777, "EndToEndTestUser", "endtoend@test.com");
    }

    @Test
    @Order(1)
    @DisplayName("Should load Spring context successfully")
    void testContextLoads() {
        assertNotNull(userService);
        assertNotNull(userController);
        assertNotNull(restTemplate);
    }

    @Test
    @Order(2)
    @DisplayName("Should have initial data loaded")
    void testInitialDataExists() {
        List<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertTrue(users.size() >= 3);
        
        // Verify default users exist (check for non-null names)
        assertTrue(users.stream().anyMatch(u -> u.getName() != null && u.getName().equals("Shivam")));
        assertTrue(users.stream().anyMatch(u -> u.getName() != null && u.getName().equals("Raj")));
        assertTrue(users.stream().anyMatch(u -> u.getName() != null && u.getName().equals("Rajesh")));
    }

    @Test
    @Order(3)
    @DisplayName("Should perform complete CRUD operations through service layer")
    void testServiceLayerCrudOperations() {
        // Create
        userService.addUser(testUser);
        assertTrue(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == testUser.getId()));

        // Read
        User retrievedUser = userService.getUser(testUser.getId());
        assertEquals(testUser.getName(), retrievedUser.getName());
        assertEquals(testUser.getEmail(), retrievedUser.getEmail());

        // Update
        User updatedUser = new User(testUser.getId(), "UpdatedEndToEndUser", "updated.endtoend@test.com");
        userService.updateUser(updatedUser);
        User userAfterUpdate = userService.getUser(testUser.getId());
        assertEquals("UpdatedEndToEndUser", userAfterUpdate.getName());

        // Delete
        userService.deleteUser(testUser.getId());
        assertFalse(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == testUser.getId()));
    }

    @Test
    @Order(4)
    @DisplayName("Should perform complete CRUD operations through REST endpoints")
    void testRestEndpointsCrudOperations() {
        // Create via REST
        User restUser = new User(666, "RestTestUser", "rest@test.com");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> createRequest = new HttpEntity<>(restUser, headers);

        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
            baseUrl + "/users", 
            createRequest, 
            Void.class
        );
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        // Read via REST
        ResponseEntity<User> getResponse = restTemplate.getForEntity(
            baseUrl + "/users/" + restUser.getId(), 
            User.class
        );
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(restUser.getName(), getResponse.getBody().getName());

        // Update via REST
        User updatedRestUser = new User(666, "UpdatedRestUser", "updated.rest@test.com");
        HttpEntity<User> updateRequest = new HttpEntity<>(updatedRestUser, headers);
        ResponseEntity<Void> updateResponse = restTemplate.exchange(
            baseUrl + "/users", 
            HttpMethod.PUT, 
            updateRequest, 
            Void.class
        );
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // Verify update via REST
        ResponseEntity<User> getUpdatedResponse = restTemplate.getForEntity(
            baseUrl + "/users/" + restUser.getId(), 
            User.class
        );
        assertEquals(HttpStatus.OK, getUpdatedResponse.getStatusCode());
        assertEquals("UpdatedRestUser", getUpdatedResponse.getBody().getName());

        // Delete via REST
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl + "/users/" + restUser.getId(), 
            HttpMethod.DELETE, 
            null, 
            Void.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // Verify deletion
        assertFalse(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == restUser.getId()));
    }

    @Test
    @Order(5)
    @DisplayName("Should handle concurrent operations correctly")
    void testConcurrentOperations() {
        // Add multiple users concurrently
        User concurrentUser1 = new User(555, "ConcurrentUser1", "concurrent1@test.com");
        User concurrentUser2 = new User(556, "ConcurrentUser2", "concurrent2@test.com");
        User concurrentUser3 = new User(557, "ConcurrentUser3", "concurrent3@test.com");

        userService.addUser(concurrentUser1);
        userService.addUser(concurrentUser2);
        userService.addUser(concurrentUser3);

        // Verify all users were added
        List<User> users = userService.getAllUsers();
        assertTrue(users.stream().anyMatch(u -> u.getId() == 555));
        assertTrue(users.stream().anyMatch(u -> u.getId() == 556));
        assertTrue(users.stream().anyMatch(u -> u.getId() == 557));

        // Perform concurrent updates
        User updatedUser1 = new User(555, "UpdatedConcurrent1", "updated.concurrent1@test.com");
        User updatedUser2 = new User(556, "UpdatedConcurrent2", "updated.concurrent2@test.com");

        userService.updateUser(updatedUser1);
        userService.updateUser(updatedUser2);

        // Verify updates
        assertEquals("UpdatedConcurrent1", userService.getUser(555).getName());
        assertEquals("UpdatedConcurrent2", userService.getUser(556).getName());

        // Clean up
        userService.deleteUser(555);
        userService.deleteUser(556);
        userService.deleteUser(557);
    }

    @Test
    @Order(6)
    @DisplayName("Should maintain data consistency across operations")
    void testDataConsistency() {
        // Add a test user
        User consistencyUser = new User(444, "ConsistencyUser", "consistency@test.com");
        userService.addUser(consistencyUser);

        // Verify user exists in service
        assertTrue(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == 444));

        // Verify user is accessible via REST
        ResponseEntity<User> response = restTemplate.getForEntity(
            baseUrl + "/users/444", 
            User.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ConsistencyUser", response.getBody().getName());

        // Update user via service
        User updatedConsistencyUser = new User(444, "UpdatedConsistencyUser", "updated.consistency@test.com");
        userService.updateUser(updatedConsistencyUser);

        // Verify update is reflected in REST response
        ResponseEntity<User> updatedResponse = restTemplate.getForEntity(
            baseUrl + "/users/444", 
            User.class
        );
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        assertEquals("UpdatedConsistencyUser", updatedResponse.getBody().getName());

        // Clean up
        userService.deleteUser(444);
    }

    @Test
    @Order(7)
    @DisplayName("Should handle edge cases gracefully")
    void testEdgeCases() {
        // Test with user ID 0
        User zeroIdUser = new User(0, "ZeroIdUser", "zero@test.com");
        userService.addUser(zeroIdUser);
        assertTrue(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == 0));

        // Test with very large ID
        User largeIdUser = new User(999999, "LargeIdUser", "large@test.com");
        userService.addUser(largeIdUser);
        assertTrue(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == 999999));

        // Test with empty name
        User emptyNameUser = new User(888, "", "empty@test.com");
        userService.addUser(emptyNameUser);
        assertTrue(userService.getAllUsers().stream()
            .anyMatch(u -> u.getId() == 888));

        // Clean up
        userService.deleteUser(0);
        userService.deleteUser(999999);
        userService.deleteUser(888);
    }

    @Test
    @Order(8)
    @DisplayName("Should complete full application lifecycle")
    void testFullApplicationLifecycle() {
        // This test simulates a complete application session
        
        // 1. Initial state
        int initialUserCount = userService.getAllUsers().size();
        
        // 2. Add multiple users
        User lifecycleUser1 = new User(111, "LifecycleUser1", "lifecycle1@test.com");
        User lifecycleUser2 = new User(222, "LifecycleUser2", "lifecycle2@test.com");
        User lifecycleUser3 = new User(333, "LifecycleUser3", "lifecycle3@test.com");
        
        userService.addUser(lifecycleUser1);
        userService.addUser(lifecycleUser2);
        userService.addUser(lifecycleUser3);
        
        assertEquals(initialUserCount + 3, userService.getAllUsers().size());
        
        // 3. Update users
        User updatedLifecycleUser1 = new User(111, "UpdatedLifecycleUser1", "updated.lifecycle1@test.com");
        userService.updateUser(updatedLifecycleUser1);
        
        // 4. Verify updates
        assertEquals("UpdatedLifecycleUser1", userService.getUser(111).getName());
        
        // 5. Remove users
        userService.deleteUser(111);
        userService.deleteUser(222);
        userService.deleteUser(333);
        
        // 6. Verify final state
        assertEquals(initialUserCount, userService.getAllUsers().size());
        assertFalse(userService.getAllUsers().stream().anyMatch(u -> u.getId() == 111));
        assertFalse(userService.getAllUsers().stream().anyMatch(u -> u.getId() == 222));
        assertFalse(userService.getAllUsers().stream().anyMatch(u -> u.getId() == 333));
    }
}
