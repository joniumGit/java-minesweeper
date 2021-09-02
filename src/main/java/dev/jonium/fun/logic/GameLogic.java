package dev.jonium.fun.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

final class GameLogic {

    public final byte[][] field;
    public final byte[][] render;
    Integer flags;
    public final int width;
    public final int height;
    private final int mines;
    private byte done;
    private int cnt;

    GameLogic(int x, int y, int mines) {
        field = new byte[x][y];
        render = new byte[x][y];
        flags = 0;
        width = x;
        height = y;
        cnt = x * y;
        done = (byte) 0;
        this.mines = mines;

        genMines(mines);
    }

    void genMines(int mines) {
        Random gen = new Random();
        boolean next = false;
        HashMap<Integer, ArrayList<Integer>> mLoc = new HashMap<>();

        for (int i = 0; i < mines; i++) {
            while (!next) {
                int x = gen.nextInt(width);
                int y = gen.nextInt(height);

                if (field[x][y] != (byte) 9) {
                    field[x][y] = (byte) 9;
                    mLoc.putIfAbsent(x, new ArrayList<>());
                    ArrayList<Integer> temp = mLoc.get(x);
                    temp.add(y);
                    mLoc.put(x, temp);
                    next = true;
                }
            }
            next = false;
        }

        for (int i : mLoc.keySet()) {
            ArrayList<Integer> x = mLoc.get(i);
            for (int j : x) {
                for (int k = -1; k < 2; k++) {
                    try {
                        if (field[i][j + k] != (byte) 9) {
                            field[i][j + k]++;
                        }
                    } catch (Exception e) {
                        assert true;
                    }
                    try {
                        if (field[i + 1][j + k] != (byte) 9) {
                            field[i + 1][j + k]++;
                        }
                    } catch (Exception e) {
                        assert true;
                    }
                    try {
                        if (field[i - 1][j + k] != (byte) 9) {
                            field[i - 1][j + k]++;
                        }
                    } catch (Exception e) {
                        assert true;
                    }
                }
            }
        }

    }

    void reveal(int x, int y) {
        try {
            if (render[x][y] == (byte) -1) {
                return;
            } else if (field[x][y] != (byte) 0 && field[x][y] != (byte) 9) {
                render[x][y] = (byte) -1;
                cnt--;
                return;
            }
        } catch (Exception e) {
            return;
        }

        render[x][y] = -1;
        cnt--;

        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                try {
                    if (field[i][j] != (byte) 0) {
                        render[i][j] = (byte) -1;
                    } else {
                        reveal(i, j);
                    }
                } catch (Exception e) {
                    assert true;
                }
            }
        }
    }

    void click(int x, int y, boolean right) {
        if (done != (byte) 0) {
            return;
        }

        if (render[x][y] == (byte) -1 && !right) {
            return;

        }

        if (render[x][y] == (byte) 1 && !right) {
            return;

        }

        if (field[x][y] == (byte) 9 && !right) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (field[i][j] == (byte) 9) {
                        render[i][j] = (byte) -1;
                    }
                }
            }
            done = (byte) -1;
            return;

        }

        if (right) {
            if (render[x][y] == (byte) 0) {
                render[x][y] = (byte) 1;
                flags++;
            } else if (render[x][y] == (byte) 1) {
                render[x][y] = (byte) 0;
                flags--;
            } else if (render[x][y] == (byte) -1) {
                return;
            }

            return;
        }

        reveal(x, y);

        flags = 0;
        cnt = width * height;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (render[i][j] == 1) {
                    flags++;
                } else if (render[i][j] == -1) {
                    cnt--;
                }
            }
        }

        if (cnt == mines) {
            for (byte[] i : render) {
                Arrays.fill(i, (byte) -1);
            }
            done = (byte) 1;
        }
    }

    byte getDone() {
        return done;
    }

}
