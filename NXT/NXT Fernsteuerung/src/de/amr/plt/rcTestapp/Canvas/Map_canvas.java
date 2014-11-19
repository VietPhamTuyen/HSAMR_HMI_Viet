package de.amr.plt.rcTestapp.Canvas;

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
	private int last_posx;
	private int last_posy;
	
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
		
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        Rect dest = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        
//		canvas.drawColor(Color.WHITE);
//		Bitmap b = Bitmap.createBitmap(map, 0, 0, canvas.getWidth(), canvas.getHeight());
//		canvas.drawBitmap(map,0, 0, null);
		canvas.drawBitmap(map, null, dest, null);
		

		
		

		current_posx = main.current_pos(true) ;
	    current_posy = main.current_pos(false) ;
         


  //      canvas.drawLine(last_posx, last_posy, current_posx, current_posy, paint);
		

		 Log.i("canvas=", String.valueOf(canvas.getWidth()));

		

		
	
	canvas.drawBitmap(car,current_posx, current_posy, null);
	}
	
}