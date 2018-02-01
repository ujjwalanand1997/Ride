package udev.com.ride;

/**
 * Created by Ujjwal on 10/4/2017.
 */

public class UserDetail {
    String status;
    String getInfo;

    public UserDetail(String status,String getInfo,String lat,String lon,String nm){
        this.status = status;
        this.getInfo = getInfo;
        pinnned(lat,lon,nm);
    }

    void pinnned(String pLat,String pLong,String pName){
        String pinLat = pLat;
        String pinLon = pLong;
        String pinName = pName;
    }
}
