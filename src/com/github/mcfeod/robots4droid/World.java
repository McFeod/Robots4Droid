package com.github.mcfeod.robots4droid;

import java.util.Set;

public class World{
	public int mLevel;
    public int mHeight;
    public int mWidth;
    private int mFastRobotCount;
    private int mRobotCount;

    //возможные ходы
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
    public Board mBoard;
    
    /*вспомогательные объекты и переменные для хранения временной информации*/
    private Point junkPos, objectPos, freePos;
    private boolean isJunkExists;
    private byte objectKind;

    private boolean areSuicidesForbidden;
    
    public World(int width, int height){
		mWidth = width;
		mHeight = height;
		mBoard = new Board(width, height);
        player = new Player();
        mLevel=0;
        //создание вспомогательных объектов
        freePos = new Point();
        junkPos = new Point();
        objectPos = new Point();
        //создание первого уровня
        initLevel();

        areSuicidesForbidden = !SettingsParser.areSuicidesOn();
        // Побочный эффект:эту настройку нельзя изменять во время партии
        // Но, пока нет соохранения партии в Bundle, изменение настроек во время партии вообще невозможно
    }

    /** Создание нового уровня */
    private void initLevel(){
    	mBoard.Clear(); //очистка доски
    	mLevel ++;
    	//увеличение энергии и очков
    	player.chEnergy((int)(mLevel*0.2+1));

		if (mLevel>1)
			player.chScore((mLevel*5));
		//определение количества роботов
        if(SettingsParser.needExtraFastBots()){
            mRobotCount = 5 + (int)(0.5 * mLevel);
            mFastRobotCount = mLevel;
            player.chEnergy(mLevel); // иначе не пройти дальше 4
        }else{
            mRobotCount = 5 + (int)(1.5 * mLevel);
            mFastRobotCount = -4 + (int)(1.2 * mLevel);
        }
    	mBoard.setRobotCount(mRobotCount, mFastRobotCount);
    	//Размещение простых роботов
    	for(int i=0; i<mRobotCount; ++i){
    		if (findFreePos())
				mBoard.SetKind(freePos, Board.ROBOT);
		}
    	//Размещение быстрых роботов
    	for(int i=0; i<mFastRobotCount; ++i){
    		if (findFreePos())
				mBoard.SetKind(freePos, Board.FASTROBOT);
    	}
    	//Размещение игрока
    	if (findSafePos())
			player.setPos(freePos);
    	//TODO отрисовка
    }

    /** Ищет свободную случайную клетку и сохраняет ее в глобальный freePos.
      Возвращает true, если свободная клетка найдена */
	private boolean findFreePos(){
    	if (mBoard.RandomFindFreePos(freePos))
    		return true;
    	//Если свободная клетка не найдена, то игра заканчивается победой
    	winner();
    	return false;
	}

	/** Проверяет, если клетка с координатами (startX, startY) - JUNK, то сохраняет
	  информацию о ней и о той куда мусор переместиться. Информация нужна для
	  дальнейшего восстановления этих клеток при небезопасном ходе.
	  Возвращает true, если ход разрешен.
	  Ход запрещен, если
	   1) точка (endX, endY) или точка (startX, startY) лежит за пределами поля
	   2) точка (endX, endY) - JUNK */
	private boolean saveInfoAboutJunk(int startX, int startY, int endX, int endY){
		isJunkExists = false;
		objectKind=0;
		if (mBoard.isJunk(startX, startY)){
			junkPos.x = startX;
			junkPos.y = startY;
			objectPos.x = endX;
			objectPos.y = endY;
			//проверки на принадлежность поля
			if (!mBoard.isOnBoard(junkPos))
				return false;
			if (!mBoard.isOnBoard(objectPos))
				return false;
			//проверка на мусор
			if (mBoard.isJunk(endX, endY))
				return false;
			isJunkExists = true;
			//сохраняем информацию о конечной клетке
			objectKind = mBoard.GetKind(endX, endY);
			mBoard.chDiff(objectKind, 1);
			//перемещаем мусор
			mBoard.SetKind(junkPos, Board.EMPTY);
			mBoard.SetKind(objectPos, Board.JUNK);
		}
		return true;
	}

	/** Восстанавливает сохраненную информацию о мусоре */
	private void backInfoAboutJunk(){
		if (isJunkExists){
			mBoard.chDiff(objectKind, 1);
			mBoard.SetKind(junkPos, Board.JUNK);
			mBoard.SetKind(objectPos, objectKind);
		}
	}
	
	/** Возвращает true, если ход выполнен*/
    public boolean movePlayer(byte where){
    	//копируем координаты игрока
    	freePos.x = player.getPos().x;
    	freePos.y = player.getPos().y;
    	switch (where){
    		//case STAY: break;
    		case TELEPORT:
    			if (findFreePos()){
    				player.setPos(freePos);
    				return true;
    			}
    			//если свободной клетки не найдено, то игра заканчивается победой
    			winner();
    			return false;
    		case SAFE_TELEPORT:
    			//проверка на достаточность энергии
    			if (player.getEnergy() < 1)
    				return false;
    			if (findSafePos()){
    				player.setPos(freePos);
        			player.chEnergy(-1);
        			return true;
    			}
    			//если свободной клетки не найдено, то игра заканчивается победой
    			winner();
    			return false;
    		case UP:
    			freePos.y--;//передвигаем игрока
    			//если мусор сдвинуть невозможно, то возвращает false
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x, freePos.y-1))
    				return false;
				break;
			case DOWN:
				freePos.y++;
				if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x, freePos.y+1))
    				return false;
    			break;
    		case LEFT:
    			freePos.x--;
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x-1, freePos.y))
    				return false;
    			break;
    		case RIGHT:
    			freePos.x++;
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x+1, freePos.y))
    				return false;
    			break;
    		case UP_LEFT:
    			freePos.x--;
    			freePos.y--;
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x-1, freePos.y-1))
    				return false;
    			break;
    		case UP_RIGHT:
    			freePos.x++;
    			freePos.y--;
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x+1, freePos.y-1))
    				return false;		
    			break;
    		case DOWN_LEFT:
    			freePos.x--;
    			freePos.y++;
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x-1, freePos.y+1))
    				return false;			
    			break;
    		case DOWN_RIGHT:
    			freePos.x++;
    			freePos.y++;
    			if (!saveInfoAboutJunk(freePos.x, freePos.y, freePos.x+1, freePos.y+1))
    				return false;		
    			break;
    	}
    	//если новые координаты игрока находятся за пределами доски, то возвращает false
    	if (!mBoard.isOnBoard(freePos)){
    		backInfoAboutJunk();//возвращаем объекты на свои места
    		return false;
    	}
    	if (areSuicidesForbidden && !isSafePos(freePos.x, freePos.y)){
    		backInfoAboutJunk();
    		return false;
    	}
    	//ход игрока
    	player.setPos(freePos);
    	return true;
    }

    /** Передвигает одного робота в сторону игрока */
	private void moveRobot(int robotX, int robotY, int playerX, int playerY){
    	int newBotX = robotX, newBotY = robotY;
    	if (robotX > playerX)
    		newBotX --;
    	else
    		if (robotX < playerX)
        		newBotX ++;
    	if (robotY > playerY)
    		newBotY --;
    	else
    		if (robotY < playerY)
        		newBotY ++;
    	mBoard.MoveObject(robotX, robotY, newBotX, newBotY);    	
    }
    
	/*Передвигает роботов, стоящих от игрока на радиусе r, к игроку.
	  Если all == true, то передвигает всех роботов, иначе - только быстрых*/
	private void moveRobots(int playerX, int playerY, int r, boolean all){
    	boolean isExists=false;
    	for (int i=0; i<2*r+1; i++){
    		//верхняя горизонталь
    		if (all)
    			isExists=mBoard.isEnemy(playerX-r+i,playerY-r);
    		else
    			isExists=mBoard.isFastRobot(playerX-r+i,playerY-r);
    		//если робот существует, то перемещаем его
    		if (isExists){
    			moveRobot(playerX-r+i, playerY-r, playerX, playerY);
    			//если радиус == 1, то игрок убит
    			if (r == 1)
    				player.isAlive = false;
    		}
			//нижняя горизонталь
    		if (all)
    			isExists=mBoard.isEnemy(playerX-r+i,playerY+r);
    		else
    			isExists=mBoard.isFastRobot(playerX-r+i,playerY+r);
			if (isExists){
				moveRobot(playerX-r+i, playerY+r, playerX, playerY);
    			if (r == 1)
    				player.isAlive = false;
    		}
    		//левая вертикаль
			if (all)
				isExists=mBoard.isEnemy(playerX-r,playerY-r+i);
    		else
    			isExists=mBoard.isFastRobot(playerX-r,playerY-r+i);
    		if (isExists){
    			moveRobot(playerX-r, playerY-r+i, playerX, playerY);
    			if (r == 1)
    				player.isAlive = false;
    		}
    		//правая вертикаль
    		if (all)
    			isExists=mBoard.isEnemy(playerX+r,playerY-r+i);
    		else
    			isExists=mBoard.isFastRobot(playerX+r,playerY-r+i);
    		if (isExists){
    			moveRobot(playerX+r, playerY-r+i, playerX, playerY);
    			if (r == 1)
    				player.isAlive = false;
    		}
    	}
    }
	
	/** Перемещение роботов */
	public void moveBots(){	
		Point playerPos = player.getPos();
		//поиск максимального радиуса
		int d=playerPos.x;
		if (playerPos.y > d)
			d=playerPos.y;
		if (mWidth-playerPos.x-1 > d)
			d=mWidth-playerPos.x-1;
		if (mHeight-playerPos.y-1 > d)
			d=mHeight-playerPos.y-1;
		//передвижение всех роботов на одну клетку
    	for (int i=1; i<=d; i++)
			moveRobots(playerPos.x, playerPos.y, i, true);
    	//передвижение быстрых роботов второй раз
    	for (int i=1; i<=d; i++)
    		moveRobots(playerPos.x, playerPos.y, i, false);
    	player.chScore(mBoard.diff2score());
    	//если игрок мертв, то игра заканчивается поражением
    	if (!player.isAlive){
    		return;
    	}
    	//если количество живых роботов == 0, то переходим на следующий уровень
    	if (mBoard.isBotsDead())    		
    		initLevel();	
	}

	/** проверка проверка соседних клеток на наличие угрозы. */
	private boolean isSafePos(int x, int y){
		//проверяем соседей в радиусе 1 клетки 
		if (!mBoard.isEmpty(x,y))
			return false;
		for(byte i=-1; i<2;++i)
			for (byte j=-1; j<2; ++j){
				if ((i==0)&&(j==0))
					continue;
				if (mBoard.isEnemy(x+j,y+i))
					return false;
				else
					if (mBoard.isEmpty(x+j,y+i)&&(isDanger2nd(x, y, i, j))) //и в радиусе 2
						return false;
			}
		return true;
	}
	
	/** проверка в радиусе 2 клеток
	 * p - проверяемая точка
	 * (x;y) - diff координат. в сумме с p даёт точку, через которую
	 * возможно вторжение роботов
	 * возвращает true, если p небезопасна */
	private boolean isDanger2nd(int px, int py, int y, int x){
		if ((x!=0)&&(y!=0)){		//диагонали
			if (mBoard.isFastRobot(px+2*x, py+y*2))
				return true;
		}
		else{ 	// проверка 3 клеток, с которых за 2 хода достигается p через (p.x+x;p.y+y)
			boolean fast = false;
			int count = 0;
			if (x==0){	//по горизонтали
				for(byte j=-1; j<2; ++j)
					if (mBoard.isEnemy(px+j,y*2+py)){
						count++;
						if (mBoard.isFastRobot(px+j,y*2+py))
							fast=true;
					}
			}
			else{	//по вертикали
				for(byte i=-1; i<2; ++i)
					if (mBoard.isEnemy(px+x*2, py+i)){
						count++;
						if (mBoard.isFastRobot(px+x*2, py+i))
							fast=true;
					}
			}
			if (fast && (count == 1)) // если нет быстрых роботов - угрозы нет
				return true;// если роботов несколько - они столкнутся на (p.x+x;p.y+y)
		}
		return false; // всё OK
	}
	
	/*Ищет координаты безопасной клетки и сохраняет их в freePos.
	  Возвращает true, если такая клетка существует*/
	private boolean findSafePos(){    
	  	for(int i=0; i<2*mHeight*mWidth; ++i)
	  		if (findFreePos())
	  			if ((freePos.x != player.getPos().x) && (freePos.y != player.getPos().y))
	  				if (isSafePos(freePos.x, freePos.y))
	  					return true;
	  	for (int i=0; i<mWidth; i++)
	  		for (int j=0; j<mHeight; j++)
	  			if ((i != player.getPos().x) && (j != player.getPos().y))
	  				if (isSafePos(i, j))
	  					return true;
	  	return false;
	}
	
    public void winner(){

    }

    public void defeat(){
    	mLevel = 0;
		player.chEnergy(-player.getEnergy());    		
		player.isAlive = true;
		initLevel();
    }

}