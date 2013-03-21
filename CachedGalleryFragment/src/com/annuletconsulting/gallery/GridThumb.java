package com.annuletconsulting.gallery;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

public class GridThumb {
	private boolean selected = false;
	private String url;
	private Bitmap bump = null;
	private ImageView imageView;
	
	public GridThumb(String url) {
		setUrl(url);
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean toggleSelected() {
		selected = !selected;
		return selected;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void getBitmap(ImageView imageView) {
		this.imageView = imageView;
		if (bump == null) {
			try {
				imageView.setTag(url);
				UrlImageViewHelper.setUrlDrawable(imageView, url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			imageView.setImageBitmap(bump);
		}
	}

	public void deSelect() {
		selected = false;
		imageView.setBackgroundColor(Color.DKGRAY);
	}

	public ImageView getImageView() {
		return imageView;
	}
}