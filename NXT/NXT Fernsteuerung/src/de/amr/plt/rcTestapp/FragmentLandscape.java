package de.amr.plt.rcTestapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentLandscape extends Fragment{

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//        Map _view = (Map) inflater.inflate(R.layout.landscapemode, container, false);
        //lets keep a reference of DrawView 
     //   drawView = (DrawView ) _view.findViewById(R.id.drawing);
//        return _view;
		
		
		
		
		
		
		return inflater.inflate(R.layout.drawable, container, false);
	}
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
	
    @Override	
	public void onDestroy(){
		super.onDestroy();
	}
	
	
	
}
