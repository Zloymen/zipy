package zipy.co.il;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Zloy on 10.06.2017.
 */
public interface IBruteForce {
    void stop();
    void run() throws KeyManagementException, NoSuchAlgorithmException;
    void sendOutcastWorker(WorkerThread worker);
    void sendPassword(String password);
}
