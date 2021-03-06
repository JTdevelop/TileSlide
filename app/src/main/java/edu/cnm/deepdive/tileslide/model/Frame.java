package edu.cnm.deepdive.tileslide.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public class Frame {

  private int size;
  private Random rng;
  private Tile[][] start;
  private Tile[][] tiles;
  private int moves;
  private boolean win = false;
  private int[] tilesOrder;
  private int[] startOrder;
  private List<Integer[]> path = new ArrayList<>();
  private int distance;
  private int lastMove;

  private static final Map<String, String> DIRECTIONS = new HashMap() {{
    put("LEFT", "left");
    put("RIGHT", "right");
    put("UP", "up");
    put("DOWN", "down");
  }};

  public boolean getWin() {
    return win;
  }

  public Frame(int size, Random rng) {
    this.size = size;
    this.rng = rng;
    start = new Tile[size][size];
    tiles = new Tile[size][size];
    for (int i = 0; i < size * size - 1; i++) {
      tiles[i / size][i % size] = new Tile(i);
    }
    tiles[size - 1][size - 1] = null;
    start[size - 1][size - 1] = null;
    tilesOrder = new int[size * size];
    startOrder = new int[size * size];
    scramble();
  }

  public void reset() {
    copy(start, tiles);
    moves = 0;
  }

  public boolean isWin() {
    int previous = -1;
    for (Tile[] tile : tiles) {
      for (Tile tile1 : tile) {
        if (tile1 == null) {
          previous++;
          continue;
        }
        if (previous >= tile1.getNumber()) {
          return false;
        }
        previous++;
      }
    }
    return true;
  }

  public void scramble() {
    shuffle();
    if (!isParityEven()) {
      swapRandomPair();
    }
    copy(tiles, start);
    moves = 0;
  }

  public Tile[][] getTiles() {
    return tiles;
  }

  public int getMoves() {
    return moves;
  }

  private int[] getBlankSpacePosition() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (tiles[i][j] == null) {
          return new int[]{i, j};
        }
      }
    }
    return new int[]{0, 0};
  }

  ;

  private boolean isMove(int row, int col) {
    return isMove(row, col, row - 1, col)
        || isMove(row, col, row, col + 1)
        || isMove(row, col, row + 1, col)
        || isMove(row, col, row, col - 1);
  }

  private boolean isMove(int fromRow, int fromCol, int toRow, int toCol) {
    return !win &&
        tiles[fromRow][fromCol] != null
        && toRow >= 0
        && toRow < size
        && toCol >= 0
        && toCol < size
        && tiles[toRow][toCol] == null;
  }

  public boolean move(int row, int col) {
    return move(row, col, row - 1, col)
        || move(row, col, row, col + 1)
        || move(row, col, row + 1, col)
        || move(row, col, row, col - 1);
  }

  private boolean move(int fromRow, int fromCol, int toRow, int toCol) {
    if (!win &&
        tiles[fromRow][fromCol] != null
        && toRow >= 0
        && toRow < size
        && toCol >= 0
        && toCol < size
        && tiles[toRow][toCol] == null
        ) {
      swap(tiles, fromRow, fromCol, toRow, toCol);
      win = isWin();
      return true;
    }
    return false;
  }

  private void copy(Tile[][] source, Tile[][] dest) {
    for (int row = 0; row < size; row++) {
      System.arraycopy(source[row], 0, dest[row], 0, size);
    }
  }

  private int distanceHome(int row, int col) {
    Tile tile = tiles[row][col];
    int homeRow;
    int homeCol;
    if (tile != null) {
      homeRow = tile.getNumber() / size;
      homeCol = tile.getNumber() % size;
    } else {
      homeRow = size - 1;
      homeCol = size - 1;
    }
    return Math.abs(row - homeRow) + Math.abs(col - homeCol);
  }

  private void shuffle() {
    for (int toPosition = size * size - 1; toPosition >= 0; toPosition--) {
      int toRow = toPosition / size;
      int toCol = toPosition % size;
      int fromPosition = rng.nextInt(toPosition + 1);
      if (fromPosition != toPosition) {
        int fromRow = fromPosition / size;
        int fromCol = fromPosition % size;
        swap(tiles, fromRow, fromCol, toRow, toCol);
      }
    }
  }

  private boolean isParityEven() {
    int sum = 0;
    Tile[][] work = new Tile[size][size];
    copy(tiles, work);
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if (tiles[row][col] == null) {
          sum += distanceHome(row, col);
          break;
        }
      }
    }
    for (int fromRow = 0; fromRow < size; fromRow++) {
      for (int fromCol = 0; fromCol < size; fromCol++) {
        int fromPosition = fromRow * size + fromCol;
        int toPosition = (work[fromRow][fromCol] != null)
            ? work[fromRow][fromCol].getNumber() : size * size - 1;
        while (toPosition != fromPosition) {
          int toRow = toPosition / size;
          int toCol = toPosition % size;
          swap(work, fromRow, fromCol, toRow, toCol);
          sum++;
          toPosition = (work[fromRow][fromCol] != null)
              ? work[fromRow][fromCol].getNumber() : size * size - 1;
        }
      }
    }
    return (sum & 1) == 0;
  }

  private void swapRandomPair() {
    int fromPosition = rng.nextInt(size * size);
    while (tiles[fromPosition / size][fromPosition % size] == null) {
      fromPosition = rng.nextInt(size * size);
    }
    int fromRow = fromPosition / size;
    int fromCol = fromPosition % size;
    int toPosition = rng.nextInt(size * size);
    while (toPosition == fromPosition
        || tiles[toPosition / size][toPosition % size] == null) {
      toPosition = rng.nextInt(size * size);
    }
    int toRow = toPosition / size;
    int toCol = toPosition % size;
    swap(tiles, fromRow, fromCol, toRow, toCol);
  }

  private void swap(Tile[][] tiles, int fromRow, int fromCol, int toRow, int toCol) {
    Tile temp = tiles[toRow][toCol];
    tiles[toRow][toCol] = tiles[fromRow][fromCol];
    tiles[fromRow][fromCol] = temp;
  }

  public int[] getTilesOrder() {
    return getOrder(tiles, tilesOrder);
  }

  public void setTilesOrder(int[] order) {
    setOrder(tiles, order);
  }

  public int[] getStartOrder() {
    return getOrder(start, startOrder);
  }

  public void setStartOrder(int[] order) {
    setOrder(start, order);
  }

  private int[] getOrder(Tile[][] tiles, int[] order) {
    int count = 0;
    for (Tile[] tile : tiles) {
      for (Tile tile1 : tile) {
        if (tile1 == null) {
          order[count] = size * size - 1;
        } else {
          order[count] = tile1.getNumber();
        }
        count++;
      }
    }
    return order;
  }

  public void setOrder(Tile[][] tiles, int[] order) {
    for (int i = 0; i < order.length; i++) {
      if (order[i] == size * size - 1) {
        tiles[i / size][i % size] = null;
      } else {
        tiles[i / size][i % size] = new Tile(order[i]);
      }
    }
  }

  public void setMoves(int moves) {
    this.moves = moves;
  }

  private List<Frame> visit() {
    List<Frame> children = new ArrayList<>();
    List<Integer[]> allowedMoves = getAllowedMoves();
    for (int i = 0; i < allowedMoves.size(); i++) {
      Integer[] move = allowedMoves.get(i);
      if (tiles[move[0]][move[1]].getNumber() != lastMove) {
        Frame newInstance = new Frame(size, new Random());
        newInstance.setTilesOrder(this.getTilesOrder());
        newInstance.setStartOrder(this.getStartOrder());
        newInstance.setMoves(this.getMoves());
        newInstance.move(move[0], move[1]);
        newInstance.addToPath(new Integer[]{move[0], move[1]});
        children.add(newInstance);
      }
    }
    return children;
  }

  ;

  private List<Integer[]> getAllowedMoves() {
    List<Integer[]> allowedMoves = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        String move = getMove(i, j);
        if (!move.equals("")) {
          allowedMoves.add(new Integer[]{i, j});
        }
      }
    }
    return allowedMoves;
  }

  ;

  private String getMove(int row, int col) {
    int[] blankSpacePosition = getBlankSpacePosition();
    int blankRow = blankSpacePosition[0];
    int blankCol = blankSpacePosition[1];
    if (blankRow > 0 && row == blankRow - 1 && col == blankCol) {
      return DIRECTIONS.get("DOWN");
    } else if (blankRow < size - 1 && row == blankRow + 1 && col == blankCol) {
      return DIRECTIONS.get("UP");
    } else if (blankCol > 0 && row == blankRow - 1 && col == blankCol - 1) {
      return DIRECTIONS.get("RIGHT");
    } else if (blankRow < size - 1 && row == blankRow - 1 && col == blankCol + 1) {
      return DIRECTIONS.get("LEFT");
    }
    return "";
  }

  private String move(int row, int col, boolean use) {
    String move = getMove(row, col);
    if (!move.equals("")) {
      int[] blankSpacePosition = getBlankSpacePosition();
      int blankRow = blankSpacePosition[0];
      int blankCol = blankSpacePosition[1];
      switch (move) {
        case "left":
          this.swap(tiles, blankRow, blankCol, blankRow, blankCol + 1);
          break;
        case "right":
          this.swap(tiles, blankRow, blankCol, blankRow, blankCol - 1);
          break;
        case "up":
          this.swap(tiles, blankRow, blankCol, blankRow + 1, blankCol);
          break;
        case "down":
          this.swap(tiles, blankRow, blankCol, blankRow - 1, blankCol - 1);
          break;
      }
      if (!move.equals("")) {
        lastMove = tiles[row][col].getNumber();
      }
    }
    return move;
  }

  private boolean move(int fromRow, int fromCol, int toRow, int toCol, boolean use) {
    if (!win &&
        tiles[fromRow][fromCol] != null
        && toRow >= 0
        && toRow < size
        && toCol >= 0
        && toCol < size
        && tiles[toRow][toCol] == null
        ) {
      swap(tiles, fromRow, fromCol, toRow, toCol);
      win = isWin();
      return true;
    }
    return false;
  }

  public int getDistance() {
    return distance;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public List<Integer[]> getPath() {
    return path;
  }

  public void setPath(List<Integer[]> path) {
    this.path = path;
  }

  public void addToPath(Integer[] direction) {
    this.path.add(direction);
  }

}
