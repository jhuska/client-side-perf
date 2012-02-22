package org.richfaces.performance;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.browsermob.core.har.Har;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
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
//        secondMethodPost();
//    }

    private void secondMethodPost(Har har) {
        try {
            URL url = new URL("http://localhost:5000/results/upload");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

            StringBuffer content = new StringBuffer();
            content.append(URLEncoder.encode("file", "UTF-8"));
//            content.append("file:");
            
//            File harFile = new File("exampleHar.har");
//            BufferedReader br = new BufferedReader(new FileReader(harFile));
//            String line = br.readLine();
//            while (line != null) {
//                content.append(URLEncoder.encode(line, "UTF-8"));
////                content.append(line);
//                line = br.readLine();
//            }
            
            httpCon.setRequestMethod("POST");
//            httpCon.setRequestProperty("Content-length", "33");
            httpCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            httpCon.setRequestProperty("Automated", "true");
            
            httpCon.setDoInput(true);
            httpCon.setDoOutput(true);
          
            //Send request
            DataOutputStream wr = new DataOutputStream (
                        httpCon.getOutputStream ());
            
            har.writeTo(wr);
            wr.flush();
            wr.close();
            
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            System.out.println(httpCon.getResponseCode());
            System.out.println(httpCon.getResponseMessage());
            out.close();
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    private void firstMethodPost() {
//        try {
//            URL url = new URL("http://localhost:5000/results/upload");
//            URLConnection conn = url.openConnection();
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            conn.setRequestProperty("Automated", "true");
//
//            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
//
//            StringBuffer content = new StringBuffer();
//            content.append(URLEncoder.encode("file=", "UTF-8"));
//            File harFile = new File(HAR);
//            BufferedReader br = new BufferedReader(new FileReader(harFile));
//            String line = br.readLine();
//            while (line != null) {
//                content.append(URLEncoder.encode(line, "UTF-8"));
//                line = br.readLine();
//            }
//
//            writer.writeBytes(content.toString());
//            writer.flush();
//            writer.close();
//
//            StringBuffer answer = new StringBuffer();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            while ((line = reader.readLine()) != null) {
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

        server.newHar("" + System.currentTimeMillis());

        webDriver
            .get("http://showcase.richfaces.org/richfaces/component-sample.jsf?demo=dataTable&sample=tableSorting&skin=blueSky");

//        webDriver.findElementByLinkText("Sort by Capital Name").click();
//        webDriver.findElementByLinkText("Sort by State Name").click();

        Har har = server.getHar();
        secondMethodPost(har);
        File saveHar = new File(System.currentTimeMillis() + ".har");
        har.writeTo(saveHar);
        
    }

}