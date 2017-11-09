import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/*  
 *  Victor Kobayashi
 *  May 24, 2015 
 *  ICS3U-1 
 *  Final Project Version 8
 */




public class Final_Project
{
	
	
	public static void main(String[] args) throws IOException, InterruptedException, LineUnavailableException, UnsupportedAudioFileException
	{
		
		
		//creates JFrame
		JFrame myFrame = new JFrame();
		//Sets Jframe Parameters
		myFrame.setVisible(true);
		myFrame.setSize(800, 620);
		myFrame.setResizable(false);
		myFrame.setTitle("Last Life Left");
		//makes sure game closes
		myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		
		
		//Creates and configures custom JPanel
		GamePanel myPanel = new GamePanel();
		myPanel.setSize(800,620);
		
		
		//puts custom JPanel in JFrame
		myFrame.setContentPane(myPanel);
		
		//enables the various listeners
		myPanel.addMouseMotionListener(myPanel);
		myPanel.addMouseListener(myPanel);
		myPanel.addKeyListener(myPanel);
		//gives focus to our GamePanel
		myPanel.requestFocus();
		
	
		
		while(true)
		{
			//pause program for 17ms
			//loop that runs about ~60 fps
			
			myPanel.run();
			myPanel.repaint();
			Thread.sleep(17);
			
		}
		
		
	}

	


}
