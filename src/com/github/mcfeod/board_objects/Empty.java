package com.github.mcfeod.board_objects;

/*
 * Класс с единственным неизменяемым экземпляром, на ссылки который
 * располагаются в пустых ячейках поля. Альтернатива: null, но в этом случае
 * перед проверкой на класс объекта добавляется дополнительное условие: существует ли объект.
 * Пример: if(<Object name>.cell[i][j] && <Object name>.cell[i][j].class == Robot) или что-то такое.
 * */
public class Empty implements GameObject {
    private static final Empty INSTANCE = new Empty();
    private Empty(){};
    // static factory method
    public static Empty getIt(){
        return INSTANCE;
    }
    public GameObject collideWith(GameObject other){
        return other;
    }
    public void crash(){}
}
