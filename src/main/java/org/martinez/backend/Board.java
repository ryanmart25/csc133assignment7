package org.martinez.backend;

import org.martinez.utils.Spot;

import java.util.Random;

public class Board {
    private final int rows = Spot.ROWS, columns = Spot.COLUMNS;
    private int[][] scoreboard;
    private int[][] gameboard; // todo consider changing to type char
    private int[][] state; // 0 = undiscovered | 1 = mine | 2 = gold
    private Random generator;
    private int score;
    public int getScore(){
        return this.score;
    }
    public int getState(int row, int column){
        return this.state[row][column];
    }
    public void setState(int row, int column, int state){
        this.state[row][column] = state;
    }
    public int[][] getGameboard(){
        return this.gameboard;
    }
    public Board(Random generator){
        this.scoreboard = new int[rows][columns];
        this.gameboard = new int[rows][columns];
        this.state = new int[rows][columns];
        this.generator = generator;
        initializeGameBoard();
        initializeScoreBoard();
        initializeStateBoard();

    }
    public void adjustScore(int row, int column){
        this.score += scoreboard[row][column];
    }
    private void initializeStateBoard(){ // initialize game state to undiscovered
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.scoreboard[i][j] = 0;
            }
        }
    }
    private void initializeGameBoard(){ // randomly seed board with 14 mines
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
    private void initializeScoreBoard(){ // seed scoreboard by reading game board and calculating score of each cell todo
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
