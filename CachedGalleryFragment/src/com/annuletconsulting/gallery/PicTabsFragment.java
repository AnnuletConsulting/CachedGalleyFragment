package com.annuletconsulting.gallery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import com.annuletconsulting.gallerytest.DoSomethinWithSelectedPics;
import com.annuletconsulting.gallerytest.R;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class PicTabsFragment extends Fragment implements OnTabChangeListener, TabContentFactory, LoaderCallbacks<Void>{
	public final static String PIC_PATHS_ARRAY = "pics_arry";
	private View view;
	private TabHost tabHost;
	private int currentTab = 0;
	private static final String TAG = "PicTabsFragment";
	private HashMap<String, ArrayList<String>> imageUrls = new HashMap<String, ArrayList<String>>();;
	private static HashMap<String, GridFragment> bucketFragments = new HashMap<String, GridFragment>();
	private static String[] buckets;
	private LayoutInflater inflater;
	private ViewGroup container;
	private Cursor imagecursor;
	private int loaderCounter = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		this.container = container;
		final String[] columns = { 	MediaStore.Images.Media.BUCKET_DISPLAY_NAME, 
									MediaStore.Images.Media.DATA, 
									MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media.DATE_TAKEN+" DESC";
		imagecursor = new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, 
				null, null, orderBy).loadInBackground();
		getLoaderManager().initLoader(loaderCounter++, null, this);
		return inflater.inflate(R.layout.interstitial, null);
	}
	
	private void init() {
		view = inflater.inflate(R.layout.pic_tabs_fragment, container);
		for (String bucket : buckets)
			getFragment(bucket);
		((Button)view.findViewById(R.id.smdPrevBtn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		((Button)view.findViewById(R.id.smdNextBtn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doNext();
			}
		});
		tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		tabHost.setOnTabChangedListener(this);
		try {
			tabHost.setCurrentTab(currentTab);
			tabHost.setup();
			for (int t=0; t<buckets.length; t++) {
				TabSpec tab = newTab(buckets[t]);
				tabHost.addTab(tab);
			}
			if (buckets.length > 0)
				updateTab(buckets[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getActivity().setContentView(view);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		bucketFragments.clear();
	}

	/**
	 * For testing/demo purposes.
	 */
	private void loadInternetImages() {
		// TODO: replace with actual code or remove.
		imageUrls.clear();
		ArrayList<String> value = new ArrayList<String>();
		value.add("http://farm9.staticflickr.com/8046/8110195389_1acbe1dda2_o.jpg");
		value.add("http://farm9.staticflickr.com/8056/8110200149_f982295e3d_h.jpg");
		value.add("http://farm9.staticflickr.com/8326/8110246647_ee9d605788_k.jpg");
		value.add("http://farm9.staticflickr.com/8470/8110232247_f9c4b79ad4_h.jpg");
		value.add("http://farm9.staticflickr.com/8472/8110253246_9af5e010ee_k.jpg");
		imageUrls.put("NY Parks (Web)", value );
	}
	
	private void loadPhoneImages() {
		for (int i = 0; i < imagecursor.getCount(); i++) {
			imagecursor.moveToPosition(i);
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			int bucketColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
			String bucket = imagecursor.getString(bucketColumnIndex);
			addToImageMap(bucket, imagecursor.getString(dataColumnIndex));
		}
		imagecursor.close();
		buckets = setToOrderedStringArray(imageUrls.keySet());
		Log.d(TAG, "loadImages(), buckets.length: "+ buckets.length);
	}
	
	private void addToImageMap(String bucket, String url) {
		if (imageUrls.containsKey(bucket)) {
			imageUrls.get(bucket).add(url);
		} else {
			ArrayList<String> al = new ArrayList<String>();
			al.add(url);
			imageUrls.put(bucket, al);
		}			
	}
	
	private static String[] setToOrderedStringArray(Set<String> keySet) {
		int i = 0;
		ArrayList<String> keys = new ArrayList<String>(keySet);
		Collections.sort(keys);
		String[] out = new String[keys.size()];
		for (String key: keys) {
			out[i++] = key;
		}
		return out;
	}

	public static String[] listToStringArray(ArrayList<String> arrayList) {
		String[] out = new String[arrayList.size()];
		int i = 0;
		for (String key: arrayList) {
			out[i++] = key;
		}
		return out;
	}

	private TabSpec newTab(String tabId) {
		Log.d(TAG, "buildTab(): tag=" + tabId);

		View indicator = LayoutInflater.from(getActivity()).inflate(R.layout.tab,
									(ViewGroup) view.findViewById(android.R.id.tabs), false);
		((TextView) indicator.findViewById(R.id.tab_text)).setText(tabId);
		((ImageView) indicator.findViewById(R.id.tab_icon)).setBackgroundResource(R.drawable.tab_img);
		TabSpec tabSpec = tabHost.newTabSpec(tabId);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(this);
		return tabSpec;
	}

	@Override
	public View createTabContent(String tag) {
		return getFragment(tag).getGridView();
	}

	@Override
	public void onTabChanged(String tabId) {
		Log.d(TAG, "onTabChanged(): tabId=" + tabId);
		updateTab(tabId);
		for (int b=0; b<buckets.length; b++) {
			if (tabId.equals(buckets[b]))
				currentTab = b;
		}
		System.gc();
	}

	private void updateTab(String tabId) {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag(tabId) != null) {
			fm.beginTransaction().replace(getFragment(tabId).getId(), getFragment(tabId), tabId).commit();
			view.invalidate();
		}
	}

	private GridFragment getFragment(String tabId) {
		if (bucketFragments.containsKey(tabId)) {
			bucketFragments.get(tabId).setUrls(listToStringArray(imageUrls.get(tabId)));
			return bucketFragments.get(tabId);
		}
		GridFragment gf = new GridFragment();
		gf.setContext(getActivity());
		Bundle bundle = new Bundle();
		bundle.putString(GridFragment.BUCKET, tabId);
		bundle.putStringArray(GridFragment.URIS, listToStringArray(imageUrls.get(tabId)));
		gf.onCreate(bundle);
		bucketFragments.put(tabId, gf);
		return gf;
	}

	private String[] getSelectedPaths() {
		ArrayList<String> urls = new ArrayList<String>();
		for (String bucket : buckets) {
			Iterator<String> iterator = bucketFragments.get(bucket).getSelectedUris().iterator();
			while (iterator.hasNext()) {
				urls.add(iterator.next());
			}
		}
		return listToStringArray(urls);
	}

	public static void deSelect(String uri) {
		for (String bucket : buckets) {
			bucketFragments.get(bucket).deSelect(uri);
		}
	}

	protected void doNext() {
		String[] selPaths = getSelectedPaths();
		if (selPaths.length == 0) {
			Toast.makeText(getActivity(), R.string.none_selected, Toast.LENGTH_LONG).show();
			return;
		}
		Intent intent = new Intent(getActivity().getBaseContext(), DoSomethinWithSelectedPics.class);
		intent.putExtra(PIC_PATHS_ARRAY , selPaths);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getActivity().startActivity(intent);
	}
	
	@Override
	public Loader<Void> onCreateLoader(int arg0, Bundle arg1) {
		AsyncTaskLoader<Void> loader = new AsyncTaskLoader<Void>(getActivity()) {
			@Override
			public Void loadInBackground() {
				loadInternetImages();
				loadPhoneImages();
				return null;
			}
		};
		loader.forceLoad();
		return loader;
	}
	@Override
	public void onLoadFinished(Loader<Void> loader, Void data) {
		init();
	}
	
	@Override
	public void onLoaderReset(Loader<Void> loader) {
	}
}