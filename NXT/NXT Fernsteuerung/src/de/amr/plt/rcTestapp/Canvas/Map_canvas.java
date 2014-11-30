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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Map_canvas extends View {

	//TODO
	/*
	 * 1024 x 500 px (ohne rot)
	 * 245 x 125 cm
	 * 1cm = ( (1024/245) x 4 )px
	 * 
	 * unterer rand = 100 px
	 * 
	 * 
	 * -------------------------------------------
	 * 87 pixel unten nicht verf�gbar
	 * grau au�en 5 cm
	 * wei� insgesammt 20
	 * wei� ein teil 8
	 * ohne l�cke 210*90
	 * 
	 * 
	 * auto = 60*100
	 */
	
	
	private Bitmap bmp;
	private Paint myPaint;
	private Bitmap map;
	private Bitmap car;
	private Bitmap reset;
	private static int zaehler = 0;

	private int current_posx;
	private int current_posy;

	public static ArrayList<Integer> position_listx = new ArrayList<Integer>();
	public static ArrayList<Integer> position_listy = new ArrayList<Integer>();

	private MainActivity main;

	private static int orientation; // 0 = landscape, 1 = portrait, 2 = both
	public boolean b_orientation;

	public Map_canvas(Context context, int orientation) {
		super(context);
		main = new MainActivity();
		this.orientation = orientation;

		map = BitmapFactory.decodeResource(getResources(),
				R.drawable.strecke);
//				R.drawable.map_hor_v1_2);
		car = BitmapFactory.decodeResource(getResources(), R.drawable.car);

		bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_action_name);
		reset = BitmapFactory.decodeResource(getResources(), R.drawable.reset);
	}

	public void destroy() {
		if (bmp != null) {
			bmp.recycle();
		}
		if (map != null) {
			map.recycle();
		}
		if (car != null) {
			car.recycle();
		}
		if (reset != null) {
			reset.recycle();
		}
	}

	public void onDestroy() {
		destroy();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		b_orientation = main.getOrientation();
		if (b_orientation == true) { // true =portrait, false = landscape
			orientation = 1;
		} else {
			orientation = 0;
		}

		if (orientation == 0) {

			position_listx = main.get_pos_list(true);
			position_listy = main.get_pos_list(false);
			current_posx = main.current_pos(true);
			current_posy = main.current_pos(false);

			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.BLACK);
			paint.setARGB(255, 255, 255, 0);
			Rect dest = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());

			// canvas.drawColor(Color.WHITE);
			// Bitmap b = Bitmap.createBitmap(map, 0, 0, canvas.getWidth(),
			// canvas.getHeight());
			// canvas.drawBitmap(map,0, 0, null);
			canvas.drawBitmap(map, null, dest, null);
			zaehler = 0;

			while (zaehler < position_listx.size()) {
				try {
					canvas.drawLine(position_listx.get(zaehler),
							position_listy.get(zaehler),
							position_listx.get(zaehler + 1),
							position_listy.get(zaehler + 1), paint);

				} catch (Exception e) {
					// TODO
					// MainActivitiy.error_ausgabe("map_canvas",
					// "position_list");
				}
				zaehler++;

			}

			/*
			 * for(int zaehler = 1; zaehler == position_listx.size();
			 * zaehler++){
			 * 
			 * if(!(position_listx.get(zaehler+1)==null) &&
			 * !(position_listx.get(zaehler+1)==null)){
			 * 
			 * canvas.drawLine(position_listx.get(zaehler),
			 * position_listy.get(zaehler), position_listx.get(zaehler +1),
			 * position_listy.get(zaehler+1), paint); } }
			 */

			canvas.drawBitmap(car, current_posx, current_posy, null);

		} else if (orientation == 1) {

		}
	}

}