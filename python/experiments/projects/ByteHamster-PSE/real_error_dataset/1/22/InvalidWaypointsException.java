package edu.kit.pse.osip.simulation.view.main;

/**
 * This exception signifies that a list of waypoints is not valid, e.g. if the list is empty.
 */
public class InvalidWaypointsException extends java.lang.IllegalArgumentException {
    private static final long serialVersionUID = 1094174566728429133L;

    /**
     * Generates the invalid waypoint exception
     * @param waypoints The invalid waypoints
     */
    public InvalidWaypointsException (Point2D[] waypoints) {
        throw new RuntimeException("Not implemented!");
    }
}
