package org.richfaces.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.browsermob.core.har.Har;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestClientSidePerformanceWebDriver {

//    @Drone
    protected FirefoxDriver webDriver;
//    
    protected ProxyServer server;

//    @Deployment(testable = false)
//    public static WebArchive createTestArchive() {
//
//        WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, new File("target/showcase.war"));
//        return war;
//    }

    @BeforeClass
    public void startProxyServer() throws Exception {

        server = new ProxyServer(4444);
        server.start();

        Proxy proxy = server.seleniumProxy();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, proxy);

        webDriver = new FirefoxDriver(capabilities);
    }

    @Test
    public void testClientSidePerformance() throws IOException {

        server.newHar("showcase");

        webDriver.get("http://showcase.richfaces.org/richfaces/component-sample.jsf?demo=dataTable&sample=tableSorting&skin=blueSky");
        
        webDriver.findElementByLinkText("Sort by Capital Name").click();
        webDriver.findElementByLinkText("Sort by State Name").click();
        
        Har har = server.getHar();
        File newHar = new File("showcase.har");
        if(newHar.exists()) {
            newHar.delete();
        }
        
        FileOutputStream fout = new FileOutputStream(newHar);
        har.writeTo(fout);

        fout.close();
    }

}