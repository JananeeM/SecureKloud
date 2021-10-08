package secureKloudTestcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FlipKart {
	ChromeDriver driver;
	JavascriptExecutor js;

	@BeforeMethod
	public void logIn() {

		// Initiating webdriver to download driver files
		WebDriverManager.chromedriver().setup();

		// setting up chrome driver
		driver = new ChromeDriver();
		// Initiating the java script executer
		js = (JavascriptExecutor) driver;
		// Implicit time out to locate the webpage and webelements
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.get("http://www.flipkart.com");
		// maximizes the window
		driver.manage().window().maximize();

	}

	@Test(dataProvider = "userDIdandLogin")
	public void flipKartOrder(String uName, String pwd) throws InterruptedException {
		// Login Id is entered
		WebElement loginId = driver.findElement(By.xpath("(//div[@class='IiD88i _351hSN']/input)[1]"));// .sendKeys(
		loginId.sendKeys(uName);
		// Checking whether login field is empty
		if (loginId.getAttribute("value").equals("")) {
			System.out.println("Login ID should be entered.Field Is empty");
		}

		// Password is inputted from Excel file
		WebElement password = driver.findElement(By.xpath("(//div[@class='IiD88i _351hSN']/input)[2]"));// .sendKeys("Jananee@1990");
		password.sendKeys(pwd);

		// Checking if password field is empty
		if (password.getAttribute("Value").equals("")) {
			System.out.println("Password should be entered.Field Is empty");
		}

		// Locating Login Button
		driver.findElement(By.xpath("//button[@class='_2KpZ6l _2HKlqd _3AWRsL']")).click();
		// Getting the Title for the current Page
		String titleOfPage = driver.getCurrentUrl();
		System.out.println("The Title of current page is :" + titleOfPage);
		// Thread.sleep() is used since more time is taken for the webelements to load
		Thread.sleep(2000);
		// selecting Electronics Option from the menu
		driver.findElement(By.xpath("(//div[@class='CXW8mj']//img)[3]")).click();
		WebElement electronics = driver.findElement(By.xpath("//span[text()='TVs & Appliances']"));
		// builder class is used for moving the mouse to the webelement
		Actions builder = new Actions(driver);
		builder.moveToElement(electronics).perform();
		String titleElectronics = driver.getTitle();
		System.out.println(driver.getTitle());

		// Verifying the title of electronics page to check if page navigation is correct
		if (titleElectronics.equals(
				"Online Shopping Site for Mobiles, Electronics, Furniture, Grocery, Lifestyle, Books & More. Best Offers!")) {
			System.out.println("Title Verified");
		}

		// Locating and clicking Inverter AC
		driver.findElement(By.xpath("//a[@title='Inverter AC']")).click();
		driver.findElement(By.xpath("(//div[@class='col col-7-12'])[2]")).click();
		// Webpage is taking too long to load hence using Thread.sleep();
		Thread.sleep(2000);
		// A new window is open hence getting the window handle in a set
		Set<String> windowHandles = driver.getWindowHandles();
		// Converting set to list so that navigation control can be switched to the
		// second window
		List<String> winHandle = new ArrayList<String>(windowHandles);
		// switches the control to the newly opened window
		driver.switchTo().window(winHandle.get(1));
		// Updating the pincode to check for delivery options
		WebElement pinCode = driver.findElement(By.xpath("//input[@placeholder='Enter Delivery Pincode']"));
		pinCode.sendKeys("603210");
		// Clicking add to cart
		driver.findElement(By.xpath("//span[text()='Check']")).click();
		driver.findElement(By.xpath("//button[@class='_2KpZ6l _2U9uOA _3v1-ww']")).click();
		Thread.sleep(5000);
		// JavScript Executor is used to scroll until the webelement is visible
		WebElement Element = driver.findElement(By.xpath("(//div[@class='_3dY_ZR']//button)[2]"));
		js.executeScript("arguments[0].scrollIntoView();", Element);
		Element.click();

		// WebPage is taking too long to load hence using Thread,sleep()
		Thread.sleep(2000);
		// Getting and printing Discounted Amount
		String discountPrice = driver.findElement(By.xpath("//div[@class='_1YVZr_']")).getText();
		System.out.println("THe Discount amount for the items in the cart is :" + discountPrice);
		// Printing the total Amount
		String amountPayable = driver.findElement(By.xpath("//div[@class='Ob17DV _3X7Jj1']//span[1]")).getText();
		System.out.println("TOtal Amount is : " + amountPayable);
		// Checking the order quantity if greater than 5 then print error message
		String orderQuantity = driver.findElement(By.xpath("//input[@class='_253qQJ']")).getAttribute("value");
		System.out.println("Order Quantity  :" + orderQuantity);
		// converting String value to Int
		int quantity = Integer.parseInt(orderQuantity);
		if (quantity > 5) {
			System.out.println("Cannot add more than 5 Items");
		}
		Thread.sleep(5000);

		// Locating PlaceOrder webelement and Clicking it
		driver.findElement(By.xpath("//span[text()='Place Order']")).click();
		// Printing the Final Payable amount
		String finalAmount = driver.findElement(By.xpath("(//div[@class='_2Tpdn3'])[2]")).getText();
		System.out.println("Final Amount Payable is : " + finalAmount);
		// Removing the added Item from cart
		driver.findElement(By.xpath("//div[@class='_3dsJAO']//span")).click();
	}

	@DataProvider
	public String[][] userDIdandLogin() throws IOException {

		// Locating the workbook
		XSSFWorkbook wb = new XSSFWorkbook(
				"C:\\Users\\Vinoth\\OneDrive\\Documents\\Janu\\Workspace\\MavenProject\\Data\\SecureKloud_FlipKart.xlsx");
		// Locating the sheet
		XSSFSheet ws = wb.getSheet("Sheet1");
		// XSSFRow row = ws.getRow(0);
		// Getting LastRow number and LastCell Number
		int lastRowNum = ws.getLastRowNum();
		int lastCellNum = ws.getRow(0).getLastCellNum();
		// String array for storing the Data read from Excel
		String[][] data = new String[lastRowNum][lastCellNum];
		for (int i = 1; i <= lastRowNum; i++) {
			for (int j = 0; j < lastCellNum; j++) {

				// String data from excel to String array
				data[i - 1][j] = ws.getRow(i).getCell(j).getStringCellValue();
			}
		}
		// Closing the workbook
		wb.close();
		return data;
	}

	@AfterMethod
	public void logOff() {
		// closing the windows
		driver.quit();
	}

}
