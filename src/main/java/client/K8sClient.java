package client;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class K8sClient {

    private KubernetesClient client;

    public K8sClient (){

        this.client = new DefaultKubernetesClient();
    }

    public KubernetesClient getClient() {
        return client;
    }
}
