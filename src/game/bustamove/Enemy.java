package game.bustamove;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Enemy 
{
	private static final int MAX_SHOOT_TIME = 50;
	
	private Context context;
	private int posX, posY;
	private int destX, destY;
	private int width, height;
	private Drawable image1, image2, destroyed;
	private int canvasWidth, canvasHeight;
	private boolean okX, okY;
	private boolean visible;
	private boolean animation;
	private int animationCounter;
	private Shoot shoot;
	private int shootTime, now;
	private int vides;
	private int type;
	
	public Enemy(Context context,int canvasWidth, int canvasHeight, int type)
	{
		this.context = context;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		posX = posY = destX = destY = animationCounter = 0;
		shootTime = (int) Math.round(Math.random()*MAX_SHOOT_TIME);
		now = -1;
		okX = okY = false;
		visible = animation = true;
		vides = 1;
		this.type = type;
		
		if(type==4)	posY = destY = (int) Math.round(Math.random()*(canvasHeight/2));
		else		posX = destX = (int) Math.round(Math.random()*(canvasWidth-width));
		
		loadImages(context,type);
		
		//shoot = new Shoot(context, canvasWidth, canvasHeight,posX,posY);
	}
	
	public void loadImages(Context context, int type)
	{
		switch(type)
		{
			case 0:	
				image1 = context.getResources().getDrawable(R.drawable.enemy11);
				image2 = context.getResources().getDrawable(R.drawable.enemy12);
				width = 22;
				height = 16;
				break;
			case 1:
				image1 = context.getResources().getDrawable(R.drawable.enemy21);
				image2 = context.getResources().getDrawable(R.drawable.enemy22);
				width = 16;
				height = 16;
				break;
			case 2:
				image1 = context.getResources().getDrawable(R.drawable.enemy31);
				image2 = context.getResources().getDrawable(R.drawable.enemy32);
				width = 24;
				height = 16;
				break;
			case 3:
				image1 = context.getResources().getDrawable(R.drawable.enemy11);
				image2 = context.getResources().getDrawable(R.drawable.enemy12);
				width = 45;
				height = 33;
				vides = 3;
				break;
			case 4:
				image1 = context.getResources().getDrawable(R.drawable.enemy41);
				image2 = context.getResources().getDrawable(R.drawable.enemy42);
				width = 48;
				height = 21;
				vides = 2;
				break;
		}
		destroyed = context.getResources().getDrawable(R.drawable.destroy);
	}
	
	public boolean getVisible()
	{
		return visible;
	}
	
	public void animationCount()
	{
		animationCounter++;
		if(animationCounter>=20)
		{
			if(animation)	animation = false;
			else			animation = true;
			animationCounter = 0;
		}
	}
	
	public void findPosition()
	{
		if(type!=4)
		{
			if(posX==destX) okX = true;
			else
			{
				if(destX>posX)	posX++;
				else			posX--;
			}
			
			if(posY==destY) okY = true;
			else
			{
				if(destY>posY)	posY++;
				else			posY--;
			}
			if(okX && okY)
			{
				destX = (int) Math.round(Math.random()*(canvasWidth-width));
				destY = posY + (int) Math.round(Math.random()*(canvasHeight-height-posY));
				okX = false;
				okY = false;
			}
		}
		else	
		{
			if(posX>canvasWidth)	visible = false;
			posX+=2;
		}
	}
	
	public void manageShoot()
	{
		if( now == shootTime )
		{
			if(shoot != null)
			{
				if(!shoot.isVisible())
				{
					shoot = null;
				}
			}
			else
			{
				shoot = new Shoot(context,canvasWidth,canvasHeight,posX, posY);
			}
			
			shootTime = (int) Math.round(Math.random()*MAX_SHOOT_TIME);
			now = -1;
		}
		now++;
	}
	
	public void setDestroyed()
	{
		image1 = destroyed;
		image2 = destroyed;
		this.visible = false;
	}
	
	public void restaVida()
	{
		this.vides --;
	}
	
	public int getVides()
	{
		return this.vides;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public void draw(Canvas canvas)
	{		
		if(visible)	findPosition();
		manageShoot();
		
		if(animation)
		{
			image1.setBounds(posX, posY, width+posX, height+posY);
			image1.draw(canvas);
		}
		else
		{
			image2.setBounds(posX, posY, width+posX, height+posY);
			image2.draw(canvas);
		}
		if(shoot != null)shoot.draw(canvas);
		
		animationCount();		
	}
	
	public int getPosX(){return this.posX;}
	public int getPosY(){return this.posY;}
	public int getAmple(){return this.width;}
	public int getAlt(){return this.height;}
	public Shoot getBala(){return this.shoot;}
	public void destroyBala(){this.shoot = null;}
}
