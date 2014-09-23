package com.github.mcfeod.robots4droid;

public class World {
    //TODO исправить этот страшный костыль
    private static int sHeight;
    private static int sWidth;

    private Player player = new Player(0,0);

    //Нужны соглашения об именовании.
    private GameObject[][] mCurrent;
    private GameObject[][] mFuture;

    // координаты верхнего левого угла отрисовываемой части поля
    private int mPlayerAreaStartX;
    private int mPlayerAreaStartY;

    public World(int height, int width){
        sHeight = height;
        sWidth = width;
        mCurrent = new GameObject[sHeight][sWidth];
        mFuture = new GameObject[sHeight][sWidth];
        clearBoard(mCurrent);
        clearBoard(mFuture);
    }

    private void clearBoard(GameObject[][] arr){
        //заполняет всё поле ссылками на Empty. Должна быть встроенная функция.
    }
    private void fitUpBoard(GameObject[][] arr){
        //заполняет mFuture ссылками на Empty и Junk в соответствии с mCurrent
    }

    //TODO подумать про read-only доступ к массиву
    public GameObject[][] getBoard(){
        return mCurrent;
    }
    public GameObject[][] getFutureBoard(){
        return mFuture;
    }

    //TODO исправить этот страшный костыль
    public static boolean isPlaceValid(int x, int y){
        return (x>=0) && (x<sWidth) && (y>=0) && (y<sHeight);
    }
    /*
    * Получает от activity ход игрока и обрабатывает
    * Если ход возможен, возвращает 0
    * Если ход невозможен, возвращает одну из описанных в классе статических констант
    * */
    public int setPlayerMove(int deltaX, int deltaY) {

        // координаты клетки, куда попадёт игрок
        int x = player.getX() + deltaX;
        int y = player.getY() + deltaY;

        //проверка, не произошёл ли выход за пределы доски
        if (!isPlaceValid(x, y)) return 1;

        GameObject inner = mCurrent[x][y];
        // если на месте хода находится нетолкаемый мусор(возле стенки)
        if ((inner instanceof Junk) && !((Junk) inner).canBeEjected(player)){
            return 2;
        }
        //TODO обдумать isMoveSuicidal и конфликт с убийством робота
        if (isMoveSuicidal()){
            return 3;
        }
        player.setFuturePos(x, y);
        return 0;

        /* Несущественные комментарии: не для чтения
        if (!isPlaceValid(x, y)) return 1;
        GameObject inner = mCurrent[x][y];
        if (inner instanceof Junk) { // если на месте хода находится мусор
            if (inner.canBeEjected(player)) {
                int pushX = x * 2 - player.getX();
                int pushY = y * 2 - player, getY ();
                mFuture[pushX][pushY] = inner.collideWith(mFuture[pushX][pushY]); //сдвигаем мусор
            } else {
                return 2;
            }
        }*/ // возникает конфликт с убийством робота и состоянием списка
        /*mFuture[x][y] = player;
        if (isMoveSuicidal()){
            // откат изменений
            // разрешение конфликта
            return 3;
        }*/
    }
    //проверяет, не будет ли игрок убит после этого хода
    private boolean isMoveSuicidal(){
        return true;
    }

    // вычисляет новые позиции объектов по всей доске
    public void rebuildBoard(){
        // проверка, был ли сделан игроком ход
        //...
        //ходы роботов
        Robot rob = new Robot(1,1,1,1,1);
        //примерно так будет выглядеть процедура ходов и обработки столкновений
        rob.moveToPlayer(player.getNextX(), player.getNextY());
        mFuture[rob.getXPos()][rob.getYPos()] =
                rob.collideWith(mFuture[rob.getXPos()][rob.getYPos()]);
        //...
        mCurrent = mFuture;
        fitUpBoard(mFuture);
    }

    // вычисляет новые позиции объектов только в отрисовываемой части поля
    public void rebuidPlayerArea(){
        player.makeMove();
        mFuture[player.getX()][player.getY()] = player;
        // ходы роботов и т.д.
    }

    // Определяет координаты верхнего левого угла отрисовываемой части поля в массиве
    private void defineDrawableArea(){
        int height, width;
        // height = Activity.getHeight() ?
        // width = Activity.getWidth() ?
        mPlayerAreaStartX = player.getX() - height/2;
        if(mPlayerAreaStartX < 0){
            mPlayerAreaStartX = 0;
        }
        mPlayerAreaStartY = player.getY() - width/2;
        if(mPlayerAreaStartY < 0){
            mPlayerAreaStartY = 0;
        }

    }
    public int getPlayerAreaStartX(){
        return mPlayerAreaStartX;
    }
    public int getPlayerAreaStartY(){
        return mPlayerAreaStartY;
    }

    // TODO список роботов, счётчик роботов, очки и т.д.
}
