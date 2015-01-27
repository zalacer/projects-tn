package tn;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import tn.JsonBean;

public class HelloWorldIT {
    private static String endpointUrl;

    @BeforeClass
    public static void beforeClass() {
        endpointUrl = "http://localhost:8080/JAX-RSdemo1";
        //endpointUrl = System.getProperty("service.url");
    }

    @Test
    public void testPing() throws Exception {
        WebClient client = WebClient.create(endpointUrl + "/hello/echo/SierraTangoNevada");
        Response r = client.accept("text/plain").get();
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
        String value = IOUtils.toString((InputStream)r.getEntity());
        assertEquals("SIERRATANGONEVADA", value);
    }

    @Test
    public void testJsonRoundtrip() throws Exception {
    	
        List<Object> providers = new ArrayList<Object>();        
        
        providers.add(new org.codehaus.jackson.jaxrs.JacksonJsonProvider());      
        
        JsonBean inputBean = new JsonBean();       
        
        inputBean.setVal1("Maple");
        
        WebClient client = WebClient.create(endpointUrl + "/hello/jsonBean", providers);
        // Webclient can connect to a web service URL
        Response r = client.accept("application/json")
            .type("application/json")
            .post(inputBean);        
        // response is from the server
        
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());       
        // response.Status.OK means HTTP 200
        
        MappingJsonFactory factory = new MappingJsonFactory();
        JsonParser parser = factory.createJsonParser((InputStream)r.getEntity());
        JsonBean output = parser.readValueAs(JsonBean.class);
        assertEquals("Maple", output.getVal2());
    }
}
