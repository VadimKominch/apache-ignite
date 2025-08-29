package org.example.scheduler;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.example.model.RevokedCertificate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CertificateTask {
    private final Ignite ignite;
    private final RestTemplate restTemplate;

    public CertificateTask(Ignite ignite, RestTemplate restTemplate) {
        this.ignite = ignite;
        this.restTemplate = restTemplate;
    }

    public void loadRevokedCertificateificatesDistributed(RevokedCertificate[] certs) {
        IgniteCache<String, RevokedCertificate> cache = ignite.cache("revoked-certs");

        // Разбиваем массив на подзадачи по 1000 элементов
        List<List<RevokedCertificate>> batches = Lists.partition(Arrays.asList(certs), 1000);

        // Распространяем обработку по всем серверным узлам
        ignite.compute().broadcast(() -> {
            IgniteCache<String, RevokedCertificate> localCache = ignite.cache("revoked-certs");

            for (List<RevokedCertificate> batch : batches) {
                Map<String, RevokedCertificate> map = batch.stream()
                        .collect(Collectors.toMap(
                                c -> c.getSerialNumber() + "|" + c.getSerialNumber(),
                                Function.identity()
                        ));
                localCache.putAll(map);
            }

            System.out.println("Batch processed on node: " + ignite.cluster().localNode().id());
        });
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void scheduledLoad() {
        RevokedCertificate[] certs = restTemplate.getForObject(
                "https://government.example.com/api/revoked-certs",
                RevokedCertificate[].class
        );

        if (certs != null && certs.length > 0) {
            loadRevokedCertificateificatesDistributed(certs);
        }
    }
}
