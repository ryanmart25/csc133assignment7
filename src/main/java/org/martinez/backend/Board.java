package org.martinez.backend;

import org.martinez.utils.Spot;

import java.util.Random;

public class Board {
    private final int rows = Spot.ROWS, columns = Spot.COLUMNS;
    private int[][] scoreboard;
    private int[][] gameboard; // todo consider changing to type char
    private Random generator;
    public Board(Random generator){
        this.scoreboard = new int[rows][columns];
        this.gameboard = new int[rows][columns];
        this.generator = generator;
    }
    private void initializeGameBoard(){
        int totalmines = 14;
        int placedmines = 0;
        int r;
        int c;
        while(placedmines < totalmines) {
            r = generator.nextInt(rows);
            c = generator.nextInt(columns);
            if(this.gameboard[r][c] == 0){
                this.gameboard[r][c] = 1;
                placedmines++;
            }
        }
    }
    private void initializeScoreBoard(){ // todo
        int minevalue = 10;
        int gold_value = 5;
        // if a cell is a mine, it's score is set to zero.
    }
    public void printScoreboard(){
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                System.out.printf("%2d ", scoreboard[row][column]);
            }
        }
    }
    public void printGameBoard(){
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if(this.gameboard[row][column] == 0){
                    System.out.print("G");
                }else{
                    System.out.println("M");
                }
            }
            System.out.print("\n");
        }
    }
}
