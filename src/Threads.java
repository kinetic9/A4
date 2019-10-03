public class Threads implements Runnable {
	
	private WordRecord wordRec;
	private int maxWords;
	private Score score;
	
	public Threads(WordRecord wordRec, Score score, int maxWords) {
		this.wordRec = wordRec;
		this.maxWords = maxWords;
		this.score = score;
	}
	
	public void run() {
		try {
        	while(true) {
        		if(wordRec.getDropped() == true) {
	        		Thread.sleep(wordRec.getSpeed());
	        		boolean hitBottom = wordRec.drop(10);
	        		
	        		if(hitBottom)
	        		{
	        			score.missedWord();
	        			if(score.getTotal() < maxWords) {
	        				wordRec.resetWord();
	        				wordRec.setDropped(true);
	        			}
	        			else {
	        				wordRec.resetWord();
	        				wordRec.setDropped(true);
	        			}
	        		}
        		}
        	}
        }
		catch (InterruptedException e) {
			System.out.println ("Thread " + Thread.currentThread().getId() + " is done running");  
		}
        catch (Exception e) {
        	//General Error catching
        	System.out.println ("Thread Exception was caught");
        	e.printStackTrace();
        }
    }
}