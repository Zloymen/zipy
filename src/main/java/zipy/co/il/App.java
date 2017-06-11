package zipy.co.il;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;

import java.net.Socket;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;


/**
 *
 *
 */
public class App{
    private static Logger LOG = LoggerFactory.getLogger(App.class);


    public static void main( String[] args ) throws IOException, URISyntaxException {
        DOMConfigurator.configure( "log4j2.xml");
        LOG.info("logger configure!");

        BruteForce bruteForce = new BruteForce();
        try {
            LOG.info("bruteForce run!");
            bruteForce.run();
        }catch (KeyManagementException | NoSuchAlgorithmException e){
            LOG.error("Error in config ingnore SSL", e);
        }
    }


}
