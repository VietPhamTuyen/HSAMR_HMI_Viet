package de.amr.plt.rcTestapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPortrait extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_main, container, false);
	}
	
	
    @Override	
	public void onDestroy(){
		super.onDestroy();
	}
	
	
	
	
}
