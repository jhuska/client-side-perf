package org.richfaces.performance;

import java.io.File;
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

    // @Drone
    protected FirefoxDriver webDriver;
    //
    protected ProxyServer server;

    // @Deployment(testable = false)
    // public static WebArchive createTestArchive() {
    //
    // WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, new File("target/showcase.war"));
    // return war;
    // }
    
    private final String HAR = "showcase.har";

    @BeforeClass
    public void startProxyServer() throws Exception {

        server = new ProxyServer(4444);
        server.start();

        Proxy proxy = server.seleniumProxy();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, proxy);

        webDriver = new FirefoxDriver(capabilities);
    }

//    @AfterClass
//    public void postHarToStorage() {
//
//        try {
//            URL url = new URL("http://127.0.0.1:5000/results/upload");
//            URLConnection conn = url.openConnection();
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setUseCaches (false);
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Automated", "true");
//            
//            DataOutputStream writer = new DataOutputStream( conn.getOutputStream() );
//            
//            StringBuffer content = new StringBuffer();
//            content.append(URLEncoder.encode("file:", "UTF-8"));
//            File harFile = new File(HAR);
//            BufferedReader br = new BufferedReader(new FileReader(harFile));
//            String line = br.readLine(); 
//            while( line != null ) {
//                content.append(URLEncoder.encode(line, "UTF-8"));
//                line = br.readLine();
//            }
//            
//            writer.writeBytes(content.toString());
//            writer.flush();
//            writer.close();
//            
//            StringBuffer answer = new StringBuffer();
//            BufferedReader reader = new BufferedReader( new InputStreamReader(conn.getInputStream()));
//            while((line = reader.readLine()) != null) {
//                answer.append(line);
//            }
//            
//            reader.close();
//            
//            System.out.println("################" + answer);
//            
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }

    @Test
    public void testClientSidePerformance() throws IOException {

        server.newHar("showcase");

        webDriver
            .get("http://showcase.richfaces.org/richfaces/component-sample.jsf?demo=dataTable&sample=tableSorting&skin=blueSky");

        webDriver.findElementByLinkText("Sort by Capital Name").click();
        webDriver.findElementByLinkText("Sort by State Name").click();

        Har har = server.getHar();
        File newHar = new File(HAR);
        if (newHar.exists()) {
            newHar.delete();
        }

        har.writeTo(newHar);
        newHar.createNewFile();
    }

}