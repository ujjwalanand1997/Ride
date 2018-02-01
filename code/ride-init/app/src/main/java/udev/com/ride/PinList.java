package udev.com.ride;

/**
 * Created by Ujjwal on 8/26/2017.
 */

public class PinList {

    public String pinnedLat;
    public String pinnedLong;
    public String pinName;

    PinList(String pinName,String pinnedLat,String pinnedLong){
        this.pinName = pinName;
        this.pinnedLat = pinnedLat;
        this.pinnedLong = pinnedLong;
    }

    public String getPinName() {
        return pinName;
    }
    public String getPinnedLat(){
        return pinnedLat;
    }
    public String getPinnedLong(){
        return pinnedLong;
    }
}
