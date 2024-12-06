package org.martinez.backend;

import org.martinez.utils.Spot;

import java.util.Random;

public class Board {
    private final int rows = Spot.ROWS, columns = Spot.COLUMNS;
    private int[][] scoreboard;
    private int[][] gameboard; // todo consider changing to type char || should store mine, gold
    private int[][] state; // 0 = undiscovered | 1 = mine | 2 = gold |||||| todo state should store undiscovered or discovered
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
    public int getTileType(int row, int column){
        return this.gameboard[row][column];
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
                this.scoreboard[i][j] = Spot.UNDISCOVERED;
            }
        }
    }
    private void initializeGameBoard(){
        int totalmines = Spot.NUMMINES;
        int placedmines = 0;
        int r;
        int c;
        while(placedmines < totalmines) { // seed mines
            r = generator.nextInt(rows);
            c = generator.nextInt(columns);
            if(this.state[r][c] == Spot.UNDISCOVERED){
                this.gameboard[r][c] = Spot.MINE;
                placedmines++;
            }
        }
        for (int row = 0; row < rows; row++) { // seed gold
            for (int column = 0; column < columns; column++) {
                if(gameboard[row][column] != Spot.MINE)
                    gameboard[row][column] = Spot.GOLD;
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
    public void printGameBoard(){ // todo, looks like it might fail to identify a mine properly
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if(this.gameboard[row][column] == Spot.GOLD){
                    System.out.print("G");
                }else{
                    System.out.println("M");
                }
            }
            System.out.print("\n");
        }
    }
}
