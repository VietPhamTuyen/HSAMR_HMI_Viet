package de.amr.plt.rcTestapp.Canvas;

import java.awt.Rectangle;
import java.util.ArrayList;

import de.amr.plt.rcParkingRobot.IAndroidHmi.ParkingSlot;
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
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Map_canvas extends View {
	
	// TODO
	/*
	 * 1024 x 500 px (ohne rot) 245 x 125 cm 1cm = ( (1024/245) x 4 )px
	 * 
	 * unterer rand = 100 px
	 * 
	 * 
	 * ------------------------------------------- 87 pixel unten nicht
	 * verf�gbar grau au�en 5 cm wei� insgesammt 20 wei� ein teil 8 ohne l�cke
	 * 210*90
	 * 
	 * 
	 * auto = 60*100
	 */
	
	private Bitmap bmp;
	private Paint myPaint;
	private Bitmap map;
	private Bitmap car;
	private Bitmap reset;
	private Bitmap connect;
	private Bitmap noconnect;
	private static int zaehler = 0;
	
	private int current_posx;
	private int current_posy;
	
	private static int no_slots;
	
	public static ArrayList<Integer> position_listx = new ArrayList<Integer>();
	public static ArrayList<Integer> position_listy = new ArrayList<Integer>();
	
	private MainActivity main;
	
	private static int orientation; // 0 = landscape, 1 = portrait, 2 = both
	public boolean b_orientation;
	
	private static boolean connection;
	
	public Map_canvas(Context context, int orientation) {
		super(context);
		main = new MainActivity();
		this.orientation = orientation;
		connection = false;
		
		map = BitmapFactory.decodeResource(getResources(), R.drawable.strecke);
		// R.drawable.map_hor_v1_2);
		car = BitmapFactory.decodeResource(getResources(), R.drawable.car);
		
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
		reset = BitmapFactory.decodeResource(getResources(), R.drawable.reset);
		
		connect = BitmapFactory.decodeResource(getResources(), R.drawable.connection);
		noconnect = BitmapFactory.decodeResource(getResources(), R.drawable.noconnection);
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
					canvas.drawLine(position_listx.get(zaehler), position_listy.get(zaehler),
							position_listx.get(zaehler + 1), position_listy.get(zaehler + 1), paint);
					
				} catch (Exception e) {
					// TODO
					// MainActivitiy.error_ausgabe("map_canvas",
					// "position_list");
				}
				zaehler++;
			}
			
			// float angle= main.get_Angle();
			//
			// Matrix matrix = new Matrix();
			// matrix.setRotate(angle, current_posx-21, current_posy-30);
			// canvas.drawBitmap(car, matrix, null);
			
			// float angle = 45;
			//
			// Matrix matrix = new Matrix();
			// matrix.setRotate(angle,
			// canvas.getWidth()/2,canvas.getHeight()/2);
			// canvas.drawBitmap(car, matrix, null);
			
			// TODO canvas.drawBitmap(car, current_posx-21, current_posy-30,
			// null);
			// canvas.rotate(180,canvas.getWidth()/2,canvas.getHeight()/2);
			
			connection = main.getConnection();
			if (connection == true) {
				canvas.drawBitmap(connect, 924, 0, null);
			} else {
				canvas.drawBitmap(noconnect, 924, 0, null);
			}
			
			no_slots = main.get_no_park();
			
			// if(no_slots > 0){
			// //TODO implement parking slots
			// int slot_nr;
			//
			// slot_nr = main.get_no_park();
			//
			// for(int slot_counter =0; slot_counter == slot_nr; slot_counter++
			// ){
			//
			//
			// ParkingSlot parking_slot = main.get_slot(slot_counter);
			//
			//
			//
			//
			// PointF front = parking_slot.getFrontBoundaryPosition();
			// // int y_front = parking_slot.getFrontBoundaryPosition();
			// PointF back = parking_slot.getBackBoundaryPosition();
			// // int y_back = parking_slot.getBackBoundaryPosition();
			//
			// Rect parklueckenrechteck = new Rect();
			//
			// // canvas.drawText(text, x, y, paint)
			//
			// canvas.clipRect(parklueckenrechteck);
			//
			//
			//
			//
			//
			//
			//
			// }
			
		}
		
	}
	
}