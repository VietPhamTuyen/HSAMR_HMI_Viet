package de.amr.plt.rcTestapp.Canvas;

import de.amr.plt.rcTestapp.R;
import de.amr.plt.rcTestapp.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Car_canvas extends View{

	Paint myPaint;
	Bitmap car;

	int x_Achse;
	int y_Achse;
	int grad;
	
	public Car_canvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		car = BitmapFactory.decodeResource(getResources(), R.drawable.car);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(car,canvas.getWidth()/2, canvas.getHeight()/2, null);
		
		
		}

	
	
}