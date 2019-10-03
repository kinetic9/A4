import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class WordPanel extends JPanel implements Runnable {
		private static final long serialVersionUID = 1L;
		public static volatile boolean done;
		private WordRecord[] words;
		private int noWords;
		private int maxY;
		private boolean running;

		
		public void paintComponent(Graphics g) {
		    int width = getWidth();
		    int height = getHeight();
		    g.clearRect(0,0,width,height);
		    g.setColor(Color.red);
		    g.fillRect(0,maxY-10,width,height);

		    g.setColor(Color.black);
		    g.setFont(new Font("Helvetica", Font.PLAIN, 26));
		   //draw the words
		   //animation must be added 
		    for (int i=0;i<noWords;i++){	    	
		    	g.drawString(words[i].getWord(),words[i].getX(),words[i].getY()+20);  //y-offset for skeleton so that you can see the words	
		    }
		   
		}
		
		public void setRunning(boolean running) {
			this.running = running;
		}
		
		WordPanel(WordRecord[] words, int maxY) {
			this.words=words; //will this work?
			noWords = words.length;
			done=false;
			this.maxY=maxY;
			this.running = false;
		}
		
		public void run() {
			while(true) {
				try {
					Thread.sleep(10);
					if(running) {
						repaint();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

	}