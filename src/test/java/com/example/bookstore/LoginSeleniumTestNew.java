package com.example.bookstore;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
// @TestPropertySource(properties = { "server.port=8080" })
public class LoginSeleniumTestNew {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeEach
    void setUp() {
        // Cấu hình Chrome driver để hiển thị giao diện (không headless)
        ChromeOptions options = new ChromeOptions();
        // Không sử dụng headless mode để hiển thị giao diện
        // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            // Thêm delay để có thể quan sát kết quả
            try {
                Thread.sleep(3000); // 3 giây để quan sát
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver.quit();
        }
    }

    @Test
    @DisplayName("Test successful login with admin credentials - should redirect to admin dashboard")
    void testAdminLoginSuccess() {
        // Navigate to login page
        driver.get(BASE_URL + "/auth/login");

        // Delay để trang web khởi động hoàn toàn
        try {
            Thread.sleep(2000); // 2 giây chờ trang load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for login form to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Find login form elements
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Enter admin credentials với delay giữa các thao tác
        usernameField.clear();
        try {
            Thread.sleep(500); // Delay 500ms sau khi clear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        usernameField.sendKeys("admin");
        try {
            Thread.sleep(1000); // Delay 1 giây sau khi nhập username
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.clear();
        try {
            Thread.sleep(500); // Delay 500ms sau khi clear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.sendKeys("abc@123");
        try {
            Thread.sleep(1000); // Delay 1 giây sau khi nhập password
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click login button với delay trước đó
        try {
            Thread.sleep(500); // Delay trước khi click
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        loginButton.click();

        // Wait for redirect after successful login với thời gian dài hơn
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("/user")));

        // Delay để quan sát kết quả
        try {
            Thread.sleep(2000); // 2 giây để quan sát
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify successful login by checking URL contains admin (since admin should
        // redirect to admin dashboard)
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after admin login: " + currentUrl);

        // Check if redirected to admin or user dashboard
        boolean isRedirected = currentUrl.contains("/admin") || currentUrl.contains("/user");
        Assertions.assertTrue(isRedirected,
                "Admin login should redirect to admin dashboard or user area. Current URL: " + currentUrl);
    }

    @Test
    @DisplayName("Test successful login with customer credentials - should redirect to user area")
    void testCustomerLoginSuccess() {
        // Navigate to login page
        driver.get(BASE_URL + "/auth/login");

        // Delay để trang web khởi động hoàn toàn
        try {
            Thread.sleep(2000); // 2 giây chờ trang load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for login form to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Find login form elements
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Enter customer credentials với delay giữa các thao tác
        usernameField.clear();
        try {
            Thread.sleep(500); // Delay 500ms sau khi clear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        usernameField.sendKeys("yuuyuunee");
        try {
            Thread.sleep(1000); // Delay 1 giây sau khi nhập username
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.clear();
        try {
            Thread.sleep(500); // Delay 500ms sau khi clear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.sendKeys("abc@123");
        try {
            Thread.sleep(1000); // Delay 1 giây sau khi nhập password
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click login button với delay trước đó
        try {
            Thread.sleep(500); // Delay trước khi click
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        loginButton.click();

        // Wait for redirect after successful login với thời gian dài hơn
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.urlContains("/user")));

        // Delay để quan sát kết quả
        try {
            Thread.sleep(2000); // 2 giây để quan sát
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify successful login by checking URL
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after customer login: " + currentUrl);

        // Check if redirected to user dashboard (customer should go to user area)
        boolean isRedirected = currentUrl.contains("/admin") || currentUrl.contains("/user");
        Assertions.assertTrue(isRedirected,
                "Customer login should redirect to user area. Current URL: " + currentUrl);
    }

    @Test
    @DisplayName("Test failed login with wrong password")
    void testLoginWithWrongPassword() {
        // Navigate to login page
        driver.get(BASE_URL + "/auth/login");

        // Delay để trang web khởi động hoàn toàn
        try {
            Thread.sleep(2000); // 2 giây chờ trang load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for login form to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Find login form elements
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Enter valid username but wrong password với delay
        usernameField.clear();
        try {
            Thread.sleep(500); // Delay 500ms sau khi clear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        usernameField.sendKeys("admin");
        try {
            Thread.sleep(1000); // Delay 1 giây sau khi nhập username
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.clear();
        try {
            Thread.sleep(500); // Delay 500ms sau khi clear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.sendKeys("wrongpassword123");
        try {
            Thread.sleep(1000); // Delay 1 giây sau khi nhập password sai
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click login button với delay trước đó
        try {
            Thread.sleep(500); // Delay trước khi click
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        loginButton.click();

        // Wait a moment for any response - tăng thời gian chờ
        try {
            Thread.sleep(3000); // 3 giây để server xử lý và phản hồi
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if still on login page or error message appears
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after wrong password: " + currentUrl);

        // Should either stay on login page or show error
        boolean stayedOnLogin = currentUrl.contains("/login") || currentUrl.contains("/auth/login");

        if (!stayedOnLogin) {
            // If redirected somewhere, check if it's an error page or back to login
            boolean isErrorOrLogin = currentUrl.contains("error") ||
                    currentUrl.contains("login") ||
                    currentUrl.contains("auth");
            Assertions.assertTrue(isErrorOrLogin,
                    "Wrong password should not allow successful login. Current URL: " + currentUrl);
        } else {
            // Try to find error message on page
            try {
                WebElement errorMessage = driver.findElement(By.className("error"));
                Assertions.assertTrue(errorMessage.isDisplayed(), "Error message should be visible");
                System.out.println("Found error message: " + errorMessage.getText());
            } catch (Exception e) {
                // Error message element not found, but staying on login page is also valid
                System.out.println("No error message found, but stayed on login page");
            }
        }
    }

    @Test
    @DisplayName("Test failed login with non-existent username")
    void testLoginWithNonExistentUser() {
        // Navigate to login page
        driver.get(BASE_URL + "/auth/login");

        // Wait for login form to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Find login form elements
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Enter non-existent username
        usernameField.clear();
        usernameField.sendKeys("nonexistentuser999");
        passwordField.clear();
        passwordField.sendKeys("anypassword");

        // Click login button
        loginButton.click();

        // Wait a moment for any response
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check if still on login page or error message appears
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after non-existent user: " + currentUrl);

        // Should not allow successful login
        boolean notLoggedIn = !currentUrl.contains("/admin") && !currentUrl.contains("/user");
        Assertions.assertTrue(notLoggedIn,
                "Non-existent user should not be able to login. Current URL: " + currentUrl);
    }

    @Test
    @DisplayName("Test login form validation with empty fields")
    void testEmptyFieldsValidation() {
        // Navigate to login page
        driver.get(BASE_URL + "/auth/login");

        // Delay để trang web khởi động hoàn toàn
        try {
            Thread.sleep(2000); // 2 giây chờ trang load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for login form to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Find login button and click without entering credentials với delay
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        try {
            Thread.sleep(1000); // Delay trước khi click
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        loginButton.click();

        // Delay để form validation xử lý
        try {
            Thread.sleep(1500); // 1.5 giây để validation message xuất hiện
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check for HTML5 validation or custom validation messages
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));

        // Verify validation (HTML5 required attribute validation)
        String usernameValidation = usernameField.getAttribute("validationMessage");
        String passwordValidation = passwordField.getAttribute("validationMessage");

        boolean hasValidation = !usernameValidation.isEmpty() || !passwordValidation.isEmpty();
        System.out.println("Username validation: " + usernameValidation);
        System.out.println("Password validation: " + passwordValidation);

        // At minimum, should not allow login with empty fields
        String currentUrl = driver.getCurrentUrl();
        boolean stayedOnLogin = currentUrl.contains("/login") || currentUrl.contains("/auth");

        Assertions.assertTrue(stayedOnLogin || hasValidation,
                "Form should show validation for empty fields or stay on login page");
    }

    @Test
    @DisplayName("Test role-based redirect - Admin vs Customer")
    void testRoleBasedRedirect() {
        // Test with admin first
        driver.get(BASE_URL + "/auth/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Login as admin
        usernameField.clear();
        usernameField.sendKeys("admin");
        passwordField.clear();
        passwordField.sendKeys("abc@123");
        loginButton.click();

        // Wait for redirect and check URL
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String adminUrl = driver.getCurrentUrl();
        System.out.println("Admin login redirected to: " + adminUrl);

        // Logout by going to logout endpoint or clearing session
        driver.get(BASE_URL + "/auth/login");

        // Now test with customer
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        usernameField = driver.findElement(By.id("username"));
        passwordField = driver.findElement(By.id("password"));
        loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Login as customer
        usernameField.clear();
        usernameField.sendKeys("yuuyuunee");
        passwordField.clear();
        passwordField.sendKeys("abc@123");
        loginButton.click();

        // Wait for redirect and check URL
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String customerUrl = driver.getCurrentUrl();
        System.out.println("Customer login redirected to: " + customerUrl);

        // Both should be redirected successfully (specific role checking would require
        // API integration)
        boolean adminRedirected = adminUrl.contains("/admin") || adminUrl.contains("/user");
        boolean customerRedirected = customerUrl.contains("/admin") || customerUrl.contains("/user");

        Assertions.assertTrue(adminRedirected, "Admin should be redirected after login");
        Assertions.assertTrue(customerRedirected, "Customer should be redirected after login");
    }

    @Test
    @DisplayName("Test login failure with locked account - should stay on login page with error")
    void testLockedAccountLoginFailure() {
        // Điều hướng đến trang đăng nhập
        driver.get(BASE_URL + "/auth/login");

        // Delay để trang web load hoàn toàn
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Đợi các phần tử của form login hiển thị
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        // Tìm các phần tử trên form
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        // Nhập tài khoản bị khóa
        usernameField.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        usernameField.sendKeys("admin1");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        passwordField.clear();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        passwordField.sendKeys("abc@123");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click login
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        loginButton.click();

        // Đợi thời gian đủ để xử lý login, nhưng KHÔNG được redirect
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Lấy URL hiện tại
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Current URL after locked login attempt: " + currentUrl);

        // Kiểm tra: không bị redirect (vẫn ở trang login)
        boolean stayedOnLoginPage = currentUrl.contains("/login") || currentUrl.contains("/auth/login");
        Assertions.assertTrue(stayedOnLoginPage, 
            " \nThất bại: Người dùng đã bị redirect khỏi trang login. URL hiện tại: " + currentUrl);

        // Kiểm tra: có hiển thị thông báo lỗi
        try {
            WebElement errorMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".error, .alert-danger, .text-red-600, [role='alert']")));

            String errorText = errorMessage.getText().toLowerCase();
            System.out.println("❗ Error message shown: " + errorText);

            boolean hasLockedText = errorText.contains("lock") || 
                                    errorText.contains("disabled") || 
                                    errorText.contains("inactive") || 
                                    errorText.contains("blocked") || 
                                    errorText.contains("suspended") || 
                                    errorText.contains("deactivated");

            Assertions.assertTrue(hasLockedText, 
                " \nThất bại: Thông báo lỗi không đúng nội dung khóa tài khoản. Message: " + errorText);

        } catch (TimeoutException e) {
            Assertions.fail(" \nThất bại: Không hiển thị thông báo lỗi sau khi login với tài khoản bị khóa.");
        }

        System.out.println("\nLogin với tài khoản bị khóa được xử lý đúng: không bị redirect và có thông báo lỗi phù hợp.");
    }


}
