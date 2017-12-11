package com.tpt.borne.tpt.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpt.borne.tpt.R;
import com.tpt.borne.tpt.models.Contact;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Borne on 6/9/2016.
 */
public class ContactListAdapter extends ArrayAdapter<Contact> {

    Context context;
//    ImageLoader loader;

    public ContactListAdapter(Context context, int resourceId,
                                    List<Contact> items) {
        super(context, resourceId, items);
        this.context = context;
//        this.map = map;
//        this.mapNames = mapNames;
//        loader = ImageLoader.getInstance();
//        loader.init(ImageLoaderConfiguration.createDefault(context));

    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        TextView lastName;
        ImageView img;
        TextView telOne;
        TextView telTwo;
        TextView mail;
        TextView role;
        TextView mailTwo;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Contact rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_contact_list, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.firstName);
            holder.lastName = (TextView) convertView.findViewById(R.id.lastName);
            holder.img = (ImageView) convertView.findViewById(R.id.imageLeft);
            holder.telOne = (TextView) convertView.findViewById(R.id.telOne);
            holder.telTwo = (TextView) convertView.findViewById(R.id.telTwo);
            holder.mail = (TextView) convertView.findViewById(R.id.mailText);
            holder.role = (TextView) convertView.findViewById(R.id.role);


            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        //holder.imageLeft.setImageResource(rowItem.getImage());
        //loader.displayImage("http://borne.io/nurture/" + rowItem.getImage(), holder.imageLeft);
        holder.name.setText(rowItem.getFirstName());
        holder.lastName.setText(rowItem.getLastName());
        holder.img.setImageResource(rowItem.getPicture());
        holder.telOne.setText(rowItem.getPhone());
        holder.telTwo.setText(rowItem.getPhone2());
        holder.mail.setText(rowItem.getMail());
        holder.role.setText(rowItem.getRole());


        if(rowItem.getProfilePicture().equals("")){
            holder.img.setImageResource(R.drawable.mentor_default_image);
        }else {
            Picasso.with(context)
                    .load("http://borne.io/tpt/" + rowItem.getProfilePicture())
                    .placeholder(R.drawable.mentor_default_image)
                    .fit().centerCrop()
                    .into(holder.img);
            //holder.imageLeft.setImageResource(rowItem.getImage());
//            loader.displayImage("http://borne.io/nurture/" + rowItem.getImage(), holder.imageLeft);
        }


        // Flag
//        loader.displayImage("https://uat.xendpay.com:9313" + map.get(rowItem.getRecipientCountryCode()), holder.flag);


        return convertView;
    }
}
