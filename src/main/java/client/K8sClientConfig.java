package client;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class K8sClientConfig {

  private KubernetesClient client;
  private static final String NAMESPACE = System.getenv().getOrDefault("namespace", "default");

  public K8sClientConfig() {
    Config config = new ConfigBuilder().withNamespace(NAMESPACE).build();
    this.client = new DefaultKubernetesClient(config);
  }

  public KubernetesClient getClient() {
    return client;
  }
}
