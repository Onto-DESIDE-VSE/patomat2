package cz.vse.swoe.ontodeside.patomat2.model;

import java.util.List;

/**
 * Examples binding values for a pattern.
 *
 * @param bindings List of example bindings
 */
public record ExampleValues(List<ResultBinding> bindings) {
}
