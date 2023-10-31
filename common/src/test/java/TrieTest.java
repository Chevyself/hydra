import lombok.NonNull;
import me.googas.hydra.util.RegexMatchFunction;
import me.googas.hydra.util.TrieNode;

public class TrieTest {
  public static void main(String[] args) {
    TrieNode<Integer> root = TrieNode.getRoot();
    // Registration for trienode
    /*
    for (int i = 0; i < 1000; i++) {
        root.register("/users/" + i + "/{name}", i);
    }

     */
    // root.register("/users/{id}/", 1);
    // root.register("/users/{id}/friends", 2);
    root.register("/users/{id}/friends/{test}", 1);

    RegexMatchFunction<Integer> pathProvider = new RegexMatchFunction<>();
    /*
    for (int i = 0; i < 1000; i++) {
        pathProvider.add("/users/" + i + "/{name}", i);
    }

     */
    // pathProvider.add("/users/{id}/", 1);
    // pathProvider.add("/users/{id}/friends", 2);
    pathProvider.add("/users/{id}/friends/{test}", 1);

    time(
        1000000,
        () -> {
          root.apply("/users/1/friends/2");
        });

    time(
        1000000,
        () -> {
          root.apply("/users/1/friends/2");
        });

    time(
        1000000,
        () -> {
          pathProvider.apply("/users/1/friends/2");
        });
  }

  public static void time(int iterations, @NonNull Runnable runnable) {
    long start = System.currentTimeMillis();
    for (int i = 0; i < iterations; i++) {
      runnable.run();
    }
    long end = System.currentTimeMillis();
    System.out.println("Took " + (end - start) + "ms");
  }
}
