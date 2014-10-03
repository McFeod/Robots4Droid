package com.github.mcfeod.robots4droid;

import android.widget.TextView;


public class World{	
    private int sLevel;
    private int sHeight;
    private int sWidth;
    private int sFastBotCount;
    private int sBotCount;
    //private int sAliveFastBotCount;
    private int AliveBotCount;
    
    // возможные ходы
	private static final byte UP = 1;
	private static final byte DOWN = 7;
	private static final byte LEFT = 3;
	private static final byte RIGHT = 5;
	private static final byte UP_LEFT = 0;
	private static final byte UP_RIGHT = 2;
	private static final byte DOWN_LEFT = 6;
	private static final byte DOWN_RIGHT = 8;
	private static final byte STAY = 4;
	private static final byte TELEPORT = 9;
	private static final byte SAFE_TELEPORT = 10;
	    
    public Player player;
    public Board sBoard, supBoard;
    private TextView text;

    public World(int width, int height, TextView text){
    	sWidth = width;
        sHeight = height;        
        sBoard = new Board(width, height);
        supBoard = new Board(5,5);	
        player = new Player();
        sLevel=0;
        this.text = text;
        initLevel();
    }
    
    public int getLevel(){
    	return sLevel;
    }

    private void initLevel(){
    /** наполнение доски в начале каждого уровня */
    	sBoard.Clear();
    	sLevel ++;
    	sBotCount = 5 + (int)(1.5 * sLevel);
    	sFastBotCount = -4 + (int)(1.2 * sLevel);
    	
    	Point tmp;
    	for(int i=0; i<sBotCount; ++i){    		
    		tmp = findFreePos();
    		if (tmp != null)
    			sBoard.CreateObject(tmp, Object.BOT);
    	}
    	AliveBotCount = sBotCount;
    	for(int i=0; i<sFastBotCount; ++i){
    		tmp = findFreePos();
    		if (tmp != null)
    			sBoard.CreateObject(tmp, Object.FASTBOT);
    	}
    	AliveBotCount += sFastBotCount;
    	tmp = findSafePos();
    	if (tmp != null)
			player.setPos(tmp);
    	//TODO отрисовка
    }
    
    private Point findFreePos(){
    	Point tmp = sBoard.RandomFindFreePos();
    	if (tmp == null){
    		winner();
    		return null;
    	}else
    		return tmp;    	
    }
    
    public boolean movePlayer(byte where){
    /** возвращает false, если ход недопустим, 
     true - после успешного завершения хода */
    	Point tmp = player.getPos().CopyPoint();
    	boolean isJunkFound = false;
    	Point newJunkPos = new Point();
    	Object junk = new Object(0), bot = new Object(0);
    	switch (where){
    		//case STAY: break;
    		case TELEPORT:
    			tmp = findFreePos();
    			if (tmp == null){
    				winner();
    				return false;
    			}else{
    				text.setText(Integer.toString(tmp.x)+" "+Integer.toString(tmp.y));
    				player.setPos(tmp);
        			return true;
    			}    				
    		case SAFE_TELEPORT:
    			if (player.getEnergy() < 1)
    				return false;
    			tmp = findSafePos();
    			if (tmp == null){
    				winner();
    				return false;
    			}else{
    				player.setPos(tmp);
        			player.chEnergy(-1);
        			return true;
    			}
    		case UP:
    			tmp.y--;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.y --;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;    					
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case DOWN:
    			tmp.y++;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.y ++;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case LEFT:
    			tmp.x--;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.x --;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case RIGHT:
    			tmp.x++;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.x ++;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case UP_LEFT:
    			tmp.x--;
    			tmp.y--;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.x --;
    					newJunkPos.y --;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case UP_RIGHT:
    			tmp.x++;
    			tmp.y--;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.x ++;
    					newJunkPos.y --;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case DOWN_LEFT:
    			tmp.x--;
    			tmp.y++;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.x --;
    					newJunkPos.y ++;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    		case DOWN_RIGHT:
    			tmp.x++;
    			tmp.y++;
    			if (sBoard.GetObject(tmp) != null)
    				if (sBoard.GetObject(tmp).GetKind() == Object.JUNK){
    					newJunkPos = tmp.CopyPoint();
    					newJunkPos.x ++;
    					newJunkPos.y ++;
    					if (!newJunkPos.isOnBoard(sWidth, sHeight))
    						return false;
    					if (sBoard.GetObject(newJunkPos) != null)
    						if (sBoard.GetObject(newJunkPos).GetKind() == Object.JUNK)
    							return false;
    					isJunkFound = true;
    					bot = sBoard.GetObject(newJunkPos);
    					junk = sBoard.GetObject(tmp);
    					sBoard.SetObject(tmp, null);
    					sBoard.SetObject(newJunkPos, junk);
    				}    			
    			break;
    	}
    	if (!tmp.isOnBoard(sWidth, sHeight)){
    		sBoard.SetObject(tmp, junk);
        	sBoard.SetObject(newJunkPos, bot);
    		return false;
    	}			
    	//TODO сделать опционально отключение "безопасных" ходов:
    	if (!isSafePos(tmp.x, tmp.y)){
    		sBoard.SetObject(tmp, junk);
        	sBoard.SetObject(newJunkPos, bot);
    		return false;
    	}
    	sBoard.SetObject(tmp, junk);
    	sBoard.SetObject(newJunkPos, bot);    		
    	if (isJunkFound)
    		sBoard.MoveObject(tmp, newJunkPos);
    	player.setPos(tmp);
    	return true;
    }
    
    private boolean isBot(int x, int y, Board board){
    	if (board != null)
    		if (board.GetObject(x, y) != null)
    			if (board.GetObject(x, y).GetKind() == Object.BOT)
    				return true;
    			else
    				return false;
    		else
    			return false;
    	else
    		return false;
    }
    
    private boolean isFastBot(int x, int y, Board board){
    	if (board != null)
    		if (board.GetObject(x, y) != null)
    			if (board.GetObject(x, y).GetKind() == Object.FASTBOT)
    				return true;
    			else
    				return false;
    		else
    			return false;
    	else
    		return false;
    }
    
    private boolean isEnemy(int x, int y, Board board){
    	if (board != null)
    		if (board.GetObject(x, y) != null)
    			if ((board.GetObject(x, y).GetKind() == Object.BOT) ||
    			 (board.GetObject(x, y).GetKind() == Object.FASTBOT))
    				return true;
    			else
    				return false;
    		else
    			return false;
    	else
    		return false;
    }
    
    private void moveBot(int botX, int botY, int playerX, int playerY){
    	int newBotX = botX, newBotY = botY;
    	if (botX > playerX)
    		newBotX --;
    	else
    		if (botX < playerX)
        		newBotX ++;
    	if (botY > playerY)
    		newBotY --;
    	else
    		if (botY < playerY)
        		newBotY ++;
    	sBoard.MoveObject(botX, botY, newBotX, newBotY);    	
    }
    
    private void moveBots(int x, int y, int r, byte kind){
    	boolean isToMove=false;
    	for (int i=0; i<2*r+1; i++){
    		//верхняя горизонталь
    		if (kind == 0)
    			isToMove=isEnemy(x-r+i,y-r,sBoard);
    		else
    			if (kind == 2)
    				isToMove=isFastBot(x-r+i,y-r,sBoard);
    		if (isToMove){
    			moveBot(x-r+i, y-r, x, y);
    			if (r == 1)
    				player.isAlive = false;
    		}
			//нижняя горизонталь
    		if (kind == 0)
    			isToMove=isEnemy(x-r+i,y+r,sBoard);
    		else
    			if (kind == 2)
    				isToMove=isFastBot(x-r+i,y+r,sBoard);
			if (isToMove){
    			moveBot(x-r+i, y+r, x, y);
    			if (r == 1)
    				player.isAlive = false;
    		}
    		//левая вертикаль
			if (kind == 0)
    			isToMove=isEnemy(x-r,y-r+i,sBoard);
    		else
    			if (kind == 2)
    				isToMove=isFastBot(x-r,y-r+i,sBoard);
    		if (isToMove){
    			moveBot(x-r, y-r+i, x, y);
    			if (r == 1)
    				player.isAlive = false;
    		}
    		//правая вертикаль
    		if (kind == 0)
    			isToMove=isEnemy(x+r,y-r+i,sBoard);
    		else
    			if (kind == 2)
    				isToMove=isFastBot(x+r,y-r+i,sBoard);
    		if (isToMove){
    			moveBot(x+r, y-r+i, x, y);
    			if (r == 1)
    				player.isAlive = false;
    		}
    	}
    }
    
    public void moveBots(){
    /** Перемещение роботов */
    	Point playerPos = player.getPos().CopyPoint();
    	int d=playerPos.x;
    	if (playerPos.y > d)
    		d=playerPos.y;
    	if (sWidth-playerPos.x-1 > d)
    		d=sWidth-playerPos.x-1;
    	if (sHeight-playerPos.y-1 > d)
    		d=sHeight-playerPos.y-1;
    	for (int i=1; i<=d; i++)
			moveBots(playerPos.x, playerPos.y, i, (byte)0);
    	for (int i=1; i<=d; i++)
			moveBots(playerPos.x, playerPos.y, i, (byte)2);
    	if (!player.isAlive){
    		defeat();
    		return;
    	}
    	int count = sBoard.GetObjectCount(Object.BOT)+sBoard.GetObjectCount(Object.FASTBOT);
    	if (count == 0){
    		player.chEnergy((int)(sLevel*0.2+1)); 
    		player.chScore((sLevel*5));
    		initLevel();
    	}
    	
    }
        
	private boolean isSafePos(int x, int y){
	/** проверка соседних клеток на наличие угрозы. */
		//чистим вспомогательный массив 5x5
		supBoard.Clear();	
		for (int i=0; i<5; i++)
			for (int j=0; j<5; j++){
				if (sBoard.GetObject(x-2+i, y-2+j) != null)
					supBoard.CreateObject(i, j, sBoard.GetObject(x-2+i, y-2+j).GetKind());
			}
		//идем по внутреннему кругу
		for (int i=1; i<4; i++)
			for (int j=1; j<4; j++)
				if (isEnemy(i,j,supBoard))
					return false;
		//идем по внешнему кругу и передвигаем быстрых роботов
		boolean isfound=false;
		if (isFastBot(0,0,supBoard)){
			supBoard.MoveObject(0, 0, 1, 1);
			isfound = true;
		}
		if (isFastBot(4,0,supBoard)){
			supBoard.MoveObject(4, 0, 3, 1);
			isfound = true;
		}
		if (isFastBot(4,4,supBoard)){
			supBoard.MoveObject(4, 4, 3, 3);
			isfound = true;
		}	
		if (isFastBot(0,4,supBoard)){
			supBoard.MoveObject(0, 4, 1, 3);
			isfound = true;
		}
		for (int i=1; i<4; i++){
			if (isFastBot(i,0,supBoard)){
				supBoard.MoveObject(i, 0, 2, 1);
				isfound = true;
			}
			if (isFastBot(i,4,supBoard)){
				supBoard.MoveObject(i, 4, 2, 3);
				isfound = true;
			}
			if (isFastBot(0,i,supBoard)){
				supBoard.MoveObject(0, i, 1, 2);
				isfound = true;
			}
			if (isFastBot(4,i,supBoard)){
				supBoard.MoveObject(4, i, 3, 2);
				isfound = true;
			}
		}
		//если был найден хотя бы один быстрый робот, то ищем его на внутреннем круге
		if (isfound)
			for (int i=1; i<4; i++)
				for (int j=1; j<4; j++)
					if (isFastBot(i,j,supBoard))
						return false;
		return true;
	} 

    private Point findSafePos(){
    /**возвращает координаты клетки, на которую можно безопасно телепортироваться*/
    	boolean isFound = false;
    	Point tmp = new Point();
    	for(int i=0; (!isFound) && (i<2*sHeight*sWidth); ++i){
    		tmp = findFreePos();
    		if (tmp != null)
    			if (isSafePos(tmp.x, tmp.y))
    				isFound = true;
    	}
    	if (isFound)
    		return tmp;
    	return null;
    }
    
    public void winner(){
    	//TODO засчитать выигрыш игроку, выход из игры
    }
    
    public void defeat(){
    	sLevel = 0;
		player.chEnergy(-player.getEnergy());    		
		player.isAlive = true;
		initLevel();
    }
    
    // TODO реализовать список роботов
}