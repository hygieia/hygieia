package hygieia.transformer;

/**
 * Transforms something into something else.
 */
public interface Transformer<S, O> {
    /**
     * Transforms a source S into an output O
     * @param source source
     * @return output
     */
    O transformer(S source);
}
