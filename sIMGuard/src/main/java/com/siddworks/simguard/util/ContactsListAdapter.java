package com.siddworks.simguard.util;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.siddworks.simguard.R;
import com.siddworks.simguard.bean.Contact;

import java.util.ArrayList;


public class ContactsListAdapter extends ArrayAdapter<Contact>{

		private static final String LOGTAG = "ContactsListAdapter";
		Context context; 
	    int layoutResourceId;  
	    private ArrayList<Contact> data;
	    String screen;
	    private boolean isRearrangeEditMode = false;
	    
	    public ContactsListAdapter(Context context, int layoutResourceId, ArrayList<Contact> data, String screen) {
			
	    	super(context, layoutResourceId, data);
			
//			Log.d(LOGTAG, "In ContactsListAdapter.ContactsListAdapter");
			
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.setData(data);
	        this.screen = screen;
	        
		}
	    
	    
	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	    	
//	    	Log.d(LOGTAG, "In CardLayoutListAdapter.getView");
	    	
	    	View row = convertView; 
	    	RowItemsHolder holder = null;
	    	
	    	if(row == null)
	    	{
	    		
	    		holder = new RowItemsHolder();
	    		   
	    		LayoutInflater inflater = (LayoutInflater) context
	    		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		row = inflater.inflate(layoutResourceId, parent, false);
	    		
	    		holder.title =  (TextView)row.findViewById(R.id.title);
	    		holder.description = (TextView)row.findViewById(R.id.description);
	    		holder.rowIconImageView = (ImageView)row.findViewById(R.id.rowIconImageView);
	    		
                row.setTag(holder);
	    	}
	    	else
	    	{
        	   holder = (RowItemsHolder)row.getTag();
            }
    	   
	    	if(holder.title != null)// && description != null)
	    	{
	    		holder.title.setText(getData().get(position).getName());
    			holder.description.setText(getData().get(position).getNumber());
    			
    			if (getData().get(position).getImage() != null) {
    	            holder.rowIconImageView.setImageBitmap(getData().get(position).getImage());
    	        }
                else {
                    // Default image
                    holder.rowIconImageView.setImageResource(R.drawable.ic_contact);
                }
	    	}
    	
	    	return row;
	    }
	    
	    public boolean isRearrangeEditMode() {
			return isRearrangeEditMode;
		}


		public void setRearrangeEditMode(boolean isRearrangeEditMode) {
			this.isRearrangeEditMode = isRearrangeEditMode;
		}

		public ArrayList<Contact> getData() {
			return data;
		}


		public void setData(ArrayList<Contact> data) {
			this.data = data;
		}

		static class RowItemsHolder
	    {
	        TextView title;
	        TextView description;
	        ImageView rowIconImageView;
	    } 
}
