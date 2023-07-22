package org.example.crawling;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
public class FileCrawling {
    public static void main(String[] args) {
        // WebDriver 경로
        Path path = Paths.get("/Users/lsm99/chromedriver_mac64", "chromedriver");

        // WebDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", path.toString());

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 브라우저 안 띄움 -> 리소스 절약 및 속도 향상
        options.addArguments("--start-maximized");
        options.addArguments("disable-popup-blocking");
        options.addArguments("disable-defult-apps");

        // WebDriver 객체 생성
        ChromeDriver driver = new ChromeDriver(options);

        // WebDriverWait 객체 생성
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1)); // 최대 1초 기다림

        // 원하는 사이트 주소 입력
        driver.get("https://unsplash.com/");

        // 웹 페이지가 완전히 로드될 때까지 기다림
        // 단, 최대 wait 에서 정의한 시간만큼 기다림
        wait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));

        // imgDownloads 경로의 폴더 생성
        File downloadsFolder = new File("/Users/lsm99/Downloads/imgDownloads");
        if (!downloadsFolder.exists()) {
            if (downloadsFolder.mkdir()) {
                log.info("imgDownloads 폴더를 생성하였습니다.");
            } else {
                log.error("imgDownloads 폴더 생성에 실패하였습니다.");
                return;
            }
        }

        int fileCount = 1; // 파일 번호를 나타내는 변수
        for (int i = 1; i <= 100;) { // 이미지 100개 다운로드
            try {
                WebElement visibleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[@class='MorZF']/img)["+i+"]")));

                String src = visibleElement.getAttribute("src");

                // images.unsplash.com/photo-라는 내용이 없으면 이미지가 아님, 건너뜀
                if (!src.contains("images.unsplash.com/photo-")) {
                    continue;
                }

                // 이미지 다운로드
                BufferedImage saveImage;
                try {
                    saveImage = ImageIO.read(new URL(src));
                } catch (IOException e) {
                    log.error("이미지를 다운로드하는 동안 오류가 발생하였습니다. URL: {}", src);
                    continue;
                }

                if (saveImage != null) {
                    try {
                        String fileName = "image" + fileCount++; // 파일 이름에 번호를 추가
                        File file = new File("/Users/lsm99/Downloads/imgDownloads/" + fileName + ".jpg");
                        ImageIO.write(saveImage, "jpg", file);
                        log.info("{} 파일을 저장하였습니다.", file.getName());
                        i++; // 이미지 다운로드에 성공한 후에만 이미지 인덱스를 증가
                    } catch (IOException e) {
                        log.error("파일을 저장하는 동안 오류가 발생하였습니다. 파일 이름: image{}", fileCount - 1);
                    }
                }
            } catch (TimeoutException e) {
                log.error("Element not visible after 5 seconds: {}", e.getMessage());

                // 스크롤 하는 부분
                // 웹 페이지가 길 경우 스크롤을 해줘야 이미지가 로드돼서 많은 이미지를 다운받을 수 있음
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,1000)");

                // 스크롤 후, 페이지 로드를 기다리기 위해 잠시 대기
                // 단, 최대 scrollWait 에서 정의한 시간만큼 기다림
                WebDriverWait scrollWait = new WebDriverWait(driver, Duration.ofSeconds(100));
                scrollWait.until(ExpectedConditions.jsReturnsValue("return document.readyState==\"complete\";"));
            }
        }

        // 탭 종료
        driver.close();
        // 드라이버 종료
        driver.quit();
    }
}
