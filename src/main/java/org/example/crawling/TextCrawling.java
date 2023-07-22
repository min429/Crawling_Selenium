package org.example.crawling;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TextCrawling {

    public static void main(String[] args) {

        // WebDriver 경로
        Path path = Paths.get("/Users/lsm99/chromedriver_mac64", "chromedriver");

        // WebDriver 설정
        System.setProperty("webdriver.chrome.driver", path.toString());

        // WebDriver 옵션 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking"); // 팝업안띄움
        options.addArguments("--headless"); // 브라우저 안띄움
        options.addArguments("--disable-gpu"); // gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); // 이미지 다운 안받음

        // WebDriver 객체 생성
        ChromeDriver driver = new ChromeDriver(options);

        // 빈 탭 생성
        driver.executeScript("window.open();");

        // 탭 목록 가져오기
        List<String> tabs = new ArrayList<>(driver.getWindowHandles());

        // 첫번째 탭으로 전환
        driver.switchTo().window(tabs.get(0));

        // 웹페이지 요청
        driver.get("https://heodolf.tistory.com/101");

        // 웹페이지에서 글제목 가져오기
        WebElement page1_title = driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div[1]/div/h1"));
        if(page1_title != null) {
            log.info("제목: {}", page1_title.getText());
        }
        // 웹페이지 소스 출력
        //System.out.println( driver.getPageSource() );

        // 탭 종료
        driver.close();



        // 두번째 탭으로 전환
        driver.switchTo().window(tabs.get(1));

        // 웹페이지 요청
        driver.get("https://heodolf.tistory.com/102");

        // 웹페이지에서 글제목 가져오기
        WebElement page2_title = driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div[1]/div/h1"));
        if(page1_title != null) {
            log.info("제목: {}", page2_title.getText());
        }

        // 웹페이지 소스 출력
        //System.out.println( driver.getPageSource() );

        // 탭 종료
        driver.close();

        // 5초 후에 WebDriver 종료
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // WebDriver 종료
            driver.quit();
        }
    }
}