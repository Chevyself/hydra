import lombok.NonNull;
import me.googas.hydra.HydraTypeIO;
import me.googas.hydra.util.TypeReference;

import java.util.List;

public class TypeReferenceTest {

    public static void main(String[] args) {
        HydraTypeIO reader = new HydraTypeIO() {
            @Override
            public <T> T read(@NonNull String string, @NonNull Class<T> clazz) {
                return null;
            }

            @Override
            public <T> T readGeneric(@NonNull String string, @NonNull TypeReference<T> typeReference) {
                System.out.println(typeReference.getType());
                System.out.println(string);
                return null;
            }
        };

        List<String> list = reader.readGeneric("[]", new TypeReference<List<String>>() {});

        System.out.println(list);
    }
}
