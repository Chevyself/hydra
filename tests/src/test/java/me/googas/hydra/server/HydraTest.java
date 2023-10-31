package me.googas.hydra.server;

import java.io.IOException;
import java.util.Scanner;
import me.googas.hydra.HydraException;
import me.googas.hydra.Request;
import me.googas.hydra.RequestHandler;
import me.googas.hydra.client.HydraClient;
import me.googas.hydra.client.HydraClientBuilder;
import me.googas.hydra.util.TrieNode;

public class HydraTest {
  public static void main(String[] args) throws HydraException, IOException {
    // Server init
    HydraServer server = new HydraServerBuilder().setMaxClients(1).setPort(3000).build().start();

    // TrieNode
    TrieNode<RequestHandler> root = TrieNode.getRoot();
    root.register("/ping", (messenger, request, response) -> {
       response.send(200);
    });

    // Client
    HydraClient client =
        new HydraClientBuilder()
            .setHost("localhost")
            .setPort(3000)
            .setRequestRegistry(root)
            .build()
            .start();

    // Test CLI
    Scanner scanner = new Scanner(System.in);
    while (true) {
      String line = scanner.nextLine();
      if (line.equalsIgnoreCase("stop")) {
        server.close();
        client.close();
        break;
      } else if (line.equalsIgnoreCase("ping")) {
        server
            .getMessaging()
            .send(
                Request.newBuilder()
                    .setPath("ping")
                    .putHeaders("init", String.valueOf(System.currentTimeMillis())),
                response -> {
                  if (response.getCode() != 200) System.out.println(response.getError());
                  else System.out.println(response.getBody() + "ms");
                });
      }
    }
  }
}
