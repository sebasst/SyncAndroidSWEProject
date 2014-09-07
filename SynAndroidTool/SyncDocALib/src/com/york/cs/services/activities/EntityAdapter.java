

package com.york.cs.services.activities;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.couchbase.lite.Database;
import com.couchbase.lite.Database.ChangeEvent;
import com.york.cs.couchbaseapi.Application;
import com.york.cs.couchbaseapi.DBObject;

public abstract class EntityAdapter<T extends DBObject> extends BaseAdapter {

	protected List<T> list;
	protected Context context;
	protected LayoutInflater mLayoutInflater = null;

	public EntityAdapter(Context context, List<T> entities) {
		this.context = context;
		this.list = entities;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		Database db = ((Application)Application.getContext()).getDatabase();
		
		db.addChangeListener(new Database.ChangeListener() {

			@Override
			public void changed(ChangeEvent arg0) {
				System.out.println("preruunable1");
				
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						
						System.out.println("ruunable1");
						update();
						
					}
				});
				
				/*getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						System.out.println("ruunable1");
						update();

					}
				});*/

			}
		});

	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int i) {
		return list != null ? list.get(i) : null;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	public String getItemIdS(int i) {
		return list.get(i).getId();
	}
	
	public String getItem_Id(int i) {
		return list != null ? list.get(i).getId() : null;
	}

	

	

	

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent) ;
	
	public abstract void update();
	
}
