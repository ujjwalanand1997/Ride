package udev.com.ride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ujjwal on 9/26/2017.
 */

public class CustomAdapter extends ArrayAdapter<PinList> {
    public CustomAdapter(Context context, ArrayList<PinList> pin) {
        super(context, 0,pin);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        PinList pins = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pin_cell, parent, false);
        }
        // Lookup view for data population
        TextView address = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView latlong = (TextView) convertView.findViewById(R.id.nameTextView);
        // Populate the data into the template view using the data object
        address.setText(pins.pinName);
        latlong.setText(pins.pinnedLat +" ,"+ pins.pinnedLong);
        // Return the completed view to render on screen
        return convertView;
    }
}
