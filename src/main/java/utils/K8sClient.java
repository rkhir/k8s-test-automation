package utils;

import client.K8sClientConfig;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.Readiable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class K8sClient {
  private static final Logger logger = LoggerFactory.getLogger(K8sClient.class);
  private KubernetesClient client;
  // private String namespace;

  public K8sClient(String namespace) {
    // this.namespace = namespace;
    this.client = new K8sClientConfig().getClient();
  }

  public KubernetesClient getRawClient() {
    return client;
  }

  public List<Service> getNamespacedServices() {

    return client.services().list().getItems();
  }

  public List<Deployment> getNamespacedDeployment() {
    logger.debug("Getting all Deployments in {} namespace", client.getNamespace());
    return client.apps().deployments().list().getItems();
  }

  public List<Namespace> getNameSpaces() {
    logger.debug("Getting all Namespaces");
    return client.namespaces().list().getItems();
  }

  public List<Pod> getNamespacedPods() {
    logger.debug("Getting all Pods in {} namespace", client.getNamespace());

    return client.pods().list().getItems();
  }

  public Pod getPod(String podName) {
    logger.debug("Getting a {} pod in {} namespace", podName, client.getNamespace());

    return client.pods().withName(podName).get();
  }

  public PodResource<Pod, DoneablePod> getPodResource(String podName) {
    logger.debug("Getting a {} pod in {} namespace", podName, client.getNamespace());

    return client.pods().withName(podName);
  }

  public Container getContainer(String containerName) {
    logger.debug("Getting container {}, in {} namespace", containerName, client.getNamespace());

    List<Container> containers =
        client.pods().list().getItems().stream()
            .map(pod -> pod.getSpec().getContainers())
            .findFirst()
            .get();

    Container container =
        containers.stream()
            .filter(container1 -> container1.getName().equals(containerName))
            .findFirst()
            .get();
    return container;
  }

  public String getContainersLog(String podName, String containerName) {
    logger.debug(
        "Getting container log {}, from {} pod in {} namespace",
        containerName,
        podName,
        client.getNamespace());

    return client.pods().withName(podName).inContainer(containerName).getLog(true);
  }

  public List<Container> getAllPodContainers(String podName) {
    return client.pods().withName(podName).get().getSpec().getContainers();
  }

  public List<EnvVar> getContainerEnvVars(String containerName) {
    logger.debug("Getting All Env variables  for container {},", containerName);
    return this.getContainer(containerName).getEnv();
  }

  public Service getService(String serviceName) {
    logger.debug("Getting {} service in {} namespace", serviceName, client.getNamespace());

    return client.services().withName(serviceName).get();
  }

  public Deployment getDeployment(String deploymentName) {
    logger.debug("Getting {} deployment in {} namespace", deploymentName, client.getNamespace());

    return client.apps().deployments().withName(deploymentName).get();
  }

  public <T extends HasMetadata> ObjectMeta getObjMetadata(T obj) {

    return obj.getMetadata();
  }

  public Secret getSecret(String secretName) {

    return client.secrets().withName(secretName).get();
  }

  public <T extends Readiable> Boolean isReady(T obj) {
    logger.debug("Checking if {} is ready", obj.toString());

    return obj.isReady();
  }

  public Job getJobs(String namespace, String jobName) {
    logger.debug("Getting job {} in {} namespace", jobName, namespace);

    return client.batch().jobs().inNamespace(namespace).withName(jobName).get();
  }
}
