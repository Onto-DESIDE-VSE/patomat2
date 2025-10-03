package cz.vse.swoe.ontodeside.patomat2.service.sort;

import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sorts pattern instances using random shuffle.
 */
@Component
public class RandomShuffleSorter implements PatternInstanceSorter {

    @Override
    public List<PatternInstance> sort(List<PatternInstance> patternInstances) {
        Objects.requireNonNull(patternInstances);
        final List<PatternInstance> result = new ArrayList<>(patternInstances);
        Collections.shuffle(result);
        return result;
    }

    @Override
    public Sort getSortMethod() {
        return Sort.RANDOM;
    }
}
