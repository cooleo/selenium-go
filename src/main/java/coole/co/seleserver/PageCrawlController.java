package coole.co.seleserver;

import java.util.concurrent.atomic.AtomicLong;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@RestController
public class PageCrawlController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/google")
    public PageResponse google(@RequestParam(value = "name", defaultValue = "World") String name) {

        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Google
        driver.get("http://www.google.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.google.com");

        // Find the text input element by its name
        WebElement element = driver.findElement(By.name("q"));

        // Enter something to search for
        element.sendKeys("Cheese!");

        // Now submit the form. WebDriver will find the form for us from the element
        element.submit();

        // Check the title of the page
        System.out.println("Page title is: " + driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("cheese!");
            }
        });

        // Should see: "cheese! - Google Search"
        System.out.println("Page title is: " + driver.getTitle());
        String title = driver.getTitle();

        //Close the browser
        driver.quit();


        return new PageResponse(counter.incrementAndGet(),
                String.format(template, title));
    }

    @RequestMapping("/youtube")
    public PageResponse youtube(@RequestParam(value = "name", defaultValue = "World") String name) {
        String html = null;
        try {
            WebDriver driver = new FirefoxDriver();
            // And now use this to visit Google
            driver.get("https://www.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ/videos");

            do {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Printing Some Text");
                // WebDriverWait wait = new WebDriverWait(driver, 5000);
                // wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("browse-items-load-more-button")));
                driver.findElement(By.className("browse-items-load-more-button")).click();
                WebDriverWait wait13 = new WebDriverWait(driver, 5000);
                wait13.until(ExpectedConditions.visibilityOfElementLocated(By.className("browse-items-load-more-button")));
                html = driver.getPageSource();

            }
            while (driver.findElement(By.className("browse-items-load-more-button")).isDisplayed());
            //Close the browser
            driver.quit();

            return new PageResponse(counter.incrementAndGet(),
                    String.format(template, html));
        } catch (Exception ex) {

            System.out.println("ex:" + ex.getMessage().toString());
            return new PageResponse(counter.incrementAndGet(),
                    String.format(template, html));
        }
    }
}
