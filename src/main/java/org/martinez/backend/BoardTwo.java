package org.martinez.backend;

import org.martinez.utils.Spot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
        ArrayList<CellData> list = new ArrayList<>(ROWS * COLUMNs);
        int placedMines = 0;
        // initialize list
        for (int element = 0; element < ROWS * COLUMNs; element++) {
            if(placedMines < nummines){ // place mines
                list.add(element, new CellData(Spot.CELL_TYPE.MINE, Spot.CELL_STATUS.NOT_EXPOSED));
                placedMines ++;
            }else{
                list.add(element, new CellData(Spot.CELL_TYPE.GOLD, Spot.CELL_STATUS.NOT_EXPOSED));
            }
        }

        // shuffle mines
        Collections.shuffle(list);
        // delinearize into array
        for (int index = 0; index < ROWS * COLUMNs; index++) {
            board[index / COLUMNs][index % COLUMNs] = list.get(index);
        }
    }

    public void printBoard(){
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNs; column++) {
                if (board[row][column].type == Spot.CELL_TYPE.GOLD)
                    System.out.print("G ");
                else
                    System.out.print("M ");
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
    public Spot.CELL_STATUS getCellStatus(int row, int column){
        return board[row][column].status;
    }
    public Spot.CELL_TYPE changeCellStatus(int row, int column){
        if(board[row][column].status == Spot.CELL_STATUS.NOT_EXPOSED)
            board[row][column].status = Spot.CELL_STATUS.EXPOSED;
        if(board[row][column].status == Spot.CELL_STATUS.EXPOSED)
            board[row][column].status = Spot.CELL_STATUS.NOT_EXPOSED;
        return board[row][column].type;
    }
    public Spot.CELL_TYPE getCellType(int row, int column){
        return board[row][column].type;
    }
    public int getCurrentScore(){
        return this.currentScore;
    }
    private static class CellData{
        private Spot.CELL_TYPE type;
        private Spot.CELL_STATUS status;
        private int cellScore;
        protected CellData(Spot.CELL_TYPE type, Spot.CELL_STATUS status){
            this.type = type;
            this.status = status;
            this.cellScore = 0;
        }
    }

}
