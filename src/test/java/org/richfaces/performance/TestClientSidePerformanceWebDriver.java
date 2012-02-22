package org.richfaces.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.browsermob.core.har.Har;
import org.browsermob.proxy.ProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestClientSidePerformanceWebDriver {

    protected FirefoxDriver webDriver;
    protected ProxyServer server;

    protected final String HOST = "localhost";
    protected final String PORT = "5000";
    protected final String PATH = "/results/upload";
    protected final String METHOD = "POST";

    protected final String CORRECT_HAR = "exampleHar.har";
        
    @BeforeClass
    public void startProxyServer() throws Exception {

        server = new ProxyServer(4444);
        server.start();

        Proxy proxy = server.seleniumProxy();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, proxy);

        webDriver = new FirefoxDriver(capabilities);
    }

    public String send(String data) throws Exception {

        // Connection
        URL url = new URL("http://" + HOST + ":" + PORT + PATH);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(METHOD);

        connection.setDoOutput(true);

        // HTTP headers
        connection.setRequestProperty("Automated", "true");

        // Send data to server
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

        data = URLEncoder.encode(data, "utf-8");

        wr.write("file=" + data);
        wr.flush();
        wr.close();

        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        String response = "";
        while ((line = rd.readLine()) != null) {
            response += line;
        }

        rd.close();

        connection.disconnect();

        return response;
    }

    private void sendHar(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));

        StringBuffer sbf = new StringBuffer();
        String line = bfr.readLine();
        if (line != null) {
            sbf.append(line);
            line = bfr.readLine();
        }

        System.out.println("Response: " + send(sbf.toString()));
    }
    
    @Test
    public void testClientSidePerformance() throws Exception {

        server.newHar("showcase");

        webDriver
            .get("http://showcase.richfaces.org/richfaces/component-sample.jsf?demo=dataTable&sample=tableSorting&skin=blueSky");

        // webDriver.findElementByLinkText("Sort by Capital Name").click();
        // webDriver.findElementByLinkText("Sort by State Name").click();

        Har har = server.getHar();
        File saveHar = new File(System.currentTimeMillis() + ".har");
        har.writeTo(saveHar);
        
        //send correct har
//        sendHar(new File(CORRECT_HAR));
        
        sendHar(saveHar);
    }

}