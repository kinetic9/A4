import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

   	static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;

	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually

	static Thread [] threads;
	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	static Score score = new Score();

	static WordPanel w;
	static Thread panelThread;
	static JFrame frame;
	static JLabel caught;
	static JLabel missed;
	static JLabel scr;
	
	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
    	
      	JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      	g.setSize(frameX,frameY);
 
    	
		w = new WordPanel(words,yLimit);
		w.setSize(frameX,yLimit+100);
	    g.add(w);
	    
	    
	    JPanel txt = new JPanel();
	    txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
	    caught =new JLabel("Caught: " + score.getCaught() + "    ");
	    missed =new JLabel("Missed:" + score.getMissed() + "    ");
	    scr =new JLabel("Score:" + score.getScore() + "    ");    
	    txt.add(caught);
	    txt.add(missed);
	    txt.add(scr);
      
		final JTextField textEntry = new JTextField("",20);
		textEntry.setEnabled(false); // disable until start is pressed
	   textEntry.addActionListener(new ActionListener()
	    { // Add listener to the text box for when enter is pressed
	      public void actionPerformed(ActionEvent evt) {
	          String text = textEntry.getText();
	          for(WordRecord word: words) {
	        	  if(word.matchWord(text)) {
	        		  score.caughtWord(text.length());
	        	  }
	          }
	          textEntry.setText("");
	          textEntry.requestFocus();
	      }
	    });
	   
	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	  
	   g.add(txt);
	    
	    JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
	   	JButton startB = new JButton("Start");
		
		// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e)
		      {
				  textEntry.setEnabled(true); // only be active when button is pressed
		    	  w.setRunning(true);
		    	  for(WordRecord word: words) {
		    		  word.setDropped(true);
		    	  }
		    	  textEntry.requestFocus();  //return focus to the text entry field
		      }
		    });
		JButton endB = new JButton("End");
			
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e)
		      {
		    	  w.setRunning(false);
		    	  for(WordRecord word: words) {
		    		  word.resetWord();
		    	  }
		    	  
		    	  w.repaint();
				  JOptionPane.showMessageDialog(frame, "Game over! Final score: "+score.getScore());
				  textEntry.setEnabled(false); // reset to not active
				  score.resetScore();
		      }
		    });
		JButton quitB = new JButton("Quit");
			
			// add the listener to the jbutton to handle the "pressed" event
		quitB.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e)
		      {
		    	  for(Thread thread: threads) {
		    		  thread.interrupt();
		    	  }
		    	  panelThread.interrupt();
		    	  System.exit(0);
		      }
		    });
		
		b.add(startB);
		b.add(endB);
		b.add(quitB);
		
		g.add(b);
    	
      	frame.setLocationRelativeTo(null);  // Center window on screen.
      	frame.add(g); //add contents to window
        frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
        frame.setVisible(true);
		
	}

	
	public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLen = dictReader.nextInt(); //size of dictionary
			

			dictStr=new String[dictLen];
			for (int i=0;i<dictLen;i++) {
				dictStr[i]=new String(dictReader.next());

			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;

	}

	public static void main(String[] args) {
    	
		//deal with command line arguments
		String[] tmpDict;
		if(args.length > 0) {
			totalWords=Integer.parseInt(args[0]);  //total words to fall
			noWords=Integer.parseInt(args[1]); // total words falling at any point
			assert(totalWords>=noWords); // this could be done more neatly
			tmpDict=getDictFromFile(args[2]); //file of words
		}
		else { //for testing
			totalWords = 10;  //total words to fall
			noWords = 5; // total words falling at any point
			tmpDict = getDictFromFile("example_dict.txt"); //file of words
		}
		if (tmpDict!=null)
			dict= new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[noWords];  //shared array of current words
		
		setupGUI(frameX, frameY, yLimit);  
    	
		//Start WordPanel thread
		panelThread = new Thread(w);
		panelThread.start();
		
		int x_inc=(int)frameX/noWords;
	  	//initialize shared array of current words
		
		threads = new Thread[noWords];

		for (int i=0;i<noWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit); 
			Runnable runnable = new Threads(words[i], score, totalWords);
			threads[i] = new Thread(runnable);
			threads[i].start();
		}
		
		while(true) {
			try {
				Thread.sleep(10);
				caught.setText("Caught: " + score.getCaught() + "    ");
				missed.setText("Missed:" + score.getMissed() + "    ");
				scr.setText("Score:" + score.getScore() + "    ");
				
				if(score.getTotal() >= totalWords) {
					w.setRunning(false);
					for(WordRecord word: words) {
						word.resetWord();
			    	}
			    	score.resetScore();
			    	w.repaint();
			    	JOptionPane.showMessageDialog(frame, "Game over! Final score: "+score.getScore());
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}