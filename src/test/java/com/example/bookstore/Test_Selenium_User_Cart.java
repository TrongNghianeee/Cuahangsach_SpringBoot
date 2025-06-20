package com.example.bookstore;

//import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test_Selenium_User_Cart {

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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    @Test
    @Order(1)
    @DisplayName("TC1: User adds item to cart")
    void testUserAddsItemToCart() {
        // Log in and navigate to cart page
        login("hehe", "@hehe123");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
            Thread.sleep(3000); // Delay to allow server to process add to cart
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Navigate back to cart and verify item is added
        driver.get(BASE_URL + "/user/Cart");
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h3[contains(text(), 'Sapiens: Lược sử loài người')]")));
        WebElement sapiensItem = driver.findElement(By.xpath("//h3[contains(text(), 'Sapiens: Lược sử loài người')]"));
        assertNotNull(sapiensItem, "Item 'Sapiens: Lược sử loài người' should be added to cart");
    }    @Test
    @Order(2)
    @DisplayName("TC2: User views cart list")
    void testUserViewsCartList() {
        // Log in and add item to cart first to ensure cart has content
        login("hehe", "@hehe123");
        
        // Add item to cart to ensure we have something to view
        driver.get(BASE_URL + "/user");
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Thêm vào giỏ')]")));
        addToCartButton.click();

        // Handle the alert
        try {
            WebDriverWait waitForAlert = new WebDriverWait(driver, Duration.ofSeconds(2));
            waitForAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            Thread.sleep(1000);
        } catch (Exception e) {
            // If no alert is present, continue
        }

        try {
            Thread.sleep(3000); // Delay to allow server to process add to cart
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Now navigate to cart page and verify cart list is displayed
        driver.get(BASE_URL + "/user/Cart");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify cart list is displayed
        List<WebElement> cartItems = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//li[contains(@class, 'px-6')]/div")));
        assertFalse(cartItems.isEmpty(), "Cart list should be displayed");
    }

    @Test
    @Order(3)
    @DisplayName("TC3: User removes item from cart")
    void testUserRemovesItemFromCart() {
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
            Thread.sleep(3000); // Short delay after accepting alert
        } catch (Exception e) {
            // If no alert is present, continue (optional: log or handle differently)
        }

        driver.get(BASE_URL + "/user/Cart");
        // Find and click the first remove button
        List<WebElement> removeButtons = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//button[contains(., 'Xóa')]"))
        );
        assertFalse(removeButtons.isEmpty(), "Should have at least one Xóa button");

        WebElement firstRemove = removeButtons.get(0);
        firstRemove.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        wait.until(ExpectedConditions.stalenessOf(firstRemove));
    }    @Test
    @Order(4)
    @DisplayName("TC4: User updates item quantity in cart")
    void testUpdatesItemQuantityInCart() {
        // Log in and add item to cart first to ensure cart has content
        login("hehe", "@hehe123");
        
        // Add item to cart to ensure we have something to update
        driver.get(BASE_URL + "/user");
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Thêm vào giỏ')]")));
        addToCartButton.click();

        // Handle the alert
        try {
            WebDriverWait waitForAlert = new WebDriverWait(driver, Duration.ofSeconds(2));
            waitForAlert.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            Thread.sleep(1000);
        } catch (Exception e) {
            // If no alert is present, continue
        }

        try {
            Thread.sleep(3000); // Delay to allow server to process add to cart
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Now navigate to cart page and test quantity update
        driver.get(BASE_URL + "/user/Cart");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String bookTitle = "Sapiens: Lược sử loài người";
        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h3[contains(text(), '" + bookTitle + "')]")
        ));

        WebElement quantityContainer = title.findElement(By.xpath(
            "./ancestor::div[contains(@class,'flex-1')]//div[contains(@class,'flex') and .//span]"
        ));

        List<WebElement> buttons = quantityContainer.findElements(By.xpath(".//button"));
        WebElement minusButton = buttons.get(0); // First button is "-"
        WebElement plusButton = buttons.get(1);  // Second button is "+"
        WebElement quantitySpan = quantityContainer.findElement(By.xpath(".//span[contains(@class, 'text-center')]"));

        // Extract and validate initial quantity
        String initialQuantityText = quantitySpan.getText().trim(); // Get raw text first
        System.out.println("Raw initial quantity text: " + initialQuantityText); // Debug output
        String numericQuantityText = initialQuantityText.replaceAll("[^0-9]", ""); // Keep only digits
        System.out.println("Numeric initial quantity text: " + numericQuantityText); // Debug output
        assertFalse(numericQuantityText.isEmpty(), "Initial quantity should not be empty");

        int initialQuantity = Integer.parseInt(numericQuantityText);
        plusButton.click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    
        String increasedText = quantitySpan.getText().trim().replaceAll("[^0-9]", "");
        System.out.println("Increased quantity text: " + increasedText); // Debug output
        assertFalse(increasedText.isEmpty(), "Increased quantity should not be empty");
        int increased = Integer.parseInt(increasedText);
        assertEquals(initialQuantity + 1, increased, "Quantity should increase by 1");

        minusButton.click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String finalText = quantitySpan.getText().trim().replaceAll("[^0-9]", "");
        System.out.println("Final quantity text: " + finalText); // Debug output
        assertFalse(finalText.isEmpty(), "Final quantity should not be empty");
        int finalQuantity = Integer.parseInt(finalText);
        assertEquals(initialQuantity, finalQuantity, "Quantity should return to original");
    }
}