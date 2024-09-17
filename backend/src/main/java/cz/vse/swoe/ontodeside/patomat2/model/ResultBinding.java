package cz.vse.swoe.ontodeside.patomat2.model;

public record ResultBinding(String name, String value, String datatype, boolean basedOnBlankNode) {

    public ResultBinding(String name, String value, String datatype) {
        this(name, value, datatype, false);
    }
}
