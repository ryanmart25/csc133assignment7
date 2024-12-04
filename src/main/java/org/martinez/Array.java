package org.martinez;

public class Array {
    float[] array;
    int size;
    int cursor;
    public Array(int size){
        this.size = size;
        this.array = new float[this.size];
        this.cursor = 0;
    }
    public void append(float n){
        this.array[cursor] = n;
        if(cursor + 1 < size){
            cursor++;
        }
    }
    public void append(float[] a, int offset, int length){
        if(offset + length > a.length){
            System.out.println("Array: offset + length > array.length");
            return;
        }else if(offset > a.length || offset < 0){
            System.out.println("offset > array.length || offset < 0 ");
            return;
        }else{
            for (int i = offset; i < length; i++) {
                this.array[cursor] = a[i];
                cursor++;
            }
        }
    }
    public float[] getArray(){
        if(cursor == 0){
            System.out.println("Array: Empty array");
        }
        return this.array;
    }
    public String toString(){
        if(cursor > 0){
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if(i % 5 == 0){
                    builder.append("\n");
                }
                if( i % 15 == 0){
                    builder.append("\n========\n");
                }
                builder.append(this.array[i] + " ");

            }
            return builder.toString();
        }
        return "[]";
    }
}
