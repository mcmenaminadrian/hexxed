package hexedit

import java.awt.event.KeyEvent;
import java.awt.event.*

class HexxedKeyListener extends KeyAdapter {
	
	HexxedKeyListener()
	{
		super()
	}

	void keyTyped(KeyEvent kE)
	{
		def keyCode = kE.getKeyCode()
		if (keyCode == KeyEvent.VK_CIRCUMFLEX) {
			backward()
		}
	}
	
	void keyPressed(KeyEvent kE)
	{
		def x = 99
	}
	
	void keyReleased(KeyEvent kE)
	{
		def x = 9
	}
}
