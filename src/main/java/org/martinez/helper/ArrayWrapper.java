package org.martinez.helper;

import java.util.ArrayList;

public class ArrayWrapper { // simple wrapper class created by me. purpose: append method
    float[] array;
    int cursor;
    int size;
    public ArrayWrapper(int size){
        this.size = size;
        array = new float[size];
        cursor = 0;
    }
    public void append(float n){

        array[cursor] = n;
        if(cursor + 1 < size){
            cursor++;
        }
    }
    public float[] getArray(){
        return this.array;
    }
}
