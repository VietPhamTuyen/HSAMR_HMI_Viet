package de.amr.plt.rcTestapp.Canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Canvas_reset extends View{
	public Canvas_reset(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
//		canvas.drawColor(Color.WHITE);

//		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawColor(Color.WHITE);
		
		}

	
	
}