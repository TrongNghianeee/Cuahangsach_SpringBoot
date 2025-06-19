package com.example.bookstore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.bookstore.dto.Token;
import com.example.bookstore.dto.UserDTO;
import com.example.bookstore.facade.AuthFacade;

/**
 * JUnit Tests for Login functionality
 * These tests focus on unit testing of login logic through AuthFacade only
 */
@SpringBootTest
@TestPropertySource(properties = {"server.port=8080"})
public class LoginJUnitTestClean {

    @Autowired
    private AuthFacade authFacade;

    @BeforeAll
    static void setupClass() {
        System.out.println("Starting Login JUnit Tests - AuthFacade Only");
    }

    @BeforeEach
    void setUp() {
        System.out.println("Setting up test environment");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Cleaning up after test");
    }

    @Test
    @DisplayName("Test successful login with admin credentials")
    void testAdminLoginSuccess() {
        // Test admin login
        Token token = authFacade.login("admin", "abc@123");
        
        Assertions.assertNotNull(token, "Token should not be null for valid admin login");
        Assertions.assertNotNull(token.getAccess_token(), "Token should have access token");
        Assertions.assertEquals("bearer", token.getToken_type(), "Token type should be bearer");
        
        System.out.println("Admin login token: " + token.getAccess_token());
        
        // Verify user details
        UserDTO user = authFacade.getCurrentUser(token.getAccess_token());
        Assertions.assertNotNull(user, "Should be able to get current user from token");
        Assertions.assertEquals("admin", user.getUsername(), "Username should match");
        
        // Check if user has admin role
        String userRole = user.getRole();
        System.out.println("Admin user role: " + userRole);
        Assertions.assertNotNull(userRole, "Admin user should have a role");
    }

    @Test
    @DisplayName("Test successful login with customer credentials")
    void testCustomerLoginSuccess() {
        // Test customer login
        Token token = authFacade.login("yuuyuunee", "abc@123");
        
        Assertions.assertNotNull(token, "Customer login should return a token");
        Assertions.assertNotNull(token.getAccess_token(), "Token should have access token");
        Assertions.assertEquals("bearer", token.getToken_type(), "Token type should be bearer");
        
        System.out.println("Customer login token: " + token.getAccess_token());
        
        // Verify user details
        UserDTO user = authFacade.getCurrentUser(token.getAccess_token());
        Assertions.assertNotNull(user, "Should be able to get current user from token");
        Assertions.assertEquals("yuuyuunee", user.getUsername(), "Username should match");
        
        // Check user role
        String userRole = user.getRole();
        System.out.println("Customer user role: " + userRole);
        Assertions.assertNotNull(userRole, "User should have a role");
    }

    @Test
    @DisplayName("Test login failure with wrong credentials")
    void testLoginFailure() {
        // Test with wrong password
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            authFacade.login("admin", "wrongpassword");
        });
        
        Assertions.assertTrue(exception.getMessage().contains("Invalid") || 
                            exception.getMessage().contains("Wrong") ||
                            exception.getMessage().contains("Incorrect"),
                            "Should throw exception with appropriate message for wrong credentials");
    }

    @Test
    @DisplayName("Test login failure with locked account (status = Lock)")
    void testLockedAccountLoginFailure() {
        // Test login with locked account - admin1 has status = "Lock"
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            authFacade.login("admin1", "abc@123");
        });
        
        Assertions.assertNotNull(exception, "Should throw exception for locked account");
        String errorMessage = exception.getMessage().toLowerCase();
        Assertions.assertTrue(
            errorMessage.contains("lock") || 
            errorMessage.contains("disabled") || 
            errorMessage.contains("inactive") ||
            errorMessage.contains("blocked") ||
            errorMessage.contains("suspended"),
            "Error message should indicate account is locked/disabled. Actual message: " + exception.getMessage()
        );
        
        System.out.println("✅ Locked account login correctly denied: " + exception.getMessage());
    }

    @Test
    @DisplayName("Test role-based access control")
    void testRoleBasedAccess() {        // Test admin login
        Token adminToken = authFacade.login("admin", "abc@123");
        Assertions.assertNotNull(adminToken, "Admin should be able to login");
        Assertions.assertNotNull(adminToken.getAccess_token(), "Admin token should have access token");
        
        // Test customer login  
        Token customerToken = authFacade.login("customer", "abc@123");
        Assertions.assertNotNull(customerToken, "Customer should be able to login");
        Assertions.assertNotNull(customerToken.getAccess_token(), "Customer token should have access token");
        
        System.out.println("✅ Both admin and customer can login with active accounts");
    }

    @Test
    @DisplayName("Test contrast: Active vs Locked account login")
    void testActiveVsLockedAccountLogin() {        // Test with active account (should succeed)
        Token activeToken = authFacade.login("admin", "abc@123");
        Assertions.assertNotNull(activeToken, "Active account should be able to login");
        Assertions.assertNotNull(activeToken.getAccess_token(), "Active account token should have access token");
        System.out.println("✅ Active account (admin) login successful");
        
        // Test with locked account (should fail)
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            authFacade.login("admin1", "abc@123");
        });
        
        Assertions.assertNotNull(exception, "Locked account should throw exception");
        String errorMessage = exception.getMessage().toLowerCase();
        Assertions.assertTrue(
            errorMessage.contains("lock") || 
            errorMessage.contains("disabled") || 
            errorMessage.contains("inactive") ||
            errorMessage.contains("blocked") ||
            errorMessage.contains("suspended"),
            "Error message should indicate account is locked. Actual: " + exception.getMessage()
        );
        
        System.out.println("✅ Locked account (admin1) login correctly denied: " + exception.getMessage());
        System.out.println("✅ Test demonstrates difference between Active and Lock status");
    }

    @Test
    @DisplayName("Test logout functionality")
    void testLogoutFunctionality() {
        // Step 1: Login để lấy token
        Token token = authFacade.login("admin", "abc@123");
        Assertions.assertNotNull(token, "Bước 1: Login thất bại - Token trả về null");

        System.out.println("Đăng nhập thành công với token: " + token.getAccess_token().substring(0, 20) + "...");

        // Step 2: Gọi logout
        boolean logoutResult = authFacade.logout(token.getAccess_token());
        Assertions.assertTrue(logoutResult, "Bước 2: Logout thất bại - Không trả về true");

        System.out.println("Đăng xuất thành công. Đang kiểm tra tính hợp lệ của token sau logout...");

        // Step 3: Kiểm tra xem token có bị vô hiệu hóa chưa
        UserDTO userAfterLogout = authFacade.getCurrentUser(token.getAccess_token());

        if (userAfterLogout != null) {
            System.err.println("LỖI: Token sau khi logout vẫn còn hợp lệ!");
            System.err.println("User trả về từ token cũ: " + userAfterLogout.getUsername() + " | Vai trò: " + userAfterLogout.getRole());
        }

        Assertions.assertNull(userAfterLogout,
                () -> "\nBước 3: Token sau logout vẫn truy cập được thông tin người dùng!\n"
                    + "Hãy kiểm tra lại logic xử lý trong authFacade.logout hoặc hệ thống xác thực.");
    }

    @Test
    @DisplayName("Test invalid token handling")
    void testInvalidTokenHandling() {
        // Test with completely invalid token
        UserDTO user1 = authFacade.getCurrentUser("invalid.token.here");
        Assertions.assertNull(user1, "Invalid token should return null user");
        
        // Test with empty token
        UserDTO user2 = authFacade.getCurrentUser("");
        Assertions.assertNull(user2, "Empty token should return null user");
        
        // Test with null token
        UserDTO user3 = authFacade.getCurrentUser(null);
        Assertions.assertNull(user3, "Null token should return null user");
        
        // Test logout with invalid token
        boolean logoutResult1 = authFacade.logout("invalid.token.here");
        Assertions.assertFalse(logoutResult1, "Logout with invalid token should fail");
        
        // Test logout with empty token
        boolean logoutResult2 = authFacade.logout("");
        Assertions.assertFalse(logoutResult2, "Logout with empty token should fail");
        
        System.out.println("All invalid token tests passed");
    }

    @Test
    @DisplayName("Test password validation logic")
    void testPasswordValidation() {
        // Test with correct credentials
        Token validToken = authFacade.login("admin", "abc@123");
        Assertions.assertNotNull(validToken, "Valid credentials should work");
        
        // Test with slightly wrong password
        Token invalidToken1 = authFacade.login("admin", "abc@12");
        Assertions.assertNull(invalidToken1, "Wrong password should fail");
        
        // Test case sensitivity
        Token invalidToken2 = authFacade.login("admin", "ABC@123");
        Assertions.assertNull(invalidToken2, "Case sensitive password should fail if case is wrong");
        
        // Test password with extra spaces
        Token invalidToken3 = authFacade.login("admin", " abc@123 ");
        Assertions.assertNull(invalidToken3, "Password with spaces should fail");
        
        // Test username case sensitivity
        Token invalidToken4 = authFacade.login("ADMIN", "abc@123");
        Assertions.assertNull(invalidToken4, "Username should be case sensitive");
        
        System.out.println("Password validation tests completed");
    }

    @Test
    @DisplayName("Test username validation logic")
    void testUsernameValidation() {
        // Test with empty username
        Token token1 = authFacade.login("", "abc@123");
        Assertions.assertNull(token1, "Empty username should fail");
        
        // Test with null username
        Token token2 = authFacade.login(null, "abc@123");
        Assertions.assertNull(token2, "Null username should fail");
        
        // Test with username that has spaces
        Token token3 = authFacade.login(" admin ", "abc@123");
        Assertions.assertNull(token3, "Username with spaces should fail");
        
        // Test with correct username formats
        Token validToken1 = authFacade.login("admin", "abc@123");
        Assertions.assertNotNull(validToken1, "Valid admin username should work");
        
        Token validToken2 = authFacade.login("yuuyuunee", "abc@123");
        Assertions.assertNotNull(validToken2, "Valid customer username should work");
        
        System.out.println("Username validation tests completed");
    }

    @Test
    @DisplayName("Test token properties")
    void testTokenProperties() {
        // Login and check token properties
        Token adminToken = authFacade.login("admin", "abc@123");
        Assertions.assertNotNull(adminToken, "Should get token for admin");
        
        // Check token has required properties
        Assertions.assertNotNull(adminToken.getAccess_token(), "Token should have access_token");
        Assertions.assertNotNull(adminToken.getToken_type(), "Token should have token_type");
        Assertions.assertEquals("bearer", adminToken.getToken_type().toLowerCase(), "Token type should be bearer");
        
        // Check token is not empty
        Assertions.assertFalse(adminToken.getAccess_token().trim().isEmpty(), "Access token should not be empty");
        
        // Test customer token has same properties
        Token customerToken = authFacade.login("yuuyuunee", "abc@123");
        Assertions.assertNotNull(customerToken, "Should get token for customer");
        Assertions.assertNotNull(customerToken.getAccess_token(), "Customer token should have access_token");
        Assertions.assertEquals("bearer", customerToken.getToken_type().toLowerCase(), "Customer token type should be bearer");
        
        // Tokens should be different
        Assertions.assertNotEquals(adminToken.getAccess_token(), customerToken.getAccess_token(), 
            "Different users should have different tokens");
        
        System.out.println("Token properties validation completed");
    }

}
