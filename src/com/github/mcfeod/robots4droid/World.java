package com.github.mcfeod.robots4droid;

public class World{	
	private int mLevel;
	private int mHeight;
	private int mWidth;
	private int mFastRobotCount;
	private int mRobotCount;
	private int mGameMode;
	private boolean mShortageMode;
	private boolean mVampMode;
	private int mMaxRobotCount;

	//возможные ходы
	public static final byte UP = 1;
	public static final byte DOWN = 7;
	public static final byte LEFT = 3;
	public static final byte RIGHT = 5;
	public static final byte UP_LEFT = 0;
	public static final byte UP_RIGHT = 2;
	public static final byte DOWN_LEFT = 6;
	public static final byte DOWN_RIGHT = 8;
	public static final byte STAY = 4;
	public static final byte TELEPORT = 9;
	public static final byte SAFE_TELEPORT = 10;
	public static final byte MINE_COST = 3;
	public static final byte SAFE_TELEPORT_COST = 1;
	private static final double MAX_LEVEL = 20;

	public final Player player;
	public final Board board;

	/*вспомогательные объекты и переменные для хранения временной информации*/
	private Point junkPos, objectPos, freePos;
	private boolean isJunkExists;
	private byte objectKind;
	private byte mReward = 0;

	public World(int width, int height, int mode, boolean shortage, boolean vamp){
		mWidth = width;
		mHeight = height;
		board = new Board(width, height);
		player = new Player();
		mLevel=0;
		mMaxRobotCount = calcMaxRobotCount();
		mGameMode = mode;
		mVampMode = vamp;
		mShortageMode = shortage;
		//создание вспомогательных объектов
		freePos = new Point();
		junkPos = new Point();
		objectPos = new Point();
		//создание первого уровня
		initLevel();
	}

	public World(int width, int height, int bots, int fastbots, int pX, int pY,
			int energy, long score, boolean isAlive, boolean isWinner,
			int level, int mode, boolean shortage, boolean vamp){
		mWidth = width;
		mHeight = height;
		board = new Board(width, height, bots, fastbots);
		player = new Player(pX, pY, energy, score, isAlive, isWinner);
		mLevel=level;
		mMaxRobotCount = calcMaxRobotCount();
		mGameMode = mode;
		mVampMode = vamp;
		mShortageMode = shortage;
		//создание вспомогательных объектов
		freePos = new Point();
		junkPos = new Point();
		objectPos = new Point();
	}

	private int calcMaxRobotCount(){
		float x=0;
		float s = mWidth * mHeight;
		for (int i=1; i<=Math.round(s/60.0); i++)
			x+=s/((2*i+1)*(2*i+1));
		return (int)x+2;
	}

	/** Создание нового уровня */
	private void initLevel(){
		board.Clear(); //очистка доски
		mLevel ++;
		//определение количества роботов
		calcBots();
		board.setRobotCount(mRobotCount, mFastRobotCount);
		//увеличение энергии и очков (не помещать раньше подсчёта роботов!!!)
		if (mLevel>1)
			player.chScore((mLevel*5));
		player.chEnergy(calcEnergy());
		//Размещение простых роботов
		for(int i=0; i<mRobotCount; ++i){
			if (findFreePos())
				board.SetKind(freePos, Board.ROBOT);
			else
				board.chDiff(Board.ROBOT, 1);
		}
		//Размещение быстрых роботов
		for(int i=0; i<mFastRobotCount; ++i){
			if (findFreePos())
				board.SetKind(freePos, Board.FASTROBOT);
			else
				board.chDiff(Board.FASTROBOT, 1);
		}
		//Размещение игрока
		if (findSafePos())
			player.setPos(freePos);
		else
			winner();
	}

	

	public boolean setMine(){
		if (player.areSuicidesForbidden)
			if (!isSafePos(player.getPos().x, player.getPos().y))
				return false;
		if (player.getEnergy() >= MINE_COST){
				player.chEnergy(-MINE_COST);
				board.SetKind(player.getPos(), Board.MINE);
				return true;
			}
		return false;
	}
	
	public byte getBombCost(){
		byte cost = 0;
		for (int i=-2; i<=2; i++)
			for (int j=-2; j<=2; j++)
				switch (Math.abs(i)|Math.abs(j)){
					case 0: continue;
					case 1: 
						if (board.isEnemy(player.getPos().x+i, player.getPos().y+j))
						cost++; break;
					default: 
						if (board.isFastRobot(player.getPos().x+i, player.getPos().y+j))
							cost+=2;
				}
		return (byte)(1+Math.round(cost/1.5));
	}

	public boolean bomb(){
		byte cost = getBombCost();

		if (player.getEnergy() >= cost){

			player.chEnergy(-cost);
			for (int i=-2; i<=2; i++)
				for (int j=-2; j<=2; j++)
					switch (Math.abs(i)|Math.abs(j)){
					case 0: continue;
					case 2: 
					case 3: if (!board.isFastRobot(player.getPos().x+i, player.getPos().y+j)) break;
					case 1:
					board.chDiff(board.GetKind(player.getPos().x+i, player.getPos().y+j), 1);
					board.SetKind(player.getPos().x+i, player.getPos().y+j, Board.EMPTY);
				}
			player.chScore(calcScore(board.diff2score()));
			if (board.isBotsDead())
				initLevel();
			return true;
		}
		return false;
	}
	/** Ищет свободную случайную клетку и сохраняет ее в глобальный freePos.
	  Возвращает true, если свободная клетка найдена */
	private boolean findFreePos(){
		return board.RandomFindFreePos(freePos);
	}

	/** Проверяет, если клетка с координатами (startX, startY) - JUNK, то сохраняет
	  информацию о ней и о той куда мусор переместиться. Информация нужна для
	  дальнейшего восстановления этих клеток при небезопасном ходе.
	  Возвращает false, если ход разрешен.
	  Ход запрещен, если
	   1) точка (endX, endY) или точка (startX, startY) лежит за пределами поля
	   2) точка (endX, endY) - JUNK */
	private boolean saveInfoAboutJunk(int startX, int startY, int endX, int endY){
		mReward = 0;
		isJunkExists = false;
		objectKind=0;
		if (board.isJunk(startX, startY)){
			junkPos.x = startX;
			junkPos.y = startY;
			objectPos.x = endX;
			objectPos.y = endY;
			//проверки на принадлежность поля
			if (!board.isOnBoard(junkPos))
				return true;
			if (!board.isOnBoard(objectPos))
				return true;
			//проверка на мусор
			if (board.isJunk(endX, endY))
				return true;
			isJunkExists = true;
			//сохраняем информацию о конечной клетке
			objectKind = board.GetKind(endX, endY);
			if ((objectKind == Board.ROBOT)||(objectKind == Board.FASTROBOT))
				mReward = 1;
			board.chDiff(objectKind, 1);
			//перемещаем мусор
			board.SetKind(junkPos, Board.EMPTY);
			board.SetKind(objectPos, Board.JUNK);
		}
		return false;
	}

	/** Восстанавливает сохраненную информацию о мусоре */
	private void backInfoAboutJunk(){
		if (isJunkExists){
			board.chDiff(objectKind, -1);
			mReward = 0;
			board.SetKind(junkPos, Board.JUNK);
			board.SetKind(objectPos, objectKind);
		}
		isJunkExists = false;
	}

	/** Возвращает true, если ход выполнен*/
	public boolean movePlayer(byte where){
		//копируем координаты игрока
		freePos.x = player.getPos().x;
		freePos.y = player.getPos().y;
		isJunkExists = false;
		switch (where){
			case STAY:
				mReward = 0;
				break;
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
				return false;
			case UP:
				freePos.y--;//передвигаем игрока
				//если мусор сдвинуть невозможно, то возвращает false
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x, freePos.y-1))
					return false;
				break;
			case DOWN:
				freePos.y++;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x, freePos.y+1))
					return false;
				break;
			case LEFT:
				freePos.x--;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x-1, freePos.y))
					return false;
				break;
			case RIGHT:
				freePos.x++;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x+1, freePos.y))
					return false;
				break;
			case UP_LEFT:
				freePos.x--;
				freePos.y--;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x-1, freePos.y-1))
					return false;
				break;
			case UP_RIGHT:
				freePos.x++;
				freePos.y--;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x+1, freePos.y-1))
					return false;
				break;
			case DOWN_LEFT:
				freePos.x--;
				freePos.y++;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x-1, freePos.y+1))
					return false;
				break;
			case DOWN_RIGHT:
				freePos.x++;
				freePos.y++;
				if (saveInfoAboutJunk(freePos.x, freePos.y, freePos.x+1, freePos.y+1))
					return false;
				break;
		}
		//если новые координаты игрока находятся за пределами доски, то возвращает false
		if (!board.isOnBoard(freePos)){
			backInfoAboutJunk();//возвращаем объекты на свои места
			return false;
		}
		if (player.areSuicidesForbidden && !isSafePos(freePos.x, freePos.y)){
			backInfoAboutJunk();
			return false;
		}
		//ход игрока
		player.setPos(freePos);
		if (mVampMode)
			player.chEnergy(mReward);
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
		board.MoveObject(robotX, robotY, newBotX, newBotY);
	}

	/*Передвигает роботов, стоящих от игрока на радиусе r, к игроку.
	  Если all == true, то передвигает всех роботов, иначе - только быстрых*/
	private void moveRobots(int playerX, int playerY, int r, boolean all){
		boolean isExists;
		for (int i=0; i<2*r+1; i++){
			//верхняя горизонталь
			if (all)
				isExists=board.isEnemy(playerX-r+i,playerY-r);
			else
				isExists=board.isFastRobot(playerX-r+i,playerY-r);
			//если робот существует, то перемещаем его
			if (isExists){
				moveRobot(playerX-r+i, playerY-r, playerX, playerY);
				//если радиус == 1, то игрок убит
			}
			//нижняя горизонталь
			if (all)
				isExists=board.isEnemy(playerX-r+i,playerY+r);
			else
				isExists=board.isFastRobot(playerX-r+i,playerY+r);
			if (isExists){
				moveRobot(playerX-r+i, playerY+r, playerX, playerY);
			}
			//левая вертикаль
			if (all)
				isExists=board.isEnemy(playerX-r,playerY-r+i);
			else
				isExists=board.isFastRobot(playerX-r,playerY-r+i);
			if (isExists){
				moveRobot(playerX-r, playerY-r+i, playerX, playerY);
			}
			//правая вертикаль
			if (all)
				isExists=board.isEnemy(playerX+r,playerY-r+i);
			else
				isExists=board.isFastRobot(playerX+r,playerY-r+i);
			if (isExists){
				moveRobot(playerX+r, playerY-r+i, playerX, playerY);
			}
		}
	}

	/** Перемещение роботов */
	public void moveBots(boolean toMoveFast){	
		Point playerPos = player.getPos();
		//поиск максимального радиуса
		int d=playerPos.x;
		if (playerPos.y > d)
			d=playerPos.y;
		if (mWidth-playerPos.x-1 > d)
			d=mWidth-playerPos.x-1;
		if (mHeight-playerPos.y-1 > d)
			d=mHeight-playerPos.y-1;
		//передвижение всех роботов на одну клетку либо быстрых второй раз
		for (int i=1; i<=d; i++)
			moveRobots(playerPos.x, playerPos.y, i, !toMoveFast);
		player.chScore(calcScore(board.diff2score()));
		/*костыль для сочетания "Ваниной спиральки" и наглости игрока, 
		 * прущего на робота.*/ 
		if (board.wasEnemy(player.getPos()))
			player.isAlive = false;
		//если игрок мертв, то игра заканчивается поражением
		if (!player.isAlive){
			return;
		}
		//если количество живых роботов == 0, то переходим на следующий уровень
		if (board.isBotsDead())
			initLevel();
	}

	/** проверка проверка соседних клеток на наличие угрозы. */
	private boolean isSafePos(int x, int y){
		//проверяем соседей в радиусе 1 клетки 
		if (!board.isEmpty(x,y))
			return false;
		for(byte i=-1; i<2;++i)
			for (byte j=-1; j<2; ++j){
				if ((i==0)&&(j==0))
					continue;
				if (board.isEnemy(x+j,y+i))
					return false;
				else
					if (board.isEmpty(x+j,y+i)&&(isDanger2nd(x, y, i, j))) //и в радиусе 2
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
			if (board.isFastRobot(px+2*x, py+y*2))
				return true;
		}
		else{ 	// проверка 3 клеток, с которых за 2 хода достигается p через (p.x+x;p.y+y)
			boolean fast = false;
			int count = 0;
			if (x==0){	//по горизонтали
				for(byte j=-1; j<2; ++j)
					if (board.isEnemy(px+j,y*2+py)){
						count++;
						if (board.isFastRobot(px+j,y*2+py))
							fast=true;
					}
			}
			else{	//по вертикали
				for(byte i=-1; i<2; ++i)
					if (board.isEnemy(px+x*2, py+i)){
						count++;
						if (board.isFastRobot(px+x*2, py+i))
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

	private void calcBots(){
		double slowMax = mMaxRobotCount * (1 - 0.1 * (mGameMode * 2 + 1));
		double fastMax = mMaxRobotCount * (0.1 * (mGameMode * 2 + 1));
		if (mLevel <= MAX_LEVEL){
			double perc = mLevel / MAX_LEVEL;
			mRobotCount = (int)Math.round(slowMax * perc);
			mFastRobotCount = (int)Math.round(fastMax * perc);
		}else{
			slowMax -= mLevel - MAX_LEVEL;
			fastMax += mLevel - MAX_LEVEL;
			mRobotCount = (slowMax > 0) ? (int)(slowMax) : 0;
			if (fastMax > mMaxRobotCount)
				fastMax = mMaxRobotCount - (fastMax - mMaxRobotCount);
			if (fastMax < 1.0)
				winner();
			mFastRobotCount = (int)(fastMax);
		}
	}

	private int calcEnergy(){
		float linear = (9*mRobotCount + 25*mFastRobotCount)/4/(mWidth+mHeight);
		int res = (int) (1 + Math.sqrt(linear));
		if (mShortageMode){
			if (mLevel == 1){
				if (!mVampMode)
					return (int)(Math.round(mWidth+mHeight)/2);
			}
			return 0;
		}
		return (res>6 ? 6:res);
	}

	private int calcScore(int primary){
		float k = 1;
		if (mShortageMode)
			k*=1.5;
		if (!mVampMode)
			k*=1.2;
		return 5*(int)Math.round(k*primary*(1+0.25*mGameMode));
	}

	public void winner(){
		player.isAlive = false;
		player.isWinner = true;
	}
	
	public int getHeight() {
		return mHeight;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getGameMode() {
		return mGameMode;
	}
	
	public boolean isVampMode() {
		return mVampMode;
	}

	public boolean isShortageMode() {
		return mShortageMode;
	}

	public int getLevel() {
		return mLevel;
	}

	public void defeat(){
		mLevel = 0;
		player.reset();
		initLevel();
	}
}
