package com.shivam.MyWeb.Service;

import com.shivam.MyWeb.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true"
})
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Clear existing data and set up test data
        testUser1 = new User(100, "TestUser1", "test1@example.com");
        testUser2 = new User(101, "TestUser2", "test2@example.com");
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers() {
        // Act
        List<User> users = userService.getAllUsers();
        
        // Assert
        assertNotNull(users);
        assertTrue(users.size() >= 3); // Should have at least the 3 default users
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Shivam")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Raj")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Rajesh")));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserById() {
        // Act
        User user = userService.getUser(1);
        
        // Assert
        assertNotNull(user);
        assertEquals(1, user.getId());
        // Check if the name is either the original or updated version
        assertTrue("Shivam".equals(user.getName()) || "UpdatedShivam".equals(user.getName()));
        assertTrue("shivam@gmail.com".equals(user.getEmail()) || "updated.shivam@gmail.com".equals(user.getEmail()));
    }

    @Test
    @DisplayName("Should add new user successfully")
    void testAddUser() {
        // Arrange
        int initialSize = userService.getAllUsers().size();
        
        // Act
        userService.addUser(testUser1);
        
        // Assert
        List<User> users = userService.getAllUsers();
        assertEquals(initialSize + 1, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId() == testUser1.getId()));
    }

    @Test
    @DisplayName("Should update existing user successfully")
    void testUpdateUser() {
        // Arrange
        User userToUpdate = new User(1, "UpdatedShivam", "updated.shivam@gmail.com");
        
        // Act
        userService.updateUser(userToUpdate);
        
        // Assert
        User updatedUser = userService.getUser(1);
        assertEquals("UpdatedShivam", updatedUser.getName());
        assertEquals("updated.shivam@gmail.com", updatedUser.getEmail());
        
        // Restore original data for other tests
        User originalUser = new User(1, "Shivam", "shivam@gmail.com");
        userService.updateUser(originalUser);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void testDeleteUser() {
        // Arrange
        userService.addUser(testUser2);
        int initialSize = userService.getAllUsers().size();
        
        // Act
        userService.deleteUser(testUser2.getId());
        
        // Assert
        List<User> users = userService.getAllUsers();
        assertEquals(initialSize - 1, users.size());
        assertFalse(users.stream().anyMatch(u -> u.getId() == testUser2.getId()));
    }

    @Test
    @DisplayName("Should get correct index for user")
    void testGetIndex() {
        // Arrange
        User user = userService.getUser(1);
        
        // Act
        int index = userService.getIndex(user);
        
        // Assert
        assertTrue(index >= 0);
        assertEquals(user.getId(), userService.getAllUsers().get(index).getId());
    }

    @Test
    @DisplayName("Should handle multiple operations correctly")
    void testMultipleOperations() {
        // Arrange
        User newUser1 = new User(200, "MultiTest1", "multi1@test.com");
        User newUser2 = new User(201, "MultiTest2", "multi2@test.com");
        
        // Act & Assert - Add multiple users
        userService.addUser(newUser1);
        userService.addUser(newUser2);
        
        List<User> users = userService.getAllUsers();
        assertTrue(users.stream().anyMatch(u -> u.getId() == 200));
        assertTrue(users.stream().anyMatch(u -> u.getId() == 201));
        
        // Update one user
        User updatedUser = new User(200, "UpdatedMultiTest1", "updated.multi1@test.com");
        userService.updateUser(updatedUser);
        
        User retrievedUser = userService.getUser(200);
        assertEquals("UpdatedMultiTest1", retrievedUser.getName());
        
        // Delete both test users
        userService.deleteUser(200);
        userService.deleteUser(201);
        
        users = userService.getAllUsers();
        assertFalse(users.stream().anyMatch(u -> u.getId() == 200));
        assertFalse(users.stream().anyMatch(u -> u.getId() == 201));
    }
}
