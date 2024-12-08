package org.martinez.utils;

public class SpotTwo { // prof's spot file
    public static String WINDOW_TITLE = "CSC 133: Click & Kill Time!";
    public static final int FRAME_DELAY = 200;
    public static int OFFSET = 40, LENGTH = 100, PADDING = 40;
    public static int ROWS = 9, COLUMNS = 6;

    public static int win_width =
            2* OFFSET + (COLUMNS -1)*PADDING + COLUMNS * LENGTH;
    public static int win_height = win_width;
            //2* OFFSET + (ROWS -1)*PADDING + ROWS * LENGTH; // uncomment when you figure out aspect ratios
    public static final float FRUSTUM_LEFT = 0.0f,   FRUSTUM_RIGHT = (float) win_width,
                FRUSTUM_BOTTOM = 0.0f, FRUSTUM_TOP = (float) win_height,
                Z_NEAR = 0.0f, Z_FAR = 100.0f;
        public static final int MINE = 2, GOLD = 1, UNDISCOVERED = 0, DISCOVERED = 1;
            public static final int NUMMINES = 14;
    public enum CELL_STATUS {NOT_EXPOSED, EXPOSED };
    public enum CELL_TYPE {MINE, GOLD};

}
