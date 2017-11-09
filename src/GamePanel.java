import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class GamePanel extends JPanel implements MouseMotionListener, MouseListener, KeyListener{
	//Dictates which game state, or which screen is run
	//1 = start screen, 2 = main game screen, 3 = game over screen, 4 = instuction screen, 5 = pause screen, 6 = options screen.
	int gameState = 1; 
	
	//Main Character and associated variables
	//Array of buffered images so there are separate images for Main char when it's taking it's left step and right step 
	//and a transparent image for when ghost ability is being used.
	BufferedImage[] imgMainChar = new BufferedImage[3];
	//controls Main char's X coordinate
	int imgMainCharX = 350;
	//controls Main char's Y coordinate
	int imgMainCharY = 300;
	//controls Main char's ability to move
	boolean movingUp=false, movingDown = false, movingLeft = false, movingRight = false;
	//used so that animation of Main char can run at a steady speed
	int animationCounter=0;
	//Allows one use of the ghost ability per game.
	byte ghost=1;
	//Controls whether the ghost ability activates
	boolean ghostActive = false;
	//Controls how long the ghost ability lasts for.
	int ghostTimer = 0;
	
	
	// Scope/Sniper and associated variables
	//Scope image
	BufferedImage imgScope;
	//controls scope's X coordinate
	int imgScopeX = 400;
	//controls Main char's Y coordinate
	int imgScopeY = 500;
	//Used to track when a bullet is shot.
	boolean shot = false;
	//Controls if user is allowed to shoot another bullet within the time frame.
	boolean canFire = true;
	//Controls wait time between shots. In other words, fire rate tracker.
	byte fireTimer = 0;
	
	//Enemy and associated variables
	//Images of enemy. One that faces left and one that faces right.
	BufferedImage[] imgEnemy = new BufferedImage[2];
	//controls the enemies's X coordinate (50 different enemies)
	int[] imgEnemyX = new int[50];
	//controls the enemies's Y coordinate (50 different enemies)
	int[] imgEnemyY = new int[50];
	//Controls whether the enemy is active and on screen (50 different enemies)
	boolean[] enemyActive = new boolean[50];
	//Manages the number of enemies on screen.
	byte enemyCounter=0;
	//Will randomly decide what side the enemy spawns.
	int direction = 0;
	//will be used to make enemies home in. (50 different enemies)
	double[] angle = new double[50];
	
	
	//Screen images
	BufferedImage imgBackground;
	BufferedImage imgStartScreen;
	BufferedImage imgPause;
	BufferedImage imgInstruction;
	BufferedImage imgGameOver;
	
	//Button images
	BufferedImage imgStartButton;
	BufferedImage imgMenuButton;
	BufferedImage imgRetryButton;
	BufferedImage imgQuitButton;
	BufferedImage imgInstructionButton;
	BufferedImage imgBackButton;
	BufferedImage imgOptionButton;
	
	//Time freeze eye and associated variables
	BufferedImage imgEye;
	//Keeps track of how many eye uses are left
	byte eye = 3;
	//Controls how long the eye ability lasts
	int eyeTimer = 0;
	//Controls whether the eye ability is active or not.
	boolean eyeActive = false;
	
	//More bullet perk/more bullet regen perk and associated variables
	BufferedImage imgMoreBullet;
	//When game is reset, bullets will always set to whatever start bullet is. Is used so that an extra bullet perk can be used.
	int startBullet = 25;
	//When game is reset, bullet regen will always set to whatever bullet regen is. Is used so that an extra bullet regen perk can be used.
	byte bulletRegen= 1;
	
	//Health perk and associated variables
	BufferedImage imgMoreHealth;
	//Start health is used so that when game resets, health is set to start health, which changes when health perk is used
	int startHealth = 200;
	
	//more Time freeze eye perk and associated variables
	BufferedImage imgMoreFreeze;
	//Start eye is used so that when game resets, number of eye uses is set to start eye, which changes when timefreeze perk is used
	byte startEye = 3;
	
	// Other important variables
	//keeps track of number of enemies killed. Will be part of calculating score
	int kills;
	//keeps track of time survived. Will be part of calculating score
	double time;
	//keeps track of health remaining in the game
	int health = 200;
	//keeps a timer on various aspects of the game, such as spawn rate and changes in difficulty
	int timer=0;
	//Controls increasing difficulty of the game. Manages the incrementing spawn rate of enemies
	int difficulty = 0;
	//keeps track of the score, calculated by number of frames survived * number of kills
	int score = (int)(kills*time);
	//to prevent accidental mouse presses during the game over screen
	boolean mouseClick=false;
	//Controls whether pause screen is active or not.
	int pause = 1;
	//Controls number of bullets left.
	int bullets = 25;
	//triggers death scene before game over screen when player dies
	boolean death = false;
	//Gravestone image
	BufferedImage imgDeath;
	
	//changes mouse icon to scope image so that the mouse becomes the scope.
	//Looks nice when you can click on options and instructions with the scope.
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image image = toolkit.getImage("Scope.png");
	Point hotSpot = new Point(0,0);
	Cursor cursor = toolkit.createCustomCursor(image, hotSpot, "scope");
	
	//Connects txt file to the game and adds a high score and a high score name
	//FileReader inFile = new FileReader("highscore.txt");
	//Creates a scanner so that new info can be stored
	Scanner input = new Scanner(inFile);
	//Will save the name of the highscorer
	String highScoreName;
	//Will save high score
	int highScore;
	
	//keeps track of x coordinate of the high score. Will later be used for moving the high score across the start screen. (Looks nice) 
	int stringHighScoreX = 800;
	
	//Creates a clip for the main game background. Boolean controls when its played
	boolean backClipActive=true;
	Clip backClip = AudioSystem.getClip();
	//Creates a clip for all the other screens. Boolean controls when its played
	boolean startClipActive = true;
	Clip startClip = AudioSystem.getClip();
	
	GamePanel() throws IOException, LineUnavailableException, UnsupportedAudioFileException{
		
		//Will be used in saving high score name and high score.
		highScoreName = input.nextLine();
		highScore = input.nextInt();
		input.close();
		inFile.close();
		
		//associates buffered images with actual images
		URL imgURL = getClass().getResource("background.jpg");
		imgBackground = ImageIO.read(imgURL);
		imgURL = getClass().getResource("mainchar.png");
		//image of main char taking left step
		imgMainChar[0] = ImageIO.read(imgURL).getSubimage(80, 0, 80, 80);
		//image of main char taking left step
		imgMainChar[1] = ImageIO.read(imgURL).getSubimage(160, 0, 80, 80);
		//image of main char appearing as ghost.
		imgURL = getClass().getResource("maincharghost.png");
		imgMainChar[2] = ImageIO.read(imgURL).getSubimage(0, 0, 80, 80);
		//associates buffered images with actual images
		imgURL = getClass().getResource("Scope.png");
		imgScope = ImageIO.read(imgURL);
		imgURL = getClass().getResource("enemy.png");
		imgEnemy[0] = ImageIO.read(imgURL);
		imgURL = getClass().getResource("startscreen.png");
		imgStartScreen = ImageIO.read(imgURL);
		imgURL = getClass().getResource("gameover.png");
		imgGameOver = ImageIO.read(imgURL);
		imgURL = getClass().getResource("pause.png");
		imgPause = ImageIO.read(imgURL);
		imgURL = getClass().getResource("instruction.png");
		imgInstruction = ImageIO.read(imgURL);
		imgURL = getClass().getResource("startbutton.png");
		imgStartButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("menubutton.png");
		imgMenuButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("retrybutton.png");
		imgRetryButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("quitbutton.png");
		imgQuitButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("instuctionbutton.png");
		imgInstructionButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("backbutton.png");
		imgBackButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("eye.png");
		imgEye = ImageIO.read(imgURL);
		imgURL = getClass().getResource("optionButton.png");
		imgOptionButton = ImageIO.read(imgURL);
		imgURL = getClass().getResource("morebullet.jpg");
		imgMoreBullet = ImageIO.read(imgURL);
		imgURL = getClass().getResource("morehealth.jpg");
		imgMoreHealth = ImageIO.read(imgURL);
		imgURL = getClass().getResource("morefreeze.jpg");
		imgMoreFreeze = ImageIO.read(imgURL);
		imgURL = getClass().getResource("death.png");
		imgDeath = ImageIO.read(imgURL);
		
		
		//links the wav file to backClip
		URL myURL = getClass().getResource("song.wav");
		backClip.open(AudioSystem.getAudioInputStream(myURL));
		
	}
	
	public void paintComponent(Graphics g){
		//controls what graphics are displayed depending on the game state the player is in.
		switch(gameState)
		{
		case 1:
			//Title screen graphics
			g.drawImage(imgStartScreen, 0, 0, this);
			g.drawImage(imgStartButton, 250, 250, this);
			g.drawImage(imgInstructionButton, 30, 440, this);
			g.drawImage(imgQuitButton, 460, 440, this);
			g.drawImage(imgOptionButton, 30, 40, this);
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.ITALIC, 25));
			g.drawString("Last Life Left ", 320, 220);
			//Shows the current high score name and high score
			g.drawString("High Score: "+ highScoreName +" "+highScore, stringHighScoreX, 380);
			g.drawImage(imgScope, imgScopeX-imgScope.getWidth()/2, imgScopeY-imgScope.getHeight()/2, this);
			
			break;
			
		case 2:
			//Game screen graphics
			g.drawImage(imgBackground, -300,-100, this);
			//draws animation when ghost ability is not active
			if(!ghostActive)
				g.drawImage(imgMainChar[animationCounter/9], imgMainCharX, imgMainCharY, this);
			//when ghost ability is active, draw non animating, transparent "ghost" of main character
			else
				g.drawImage(imgMainChar[2], imgMainCharX, imgMainCharY, this);
			//draws multiple enemies per frame
			for(int i = 0; i<50; i++)
			{
				if(enemyActive[i])
				{
					//Enemies will always face the players direction. If the enemy is left of the main character, it will face right, and will face left is it's to the right of the main character
				
						g.drawImage(imgEnemy[0], imgEnemyX[i]-imgEnemy[0].getWidth()/2, imgEnemyY[i]-imgEnemy[0].getHeight()/2, this);
				
				}
			}
			//draws eye image if the eye (time freeze) ability is being used
//			if(eyeActive)
//				g.drawImage(imgEye, 0, 0, this);
//			
//			g.drawImage(imgScope, imgScopeX-imgScope.getWidth()/2, imgScopeY-imgScope.getHeight()/2, this);
			
			//draws the health bar border larger if health perk is being used.
			g.setColor(Color.GRAY);
			if(startHealth==300)
				g.fillRect(20, 35, 310, 20);
			else
				g.fillRect(20, 35, 210, 20);
			
			//draws health meter
			g.setColor(Color.RED);
			g.fillRect(25, 40, health, 10);
			g.setFont(new Font("Arial", Font.BOLD, 17));
		
			//moves the text further to the right if health mod is being used. Shows the exact amount of health left
			if(startHealth == 300)
				g.drawString(String.valueOf(health)+"/300", 340, 53);
			else
				g.drawString(String.valueOf(health)+"/200", 240, 53);
			
			g.setColor(Color.WHITE);
			//draws useful stats such as time freezes left, kills, time survived and bullets remaining
			g.drawString("Bullets Remaining: "+String.valueOf(bullets), 20, 75);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Kills: "+String.valueOf(kills), 650, 35);
			g.drawString("Time: "+String.valueOf(Math.round(time/60*10)/10.0) , 650, 60);
			g.drawString("Time Freezes Remaining: "+String.valueOf(eye), 500, 560);
			
			//draws the death scene
			if(death)		
			{
				
				//one second later, the background will turn black and show the letters
				if(timer>60)
				{
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, 800, 600);
					g.setColor(Color.WHITE);
					g.setFont(new Font("Arial", Font.BOLD, 50));
					g.drawString("You Blacked Out", 190, 100);
				}
				//draws grave stone where player dies. This is put below so that it is the top layer
				g.drawImage(imgDeath, imgMainCharX-30, imgMainCharY-50, this);
			}
			break;
			
		case 3:
			//Game over screen graphics
		    g.drawImage(imgGameOver, 0, 0, this);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("Score: "+String.valueOf(score), 250, 250);
			g.drawImage(imgRetryButton, 250, 325, this);
			g.drawImage(imgMenuButton, 30, 440, this);
			g.drawImage(imgQuitButton, 450, 440, this);
			g.drawImage(imgScope, imgScopeX-imgScope.getWidth()/2, imgScopeY-imgScope.getHeight()/2, this);
			break;
			
		case 4:
			//Instruction screen graphics
			g.drawImage(imgInstruction, 0, 0, this);
			g.drawImage(imgBackButton, 30, 490, this);
			g.drawImage(imgScope, imgScopeX-imgScope.getWidth()/2, imgScopeY-imgScope.getHeight()/2, this);
			break;
			
		case 5:
			//Pause screen graphics
			g.drawImage(imgPause, 0, 0, this);
			break;
			
//		case 6:
//			//Option screen graphics
//			g.setColor(Color.BLACK);
//			g.fillRect(0, 0, 800, 600);
//			g.drawImage(imgMoreBullet, 40, 30, this);
//			g.drawImage(imgMoreBullet, 200+imgMoreBullet.getWidth(),30, this);
//			g.drawImage(imgMoreHealth, 40, 100+imgMoreBullet.getHeight(), this);
//			g.drawImage(imgMoreFreeze,200+imgMoreBullet.getWidth(), 100+imgMoreBullet.getHeight(), this);
//			g.setColor(Color.WHITE);
//			g.setFont(new Font("Arial", Font.BOLD, 20));
//			g.drawString("Press Esc to exit", 300, 20);
//			g.drawString("Start off with 50 bullets instead of 25", 10, 47+imgMoreBullet.getHeight());
//			g.drawString("Regenerate 2x more bullets", 210+imgMoreBullet.getWidth(), 47+imgMoreBullet.getHeight());
//			g.drawString("300 Health instead of 200", 40, 350+imgMoreBullet.getHeight());
//			g.drawString("One extra time freeze", 210+imgMoreBullet.getWidth(), 350+imgMoreBullet.getHeight());
//			g.drawImage(imgScope, imgScopeX-imgScope.getWidth()/2, imgScopeY-imgScope.getHeight()/2, this);
//			break;
		}
	}
	
	public void run() throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException
	{
		//runs the actual game. Code that runs varies with the game state value
		switch(gameState)
		{
		case 1:
			//starts start screen clip and turn off startClipActive so that it can be switched back when needed. 
			if(backClipActive)
			{
				backClip.loop(100);
				//Allows the clip to be run again when game state becomes 2 again.
				backClipActive=false;
			}
			//sets the cursor icon to scope image
			setCursor(cursor);
			//has the high score move across the screen right to left
			stringHighScoreX-=3;
			//when high score leaves the screen, it appears of the other side
			if(stringHighScoreX<-300)
				stringHighScoreX = 800;
			//Checks for clicking on buttons
			if(mouseClick)
			{
				//Plays gunshot sound when mouse is clicked. Does not have to be clicking anything.
				Clip myClip = AudioSystem.getClip();
				URL myURL = getClass().getResource("gunshot.wav");
				myClip.open(AudioSystem.getAudioInputStream(myURL));
				myClip.start();
				//checks if the cursor is clicking on the play button
				if(imgScopeX >= 250 && imgScopeX <=550)
				{
					if(imgScopeY >= 250 && imgScopeY <=350)
					{
						//starts the game, clearing all previous game data beforehand.
						kills = 0;
						time = 0;
						timer = 0;
						difficulty = 0;
						eye=startEye;
						eyeActive = false;
						ghost = 1;
						bullets = startBullet;
						health = startHealth;
						imgMainCharX = 350;
						imgMainCharY = 300;
						for(int i = 0; i<50; i++)
						{
							enemyActive[i]=false;
						}
						gameState = 2;
						//stops start screen clip and sets it up so it can be used again
						startClipActive = true;
						startClip.stop();
					}	
				}
				//Checks if the cursor is clicking on either the instruction button or the quit button
				else if(imgScopeY >= 440 && imgScopeY <=540)	
				{
					//Checks if the cursor is clicking on the instruction button and takes the user to instruction screen if so. 
					if(imgScopeX >= 30 && imgScopeX <=430)
						gameState = 4;
					//Checks if the cursor is clicking on quit button and exits the game if so.
					else if(imgScopeX >= 460 && imgScopeX <=760)
						System.exit(ABORT);
				}
				//checks if the cursor is clicking on the option button and takes user to option screen if so.
				else if(imgScopeY >= 40 && imgScopeY <=90&&imgScopeX >= 30 && imgScopeX <=130)
					gameState=6;
				
				//turns off mouse click after click is finished so another click can be made.
				mouseClick = false;
			}
			break;
			
		case 2:
			//Will only run when death scene is not active
		if(!death)
		{
			//starts the background clip when the main game begins. Will also stop when the game is over.
			if(backClipActive)
			{
				backClip.loop(100);
				//Allows the clip to be run again when game state becomes 2 again.
				backClipActive=false;
			}
			//increments the time survived
			time++;
			//sets the cursor icon to scope image
			setCursor(cursor);
			//Picks with edge the enemy appears from	
			direction = (int)(4*Math.random());
			//increments timer used for bullet rate, spawn rate, bullet regeneration rate, and health regeneration rate.
			timer++;
			//updates score
			score = (int)(kills*time);
			//starts the eye timer when eye ability is active. Used to track amount of time it's active
			if(eyeActive)
				eyeTimer++;
			
			//Turns off the eye ability after 4 seconds
			if(eyeTimer % 240 == 0)
				eyeActive = false;
			
			//Starts ghost timer when ghost ability activates
			if(ghostActive)
				ghostTimer++;
			
			//Turns off the ghost ability after 100 frames.
			if(ghostTimer % 100 == 0)
				ghostActive = false;
			
			
			//Player can only move when the eye ability is not being used. Used so that the player also freezes when the eye ability is used.
			if(eyeActive == false)
			{
				//Booleans are used for smoother movement as well as diagonal movement.
				//animation counter is incremented so that the walking animation can occur
				//When moving up boolean is true, player moves up and animation increments
				if(movingUp)
				{
					imgMainCharY -= 5;
					animationCounter++;
				}
				//When moving left boolean is true, player moves lest and animation increments
				if(movingLeft)
				{
					imgMainCharX -= 5;
					animationCounter++;
				}
				//When moving down boolean is true, player moves down and animation increments	
				if(movingDown)
				{
					imgMainCharY += 5;
					animationCounter++;
				}
				//When moving right boolean is true, player moves right and animation increments
				if(movingRight)
				{
					imgMainCharX += 5;
					animationCounter++;
				}
			}
			//resets animation counter so that animation can loop
			if(animationCounter>=18)
				animationCounter=0;
			
			//Brings the character to the other side when player is moving too much to one side. It is similar to the PacMan when 
			//PacMan enters through one side of the screen and appears on the other. Can be used as a method of escape
			//bottom of screen to top of screen
			if(imgMainCharY > this.getHeight() -40)
				imgMainCharY =-50;	
			//top of screen to bottom of screen
			if(imgMainCharY <-70)
				imgMainCharY = this.getHeight()-40;
			//right of screen to left of screen
			if(imgMainCharX > this.getWidth()-40)
				imgMainCharX =-40;	
			//left of screen to right of screen
			if(imgMainCharX <-60)
				imgMainCharX = this.getWidth()-40;
			
			//Checks if a bullet is shot, and that bullet is allowed to be fired and checking that the player has enough ammo.
			if(shot && canFire && bullets>0)
			{
				//turns off canFire so that another bullet can not be shot
				//timer will be used later to turn it on after 8 frames.
				canFire = false;
				//Produces the bullet sound.
				Clip myClip = AudioSystem.getClip();
				URL myURL = getClass().getResource("gunshot.wav");
				myClip.open(AudioSystem.getAudioInputStream(myURL));
				myClip.start();
				//reduces the number of bullets left by one, signifying it's use.
				bullets--;
				//Checks every enemy to see if one was hit. If an enemy was hit. it will disappear and the kills will increase
				for(int i = 0; i<50; i++)
				{
					if(enemyActive[i])
					{
						//checks if scope is within the enemie's X range (distance collision was not accurate enough)
						if(imgScopeX >= imgEnemyX[i]-20 && imgScopeX <= imgEnemyX[i]+30)
						{
							//checks if scope is within the enemie's Y range (distance collision was not accurate enough)
							if(imgScopeY >= imgEnemyY[i]-70 && imgScopeY <= imgEnemyY[i]+60)
							{
								//enemy(s) disappear and kills increments
								enemyActive[i] = false;
								kills++;
							}
						}
					}
				}
				
				//Checks if player shoots the main character and ghost ability is not active
				//checks if scope is within the main char's X range (distance collision was not accurate enough) and that ghost is not active
				if(imgScopeX >= imgMainCharX+5 && imgScopeX <= imgMainCharX+80 && !ghostActive)
				{
					//checks if scope is within the enemie's Y range (distance collision was not accurate enough)
					if(imgScopeY >= imgMainCharY  && imgScopeY <= imgMainCharY+75)
					{
						//health penalty if main character is shot.
						health-=100;
						//ends the game if health reaches 0 or below
						if(health <= 0)
						{
							//turns death to true, running death scene
							death = true;
							timer = 0;
						}
					}
				}
			}
			
			//checks if a bullet was fired
			if(!canFire)
			{
				//starts a timer of how much time has gone by since the shot was fired
				fireTimer++;
				//After 8 frames, canFire is turned back on and another bullet can be shot. This is used so that the fire rate is controlled
				if(fireTimer>=8)
				{
					fireTimer = 0;
					canFire = true;
				}
			}
			//Spawns an enemy every 60 frames. As game goes on, spawning rate increases. Enemies will also not spawn if eye ability is being used.
			if(timer%Math.max(60-difficulty, 8) == 0 && eyeActive == false)
			{
				//makes the enemy in the enemyCounter index active.
				enemyActive[enemyCounter] = true;
				// uses the direction number from above to randomize the edge the enemy starts at.
				switch(direction)
				{
				case 0:
					//Left edge
					imgEnemyX[enemyCounter] = -30;
					imgEnemyY[enemyCounter] = (int)(580*Math.random());
					break;
				case 1:
					//Right edge
					imgEnemyX[enemyCounter] = 830;
					imgEnemyY[enemyCounter] = (int)(580*Math.random());
					break;
				case 2:
					//Top edge
					imgEnemyX[enemyCounter] = (int)(780*Math.random());
					imgEnemyY[enemyCounter] = -30;
					break;
				case 3:
					//Bottom edge
					imgEnemyX[enemyCounter] = (int)(780*Math.random());
					imgEnemyY[enemyCounter] = 600;
					break;
				}
				//Increases enemy counter
				enemyCounter++;
				//Prevents an array out of bounds error.
				if(enemyCounter == 50)
				{
					enemyCounter = 0;   
				}
			}
			
			//Increases the spawn rate every 400 frames.
			if(timer%400==0)
			{
				difficulty+=3;
			}
				
			//Allows health and bullets to be regenerated every 30 frames, or half second
			if(timer%30==0)
			{
				// if health perk is being used, will only allow regeneration if health is under 300.
				//Bullets will only regenerate when health is under full, encouraging some intentional harm to the main character
				if(startHealth == 300)
				{
					if(health<300)
					{
						//Restores one health point and one bullet.
						health++;
						bullets+= bulletRegen;
					}
				}
				//If health perk is not used, allow health regeneration when health is under 200
				//Bullets will still only regenerate when health is under full, encouraging some intentional harm to the main character
				else if(health<200)
				{
					health++;
					//If bulletRegen perk is being used, the player will gain 2x more bullets.
					bullets+=bulletRegen;
				}
			}
			//Controls the movement of the enemies
			for(int i = 0; i<50; i++)
			{
				//Allows movement if the enemy is active and the eye is not active. when eye is active, they freeze 
				if(enemyActive[i]&& eyeActive==false)
				{
					//Finds the angle between main character and enemy 
					angle[i] = Math.atan2(imgMainCharY-imgEnemyY[i], imgMainCharX-imgEnemyX[i]);
					//uses angle to move accordingly. This is used so the enemies will home in on the main character 
					imgEnemyX[i]+=1.5*Math.cos(angle[i]);
					imgEnemyY[i]+=1.5*Math.sin(angle[i]);
					//Checks if any enemies collided with the Main Character while ghost ability is not active.
					//checks if enemy is within the main char's X range (distance collision was not accurate enough)
					if(imgEnemyX[i] >= imgMainCharX && imgEnemyX[i] <= imgMainCharX+100&&!ghostActive)
					{
						//checks if enemy is within the main char's Y range (distance collision was not accurate enough)
						if(imgEnemyY[i] >= imgMainCharY && imgEnemyY[i] <= imgMainCharY+100&&!ghostActive)
						{
							//If the main character was hit, a hit sound is played
							Clip myClip = AudioSystem.getClip();
							URL myURL = getClass().getResource("hit.wav");
							myClip.open(AudioSystem.getAudioInputStream(myURL));
							myClip.start();
							//Main Character loses 8 health for every frame he is being hit by the enemy
							health-=8;
							//Ends the game if health goes to or below 0.
							if(health <= 0)
							{
								//runs death scene
								death = true;
								timer = 0;
							}
						}
					}
				}
			}
		}
		//runs death scene part
		else
		{
			//stops music
			backClip.stop();
			backClipActive = true;
			timer++;
			//keeps death scene for 150 frames before moving to game over screen.
			if(timer>150)
			{
				gameState = 3;
				timer = 0;
				//death scene is reset so game can be looped
				death = false;
			}
					
		}
		break;
			
		case 3:
			// changes cursor image.
			setCursor(cursor);
			//increments timer.
			timer++;
			//because start clip was turned off after main game state was active, it can now be turned back on.
			//This also allows for seamless transition of music between the start screen, the  instruction screen, the option screen, and the game over screen
			if(startClipActive)
			{
				startClip.loop(100);
				startClipActive = false;
			}
			//If a high score is achieved, the high score name will be recorded
//			if(score>highScore)
//			{
//				String name;
//				//Takes in a name
//				name = JOptionPane.showInputDialog("Name: ");
//				highScoreName = name;
//				//Takes in the score
//				highScore = (int)(score);
//				
//				FileWriter outFile = new FileWriter("highscore.txt");
//				//records highscore name in txt file
//				outFile.write(highScoreName+"\n");
//				//records highscore name in txt file
//				outFile.write(highScore+"");
//				//closes outfile.
//				outFile.close();
//			}
			if(mouseClick)
			{
				//Plays gunshot sound when mouse is clicked
				Clip myClip = AudioSystem.getClip();
				URL myURL = getClass().getResource("gunshot.wav");
				myClip.open(AudioSystem.getAudioInputStream(myURL));
				myClip.start();
				//Checks if retry button is pressed
				if(imgScopeY>=325 && imgScopeY<= 425)
				{
					if(imgScopeX >= 250 && imgScopeX <= 550)
					{
						//resets all stats from previous game and starts it again
						timer = 0;
						difficulty = 0;
						time=0;
						kills = 0;
						eye=startEye;
						eyeActive = false;
						ghost = 1;
						bullets = startBullet;
						health = startHealth;
						imgMainCharX = 350;
						imgMainCharY = 300;
						for(int i = 0; i<50; i++)
						{
							enemyActive[i]=false;
						}
						gameState = 2;
						startClipActive = true;
						startClip.stop();
					}
				}
				//Checks if either the menu button or quit button where clicked
				else if(imgScopeY>440 && imgScopeY<= 540)
				{
					//Checks if the menu button was clicked and changes to the start screen if so
					if(imgScopeX >= 30 && imgScopeX <= 330)
						gameState = 1;
					
					//Checks if the quit button was clicked and exits the game if so
					else if(imgScopeX >= 460 && imgScopeX <= 760)
						System.exit(ABORT);
				}
				//turns of mouse click so process can be reset.
				mouseClick = false;
			}
			break;
			
		case 4:
			// changes cursor image.
			setCursor(cursor);
			//Checks if mouse was clicked
			if(mouseClick)
			{
				//Plays gunshot sound
				Clip myClip = AudioSystem.getClip();
				URL myURL = getClass().getResource("gunshot.wav");
				myClip.open(AudioSystem.getAudioInputStream(myURL));
				myClip.start();
				//checks if back to menu button was clicked on and changes to start screen if so.
				if(imgScopeY>=490 && imgScopeY<= 540)
				{
					if(imgScopeX >= 30 && imgScopeX <= 130)
					{
						gameState = 1;
					}
				}
				//turns of mouse click so process can be reset.
				mouseClick = false;
			}
			break;
			
		case 5:
			//Pause screen pauses the whole game. Therefore, nothing changes.
			break;
			
		case 6:
			// changes cursor image.
			setCursor(cursor);
			//Checks if the mouse is clicked
			if(mouseClick)
			{
				//Plays gunshot sound
				Clip myClip = AudioSystem.getClip();
				URL myURL = getClass().getResource("gunshot.wav");
				myClip.open(AudioSystem.getAudioInputStream(myURL));
				myClip.start();
				//Checks if more bullet or more bullet regen perk was clicked.
				if(imgScopeY>=20 && imgScopeY<= 250)
				{
					//Checks if more bullet perk was clicked.
					if(imgScopeX >= 40 && imgScopeX <= 40+imgMoreBullet.getWidth())
					{
						//gives more starting bullets, and keeps the rest of the stats the same
						startBullet = 50;
						bulletRegen= 1;
						startHealth = 200;
						startEye = 3;		
						
					}
					//Checks if bullet regen perk was clicked.
					else if(imgScopeX >= 200+imgMoreBullet.getWidth() && imgScopeX <= 200+2*imgMoreBullet.getWidth())
					{
						//gives more bullet regen, and keeps the rest of the stats the same.
						startBullet = 25;
						bulletRegen= 2;
						startHealth = 200;
						startEye = 3;
					}
				}
				//Checks if either more health or more time freeze perk was clicked
				else if(imgScopeY>=280 && imgScopeY<= 570)
				{
					//Checks if more health perk was clicked
					if(imgScopeX >= 40 && imgScopeX <= 40+imgMoreBullet.getWidth())
					{
						//gives more health, and keeps the rest of the stats the same.
						startBullet = 25;
						bulletRegen= 1;
						startHealth = 300;
						startEye = 3;
						
					}
					//Checks if more time freeze perk was clicked
					else if(imgScopeX >= 200+imgMoreBullet.getWidth() && imgScopeX <= 200+2*imgMoreBullet.getWidth())
					{
						//gives more eye uses, and keeps the rest of the stats the same.
						startBullet = 25;
						bulletRegen= 1;
						startHealth = 200;
						startEye = 4;	
					}
					//The player is only allowed to use one perk per game.
					
					//Special easter egg where if you click on the bottom right corner of the screen, you will receive all perks
					if(imgScopeX+imgScope.getWidth()/2 >=750 && imgScopeY+imgScope.getHeight()/2 >=550)
					{
						startBullet = 50;
						bulletRegen= 2;
						startHealth = 300;
						startEye = 4;	
					}
				}
				//turns of the mouse click after mouse is clicked
				mouseClick = false;
			}
			
			break;
		}
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//moves the scope according to where the mouse moves. Allows constant firing while dragging
		imgScopeX = e.getX();
		imgScopeY = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//moves the scope according to where the mouse moves.
		imgScopeX = e.getX();
		imgScopeY = e.getY();
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		//This switch statement was done to force the player to wait 1 second after the game is over before clicking
		//This was done to remove the chances of accidentally clicking a button on the gameover screen from shooting in the main game.
		switch(gameState)
		{
		//Allows the mouseClick to be true in the option, start, and instuction screen
		case 1:
		case 4:
		case 6:
			mouseClick = true;
			break;
		
		//Forces the player to wait before clicking on the gameover screen
		case 3:
			if(timer>60)
				mouseClick = true;
			break;
			
			//mouseClick will be disabled on all other screens
			default:
				mouseClick = false;
				break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//shot is fired if the main game screen is active
		if(gameState == 2)
			shot = true;
			
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//shot is deactivated if the main game screen is active so that firing can be turned off.
		if(gameState==2)
			shot = false;
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		//enables booleans that control main character movement.
			switch(e.getKeyChar())
			{
			case 'w':
				movingUp =true;
				break;
				
			case 'a':
				movingLeft = true;
				break;
				
			case 's': 
				movingDown = true;
				break;
				
			case 'd':
				movingRight = true;
				break;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//enables booleans that control main character movement so movement can be stopped.
			switch(e.getKeyChar())
			{
			case 'w':
				movingUp =false;
				break;
				
			case 'a':
				movingLeft = false;
				break;
				
			case 's': 
				movingDown = false;
				break;
				
			case 'd':
				movingRight = false;
				break;
			}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//allows use of escape key to exit the option screen.
		if(gameState == 6 || gameState == 4)
		{
			if(e.getKeyChar()== 27)
			{
				//brings user back to start screen
				gameState = 1;
			}
		}
		
		switch(e.getKeyChar())
		{
		//pause integer is used so that when pause is even the game pauses. This allows the player to type p to pause and unpause
		case 'p':
			pause++;
			if(pause%2 == 0)
				gameState = 5;
			else
				gameState = 2;
			break;
		//when q is typed and there is at least one eye use remaining, the ability activates
		case 'q':
			if(eye > 0)
			{
				//activates eye ability
				eyeActive =true;
				//Sound effect is played
				Clip myClip;
				try {
					myClip = AudioSystem.getClip();
					URL myURL = getClass().getResource("eye.wav");
					myClip.open(AudioSystem.getAudioInputStream(myURL));
					myClip.start();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedAudioFileException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//number of eye uses left decreases
				eye--;
			}
			break;
			
		case 'e':
			//when e is tapped and there is a ghost use left, ghost activates
			if(ghost>0)
			{
				ghostActive = true;
				ghost--;
			}	
			break;
		}
		
			
	}
	
	
}
