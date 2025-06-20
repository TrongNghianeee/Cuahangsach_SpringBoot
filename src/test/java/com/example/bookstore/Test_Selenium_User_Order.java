package com.example.bookstore;

//import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Test_Selenium_User_Order {

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
    
   // Helper method to perform login
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
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    driver.get(BASE_URL + "/user/Order");

    wait.until(ExpectedConditions.presenceOfElementLocated(
        By.xpath("//h1[contains(text(),'Đơn hàng')]")
    ));
}


    @Test
    @DisplayName("User views list of orders")
    void testUserViewsOrderList() {
        login("hehe", "@hehe123");

        // Verify orders exist
        List<WebElement> orderItems = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
            By.xpath("//h3[contains(text(),'Đơn hàng #')]")
        ));
        assertFalse(orderItems.isEmpty(), "Order list should be displayed");
    }

    @Test
    @DisplayName("User views order details")
    void testViewOrderDetails() {
        login("hehe", "@hehe123");

        WebElement viewDetailButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Xem chi tiết')]")));
        viewDetailButton.click();

        WebElement orderDetailHeader = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h3[contains(text(), 'Chi tiết đơn hàng #')]")));
        assertTrue(orderDetailHeader.isDisplayed(), "Order details should be displayed");

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Đóng')]")));
        closeButton.click();
    }

    @Test
    @DisplayName("User cancels an order if possible")
    void testCancelOrderIfPossible() {
        login("hehe", "@hehe123");
        List<WebElement> cancelButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//button[contains(text(), 'Hủy đơn hàng')]")));
        if (cancelButtons.isEmpty()) {
            System.out.println("No cancellable orders found — skipping cancel test.");
            return; // Nothing to test
        }

        cancelButtons.get(0).click();

        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        WebElement canceledStatus = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), 'Đã hủy')]")));
        assertTrue(canceledStatus.isDisplayed(), "Order status should be updated to 'Đã hủy'");
    }
        @Test
    @DisplayName("User checks visible status texts")
    void testVisibleStatuses() {
        login("hehe", "@hehe123");
        
        // Wait for orders to be loaded first
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h3[contains(text(),'Đơn hàng #')]")
        ));
        
        // Try to find status spans with more flexible selector based on the actual HTML structure
        List<WebElement> statusSpans = null;
        
        // Try multiple selectors to find status elements
        try {
            // First try: Look for spans with status-related classes or content
            statusSpans = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//span[contains(@class, 'inline-flex') and contains(@class, 'items-center')]")));
        } catch (Exception e1) {
            try {
                // Second try: Look for any span containing status text
                statusSpans = driver.findElements(By.xpath("//span[contains(text(), 'Đang xử lý') or contains(text(), 'Đã giao') or contains(text(), 'Đã hủy')]"));
            } catch (Exception e2) {
                try {
                    // Third try: Look for spans within order list items
                    statusSpans = driver.findElements(By.xpath("//li[contains(@class, 'px-6')]//span"));
                } catch (Exception e3) {
                    // Final fallback: Look for any elements with status text content
                    statusSpans = driver.findElements(By.xpath("//*[contains(text(), 'Đang xử lý') or contains(text(), 'Đã giao') or contains(text(), 'Đã hủy') or contains(text(), 'Không xác định')]"));
                }
            }
        }

        assertFalse(statusSpans.isEmpty(), "There should be at least one status element");
        
        System.out.println("Found " + statusSpans.size() + " status elements");
        
        statusSpans.forEach(span -> {
            String statusText = span.getText().trim();
            System.out.println("Order status: '" + statusText + "'");
            assertTrue(statusText.length() > 0, "Status text should not be empty");
            
            // More flexible status validation - check if text contains expected status words
            boolean isValidStatus = statusText.contains("Đang xử lý") || 
                                  statusText.contains("Đã giao") || 
                                  statusText.contains("Đã hủy") || 
                                  statusText.contains("Không xác định") ||
                                  statusText.equals("Đang xử lý") || 
                                  statusText.equals("Đã giao") || 
                                  statusText.equals("Đã hủy") || 
                                  statusText.equals("Không xác định");
            
            assertTrue(isValidStatus, 
                "Status should contain one of the expected values. Found: '" + statusText + "'");
        });
    }

    // @Test
    // @DisplayName("User checks total order count")
    // void testEstimateTotalOrderCount() {
    //     login("hehe", "@hehe123");
    //     List<WebElement> orders = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
    //             By.cssSelector("li.px-6.py-6")));

    //     int count = orders.size();
    //     System.out.println("Total orders found: " + count);

    //     assertTrue(count >= 0, "Count should be >= 0");
    //     assertTrue(count <= 100, "Count should be reasonable (e.g., <= 100)"); // Optional upper bound
    // }
}
