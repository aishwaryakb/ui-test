package com.bren.qa.testcases;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.bren.qa.base.Base;
import com.bren.qa.pages.DifferentDocumentCategoriesPage;
import com.bren.qa.pages.DocumentsPage;
import com.bren.qa.pages.LaunchPage;
import com.bren.qa.pages.LoginPage;
import com.bren.qa.pages.OtpVerificationPage;
import com.bren.qa.pages.SingleApartmentHomePage;
import com.bren.qa.pages.ViewADocumentPage;
import com.bren.qa.report.ExtentManager;

public class MyApartmentDocumentsPageTest extends Base {
	LaunchPage launchPage;
	LoginPage loginPage;
	OtpVerificationPage otpVerificationPage;
	SingleApartmentHomePage myHomePage;
	DocumentsPage docsPage;
	ViewADocumentPage viewADocumentPage;
	DifferentDocumentCategoriesPage differentDocumentCategoriesPage;
	DifferentDocumentCategoriesPage differentDocumentCategoriesPageNext;
	String expectedPageHeading = "Documents";
	
	public MyApartmentDocumentsPageTest() {
		super();
	}
	@BeforeMethod
	public void setup() throws MalformedURLException, InterruptedException {
		initialization();
		launchPage = new LaunchPage();
		loginPage = launchPage.clickSignInButton();
		otpVerificationPage = loginPage.enterNumber(prop.get("number").toString());
		Thread.sleep(8000);
		driver.manage().timeouts().implicitlyWait(240, TimeUnit.SECONDS);
        driver.findElementByXPath("//*[@text = 'Enter OTP']");
		myHomePage = otpVerificationPage.inputOtp(prop.getProperty("otp").toString());
		differentDocumentCategoriesPage = myHomePage.clickDocuments();
		Thread.sleep(5000);
		differentDocumentCategoriesPageNext = differentDocumentCategoriesPage.clickNextDocument();
		docsPage = differentDocumentCategoriesPage.clickDocument();
	}
	@Test(priority = 1)
	public void documentsViewVerification() throws IOException, InterruptedException {
		Thread.sleep(5000);
		viewADocumentPage = docsPage.clickOnOneDoc();
		Thread.sleep(8000);
		boolean isDocDisplayed = viewADocumentPage.isDocumentDisplayed();
		Assert.assertTrue(isDocDisplayed, "Document isn't opened");
		ExtentManager.getExtentTest().log(Status.PASS, "Document Viewing is verified");
	}
	// pending @Test(priority = 3)
	public void documentDownloadVerification() throws IOException, InterruptedException {
		Thread.sleep(3000);
		docsPage.clickDownload();
		driver.findElementByXPath("//*[@text = 'ALLOW']").click();
		driver.manage().timeouts().implicitlyWait(60,TimeUnit.SECONDS);
		String  actualtoastMessage = driver.findElementByXPath("//android.widget.Toast[1]").getAttribute("name");
		System.out.print(actualtoastMessage);
		Thread.sleep(20000);
		String fileName = driver.findElementByXPath("//android.widget.TextView[@index = '1']").getAttribute("text");
		byte[] fileBase64 = driver.pullFile("/storage/emulated/0/Android/data/com.brencorp.play.mybren/files/Download/"+fileName);
		Assert.assertTrue(fileBase64.length > 0, "Document downloaded was unsuccessfull");
		ExtentManager.getExtentTest().log(Status.PASS, "Document downloaded successfully");
	}
	@Test(priority = 2)
	public void documentShareVerification() throws IOException, InterruptedException {
		Thread.sleep(5000);
		docsPage.clickShare();
		driver.findElementByXPath("//*[@text = 'ALLOW']").click();
		driver.manage().timeouts().implicitlyWait(60,TimeUnit.SECONDS);
		boolean shareTitle = driver.findElementByXPath("//*[@text = 'Share']").isDisplayed();
		Assert.assertTrue(shareTitle, "Not able to share the document");
		ExtentManager.getExtentTest().log(Status.PASS, "Able to share the document");
	}
	@Test(priority = 4)
	public void documentPrintVerification() throws IOException, InterruptedException {
		Thread.sleep(5000);
		docsPage.clickPrint();
		driver.manage().timeouts().implicitlyWait(120,TimeUnit.SECONDS);
		boolean selectAPrinterTitle = driver.findElementByXPath("//*[@text = 'Select a printer']").isDisplayed();
		Assert.assertTrue(selectAPrinterTitle, "Not Able to print the document");
		ExtentManager.getExtentTest().log(Status.PASS, "Able to Print the document");
	}
	@AfterMethod()
	public void tearDown() {
		driver.quit();
	}
}
