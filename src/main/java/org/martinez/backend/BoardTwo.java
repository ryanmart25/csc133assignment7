package org.martinez.backend;

import org.martinez.utils.SpotTwo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.martinez.utils.SpotTwo.COLUMNS;

public class BoardTwo {

    private int currentScore;
    private  final int ROWS , COLUMNs , NUMMINES ;
    private boolean gameActive = false;
    private CellData[][] board;
    public BoardTwo(int rows, int columns, int nummines){
        this.ROWS = rows;
        this.COLUMNs = columns;
        this.NUMMINES = nummines;
        this.gameActive = true;
        board = new CellData[ROWS][COLUMNs];
        CellData[][] boardB = new CellData[ROWS][COLUMNs];
        ArrayList<CellData> list = new ArrayList<>(ROWS * COLUMNs);
        int placedMines = 0;
        // initialize list
        for (int element = 0; element < ROWS * COLUMNs; element++) {
            if(placedMines < nummines){ // place mines
                list.add(element, new CellData(SpotTwo.CELL_TYPE.MINE, SpotTwo.CELL_STATUS.NOT_EXPOSED));
                placedMines ++;
            }else{
                list.add(element, new CellData(SpotTwo.CELL_TYPE.GOLD, SpotTwo.CELL_STATUS.NOT_EXPOSED));
            }
        }

        // shuffle mines
        Collections.shuffle(list);
        // delinearize into array

        for (int index = 0; index < ROWS * COLUMNs; index++) {
            board[index / COLUMNs][index % COLUMNs] = list.get(index);
        }

        // initialize scores
        // create second array for seeding scores and reading from. Scores will be generated in boardB and then wrote to the real board
        int next_row;
        int next_column;
        int previous_row, previous_column;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                SpotTwo.CELL_TYPE targetCellType = getCellType(row, column);
                if(targetCellType == SpotTwo.CELL_TYPE.MINE) { // if the current cell is a mine, set its on click score to zero
                    board[row][column].cellScore = 0;
                    continue;
                }
                next_row = (row +1) % ROWS; // get the components of the positions of adjacent cells.
                next_column = (column + 1) % COLUMNS;
                previous_row = (row - 1 + ROWS) % ROWS;
                previous_column = (column - 1 + COLUMNS) % COLUMNS;
                // calculate the onclick score of this cell, using Next Nearest Neighbors
                board[row][column].cellScore += incrementCellScore(previous_row, next_column);
                board[row][column].cellScore += incrementCellScore(previous_row, column);
                board[row][column].cellScore += incrementCellScore(previous_row, previous_column);
                board[row][column].cellScore += incrementCellScore(row, previous_column);
                board[row][column].cellScore += incrementCellScore(next_row, previous_column);
                board[row][column].cellScore += incrementCellScore(next_row, column);
                board[row][column].cellScore += incrementCellScore(next_row, next_column);
                board[row][column].cellScore += incrementCellScore(row, next_column);

            }
        }
    }
    private int incrementCellScore(int row, int column){
        int accumulatedScore = 0;
        if(board[row][column].type == SpotTwo.CELL_TYPE.MINE){
            accumulatedScore +=10;
        }
        if(board[row][column].type == SpotTwo.CELL_TYPE.GOLD){
            accumulatedScore+=5;
        }
        return accumulatedScore;
    }
    public void printBoard(){
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNs; column++) {
                if (board[row][column].type == SpotTwo.CELL_TYPE.GOLD)
                    System.out.print("G ");
                else
                    System.out.print("M ");
            }
            System.out.print("\n");
        }
    }
    public void printStatus(){
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNs; column++) {
                if (board[row][column].status == SpotTwo.CELL_STATUS.NOT_EXPOSED)
                    System.out.print("N ");
                else
                    System.out.print("E ");
            }
            System.out.print("\n");
        }
    }
    public boolean isGameActive(){
        return gameActive;
    }
    public void printCellScores(){
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNs; column++) {
                System.out.print(board[row][column].cellScore + " ");
            }
            System.out.print("\n");
        }
    }
    public SpotTwo.CELL_STATUS getCellStatus(int row, int column){
        return board[row][column].status;
    }
    public SpotTwo.CELL_TYPE changeCellStatus(int row, int column){
        if(board[row][column].status == SpotTwo.CELL_STATUS.NOT_EXPOSED){
            System.out.println("changing cell status 2");
            board[row][column].status = SpotTwo.CELL_STATUS.EXPOSED;
        }
        currentScore += board[row][column].cellScore;


        return board[row][column].type;
    }
    public SpotTwo.CELL_TYPE getCellType(int row, int column){
        return board[row][column].type;
    }
    public int getCurrentScore(){
        return this.currentScore;
    }
    private static class CellData{
        private SpotTwo.CELL_TYPE type;
        private SpotTwo.CELL_STATUS status;
        private int cellScore;
        protected CellData(SpotTwo.CELL_TYPE type, SpotTwo.CELL_STATUS status){
            this.type = type;
            this.status = status;
            this.cellScore = 0;
        }
    }

}
