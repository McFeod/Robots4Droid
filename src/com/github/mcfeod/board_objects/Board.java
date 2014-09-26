package com.github.mcfeod.board_objects;
// Автор: silent git(11). Alex pdk пишет только битые jpg, причём из головы и в блокноте.
// (ещё svg в UTF-8 тем же способом)
// Комментарии написаны глубокой ночью, с тоски, под соседский перфоратор и проект "Голос", отсюда их странный характер.
// Обещаю исправить(не сразу) лексические и стилистические ошибки, а также удалить ненужные повествовательные моменты.
/* --- Класс Board. ---
* Очевидно, доска. Не отвечает за игровой процесс.
* Чтобы кто попало её не модифицировал (на ветке работает целый один злоумышленник),
* модифицирующие методы вместе с применяющими их объектами заключены в пакет board_objects.
*
* В реализации используются два приватных массива: mCurrent и mFuture.
* Без массива mFuture программа будет работать неоднозначно в такой ситуации:
*  R|_|_
*  _|R|_
*  _|P|_
* где R -- робот, P -- игрок.
* Если первым сходит центральный робот, игрок будет убит. А если второй, в центре произойдёт столкновение и игрок выживет.
* Второй массив, где отмечают новые позиции роботы, создаёт иллюзию одновременности их перемещений.
*
* Другое дело, что абсолютно не нужен первый массив, т.к. роботы и кучи перебираются по спискам.
* Посему массив mCurrent будет постепенно выпилен.
* */
public class Board {
    private int mWidth;
    private int mHeight;
    private GameObject[][] mCurrent;
    private GameObject[][] mFuture;

    public Board(int height, int width){
        mHeight = height;
        mWidth = width;
        mCurrent = new GameObject[mHeight][mWidth];
        mFuture = new GameObject[mHeight][mWidth];
        clearBoard(mCurrent);
        clearBoard(mFuture);
    }
    /* Функция проверяет, существует ли в массиве клетка с такими координатами.*/
    public boolean exists(int x, int y){
        return (x>0)&&(x<mHeight)&&(y>0)&&(y<mWidth);
    }
    /* public void refresh();
    * Эта функция заменяет старый массив mCurrent свежим массивом, сформированным в mFuture.
    * 0 бессмысленности этого действия написано в комментарии к классу.
    * Кроме того массив mFuture забивается ссылками на Empty, что вполне осмысленно.
    *
    * Заметим, что забивать нулями можно двумя разными способами:
    * -- библиотечной или своей функцией, которая перебирает все клетки массива
    * -- перебрав список всех имеющихся на доске объектов и занулив клетки, где они стояли.
    * При втором способе будет повреждён массив mCurrent, т.к. 2 ссылки укажут на одну область памяти,
    * а копировать массивы грех.
    * Но как было сказано в описании класса, mCurrent к моменту вызова этой функции уже никому не нужен.
    *
    * Внимание:
    * Вызывать эту функцию имеет право только GamePlay и только после того, как игрок сделал свой ход.
    */
    //TODO подумать про то, что массовое зануление(1 вариант) занулит клетку mFuture, куда встал игрок.
    public void refresh(){
        //вытесняет mFuture в mCurrent, очищает mFuture
    }
    public int getWidth(){
        return mWidth;
    }
    public int getHeight(){
        return mHeight;
    }
    /* Если функциям normaliseX и normaliseY подаются координаты, клетки с которыми не существует,
    * они возвращают близжайшие? к переданным координаты, такие, что клетка с ними уже существует.
    * Эти функции вызываются объектом View методом setNewView (Я знаю, что название недостоверно отражает
    * назначение функции, т.к. похоже на статический метод-фабрику. Исправлю.).
    * */
    public int normaliseX(int x){
        // дублирование кода с normaliseY
        if(x<0){
            x=0;
        }else if(x>=mWidth){
            x=mWidth-1;
        }
        return x;
    }
    public int normaliseY(int y){
        // дублирование кода с normaliseX
        if(y<0){
            y=0;
        }else if(y>=mHeight){
            y=mHeight-1;
        }
        return y;
    }

    /* Пакетные методы, доступные только объектам, реализующим интерфейс GameObject,
    * в том числе наследующим Playable( а вы уже посмотрели projectModel.svg ?)
    * */
    // Метод get будет уничтожен вместе с mCurrent(мир сложен, в коде не опечатка).
    GameObject get(int x, int y){
        return mFuture[x][y];
    }
    GameObject getCurrent(int x, int y){
        return mCurrent[x][y];
    }

    /* ------------------------------------------
     * Далее описаны методы, модифицирующие значения в клетках массива.
     * Во избежание путаницы с координатами в массиве и приватных полях объектов,
     * эти методы объекты должны применять только на себя.
     *
     * У читателя может возникнуть вопрос: зачем нужны аргументы x и y, если можно извлечь их
     * с помощью fill.getX() и fill.getY()? Ответ: т.к. эти значения уже извлечены
     * внутри вызывающей данную функции и хранятся там в виде локальных переменных,
     * у программиста возникает искушение их использовать и повысить производительность на 0,00001%.
     */
    void set(int x, int y, GameObject fill){
        mFuture[x][y] = fill;
    }
    // Метод setCurrent будет уничтожен вместе с mCurrent
    void setCurrent(int x, int y, GameObject fill){
        mCurrent[x][y] = fill;
    }

    /* Метод add устраивает в клетке столкновение приезжего с её хозяином(тем, кто приехал раньше).
    Входные данные: x, y -- координаты клетки
    addict -- приезжий наркоман (я не смог образовать существительное от глагола add,
    варианты added и being_added мне не понравились, visitor и newcomer плохо сочетаются с именем функции)
    Назначение: см. комменарий к collideWith в GameObject */
    //TODO написать адекватный комментарий(>=10 строк) к collideWith в GameObject, т.к. это одна из самых важных функций
    void add(int x, int y, GameObject addict){
        mFuture[x][y] = mFuture[x][y].collideWith(addict);
    }
    // Метод addCurrent будет объединён с методом add
    void addCurrent(int x, int y, GameObject addict){
        mCurrent[x][y] = mCurrent[x][y].collideWith(addict);
    }

    private void clearBoard(GameObject[][] arr){
        //самоочевидна, используется в конструкторе
    }

}
