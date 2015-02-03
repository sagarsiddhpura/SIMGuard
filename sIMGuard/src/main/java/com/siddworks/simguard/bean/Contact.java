package com.siddworks.simguard.bean;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.noveogroup.android.log.Log;
import com.siddworks.simguard.util.ContactsListAdapter;

import java.io.InputStream;
import java.util.Comparator;

public class Contact {
	private String name;
	private String photo;
	private String number;
	private long id;
	private long groupId;
	private Bitmap image = null;
	private ContactsListAdapter adapter;
	private Context mContext;
	private int hits;
	private int order;
	
	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	private static final String[] PHOTO_ID_PROJECTION = new String[] {
	    ContactsContract.Contacts.PHOTO_ID
	};
	private static final String[] PHOTO_BITMAP_PROJECTION = new String[] {
	    ContactsContract.CommonDataKinds.Photo.PHOTO
	};

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	public void loadImage(Context mContext, ContactsListAdapter adapter2) {
        // HOLD A REFERENCE TO THE ADAPTER
        this.adapter = adapter2;
        this.mContext = mContext;
        new ImageLoadTask().execute(id+"");
    }

	@Override
	public boolean equals(Object object) {
		
	    boolean result = false;
	    if (object == null || object.getClass() != getClass()) {
	    	System.out.println("Error in object type");
	      result = false;
	    } else {
//	    	System.out.println("comparing:"+this+" and:"+(Contact)object);
	    	Contact cnt = (Contact) object;
	      if (this.getNumber().equals(cnt.getNumber())) {
//	    	  System.out.println("numbers equal");
	        result = true;
	      }
	    }
//    	System.out.println("result:"+result);
	    return result;
	}
	 
	@Override
	public int hashCode() {
	    return this.getNumber().hashCode();
	}
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	@Override
	public String toString() {
		return "\nContact [name=" + name + ", number=" + number + ", id=" + id
				+ ", groupId=" + groupId + ", hits=" + hits + ", order="
				+ order + "]";
	}
	
	public static Comparator<Contact> contactHitsComparator 
	    = new Comparator<Contact>() {
	
		public int compare(Contact cnt1, Contact cnt2) {
		
			//ascending order
			return cnt2.getHits() - cnt1.getHits();
		}
	
	};
	
	// ASYNC TASK TO AVOID CHOKING UP UI THREAD
    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
 
        @Override
        protected void onPreExecute() {
//            Log.i("ImageLoadTask", "Loading image...");
        }
 
        // PARAM[0] IS IMG URL
        protected Bitmap doInBackground(String... param) {
            Log.i("Attempting to load image URL: " + param[0]);
            try {
            	
            	final Integer thumbnailId = fetchThumbnailId();
            	Log.i("thumbnailId:"+thumbnailId);
            	if (thumbnailId != null) {
                    final Bitmap thumbnail = fetchThumbnail(thumbnailId);
                    return thumbnail;
                }
            	else
            	{
            		return null;
            	}
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
 
        protected void onPostExecute(Bitmap ret) {
            if (ret != null) {
                Log.i("ImageLoadTask", "Successfully loaded " + name + " image:" + ret);
                image = ret;
                if (adapter != null) {
                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                	adapter.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load " + name + " image");
            }
        }
    }
    
    final Bitmap fetchThumbnail(final int thumbnailId) {

        final Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, thumbnailId);
        final Cursor cursor = mContext.getContentResolver().query(uri, PHOTO_BITMAP_PROJECTION, null, null, null);

        try {
            Bitmap thumbnail = null;
            if (cursor.moveToFirst()) {
                final byte[] thumbnailBytes = cursor.getBlob(0);
                if (thumbnailBytes != null) {
                    thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
                }
            }
            return thumbnail;
        }
        finally {
            cursor.close();
        }

    }
    
    private Integer fetchThumbnailId() {

        final Uri uri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(number));
        final Cursor cursor = mContext.getContentResolver().query(uri, PHOTO_ID_PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        try {
            Integer thumbnailId = null;
            if (cursor.moveToFirst()) {
                thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            }
            return thumbnailId;
        }
        finally {
            cursor.close();
        }

    }
    
    public InputStream openPhoto(long contactId) {
       
        return null;
    }

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}}


