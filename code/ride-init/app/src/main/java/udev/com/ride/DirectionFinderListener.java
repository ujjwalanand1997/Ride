package udev.com.ride;

import java.util.List;

/**
 * Created by Ujjwal on 8/2/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
