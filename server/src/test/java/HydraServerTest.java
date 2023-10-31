import me.googas.hydra.server.HydraServer;
import me.googas.hydra.server.HydraServerBuilder;
import me.googas.hydra.server.HydraServerException;

public class HydraServerTest {

  public static void main(String[] args) throws HydraServerException {
    HydraServer server = new HydraServerBuilder().setPort(30112).setMaxClients(1).build().start();
    System.out.println("Server started");
  }
}
