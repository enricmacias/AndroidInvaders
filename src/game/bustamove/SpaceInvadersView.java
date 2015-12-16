package game.bustamove;

import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class SpaceInvadersView extends SurfaceView implements SurfaceHolder.Callback
{    
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////THREAD//////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    class SpaceInvadersThread extends Thread
	{
    	//////////////////////////////////////////////////////////////////////
    	///////////////////////////CONSTANTS//////////////////////////////////
    	//////////////////////////////////////////////////////////////////////
    	//Estats del joc
    	public static final int PAUSE = 0;
        public static final int READY = 1;
        public static final int RUNNING = 2;
        public static final int LOSE = 3;
        public static final int WIN = 4;
        
        private static final int MAX_ENEMIES = 10;
        private static final int MAX_SLEEP = 150;
        private static final int MAX_SHOOT = 4;
        private static final int MAX_VIDES = 10;
        
    	//////////////////////////////////////////////////////////////////////
    	///////////////////////////VARIABLES//////////////////////////////////
    	//////////////////////////////////////////////////////////////////////
        
        private int canvasHeight = 1;
        private int canvasWidth = 1;
        
        //Generals
        private Context context;
        private SurfaceHolder surfaceHolder;
        private Handler handler;
        private boolean run;
        private int mode; 
        
        //Imatges
        private Bitmap backgroundImage;
        
        //Enemics
        private Enemy e[];
        private int numEnemies;
        private int sleepTime, now;
        
        private Spaceship spaceship;
        private int points;
        private Paint textFormat;
        private Paint colors;
        private Shoot shoot[];
        private Rect vides[];
        private int numVides;
        
        //So
        private MediaPlayer mp;
        private MediaPlayer laser[];
        
		public SpaceInvadersThread(SurfaceHolder surfaceHolder, Context context, Handler handler)
		{
			this.context = context;
			this.surfaceHolder = surfaceHolder;
			this.handler = handler;
			
			e = new Enemy[MAX_ENEMIES];
			shoot = new Shoot[MAX_SHOOT];
			laser = new MediaPlayer[MAX_SHOOT];
			vides = new Rect[MAX_VIDES];
			
			Resources res = context.getResources();
			backgroundImage = BitmapFactory.decodeResource(res,R.drawable.fons);
			
			//So
			mp = new MediaPlayer();
			if(mp!=null)
			{
				try
				{
					mp = MediaPlayer.create(getContext(), R.raw.musica);
					mp.setLooping(true);
					mp.start();
				}catch(IllegalArgumentException e){}
			}
			
			for(int i=0;i<MAX_SHOOT;i++)
			{
				laser[i] = new MediaPlayer();
				if(laser[i]!=null)
				{
					try
					{
						laser[i] = MediaPlayer.create(getContext(), R.raw.laser);
					}catch(IllegalArgumentException e){}
				}
			}
			
			//Tipografia
			textFormat = new Paint();
			textFormat.setAntiAlias(true);
			textFormat.setStyle(Paint.Style.FILL);
			textFormat.setAntiAlias(true);
			textFormat.setTextSize(20);
			textFormat.setColor(Color.WHITE);
			textFormat.setTextSize(20);
			
			colors = new Paint();
			colors.setAntiAlias(true);
			colors.setStyle(Paint.Style.FILL);
			colors.setARGB(150, 0, 255, 0);
		}
		
		public void doStart()
		{
			sleepTime = (int) Math.round(Math.random()*MAX_SLEEP);
			now = points = 0;
			numEnemies = 0;
			numVides = MAX_VIDES;
			
			for(int i=0;i<MAX_ENEMIES;i++)
			{
				e[i] = null;
			}
			
			for(int i=0;i<MAX_SHOOT;i++)
			{
				shoot[i] = null;
			}
		
			synchronized (surfaceHolder) 
			{
				setMode(RUNNING,null);
			}
		
			spaceship = new Spaceship(context,canvasWidth,canvasHeight);
			
			for(int i = 0; i<MAX_VIDES; i++)
			{
				vides[i] = new Rect(Math.round(canvasWidth/3)+(i*10) + 30, 10, Math.round(canvasWidth/3)+(i*10) + 35, 20);
			}
			
			colors.setARGB(150, 0, 0, 255);
		}
				
		public void pause() 
		{
            synchronized (surfaceHolder) 
            {
                if (mode == RUNNING) setMode(PAUSE,null);
                if(mp.isPlaying())
            	{
                	mp.pause();
            	}
                for(int i = 0; i<MAX_SHOOT; i++)
                {
                	laser[i].stop();
                }
            }
        }
		
		public synchronized void restoreState(Bundle savedState) 
		{
            synchronized (surfaceHolder) 
            {
                setMode(PAUSE,null);
                try {
    				mp.reset();
    				mp.prepare();
    				mp.start();
    				
    				for(int i=0;i<MAX_SHOOT;i++)
    				{
    					laser[i].reset();
    					laser[i].prepare();
    				}
    			} catch (IllegalStateException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
            }
		}
		
		@Override
        public void run() 
		{
            while (run) 
            {
                Canvas c = null;
                try 
                {
                    c = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder)
                    {
                        doDraw(c);
                    }
                } finally 
                {
                    if (c != null)	surfaceHolder.unlockCanvasAndPost(c);
                }
                
                try
                {
                	//Thread.sleep(33);
                }
                catch(Exception e)
                {
                	
                }
            }
        }
		
		public void setRunning(boolean b) 
		{
            run = b;
        }
		
		public void setMode(int _mode, CharSequence message) 
		{
	        synchronized (surfaceHolder) 
	        {
	            mode = _mode;

	            if (mode == RUNNING) 
	            {
	                Message msg = handler.obtainMessage();
	                Bundle b = new Bundle();
	                b.putString("text", "");
	                b.putInt("viz", View.INVISIBLE);
	                msg.setData(b);
	                handler.sendMessage(msg);
	            } 
	            else 
	            {
	                Resources res = context.getResources();
	                CharSequence str = "";
	                if (mode == READY)		str = res.getText(R.string.mode_ready);
	                else if (mode == PAUSE)	str = res.getText(R.string.mode_pause);
	                else if (mode == LOSE)	str = res.getText(R.string.mode_lose_prefix)+Integer.toString(points)+res.getText(R.string.mode_lose_suffix);
	                else if (mode == WIN)	str = res.getString(R.string.mode_win);

	                if (message != null) 	str = message + "\n" + str;
	                
	                Message msg = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("text", str.toString());
                    b.putInt("viz", View.VISIBLE);
                    msg.setData(b);
                    handler.sendMessage(msg);
	            }
	        }
	    }
		
		public void setSurfaceSize(int width, int height) 
		{
            synchronized (surfaceHolder) 
            {
                canvasWidth = width;
                canvasHeight = height;
                
                //Adjustar background image
            }
        }
		
		public void unpause() 
		{
			setMode(RUNNING, null);
			if(!mp.isPlaying())	mp.start();
			try {
				
				for(int i=0;i<MAX_SHOOT;i++)
				{
					laser[i].prepare();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		boolean doKeyDown(int keyCode, KeyEvent msg) 
		{
			synchronized (surfaceHolder) 
			{
                boolean okStart = false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) okStart = true;

                if (okStart && (mode == READY || mode == LOSE ) )
                {
                    //READY & OK -> Inicialitzem i començem
                    doStart();
                    return true;
                } 
                else if (mode == PAUSE && okStart) 
                {
                    //PAUSE -> Reprenem l'aplicació
                    unpause();
                    return true;
                } 
                else if (mode == RUNNING) 
                {
                    //RUNNING -> En ple joc
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                    {
                    	for(int i=0;i<MAX_SHOOT;i++)
        				{
        					if(shoot[i] == null)	
        					{
        						shoot[i] = new Shoot(context,canvasWidth,canvasHeight,spaceship.getPosX(),spaceship.getPosY(), 0, spaceship.getPosX(), spaceship.getWidth(), spaceship.getHeight());
        						i = MAX_SHOOT;
        					}
        					else
        						if(!shoot[i].isVisible())	shoot[i] = null;
        				}
                        return true;
                    } 
                    else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) 
                    {
                    	spaceship.moveLeft();
                        return true;
                    } 
                    else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) 
                    {
                    	spaceship.moveRight();
                        return true;
                    } 
                    else if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
                    {
                        pause();
                        return true;
                    }
                }

                return false;
            }
		}
		 
		boolean doKeyUp(int keyCode, KeyEvent msg) 
		{
			boolean handled = false;

            return handled;
		}
		
		boolean doTouchEvent(MotionEvent event)
		{
			int action = event.getAction(); 
			int mCurX = (int)event.getX(); 
			int mCurY = (int)event.getY();
				
			spaceship.setCurPos(mCurX,mCurY);
			if(action == MotionEvent.ACTION_DOWN)
			{
				for(int i=0;i<MAX_SHOOT;i++)
				{
					if(shoot[i] == null)	
					{
						shoot[i] = new Shoot(context,canvasWidth,canvasHeight,spaceship.getPosX(),spaceship.getPosY(), mCurX, mCurY, spaceship.getWidth(), spaceship.getHeight());
						laser[i].start();
						i = MAX_SHOOT;
					}
					else
						if(!shoot[i].isVisible())	shoot[i] = null;
				}
				
			}
			if(action == MotionEvent.ACTION_MOVE)
			{
				spaceship.setPosX(Math.round(mCurX));
			}
			
			return true;
		}
		
		private void enemiesIdle()
		{
			if( now == sleepTime )
			{
				for(int i=0;i<MAX_ENEMIES;i++)
				{
					if(e[i] != null)
					{
						if(!e[i].getVisible())
						{
							e[i] = null;
							numEnemies--;
						}
					}
					else
					{
						int type = (int) Math.round(Math.random()*4);
						e[i] = new Enemy(context,canvasWidth,canvasHeight,type);
						numEnemies++;
						i = MAX_ENEMIES;
					}
				}
				
				for(int i=0;i<MAX_ENEMIES;i++)
				{
					if(e[i] != null)
					{
						if(!e[i].getVisible())
						{
							e[i] = null;
							numEnemies--;
						}
					}
				}
				sleepTime = (int) Math.round(Math.random()*MAX_SLEEP);
				now = -1;
			}
			now++;
			
			//GAME OVER
			if(numVides == 0)	setMode(LOSE, "");	
		}
		
		private void doDraw(Canvas canvas) 
		{
			canvas.drawBitmap(backgroundImage, 0, 0, null);
			
			if(mode==RUNNING)
			{			
				canvas.drawText("SCORE:"+points, 15, 20, textFormat);
				for(int i = 0; i<numVides; i++)
				{
					if(numVides<7)colors.setARGB(255, 255, 255, 0);
					if(numVides<3)colors.setARGB(255, 255, 0, 0);
					canvas.drawRect(vides[i], colors);
				}
				
				enemiesIdle();
				spaceship.draw(canvas);
				for(int i=0;i<MAX_ENEMIES;i++)
				{
					if(e[i]!=null)	e[i].draw(canvas);
				}
				
				
				for(int i=0;i<MAX_SHOOT;i++)
				{
					if(shoot[i] != null)	shoot[i].draw(canvas);
				}
				
				calculaColisions();
			}
		}
		
		public void calculaColisions()
		{
			//Colisions Dispars-Enemics
			for(int i=0; i<MAX_SHOOT; i++)
			{
				
				if(shoot[i]!= null)
				{
					int px = shoot[i].getPosX() + (shoot[i].getAmple()/2);
					int py = shoot[i].getPosY() + (shoot[i].getAlt()/2);
					
					for(int j=0; j<MAX_ENEMIES; j++)
					{
						if(e[j]!= null)
						{
							if( (py > e[j].getPosY()) & (py < (e[j].getPosY() + e[j].getAlt())) )
							{
								if( (px > e[j].getPosX()) & (px < (e[j].getPosX() + e[j].getAmple())) )
								{
									shoot[i] = null;
									e[j].restaVida();
									if(e[j].getVides()== 0)
									{
										e[j].setDestroyed();
										if(e[j].getType()== 4)		points +=100;
										else if(e[j].getType()== 3) points += 30;
										else 						points += 10;
									}
									
									j=MAX_ENEMIES;
									i=MAX_SHOOT;
								}
							}
						}
					}
				}
			}
			
			Shoot b;
			int pxB, pyB;
			int bottom, right, left;
			for(int i=0; i<MAX_ENEMIES; i++)
			{
				if(e[i] != null)
				{
					b = e[i].getBala();
					if(b != null)
					{
						pxB = b.getPosX() + (b.getAmple()/2);
						pyB = b.getPosY() + (b.getAlt()/2);
						
						if( (pyB > spaceship.getPosY()) & (pyB < (spaceship.getPosY() + spaceship.getHeight())) )
						{
							if( (pxB > spaceship.getPosX()) & (pxB < (spaceship.getPosX() + spaceship.getWidth())) )
							{
								e[i].destroyBala();
								if(numVides > 0) numVides --;
							}
						}
					}
					right = e[i].getPosX() + e[i].getAmple();
					left = e[i].getPosX();
					bottom = e[i].getPosY() + e[i].getAlt();
					if( (bottom  > spaceship.getPosY()))
					{
						if( !(right < spaceship.getPosX()) & !(left > (spaceship.getPosX() + spaceship.getWidth())) )
						{
							e[i] = null;
							if(numVides > 0) numVides --;
						}
					}
				}
			}
		}
	}
     
    //////////////////////////////////////////////////////////////////////
	///////////////////////////VARIABLES//////////////////////////////////
	//////////////////////////////////////////////////////////////////////
    //Textos
    private TextView infoText;
    
    SpaceInvadersThread thread;
    
	public SpaceInvadersView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		thread = new SpaceInvadersThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                infoText.setVisibility(m.getData().getInt("viz"));
                infoText.setText(m.getData().getString("text"));
            }
        });
		
		setFocusable(true);
	}
	
	public SpaceInvadersThread getThread() 
	{
        return thread;
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) 
	{
        return thread.doKeyDown(keyCode, msg);
    }
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) 
	{
        return thread.doKeyUp(keyCode, msg);
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent e)
	{		
		return thread.doTouchEvent(e);
	}
	
	@Override
    public void onWindowFocusChanged(boolean hasWindowFocus) 
	{
        if (!hasWindowFocus) thread.pause();
    }
	
	public void setTextView(TextView newText) 
	{
        infoText = newText;
    }
	
	//Funcions del implements
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
    {
    	thread.setSurfaceSize(width, height);
    }
    public void surfaceCreated(SurfaceHolder arg0) 
    {
        thread.setRunning(true);
        thread.start();
    }
    public void surfaceDestroyed(SurfaceHolder arg0) 
    {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) 
        {
            try 
            {
                thread.join();
                retry = false;
            } 
            catch (InterruptedException e) {}
        }
    }
}
