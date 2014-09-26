package com.github.mcfeod.board_objects;

/*
 * Упрощает хранение данных в массиве и обработку игровых ситуаций.
 */
public interface GameObject {
   /*
    * При столкновении в клетку, где оно произошло, записывается возвращаемая этим методом ссылка.
    * */
    public GameObject collideWith(GameObject other);
   /*
    * Совершает положенные перед смертью в столкновении действия:
    *   Player вызывает метод, который где-то помечает, что игра проиграна.
    *   Robot вызывает метод удаления себя из связного списка.
    * */
    public void crash();
}