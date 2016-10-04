package ece.course.lab_3;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private GameView mGameView;
    private Button btnStart;
    private final int PIECE_NONE = 0;
    private final int PIECE_BLUE = 1;
    private final int PIECE_RED = 2;
    private final int STATE_NOT_START = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_BLUE_WIN = 2;
    private final int STATE_RED_WIN =3;
    private final int STATE_DRAW_GAME = 4;
    private String TAG_GAME_STATE = "tagGameState";
    private String TAG_IS_BLUE_TURN = "tagIsBlueTurn";
    private String TAG_LINE_LEFT ="tagLineLeft";
    private String TAG_LINE_MIDDLE ="tagLineMiddle";
    private String TAG_LINE_RIGHT ="tagLineRight";
    private String TAG_WIN_LINE ="tagWinLine";
    private int[][] boardState = new int[3][3];
    private boolean[] hvWinLine = new boolean[8];
    private boolean isBlueTurn = true;
    private int gameState = STATE_NOT_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.wait_start);
        mGameView = (GameView) findViewById(R.id.mGameView);
        mGameView.setHandler(new Handler() {
            public void handleMessage(Message msg) {
                if (gameState != STATE_PLAYING)
                    return;
                int posX = msg.getData().getInt(GameView.TAG_ON_TOUCH_X);
                int posY = msg.getData().getInt(GameView.TAG_ON_TOUCH_Y);
                inputPiece(posX, posY);
                mGameView.invalidate();
//                float posX = msg.getData().getFloat(GameView.TAG_ON_TOUCH_X);
//                float posY = msg.getData().getFloat(GameView.TAG_ON_TOUCH_Y);
//                String tmp = "X: " + posX + ", Y: " + posY;
//                setTitle(tmp);
//                Log.i("Msg", tmp);
//                mGameView.invalidate();
            }
        });
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                gameState = STATE_PLAYING;
                if (isBlueTurn)
                    setTitle(R.string.turn_blue);
                else
                    setTitle(R.string.turn_red);
                btnStart.setVisibility(View.INVISIBLE);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        boardState[i][j] = PIECE_NONE;
                    }
                }
                for (int i = 0; i < 8; i++)
                    hvWinLine[i] = false;
                mGameView.cleanAll();
                mGameView.invalidate();
//                btnStart.setVisibility(View.INVISIBLE);
//                mGameView.invalidate();
            }
        });
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(TAG_GAME_STATE, gameState);
        outState.putBoolean(TAG_IS_BLUE_TURN, isBlueTurn);
        outState.putIntArray(TAG_LINE_LEFT, boardState[0]);
        outState.putIntArray(TAG_LINE_MIDDLE, boardState[1]);
        outState.putIntArray(TAG_LINE_RIGHT, boardState[2]);
        outState.putBooleanArray(TAG_WIN_LINE, hvWinLine);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        gameState = savedInstanceState.getInt(TAG_GAME_STATE, STATE_NOT_START);
        isBlueTurn = savedInstanceState.getBoolean(TAG_IS_BLUE_TURN, true);
        boardState[0] = savedInstanceState.getIntArray(TAG_LINE_LEFT);
        boardState[1] = savedInstanceState.getIntArray(TAG_LINE_MIDDLE);
        boardState[2] = savedInstanceState.getIntArray(TAG_LINE_RIGHT);
        hvWinLine = savedInstanceState.getBooleanArray(TAG_WIN_LINE);
        if (gameState == STATE_PLAYING) {
            btnStart.setVisibility(View.INVISIBLE);
            if (isBlueTurn)
                setTitle(R.string.turn_blue);
            else
                setTitle(R.string.turn_red);
        }
        else {
            btnStart.setVisibility(View.VISIBLE);
            switch (gameState) {
                case STATE_NOT_START : setTitle(R.string.wait_start); break;
                case STATE_BLUE_WIN : setTitle(R.string.win_blue); break;
                case STATE_RED_WIN : setTitle(R.string.win_red); break;
                case STATE_DRAW_GAME : setTitle(R.string.draw_game); break;
            }
        }
        mGameView.cleanAll();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardState[i][j] == PIECE_BLUE)
                    mGameView.setBlueCross(i, j);
                else if (boardState[i][j] == PIECE_RED)
                    mGameView.setRedCircle(i, j);
            }
        }
        for (int i = 0; i < 8; i++)
            if (hvWinLine[i])
                mGameView.setWinLine(i);
        mGameView.invalidate();
    }

    private void inputPiece(int posX, int posY) {
        if (boardState[posX][posY] != PIECE_NONE)
            return;
        if (isBlueTurn) {
            boardState[posX][posY] = PIECE_BLUE;
            mGameView.setBlueCross(posX, posY);
            if(boardState[0][2]==PIECE_BLUE&&boardState[1][2]==PIECE_BLUE&&boardState[2][2]==PIECE_BLUE)
            {mGameView.setWinLine(0);gameState = STATE_BLUE_WIN;}
            isBlueTurn = false;
            setTitle(R.string.turn_red);
        }
        else {
            boardState[posX][posY] = PIECE_RED;
            mGameView.setRedCircle(posX, posY);
            isBlueTurn = true;
            setTitle(R.string.turn_blue);
        }
    }
}
