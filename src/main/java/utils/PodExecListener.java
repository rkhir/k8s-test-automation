package utils;

import io.fabric8.kubernetes.client.dsl.ExecListener;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PodExecListener implements ExecListener {
  private static final Logger logger = LoggerFactory.getLogger(K8sClient.class);

  @Override
  public void onOpen(Response response) {
    logger.debug("The Stream is now Open to the Pod ");
  }

  @Override
  public void onFailure(Throwable t, Response response) {
    logger.debug("Exception " + t.getMessage() + t.getStackTrace());
  }

  @Override
  public void onClose(int code, String reason) {
    logger.debug("The Stream is Closed ");
  }
}
