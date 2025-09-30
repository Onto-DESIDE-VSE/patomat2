package cz.vse.swoe.ontodeside.patomat2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record ResultBinding(String name, String value, String datatype, boolean basedOnBlankNode, Optional<String> label) {

    public ResultBinding(String name, String value, String datatype) {
        this(name, value, datatype, false);
    }

    public ResultBinding(String name, String value, String datatype, boolean basedOnBlankNode) {
        this(name, value, datatype, basedOnBlankNode, Optional.empty());
    }
}
