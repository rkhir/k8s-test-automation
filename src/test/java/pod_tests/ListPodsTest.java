package pod_tests;

import client.K8sClient;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.junit.Before;
import org.junit.Test;
import utils.ClientHelper;

import java.util.ArrayList;
import java.util.List;

public class ListPodsTest {
    private KubernetesClient client;
    private K8sClient k8sClient= new K8sClient();
    private ClientHelper helper;
    @Before
    public void setup(){

        this.client = k8sClient.getClient();
        this.helper = new ClientHelper(k8sClient);
    }

    @Test
    public void getAllPods(){

        helper.getNameSpaces().forEach(item -> System.out.printf("%s namespaces %s %s \n",item.getKind(),item.getStatus().getPhase(),item.getMetadata().getName()));
        helper.getNamespacedPods("openfaas").forEach(item -> System.out.printf("%s pods %s %s \n",item.getKind(),item.getStatus().getPhase(),item.getMetadata().getName()));

        helper.getNamespacedServices("openfaas").forEach(item -> System.out.printf("%s Service %s %s \n",item.getKind(),item.getStatus(),item.getMetadata().getName()));;
        //NamespaceList list =  client.namespaces().list();
        //list.getItems().forEach(item -> System.out.printf("%s batata %s %s \n",item.getKind(),item.getStatus().getPhase(),item.getMetadata().getName()));
        //PodList podList =client.pods().inNamespace("default").list();
        //System.out.println(podList.getItems().size() + " size");
        //podList.getItems().forEach(item-> System.out.printf("%s tomato %s %s \n",item.getKind(),item.getSpec().getInitContainers().get(0).getImage()
               // ,item.getMetadata().getName()));


    }
}
