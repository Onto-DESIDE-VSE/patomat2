package cz.cvut.kbss.ontodeside.patomat2.environment;

import java.net.URI;
import java.util.Random;

public class Generator {

    private static final Random RANDOM = new Random();

    private Generator() {
        throw new AssertionError();
    }

    public static URI generateUri() {
        return URI.create("https://example.com/" + RANDOM.nextInt(1000000));
    }
}
