package cz.vse.swoe.ontodeside.patomat2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ResultBinding(String name, String value, String datatype, @JsonIgnore  boolean basedOnBlankNode) {

    public ResultBinding(String name, String value, String datatype) {
        this(name, value, datatype, false);
    }
}
