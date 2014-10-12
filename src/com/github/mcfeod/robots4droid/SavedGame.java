package com.github.mcfeod.robots4droid;

public class SavedGame {
    private static int sNumber = 0;

    private Player mPlayer;
    private int mLevel;
    private Board mBoard;
    private int mNumber;

    private static StringBuilder sBuilder = new StringBuilder();

    public SavedGame(Player player, int level, Board board){
        mPlayer = player;
        mLevel = level;
        mBoard = board;
        mNumber = ++sNumber;
    }
    public SavedGame(){
        //это заглушка
        mPlayer = new Player();
        mLevel = 1;
        mBoard = new Board(10,10);
        mNumber = ++sNumber;
    }

    public int getNumber() {
        return mNumber;
    }

    @Override
    public String toString(){
        sBuilder.setLength(0);
        sBuilder.append(mNumber);
        sBuilder.append("# Level: ");
        sBuilder.append(mLevel);
        sBuilder.append(" Score: ");
        sBuilder.append(mPlayer.getScore());
        return sBuilder.toString();
    }
}
