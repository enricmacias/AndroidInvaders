package game.bustamove;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Spaceship 
{
	private int posX, posY;
	private int destX, destY;
	private int spaceshipWidth, spaceshipHeight, laserWidth, laserHeight;
	private float angle;
	private Drawable spaceship, laser;
	private int canvasWidth, canvasHeight;
	
	public Spaceship(Context context,int canvasWidth, int canvasHeight)
	{
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		spaceship = context.getResources().getDrawable(R.drawable.spaceship);
		laser = context.getResources().getDrawable(R.drawable.laser);
		spaceshipWidth = 54;
		spaceshipHeight = 28;
		laserWidth = 7;
		laserHeight = 18;
		posX = (canvasWidth/2) - (spaceshipWidth/2);
		posY = canvasHeight-40;
	}
	
	public void moveRight()
	{
		posX+=10;
	}
	
	public void moveLeft()
	{
		posX-=10;
	}
	
	public void setPosX(int posX)
	{
		this.posX = posX - (spaceshipWidth/2);
	}
	
	public int getPosX()
	{
		return posX;
	}
	
	public int getPosY()
	{
		return posY;
	}
	
	public int getWidth()
	{
		return spaceshipWidth;
	}
	
	public int getHeight()
	{
		return spaceshipHeight;
	}
	
	public void setCurPos(int destX, int destY)
	{
		this.destX = destX;
		this.destY = destY;
	}
	
	public void disminueixAngle()
	{
		angle--;
	}
	
	public void draw(Canvas canvas)
	{
		if(posX<0)								posX = 0;
		if(posX>(canvasWidth-spaceshipWidth))	posX = canvasWidth-spaceshipWidth;
		
		angle = (float) Math.toDegrees(Math.atan2(destY-posY, destX-posX)) + 90;
		
		spaceship.setBounds(posX, posY, spaceshipWidth+posX, spaceshipHeight+posY);
		spaceship.draw(canvas);
		
		int laserPos = (int) posX+(spaceshipWidth/2)-(laserWidth/2);
		
		canvas.save();
		
		laser.setBounds(laserPos, posY-laserHeight, laserPos+laserWidth, posY);
		canvas.rotate(angle, posX+(spaceshipWidth/2), posY+((spaceshipHeight/2)+20));
		laser.draw(canvas);
		canvas.restore();
	}	
}
