package com.github.mcfeod.robots4droid;

import java.util.Random;

public class SimpleWorld {
    // минимальная рабочая версия
    private static int sHeight;
    private static int sWidth;  
    private static int sFastBotCount;
    private static int sBotCount;
    private static int sAliveFastBots;
    private static int sAliveBots;
    
    private static Random rnd = new Random(System.currentTimeMillis());
    
    private static enum Movement{
    	UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, 
    	DOWN_LEFT, DOWN_RIGHT, STAY, TELE, TELE_SAFE
    } // возможные ходы
    
    // "персонажи"
    private static final byte BOT = 2;
    private static final byte FASTBOT = 3;
    private static final byte EMPTY = 0;
    private static final byte JUNK = 4;
    
    private SimplePlayer player = new SimplePlayer(0, 0);//(rnd.nextInt(sHeight),rnd.nextInt(sHeight));
    
    private static byte sBoard[][], sTempBoard[][];
 
    public SimpleWorld(int height, int width){
        sHeight = height;
        sWidth = width;
        sBoard = new byte[sHeight][sWidth];
        sTempBoard = new byte[sHeight][sWidth];
        initLevel();
    }
    
    private void clrBoards(){
    /** обнуление массивов с досками */
    	for(int i=0; i < sHeight; ++i)
        	for(int j=0; j < sWidth; ++j){
        		sBoard[i][j] = EMPTY;
        		sTempBoard[i][j] = EMPTY;   
        	}
    }

    private void initLevel(){
    /** наполнение доски в начале каждого уровня */
    	clrBoards();
    	player.incLevel();
    	sBotCount = 5 + (int)(1.5*player.getLevel());
    	sFastBotCount = -4 + (int)(1.2*player.getLevel());
    	for(int i=0; i<sBotCount; ++i)
    		spawn(BOT);
    	sAliveBots = sBotCount;
    	for(int i=0; i<sFastBotCount; ++i)
    		spawn(FASTBOT);  
    	sAliveFastBots = sFastBotCount;	
    	player.setPos(findSafePos());
    	//TODO отрисовка
    }
    
    public boolean movePlayer(Movement where){
    /** возвращает false, если ход недопустим, true - после успешного завершения хода */
    	Point tmp = new Point(player.getPos());
    	switch(where){
    		case STAY: break;
    		case TELE: player.setPos(findPos()); return true;
    		case TELE_SAFE: tmp = findSafePos(); break;
    		case UP: tmp.y--; break;
    		case DOWN: tmp.y++; break;
    		case LEFT: tmp.x--; break;
    		case RIGHT: tmp.x++; break;
    		case UP_LEFT: tmp.x--; tmp.y--; break;
    		case UP_RIGHT: tmp.x++; tmp.y--; break;
    		case DOWN_LEFT: tmp.x--; tmp.x++; break;
    		case DOWN_RIGHT: tmp.x++; tmp.y++;
    	}
    	if (!tmp.isOnBoard(sHeight, sWidth))
			return false;
    	//TODO сделать опционально отключение "безопасных" ходов:
    	if (!isSafePos(tmp))
    		return false;
    	player.setPos(tmp);
    	return true;
    }
    
    
    private void spawn(byte person){
    /** устанавливает person на случайную свободную клетку */
    	Point p = findPos();
    	sBoard[p.x][p.y] = person;
    }
    
    private Point findPos(){
    /** ищет случайную свободную клетку */
    	int x = rnd.nextInt(sHeight);
    	int y = rnd.nextInt(sWidth);
    	for(int i=0; i<sHeight*sWidth; ++i){
    		if (sBoard[x][y] == EMPTY)
    			return new Point(x,y);
    		else{
    			x = rnd.nextInt(sHeight);
    			y = rnd.nextInt(sWidth);
    		}
    	}
    	winner();
    	return null;
    }
    
    public void moveBots(){
    /** Перемещения роботов */
    	// клонируем доску без врагов   	
    	for(int i=0; i<sHeight; ++i)
    		for(int j=0; j<sWidth; ++j)
    			switch(sBoard[i][j]){
	    			case EMPTY:
	    			case BOT:
	    			case FASTBOT: sTempBoard[i][j]=EMPTY; break;
	    			default: sTempBoard[i][j]=JUNK; }
    	//перемещаем всех роботов на 1 шаг:
    	int pX = player.getPos().x;
    	int pY = player.getPos().y;
    	byte diffX,diffY;
    	for(int i=0; i<sHeight; ++i)
    		for(int j=0; j<sWidth; ++j)
    			if ((sBoard[i][j]==BOT)||(sBoard[i][j]==FASTBOT)){ // Не стал использовать сложение, чтобы потом 
    				diffX = diffY = 0;						// переписать при помощи классов. Получилась эта жуть
    				if (j<pX) diffX++; 
    				else if (j>pX) diffX--;
    				if (i<pY) diffY++; 
    				else if (i>pY) diffY--;
    				switch (sTempBoard[i+diffY][j+diffX]){
    					case BOT: 
    						if (sBoard[i][j] == BOT){
    							player.incScore(20);
    							sAliveBots-=2; }
    						else {
    							player.incScore(25);
    							sAliveBots--; sAliveFastBots--; }
    						sTempBoard[i+diffY][j+diffX] = JUNK;
    						break;
    					case FASTBOT:
    						if (sBoard[i][j] == BOT) {
    							player.incScore(25);
    							sAliveBots--; sAliveFastBots--; }
    						else { 
    							player.incScore(30);
    							sAliveFastBots-=2; } 
    						sTempBoard[i+diffY][j+diffX] = JUNK;
    						break;
    					case JUNK:
    						if (sBoard[i][j] == BOT) {
    							player.incScore(10);
    							sAliveBots--;}
    						else { 
    							player.incScore(15);
    							sAliveFastBots--; } 
    						break;
    					case EMPTY:
    						sTempBoard[i+diffY][j+diffX] = sBoard[i][j];		
    				} // switch (sTempBoard[i+diffY][j+diffX])
    			} // if ((sBoard[i][j]==BOT)||(sBoard[i][j]==FASTBOT))
    	checkState(sTempBoard);
    	
    	//теперь переставляем на главную доску всё, кроме быстрых роботов
    	for(int i=0; i<sHeight; ++i)
    		for(int j=0; j<sWidth; ++j)
    			switch(sTempBoard[i][j]){
	    			case EMPTY:
	    			case FASTBOT: sBoard[i][j]=EMPTY; break;
	    			default: sBoard[i][j]=sTempBoard[i][j]; }
    	
    	//второй ход быстрых роботов
    	for(int i=0; i<sHeight; ++i)
    		for(int j=0; j<sWidth; ++j)
    			if (sTempBoard[i][j]==FASTBOT){
    				diffX = diffY = 0;						
    				if (j<pX) diffX++; 
    				else if (j>pX) diffX--;
    				if (i<pY) diffY++; 
    				else if (i>pY) diffY--;
    				switch (sBoard[i+diffY][j+diffX]){
						case BOT:
							player.incScore(25);
							sAliveBots--; sAliveFastBots--; 
							sBoard[i+diffY][j+diffX] = JUNK;
							break;
						case FASTBOT:
							player.incScore(30);
							sAliveFastBots-=2; 
							sBoard[i+diffY][j+diffX] = JUNK;
							break;
						case JUNK:
							player.incScore(15);
							sAliveFastBots--;
							break;
    					case EMPTY:
    						sBoard[i+diffY][j+diffX] = FASTBOT;
    				} //switch (sBoard[i+diffY][j+diffX])
    			} //if (sTempBoard[i][j]==FASTBOT)
    	
    checkState(sBoard);
    	    	
    } //moveBots()
    
    
    private boolean isSafePos(Point p){
    /** проверка соседних клеток на наличие угрозы. */
    	//попытка смоделировать 1 ход на участке 5х5 вокруг точки p
    	// TODO оптимизировать, а лучше переписать вовсе
    	byte[][] little1 = new byte[5][5];
    	byte[][] little2 = new byte[5][5];
    	int startY=max(0, p.y-2);
    	int finishY = (min(sHeight,p.y+2));
    	int startX = max(0, p.x-2);
    	int finishX = (min(sWidth,p.x+2));
    	int pX = p.x - startX;
    	int pY = p.y - startY;
    	for(int i=startY; i<finishY; ++i)
    		for(int j=startX; j<finishX; ++j){
    			little1[i-startY][j-startX] = sBoard[i][j];
    	} // копия участка получена
    	finishX -= startX; finishY -= startY;
    	// клонируем доску без врагов   	
    	for(int i=0; i<finishY; ++i)
    		for(int j=0; j<finishX; ++j)
    			switch(little1[i][j]){
	    			case EMPTY:
	    			case BOT:
	    			case FASTBOT: little2[i][j]=EMPTY; break;
	    			default: little2[i][j]=JUNK; }
    	//первая часть хода
    	for(int i=0; i<finishY; ++i)
    		for(int j=0; j<finishX; ++j)
    			if ((little1[i][j]==BOT)||(little1[i][j]==FASTBOT)){ 
    				startX = startY = 0;						
    				if (j<pX) startX++; 
    				else if (j>pX) startX--;
    				if (i<pY) startY++; 
    				else if (i>pY) startY--;
    				switch (little2[i+startY][j+startX]){
    					case BOT: 
    					case FASTBOT:
    						little2[i+startY][j+startX] = JUNK;
    						break;
    					case EMPTY:
    						little2[i+startY][j+startX] = little1[i][j];		
    				}
    			} 
    	if (little2[pY][pX] != EMPTY)
    		return false;
    	// 2 перестановка
    	for(int i=0; i<finishY; ++i)
    		for(int j=0; j<finishX; ++j)
    			switch(little2[i][j]){
	    			case EMPTY:
	    			case FASTBOT: little1[i][j]=EMPTY; break;
	    			default: little1[i][j]=little2[i][j]; }
    	
    	//второй ход быстрых роботов
    	for(int i=0; i<finishY; ++i)
    		for(int j=0; j<finishX; ++j)
    			if (little2[i][j]==FASTBOT){
    				startX = startY = 0;						
    				if (j<pX) startX++; 
    				else if (j>pX) startX--;
    				if (i<pY) startY++; 
    				else if (i>pY) startY--;
    				switch (little1[i+startY][j+startX]){
						case BOT:
						case FASTBOT: 
							little1[i+startY][j+startX] = JUNK;
							break;
    					case EMPTY:
    						little1[i+startY][j+startX] = FASTBOT;
    				} //switch (little1[i+startY][j+startX])
    			} //if (little2[i][j]==FASTBOT)
    	if (little2[pY][pX] != EMPTY)
    		return false;
    	return true;
    }
   
    private Point findSafePos(){
    /**возвращает координаты клетки, на которую можно безопасно телепортироваться*/
    	boolean fail = true;
    	Point tmp = findPos();
    	for(int i=0; fail && (i<sHeight*sWidth); ++i){
    		if (isSafePos(tmp))
    			fail = false; 
    		else
    			tmp = findPos();			
    	}
    	if (fail){
    		winner();
    	}
    	return(tmp);
    }
    
    private void checkState(byte[][] arr){
    	Point pos=player.getPos();
    	if ((arr[pos.y][pos.x])!=EMPTY){
    		//TODO оповещение GAME OVER
    	}
    	// TODO отрисовка
    	if ((sAliveBots == 0)&&(sAliveFastBots==0))
    		initLevel();
    }
    public void winner(){
    	//TODO засчитать выигрыш игроку, выход из игры
    }
    
    private int max(int a, int b){return (a>b)?a:b;}
    private int min(int a, int b){return (a<b)?a:b;}
    
    // TODO реализовать список роботов
}
