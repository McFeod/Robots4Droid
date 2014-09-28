/* Модуль проверки логической составляющей игры
 * (консольная версия, к которой подключается логика)
 * управление:
 * r - random teleport;
 * t - teleport (safe)
 *	q w e
 *	a s d
 *	z x c
 * - соответствующие направления движения
 *
 */
import java.io.*;
public class RobotsConsole {
	public static void main (String args[]) {
		SimpleGameField wrld = new SimpleGameField(20,30);
		System.out.println("Level: "+wrld.player.getLevel()+"; Energy: "
		+ wrld.player.getEnergy()+ "; Score: "+ wrld.player.getScore());
		System.out.print(wrld.showBoard());
		char ch;
		int code;
		//for(int i=0; i<10; ++i)
			//wrld.player.incLevel();
		try{while ( (code = System.in.read())!= -1)
		{	ch = (char) code;
			boolean succ=false;
			if (ch=='\n')continue;
			System.out.println ("\u001b[2J");
			switch (ch){
				case 'q': succ = wrld.movePlayer((byte)(4));break;
				case 'w': succ = wrld.movePlayer((byte)(0));break;
				case 'e': succ = wrld.movePlayer((byte)(5));break;
				case 'r': succ = wrld.movePlayer((byte)(9));break;
				case 't': succ = wrld.movePlayer((byte)(10));break;
				case 'a': succ = wrld.movePlayer((byte)(2));break;
				case 's': succ = wrld.movePlayer((byte)(8));break;
				case 'd': succ = wrld.movePlayer((byte)(3));break;
				case 'z': succ = wrld.movePlayer((byte)(6));break;
				case 'x': succ = wrld.movePlayer((byte)(1));break;
				case 'c': succ = wrld.movePlayer((byte)(7));break;
				default: System.exit(0); break;
			}
			if (succ)
				wrld.moveBots();
			else
				System.out.println("NOPE!");
			if (wrld.player.isAlive){
				System.out.println("Level: "+wrld.player.getLevel()+"; Energy: "
				+ wrld.player.getEnergy()+ "; Score: "+ wrld.player.getScore());
				System.out.print(wrld.showBoard());
			}
			else{
				System.out.println("GAME OVER!");
				System.out.println("Your score is "+ wrld.player.getScore());
				break;
			}
		}}catch(IOException e){e.printStackTrace();}
	}
}

