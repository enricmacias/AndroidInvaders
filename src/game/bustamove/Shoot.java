package game.bustamove;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Shoot 
{
	private static final int SPACESHIP_SHOOT = 1;
	private static final int ENEMY_SHOOT = 2;
	
	private int posX, posY;
	private int width, height;
	private float angle;
	private Drawable image;
	private int canvasWidth, canvasHeight;
	private boolean visible;
	private int type;
	
	public Shoot(Context context,int canvasWidth, int canvasHeight, int posX, int posY, int destX, int destY, int spaceshipWidth, int spaceshipHeight)
	{
		type = SPACESHIP_SHOOT;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		image = context.getResources().getDrawable(R.drawable.shoot);
		width = 6;
		height = 12;
		this.posX = posX + (spaceshipWidth/2) - (width/2);
		this.posY = posY + ((spaceshipHeight/2)+20) - (height/2);
		visible = true;
		angle = (float) Math.toDegrees(Math.atan2(destY-posY, destX-posX)) + 90;
	}
	
	public Shoot(Context context, int canvasWidth, int canvasHeight, int posX, int posY)
	{
		type = ENEMY_SHOOT;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		image = context.getResources().getDrawable(R.drawable.enemy_shoot);
		width = 7;
		height = 7;
		this.posX = posX;
		this.posY = posY;
		visible = true;
		
		int destX = (int) Math.round(Math.random()*canvasWidth);;
		int destY = posY + (int) Math.round(Math.random()*(canvasHeight-posY));
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public void setVisible(boolean b)
	{
		this.visible = b;
	}
	
	public void draw(Canvas canvas)
	{
		if(posY<0)					visible = false;
		else if(posY>canvasHeight)	visible = false;
		else if(posX<0)				visible = false;
		else if(posX>canvasWidth)	visible = false;
		else
		{
			switch(type)
			{
				case SPACESHIP_SHOOT:
					double desp = Math.sin(Math.toRadians(angle));
					if(Math.sin(Math.toRadians(angle))>0)	posX+=desp*10;
					else									posX+=desp*10;
					
					desp = Math.cos(Math.toRadians(angle));
					posY -= desp*10;
					
					canvas.save();
					canvas.rotate(angle, posX, posY);
					image.setBounds(posX, posY, posX+width, posY+height);
					image.draw(canvas);
					canvas.restore();
					break;
					
				case ENEMY_SHOOT:
					posY+=3;
					image.setBounds(posX, posY, posX+width, posY+height);
					image.draw(canvas);
					break;
			}
		}
	}
	
	public int getPosX(){return this.posX;}
	public int getPosY(){return this.posY;}
	public int getAmple(){return this.width;}
	public int getAlt(){return this.height;}
}
