package com.github.mcfeod.robots4droid;

import com.github.mcfeod.board_objects.Playable;

import java.util.Iterator;

public class playableList implements Iterable<Playable> {
    private Playable mHead = null;
    private int mSize = 0;

    public playableList(){}

    public int getSize(){
        return mSize;
    }
    public boolean isEmpty(){
        return mSize==0;
    }
    public void add(Playable obj){
        if(mSize!=0){
            obj.next = mHead.next;
            obj.previous = mHead;
            mHead.next = obj;
            mSize++;
        }else{
            mHead = obj;
            mHead.next = mHead;
            mHead.previous = mHead;
            mSize = 1;
        }
    }
    // опасный, но эффективный метод:
    // принадлежность obj к данному playableList на совести разработчика
    public void remove(Playable obj){
        if(mSize!=0){
            obj.previous.next = obj.next;
            obj.next.previous = obj.previous;
            mSize--;
        }else{
            mHead = null;
            mSize = 0;
        }
    }

    @Override
    public Iterator<Playable> iterator() {
        return new playableIterator(mHead);
    }
}
