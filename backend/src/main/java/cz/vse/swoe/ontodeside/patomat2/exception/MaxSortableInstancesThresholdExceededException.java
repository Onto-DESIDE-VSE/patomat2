package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that the client requested too many pattern instances to be sorted.
 */
public class MaxSortableInstancesThresholdExceededException extends PatOMat2Exception {

    public MaxSortableInstancesThresholdExceededException(String message) {
        super(message);
    }
}
