package cz.vse.swoe.ontodeside.patomat2.service.sort;

import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;

import java.util.List;

/**
 * Sorter of pattern instances.
 */
public interface PatternInstanceSorter {

    /**
     * Sorts the specified pattern instances using the method supported by this implementation.
     * <p>
     * The provided list is not modified.
     *
     * @param patternInstances Pattern instances to sort
     * @return Sorted list of pattern instances
     * @see #getSortMethod()
     */
    List<PatternInstance> sort(List<PatternInstance> patternInstances);

    /**
     * Returns the sort method supported by this implementation.
     *
     * @return Sort method
     */
    Sort getSortMethod();
}
