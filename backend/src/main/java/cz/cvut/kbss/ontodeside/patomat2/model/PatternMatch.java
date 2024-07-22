package cz.cvut.kbss.ontodeside.patomat2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternMatch {

    /**
     * Bindings of values that represent the pattern match instance.
     */
    private List<ResultBinding> bindings = new ArrayList<>();

    /**
     * The pattern whose match this is.
     */
    @JsonIgnore
    private Pattern pattern;

    public PatternMatch() {
    }

    public PatternMatch(Pattern pattern, List<ResultBinding> bindings) {
        this.bindings = new ArrayList<>(bindings);
        this.pattern = pattern;
    }

    public List<ResultBinding> getBindings() {
        return bindings;
    }

    public void setBindings(List<ResultBinding> bindings) {
        this.bindings = bindings;
    }

    public void addBinding(String name, String value, String datatype) {
        bindings.add(new ResultBinding(name, value, datatype));
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @JsonIgnore
    public Set<String> getVariables() {
        return bindings.stream().map(ResultBinding::name).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof PatternMatch that)) {return false;}
        return Objects.equals(getPattern(), that.getPattern()) && Objects.equals(getBindings(),
                that.getBindings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPattern(), getBindings());
    }
}
