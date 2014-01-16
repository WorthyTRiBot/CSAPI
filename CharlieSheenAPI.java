import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;

/**
 * @author Worthy
 */
public class CharlieSheenAPI {
	private static final int PEEP_AMOUNT = 80;
	private static final Dimension gameDimensions = new Dimension(765, 503);
	private static int creepValue = PEEP_AMOUNT, rotationValue = -1, x = -1, y = -1, appearTime = -1, minTime = -1, maxTime = -1;
	private static long startTime = -1, bufferTime = -1;
	private static boolean mousedOver = false;
	private static Image charlieSheen;


	/**
	 * Fool newbies with random images of Charlie Sheen!
	 * @param minTime - minimum time for him to appear in milliseconds
	 * @param maxTime - maximum time for him to appear in milliseconds
	 */
	public CharlieSheenAPI(int minTime, int maxTime) {
		CharlieSheenAPI.minTime = minTime;
		CharlieSheenAPI.maxTime = maxTime;
		resetSheen();
	}

	public void draw(Graphics2D g) {
		if (System.currentTimeMillis() <= startTime+appearTime) return;
		if (rotationValue == -1) {
			rotationValue = General.random(0, 3); //bottom, left, top, right
			if (rotationValue%2==0) {
				x = General.random(0, gameDimensions.width-charlieSheen.getWidth(null));
				y = rotationValue==2 ? -charlieSheen.getHeight(null) : gameDimensions.height;
			} else {
				x = rotationValue==1 ? -charlieSheen.getHeight(null) : gameDimensions.width;
				y = General.random(0, gameDimensions.height-charlieSheen.getHeight(null));
			}
			charlieSheen = rotate(charlieSheen, rotationValue*90);
		}

		int posX = x - animateCreep(rotationValue%2!=0, mousedOver) * ((rotationValue==1)?-1:1);
		int posY = y - animateCreep(rotationValue%2==0, mousedOver) * ((rotationValue==2)?-1:1);

		g.drawImage(charlieSheen, posX, posY, null);
		if (mousedOver(posX, posY) && creepValue >= PEEP_AMOUNT) resetSheen();	
	}

	private void resetSheen() {
		appearTime = General.random(minTime, maxTime);
		creepValue = PEEP_AMOUNT;
		rotationValue = -1;
		mousedOver = false;
		startTime = bufferTime = System.currentTimeMillis();
		charlieSheen = getImage("http://i.imgur.com/ZETc3Xf.png");
		
		General.println(appearTime);	//TODO delete
	}

	private boolean mousedOver(int posX, int posY) {
		Rectangle r = new Rectangle(posX, posY, charlieSheen.getWidth(null), charlieSheen.getHeight(null));
		if (r.contains(Mouse.getPos())) mousedOver = true;
		return mousedOver;
	}

	private int animateCreep(boolean animate, boolean reverse) {
		if (!animate || System.currentTimeMillis() - bufferTime <= ((reverse)?1:30)) return 0;
		if ((!reverse && creepValue <= 0) || (reverse && creepValue >= PEEP_AMOUNT)) return PEEP_AMOUNT - creepValue;

		creepValue += reverse?1:-1;
		bufferTime = System.currentTimeMillis();

		return PEEP_AMOUNT - creepValue;
	}

	private static Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	private static Image rotate(Image img, double angle) {
		double sin = Math.abs(Math.sin(Math.toRadians(angle))), cos = Math.abs(Math.cos(Math.toRadians(angle)));
		int w = img.getWidth(null), h = img.getHeight(null), neww = (int) Math.floor(w*cos + h*sin), newh = (int) Math.floor(h*cos + w*sin);

		BufferedImage bimg = new BufferedImage(neww, newh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bimg.createGraphics();
		g.translate((neww-w)/2, (newh-h)/2);
		g.rotate(Math.toRadians(angle), w/2, h/2);
		g.drawRenderedImage((BufferedImage) img, null);
		g.dispose();

		return (Image) bimg;
	}
}