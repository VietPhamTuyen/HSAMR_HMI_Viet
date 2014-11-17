package de.amr.plt.rcTestapp.Canvas;

import de.amr.plt.rcTestapp.R;
import de.amr.plt.rcTestapp.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Map_canvas extends View{
    private Bitmap bmp;
	private Paint myPaint;
	private Bitmap map;
	private Bitmap car;
    private int x = 0;
	
	
	public Map_canvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		map = BitmapFactory.decodeResource(getResources(), R.drawable.map_hor_v1_2);
		car = BitmapFactory.decodeResource(getResources(), R.drawable.car);

		
		  bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
	   	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		canvas.drawColor(Color.WHITE);
		//Bitmap b = Bitmap.createBitmap(map, 0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.drawBitmap(map,0, 0, null);

		
		
		
		
		
		x=0;
		 while (x < canvas.getWidth()) {

             Log.i("a=", String.valueOf(x));
             canvas.drawBitmap(bmp, x, 10, null);
             x++;
         //    sleep(10);
      }
		 Log.i("canvas=", String.valueOf(canvas.getWidth()));

		

		
	
	canvas.drawBitmap(car,canvas.getWidth()/2, canvas.getHeight()/2, null);
	}
	
}