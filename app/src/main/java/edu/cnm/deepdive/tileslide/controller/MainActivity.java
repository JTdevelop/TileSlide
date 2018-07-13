package edu.cnm.deepdive.tileslide.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import edu.cnm.deepdive.tileslide.R;
import edu.cnm.deepdive.tileslide.model.Frame;
import edu.cnm.deepdive.tileslide.view.FrameAdapter;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

  private static int PUZZLE_SIZE = 3;

  private Frame frame;
  private FrameAdapter adapter;
  private GridView tileGrid;
  private Button reset;
  private Button shuffle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tileGrid = findViewById(R.id.tile_grid);
    tileGrid.setNumColumns(PUZZLE_SIZE);
    tileGrid.setOnItemClickListener(this);
    createPuzzle();
    reset = findViewById(R.id.reset);
    reset.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        createPuzzle();
      }
    });
    shuffle = findViewById(R.id.shuffle);
    shuffle.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        createPuzzle();
      }
    });
  }

  // This is where the puzzle pieces are moved.
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    frame.move(position / PUZZLE_SIZE, position % PUZZLE_SIZE);
    adapter.notifyDataSetChanged();
  }

  // This is where the puzzle object is created
  private void createPuzzle() {
    frame = new Frame(PUZZLE_SIZE, new Random());
    adapter = new FrameAdapter(this, frame);
    tileGrid.setAdapter(adapter);
  }

}














