public class WordRecord {
	private String text;
	private  int x;
	private int y;
	private int maxY;
	private boolean dropped;
	
	private int fallingSpeed;
	private static int maxWait=1500;
	private static int minWait=100;

	public static WordDictionary dict;
	

	
	WordRecord() {
		text="";
		x=0;
		y=0;
		maxY=300;
		fallingSpeed=(int)(Math.random() * (maxWait-minWait)+minWait);
		dropped = false;
	}
	
	WordRecord(String text) {
		this();
		this.text=text;
	}
	
	WordRecord(String text,int x, int maxY) {
		this(text);
		this.x=x;
		this.maxY=maxY;
	}
	
// all getters and setters must be synchronized
	//returns true if at maxY, false otherwise
	public synchronized  void setY(int y) {
		if (y>maxY) {
			y=maxY;
		}
		else {
			this.y=y;
		}
	}
	
	public synchronized  void setX(int x) {
		this.x=x;
	}
	
	public synchronized  void setWord(String text) {
		this.text=text;
	}
	
	public synchronized  void setDropped(boolean dropped) {
		this.dropped = dropped;
	}
	
	public synchronized  boolean getDropped() {
		return dropped;
	}

	public synchronized  String getWord() {
		return text;
	}
	
	public synchronized  int getX() {
		return x;
	}	
	
	public synchronized  int getY() {
		return y;
	}
	
	public synchronized  int getSpeed() {
		return fallingSpeed;
	}

	public synchronized void setPos(int x, int y) {
		setY(y);
		setX(x);
	}
	public synchronized void resetPos() {
		setY(0);
	}

	public synchronized void resetWord() {
		dropped = false;
		resetPos();
		text=dict.getNewWord();
		fallingSpeed=(int)(Math.random() * (maxWait-minWait)+minWait);
		//System.out.println(getWord() + " falling speed = " + getSpeed());
	}
	
	public synchronized boolean matchWord(String typedText) {
		//System.out.println("Matching against: "+text);
		if (typedText.equals(this.text)) {
			resetWord();
			return true;
		}
		else
			return false;
	}
	
	public synchronized boolean drop(int inc) {
		setY(y+inc);
		if(y >= maxY) {
			return true;
		}
		else {
			return false;
		}
	}
}