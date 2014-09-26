package com.github.mcfeod.robots4droid;

import com.github.mcfeod.board_objects.Playable;

import java.util.Iterator;

public class playableIterator implements Iterator<Playable> {
    private Playable head;
    private Playable curr;

    public playableIterator(Playable head){
        this.head = head;
        curr = head;
    }
    @Override
    public boolean hasNext(){
        return curr.next != head;
    }
    @Override
    public Playable next() {
        return curr.next;
    }
    @Override
    public void remove() {}
}
