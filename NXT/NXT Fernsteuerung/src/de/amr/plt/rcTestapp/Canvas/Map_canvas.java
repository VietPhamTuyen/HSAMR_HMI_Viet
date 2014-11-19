package de.amr.plt.rcTestapp.Canvas;

import java.util.ArrayList;

import de.amr.plt.rcTestapp.MainActivity;
import de.amr.plt.rcTestapp.R;
import de.amr.plt.rcTestapp.R.drawable;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Map_canvas extends View{
	
    private Bitmap bmp;
	private Paint myPaint;
	private Bitmap map;
	private Bitmap car;
    
	private int current_posx;
	private int current_posy;

	public static ArrayList<Integer> position_listx= new ArrayList<Integer>();
	public static ArrayList<Integer> position_listy= new ArrayList<Integer>();
	
    private MainActivity main;
    
    
	
	
	public Map_canvas(Context context) {
		super(context);
		main = new MainActivity();
		
		// TODO Auto-generated constructor stub
		map = BitmapFactory.decodeResource(getResources(), R.drawable.map_hor_v1_2);
		car = BitmapFactory.decodeResource(getResources(), R.drawable.car);

		
		  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
	   	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		position_listx = main.get_pos_list(true);
		position_listy = main.get_pos_list(false);
		current_posx = main.current_pos(true) ;
	    current_posy = main.current_pos(false) ;
		
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        Rect dest = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        
//		canvas.drawColor(Color.WHITE);
//		Bitmap b = Bitmap.createBitmap(map, 0, 0, canvas.getWidth(), canvas.getHeight());
//		canvas.drawBitmap(map,0, 0, null);
		canvas.drawBitmap(map, null, dest, null);
		
			
			for(int zaehler = 1; zaehler == position_listx.size(); zaehler++){
				
				if(!(position_listx.get(zaehler+1)==null) && !(position_listx.get(zaehler+1)==null)){
					
					canvas.drawLine(position_listx.get(zaehler), position_listy.get(zaehler), 
							position_listx.get(zaehler +1), position_listy.get(zaehler+1), paint);
				}
			}
			 
			 
			 
			 canvas.drawBitmap(car,current_posx, current_posy, null);
			
		

	}
	
}