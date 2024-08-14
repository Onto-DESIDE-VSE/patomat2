package cz.vse.swoe.ontodeside.patomat2;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphRemover {

    private static final String URL = "https://termit.agentura-cas.cz/dss-test/sluzby/db-server/repositories/termit/statements";

    @Disabled
    @Test
    void removeGraphs() throws Exception {
        var trustManager = new X509ExtendedTrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
            }
        };
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        try (final HttpClient httpClient = HttpClient.newBuilder().sslContext(sslContext).build()) {
            Files.readAllLines(new File("/home/kidney/Downloads/graphs-to-remove.csv").toPath()).forEach(g -> {
                System.out.println("Dropping graph <" + g + ">");
                final HttpRequest req = HttpRequest.newBuilder()
                                                   .POST(HttpRequest.BodyPublishers.ofString("update=CLEAR GRAPH <" + g + ">"))
                                                   .uri(URI.create(URL))
                                                   .header("Content-Type", "application/x-www-form-urlencoded")
                                                   .header("Authorization", "GDB eyJ1c2VybmFtZSI6Im1hcnRpbi5sZWR2aW5rYUBmZWwuY3Z1dC5jeiIsImF1dGhlbnRpY2F0ZWRBdCI6MTcyMzY0MzU2MTg4MH0=.IiYeufUklgAyi+I66HXL2vtG3laILRhu4jNLLG88sdA=")
                                                   .build();
                try {
                    final HttpResponse<Void> resp = httpClient.send(req, HttpResponse.BodyHandlers.discarding());
                    assertEquals(204, resp.statusCode());
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
