package com.annuletconsulting.gallery;

import java.util.ArrayList;
import java.util.HashMap;
import com.annuletconsulting.gallerytest.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This Fragment represents one tab or bucket in the PicTabsFragment.
 * 
 * @author Walt Moorhouse
 */
public class GridFragment extends Fragment {
	public static final String BUCKET = "bucket";
	public static final String URIS = "uris";
	private String bucket;
	private String[] urls;
    private GridView gridView;
    private GridAdapter myAdapter;
    private HashMap<String, GridThumb> gridObjects;
	private Activity context;
	private TextView counterView = null;
	
	/**
	 * If you use this constructor, you must you setBucket() and setUrls().
	 */
	public GridFragment() {
		super();
	}
	
	@Override
	public void setArguments(Bundle args) {
		if (args != null) {
			super.setArguments(args);
			if (args.containsKey(BUCKET))
				bucket = args.getString(BUCKET);
			if (args.containsKey(URIS))
				urls = args.getStringArray(URIS);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (gridView == null)
			onCreate(null);
		return gridView;
	}
	
	public GridView getGridView() {
		if (gridView == null)
			onCreate(null);
		return gridView;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
		gridObjects = new HashMap<String, GridThumb>();
		addUrls();
        myAdapter.notifyDataSetChanged();
	}
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setArguments(savedInstanceState);
        context.setContentView(R.layout.grid_view);
        gridObjects = new HashMap<String, GridThumb>();
        addUrls();
        gridView = (GridView) context.findViewById(R.id.galleryGridView);
        myAdapter = new GridAdapter();
        gridView.setAdapter(myAdapter);
        gridView.setTag(bucket);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
            	if (gridObjects.get(urls[position]).isSelected() || !gridObjects.get(urls[position]).isSelected()) {
	                if (gridObjects.get(urls[position]).toggleSelected())
	                	add(1);
	                else
	                	add(-1);
	                myAdapter.notifyDataSetChanged();
            	}
            }
        });
    }

	private void addUrls() {
        for (String url : urls) {
			try {
				gridObjects.put(url, new GridThumb(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

	public ArrayList<String> getSelectedUris() {
		ArrayList<String> al = new ArrayList<String>();
		for (String key : gridObjects.keySet()) {
			GridThumb thumb = gridObjects.get(key);
			if (thumb.isSelected())
				al.add(thumb.getUrl());
		}
		return al;
	}
	
	/**
	 * If a counterView has been provided to show the number of images selected, this will update it.
	 * @param amount to add (or subtract if negative)
	 */
	protected void add(int i) {
		if (counterView == null)
			return;
		counterView.setText(String.valueOf(Integer.parseInt(counterView.getText().toString())+i));
	}
	
	/** 
	 * If you want to add an Selection counter to show the user how many pics are selected, add it here.
	 * 
	 * @param counterView
	 */
	public void setCounterView(TextView counterView) {
		this.counterView = counterView;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	public void deSelect(String uri) {
		if (gridObjects.containsKey(uri))
			gridObjects.get(uri).deSelect();
	}

	/**
	 * Private Adapter Class for GridThumbs.
	 */
    private class GridAdapter extends BaseAdapter  {
        private LayoutInflater mInflater;

        public GridAdapter() {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
        	GridThumb thumb = getItem(position);
        	try {
	            if (view == null) {
	                view = mInflater.inflate(R.layout.grid_thumb, null);
	            }
	            thumb.getBitmap((ImageView) view.findViewById(R.id.icon));
	    		if (thumb.isSelected()) {
	    			thumb.getImageView().setBackgroundColor(Color.BLUE);
	            } else {
	            	thumb.getImageView().setBackgroundColor(Color.DKGRAY);
	            }
	    	} catch (Exception error) {
				error.printStackTrace();
	    	}
    		System.gc();
            return view;
        }

        @Override
        public int getCount() {
            return gridObjects.size();
        }

        @Override
        public GridThumb getItem(int position) {
            return gridObjects.get(urls[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
