package com.example.bookstore;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test_Selenium_User_Payment {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeEach
    void setUp() {
        // Configure Chrome driver to display UI (not headless)
        ChromeOptions options = new ChromeOptions();
        // Uncomment to use headless mode if needed
        // options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--start-maximized");

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(3000); // 3-second delay to observe results
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver.quit();
        }
    }

    // Helper method to perform login and navigate to cart page
    private void login(String username, String password) {
        driver.get(BASE_URL + "/auth/login");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));

        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);

        loginButton.click();

        try {
            Thread.sleep(2000); // Delay to allow server to process login
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        driver.get(BASE_URL + "/user/Cart");

        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h1[contains(text(), 'Giỏ hàng')]")
        ));
    }

    @Test
    @Order(1)
    @DisplayName("TC1: User triggers payment")
    void testUserTriggersPayment() {
        // Log in and navigate to cart page
        login("hehe", "@hehe123");

         driver.get(BASE_URL + "/user");
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Thêm vào giỏ')]")));
        addToCartButton.click();

        // Handle the alert
        try {
            WebDriverWait waitForAlert = new WebDriverWait(driver, Duration.ofSeconds(2));
            waitForAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept(); // Accept the alert
            Thread.sleep(1000); // Short delay after accepting alert
        } catch (Exception e) {
            // If no alert is present, continue (optional: log or handle differently)
        }
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String bookTitle = "Sapiens: Lược sử loài người";
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h3[contains(text(), '" + bookTitle + "')]")));
        assertNotNull(title, "Item 'Sapiens: Lược sử loài người' should be added to cart");

        // Enter shipping address
        WebElement addressField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//textarea[contains(@placeholder, 'Nhập địa chỉ giao hàng')]")));
        addressField.clear();
        addressField.sendKeys("123 Đường ABC, Quận 1, TP.HCM");

        try {
            Thread.sleep(2000); // Delay to allow input to process
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Select COD payment method
        WebElement codRadio = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(., 'Tiền mặt khi nhận hàng')]/input[@type='radio']")));
        codRadio.click();

        try {
            Thread.sleep(2000); // Delay to allow selection to process
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Click the checkout button
        WebElement checkoutButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Vui lòng nhập địa chỉ giao hàng') or contains(text(), 'Thanh toán')]")));
        checkoutButton.click();

        try {
            Thread.sleep(2000); // Delay to allow checkout process to start
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify checkout initiation (e.g., redirection to orders page or confirmation)
        boolean isCheckoutInitiated = driver.getCurrentUrl().contains("/user/orders") ||
                driver.findElements(By.xpath("//div[contains(text(), 'Đặt hàng thành công')]")).size() > 0;
        assertTrue(isCheckoutInitiated, "Payment process should be initiated");
    }

    @Test
    @Order(2)
    @DisplayName("TC2: User views payment details for an order")
    void testUserViewsPaymentDetailsForOrder() {
        // Log in and navigate to orders page (assuming checkout from TC1 creates an order)
        login("hehe", "@hehe123");
        driver.get(BASE_URL + "/user/Order");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h1[contains(text(), 'Đơn hàng')]")));

        // Assume the first order is the latest one created from TC1
        WebElement orderItem = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//div[contains(@class, 'order-item')])[1]")));
        orderItem.click();

        try {
            Thread.sleep(2000); // Delay to allow order details to load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify payment details are displayed (e.g., amount and method)
        WebElement paymentAmount = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), 'Tổng cộng')]/following-sibling::div[contains(text(), '199.000')]")));
        WebElement paymentMethod = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), 'Phương thức thanh toán')]/following-sibling::div[contains(text(), 'Tiền mặt')]")));

        assertNotNull(paymentAmount, "Payment amount should be displayed");
        assertNotNull(paymentMethod, "Payment method should be displayed");
    }

    @Test
    @Order(3)
    @DisplayName("TC3: User views payment history")
    void testUserViewsPaymentHistory() {
        // Log in and navigate to orders or payment history page
        login("hehe", "@hehe123");
        driver.get(BASE_URL + "/user/Order");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h1[contains(text(), 'Đơn hàng')]")));

        // Verify payment history section exists
        WebElement paymentHistorySection = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'payment-history')]")));
        assertNotNull(paymentHistorySection, "Payment history section should be displayed");

        // Verify at least one payment record (e.g., from TC1)
        WebElement paymentRecord = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class, 'payment-history')]//div[contains(text(), '199.000')]")));
        assertNotNull(paymentRecord, "Payment history should contain at least one record");
    }

    @Test
    @Order(4)
    @DisplayName("TC4: User views total amount spent per order")
    void testUserViewsTotalAmountSpentPerOrder() {
        // Log in and navigate to orders page
        login("hehe", "@hehe123");
        driver.get(BASE_URL + "/user/Order");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h1[contains(text(), 'Đơn hàng')]")));

        // Assume the first order is the latest one created from TC1
        WebElement orderItem = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("(//div[contains(@class, 'order-item')])[1]")));
        orderItem.click();

        try {
            Thread.sleep(2000); // Delay to allow order details to load
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify total amount spent is displayed
        WebElement totalAmount = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(text(), 'Tổng cộng')]/following-sibling::div[contains(text(), '199.000')]")));
        assertNotNull(totalAmount, "Total amount spent should be displayed for the order");
    }
}