package cz.cvut.kbss.ontodeside.patomat2.service.pattern;

import java.util.ArrayList;
import java.util.List;

public class ResultRow {

    private final List<ResultBinding> bindings = new ArrayList<>();

    public ResultBinding addBinding(ResultBinding binding) {
        bindings.add(binding);
        return binding;
    }

    public List<ResultBinding> getBindings() {
        return bindings;
    }
}
