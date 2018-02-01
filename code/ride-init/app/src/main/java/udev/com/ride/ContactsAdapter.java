package udev.com.ride;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ujjwal on 11/25/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    List<LauncherActivity.ListItem> listItems;
    Context context;

    public ContactsAdapter(List<LauncherActivity.ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LauncherActivity.ListItem listItem = listItems.get(position);


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView header;
        TextView number;

        public ViewHolder(View itemView) {
            super(itemView);

            header = (TextView)itemView.findViewById(R.id.head);
            number = (TextView)itemView.findViewById(R.id.number);
        }
    }
}
