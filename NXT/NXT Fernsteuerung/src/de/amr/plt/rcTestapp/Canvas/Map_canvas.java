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

public class Map_canvas extends View{

	Paint myPaint;
	Bitmap map;
	
	public Map_canvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		map = BitmapFactory.decodeResource(getResources(), R.drawable.map_hor_v1_2);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(map,0, 0, null);
		}

	
	
}