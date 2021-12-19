package fr.neyox.autovideo.tiktok;

import fr.neyox.autovideo.Main;
import fr.neyox.autovideo.exceptions.LoginException;
import fr.neyox.autovideo.exceptions.UploadException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Getter
public class TiktokUploader {

    private final String name;
    private final File video;
    private ScheduledFuture<?> uploadTask;

    static {
        System.setProperty("webdriver.chrome.driver", ".\\lib\\chromedriver.exe");
    }

    public void upload() throws UploadException, LoginException {
        System.out.println("Starting to uplaod (" + getVideo().getName()+")");
        WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--disable-gpu","--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080", "--ignore-certificate-errors", "--start-maximized", "--proxy-bypass-list=*", "--proxy-server='direct://'", "--disable-extensions", "--allow-running-insecure-content", "user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36").setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe"));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get("https://www.tiktok.com/upload?lang=fr");
        System.out.println("Prepare to login");
        TikTokLogin login = new TikTokLogin("", "", TikTokLogin.LoginType.FACEBOOK);
        login.login(driver);
        driver = driver.switchTo().window(driver.getWindowHandles().toArray(new String[]{})[0]);
        WebDriverWait wait = new WebDriverWait(driver, 150);
        WebDriver finalDriver = driver;
        wait.until(f-> {
            System.out.println("Preparing to upload video");
                String nameHtml = ".DraftEditor-editorContainer>div";
                new Actions(finalDriver).moveToElement( finalDriver.findElement(By.cssSelector(nameHtml))).perform();
                finalDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                finalDriver.findElement(By.cssSelector(".button-wrapper.jsx-2658524077 button.jsx-2658524077")).click();
                finalDriver.findElement(By.cssSelector(nameHtml)).sendKeys(this.name);
                finalDriver.findElement(By.cssSelector(".upload-btn-input.jsx-1828163283")).sendKeys(this.video.getAbsolutePath());
            this.uploadTask = Main.scheduler.scheduleAtFixedRate(()->{
                if (finalDriver.getCurrentUrl().contains("https://www.tiktok.com/upload")){
                    finalDriver.findElements(By.tagName("button")).stream().filter(webElement -> webElement.getText().equals("Publier")).forEach(WebElement::click);

                    try {
                        if (finalDriver.findElement(By.cssSelector(".modal-btn.jsx-1801464094"))!=null){
                            uploadTask.cancel(true);
                            System.out.println("Upload finish !");
                            finalDriver.close();
                        }
                    } catch (Exception ignored){}

                }

            }, 0, 5, TimeUnit.SECONDS);

            return f;
        });

    }

}
