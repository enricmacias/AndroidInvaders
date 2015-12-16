package game.bustamove;

import game.bustamove.SpaceInvadersView;
import game.bustamove.SpaceInvadersView.SpaceInvadersThread;
import game.bustamove.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class SpaceInvaders extends Activity 
{    
	private SpaceInvadersView gameView;
	private SpaceInvadersThread gameThread;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.bustamove_layout);
        
        gameView = (SpaceInvadersView) findViewById(R.id.SpaceInvadersView);
        gameThread = gameView.getThread();
        gameView.setTextView((TextView) findViewById(R.id.text));
        
        if (savedInstanceState == null)	gameThread.setMode(SpaceInvadersThread.READY, null);
        else 							gameThread.restoreState(savedInstanceState);
    }
    
    //Cridat quan l'activitat perd el focus.
    @Override
    protected void onPause() 
    {
        super.onPause();
        gameView.getThread().pause();
    }
}