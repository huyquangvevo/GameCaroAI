package com.example.dell.gamecaro;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    LinearLayout chessBoardLayout;
    Button buttonNewGame;
    static final int rowBoard = 30;
    static final int colBoard = 30;
    final int winTotal = 5;
    static Cell[][] arrayCell ;
    static int[][] cellBoard;
    Cell preCell;
    int amountDefense = -1;
    int amountAttack = -1;
    Cell previewCell;
    int preMAX = -1000000000;
    int preMIN = 1000000000;
    int depth  = 3;
    boolean playing;
    boolean first;

    int minRowRange = 0;
    int maxRowRange = rowBoard - 1;
    int minColRange = 0;
    int maxColRange = colBoard - 1;


    @SuppressLint("WrongConstant")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayCell = new Cell[rowBoard][colBoard];
        cellBoard = new int[rowBoard][colBoard];
        playing = false;
        first = true;



        for(int i=0;i<rowBoard;i++)
            for(int j=0;j<colBoard;j++){
                cellBoard[i][j] = 0;
            }

        chessBoardLayout = (LinearLayout) findViewById(R.id.chess_board_layout);
        buttonNewGame = (Button) findViewById(R.id.button_new_game);

        HorizontalScrollView hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view) ;




        amountAttack = -1;
        amountDefense = -1;

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetGame();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("New Game !");
                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.setMessage("Bạn thích ai chơi trước ?");
                alertDialog.setPositiveButton("Me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        arrayCell[rowBoard/2][colBoard/2].player = -1;
                        arrayCell[rowBoard/2][colBoard/2].setText("O");
                        arrayCell[rowBoard/2][colBoard/2].setTextColor(Color.BLACK);
                        preCell = arrayCell[rowBoard/2][colBoard/2];
                        cellBoard[rowBoard/2][colBoard/2] = -1;
                        first = false;


                    }
                });

                alertDialog.setNegativeButton("You", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Good Luck!",Toast.LENGTH_SHORT).show();
                        first = true;
                    }
                });
                alertDialog.show();
            }
        });

        for(int j=0;j<rowBoard;j++) {
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < colBoard; i++) {
                Cell c = new Cell(this,j,i,0);
                c.setBackground(this.getResources().getDrawable(R.drawable.cell_effect));
                c.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                c.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
                c.setTextColor(Color.RED);

                c.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if(!playing) {
                            Cell thisCell = (Cell) view;
                            if (preCell != null)
                                preCell.setTextColor(Color.BLUE);
                            thisCell.setText("X");

                            arrayCell[thisCell.rowCell][thisCell.columnCell].player = 1;
                            cellBoard[thisCell.rowCell][thisCell.columnCell] = 1;

                            amountDefense = -1;
                            amountAttack = -1;
                            preMAX = -1000000000;
                            preMIN = 1000000000;

                            if (checkWinner(arrayCell[thisCell.rowCell][thisCell.columnCell])) {
                                setResult("You Win");
                            } else {
                                playing = true;
                                if(first){
                                    playFirst(thisCell);
                                    playing = false;
                                    first = false;
                                }
                                else {
                                    setRange();
                                    Heuristic task = new Heuristic();
                                    task.execute();
                                    buttonNewGame.setText("AI Thinking...");
                                }

                            }
                        }
                    }
                });
                arrayCell[j][i] = c;
                l.addView(arrayCell[j][i]);
            }
            chessBoardLayout.addView(l);
        }


    }

    public void playFirst(Cell c){
        Random rand = new Random();
        rand.nextInt();

        int a = 0;
        int b = 0;

            while (a == 0 && b == 0){
                a = rand.nextInt(3)  - 1;
                b = rand.nextInt(3) - 1;
            }

        int row = c.rowCell + a;
        int col = c.columnCell + b;
        if(row >= 0 && col >=0 && row<rowBoard && col <colBoard){
            arrayCell[row][col].player = -1;
            arrayCell[row][col].setText("O");
            cellBoard[row][col] = -1;
            arrayCell[row][col].setTextColor(Color.BLACK);
            preCell = arrayCell[row][col];

        }

    }

    public void resetGame(){

        for(int k=0;k<rowBoard;k++)
            for(int j=0;j<colBoard;j++){
                arrayCell[k][j].player = 0;
                cellBoard[k][j] = 0;
                arrayCell[k][j].setText("");
                arrayCell[k][j].setTextColor(Color.RED);

            }


        minRowRange = 0;
        maxRowRange = rowBoard -1;
        minColRange = 0;
        maxColRange = colBoard -1;
        preCell = null;


    };

    public void setResult(String result){

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Bạn có muốn chơi lại");
        alert.setIcon(R.drawable.ic_launcher_foreground);
        alert.setMessage(result);
        alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this,"Click to New Game",Toast.LENGTH_LONG).show();
            }
        });

        alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this,"Thank You!",Toast.LENGTH_LONG).show();
            }
        });

        alert.show();


    };

    public void setRange(){
        int preMinRow = minRowRange;
        int preMaxRow = maxRowRange;
        int preMinCol = minColRange;
        int preMaxCol = maxColRange;

        maxRowRange = 0;
        minRowRange = rowBoard -1 ;
        maxColRange = 0;
        minColRange = colBoard -1 ;

        for(int i=preMinRow;i<=preMaxRow;i++)
            for(int j=preMinCol;j<=preMaxCol;j++)
                if(arrayCell[i][j].player != 0){
                    if(i>maxRowRange)
                        maxRowRange = i;
                    if(i<minRowRange)
                        minRowRange = i;
                    if(j>maxColRange)
                        maxColRange = j;
                    if(j<minColRange)
                        minColRange = j;
                }

                int cache;
        cache = minRowRange;
        while (minRowRange-cache<5){
            cache--;
            if(cache < 0 ){
                cache ++;
                break;
            }
        }
        minRowRange = cache;
        cache = maxRowRange;
        while (cache-maxRowRange<5){
            cache++;
            if(cache == rowBoard){
                cache--;
                break;
            }
        }
        maxRowRange = cache;
        cache = minColRange;
        while (minColRange-cache<5){
            cache--;
            if(cache < 0){
                cache++;
                break;
            }
        }
        minColRange = cache;
        cache = maxColRange;
        while (cache - maxColRange <5){
            cache++;
            if(cache == colBoard){
                cache--;
                break;
            }
        }
        maxColRange = cache;

    }

    int valueMAX;

    private class Heuristic extends AsyncTask<Void, Void, Cell> {


        protected Cell doInBackground(Void... voids) {

            int defenseScore;
            int attackScore = 0;
            int maxDefense = -100000;
            int maxAttack = -100000;
            Cell resultDefense = arrayCell[0][0];
            Cell resultAttack = arrayCell[0][0];

          //  boolean theFirst = true;
           // preMAX = -1000000000;
            boolean theFirst = true;
            valueMAX = -1000;

            for(int i=minRowRange;i<=maxRowRange;i++)
                for(int j=minColRange;j<=maxRowRange;j++){
                    if(arrayCell[i][j].player == 0) {
                        previewCell = arrayCell[i][j];

                        attackScore = getMax(arrayCell[i][j],1,true);
                        if(theFirst){
                            valueMAX = attackScore;
                            theFirst=false;
                        }

                        if(attackScore >= maxAttack){
                            resultAttack = arrayCell[i][j];
                            maxAttack = attackScore;

                        }

                    }
                }

            preMAX = -1000000000;
            preMIN = 1000000000;

            theFirst = true;

            for(int i=minRowRange;i<=maxRowRange;i++)
                for(int j=minColRange;j<=maxColRange;j++){
                    if(arrayCell[i][j].player  == 0){
                        previewCell = arrayCell[i][j];
                        defenseScore = getMax(arrayCell[i][j],1,false);
                       if(theFirst){
                           valueMAX = defenseScore;
                           theFirst = false;
                       }
                        if(defenseScore >= maxDefense){
                           resultDefense = arrayCell[i][j];
                            maxDefense = defenseScore;

                        }


                    }
                }

              //  return resultDefense;
            //return resultAttack;
               return ((amountDefense>=amountAttack-1)&&(amountDefense>=winTotal-1)&&(maxDefense>=maxAttack)) ? resultDefense : resultAttack;
        }

        protected void onPostExecute(Cell c){



            arrayCell[c.rowCell][c.columnCell].setText("O");
            arrayCell[c.rowCell][c.columnCell].setTextColor(Color.BLACK);
            preCell = arrayCell[c.rowCell][c.columnCell];



            arrayCell[c.rowCell][c.columnCell].player = -1;
            cellBoard[c.rowCell][c.columnCell] = -1;
            buttonNewGame.setText("Chơi Lại");
            playing = false;
            if(checkWinner(arrayCell[c.rowCell][c.columnCell])) {

                setResult("Game Over!");

            }



        }


    }



    public boolean checkWinner(Cell c){

        int[] scoreDirect;
        scoreDirect = new int[4];
        boolean result = false;
        scoreDirect[0] = getScore(c.rowCell,c.columnCell,c.player,1,1);
        scoreDirect[1] = getScore(c.rowCell,c.columnCell,c.player,1,0);
        scoreDirect[2] = getScore(c.rowCell,c.columnCell,c.player,1,-1);
        scoreDirect[3] = getScore(c.rowCell,c.columnCell,c.player,0,1);
        for(int i=0;i<4;i++)
            if(scoreDirect[i]>=winTotal){
                result = true;
                break;
            }

        return  result;
    }

    public int getScore(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget){

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while(cellBoard[indexRow][indexCol] == player){
                score++;
                indexRow = indexRow + indexRowTarget;
                indexCol = indexCol + indexColTarget;

                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;


        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while (cellBoard[indexRow][indexCol] == player){
                score += 1;
                indexRow -=  indexRowTarget;
                indexCol -=  indexColTarget;

                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        return score;

    }





    public int getMax(Cell cell,int level,boolean flagAttack){
        int result = 0;

        if(level == depth ){
            if(depth % 2 == 1)
            cellBoard[cell.rowCell][cell.columnCell] = -1;
            else
                cellBoard[cell.rowCell][cell.columnCell] = 1;
            int  scoreCache = getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,0,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,-1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,0,1,flagAttack);

            cellBoard[cell.rowCell][cell.columnCell] = 0;
            return scoreCache;

        }

        if(level % 2 == 0 && level != depth){
            int maxScore = -10000000;
            int scoreCache = 0;
            cellBoard[cell.rowCell][cell.columnCell] = 1;
            boolean theFirst = true;
            for(int i=minRowRange;i<=maxRowRange;i++)
                for (int j=minColRange;j<=maxColRange;j++){
                    if(cellBoard[i][j] == 0){

                        if(theFirst){
                            preMIN = 1000000000;
                            theFirst = false;
                        }

                        scoreCache = getMax(arrayCell[i][j],level+1,flagAttack);


                        if(scoreCache>=maxScore){
                            maxScore = scoreCache;
                        }
                        if(scoreCache>preMIN){
                            cellBoard[cell.rowCell][cell.columnCell] = 0;
                            return scoreCache;
                        }
                    }
                }

            result = maxScore;
            result += getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,0,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,-1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,0,1,flagAttack);


            cellBoard[cell.rowCell][cell.columnCell] = 0;
           // if(maxScore<preMIN)
          //      preMIN = maxScore;
             if(result<preMIN)
                preMIN = result;

        }

        if(level % 2 == 1 && level != depth){


            int minScore = 100000000;
            int scoreCache = 0;

            cellBoard[cell.rowCell][cell.columnCell] = -1;
          //  previewCell = cell;
            boolean theFirst = true;
            for(int i=minRowRange;i<=maxRowRange;i++)
                for(int j=minColRange;j<=maxColRange;j++){
                    if(cellBoard[i][j] == 0){
                        if(theFirst){
                            preMAX = -100000;
                            theFirst = false;
                        }
                        scoreCache = getMax(arrayCell[i][j],level+1,flagAttack);
                        if(scoreCache<=minScore){
                            minScore = scoreCache;
                        }


                        if(scoreCache<preMAX){
                            cellBoard[cell.rowCell][cell.columnCell] = 0;
                            return  scoreCache;
                        }

                        if(level == 1){
                            int extra = scoreCache + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,1,flagAttack)
                                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,0,flagAttack)
                                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,-1,1,flagAttack)
                                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,0,1,flagAttack);
                            if(extra<valueMAX){
                                cellBoard[cell.rowCell][cell.columnCell] = 0;
                                return extra;
                            }

                        }


                    }
                }

            result = minScore;
            result += getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,0,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,-1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,0,1,flagAttack);

            cellBoard[cell.rowCell][cell.columnCell] = 0;

              if(result>preMAX)
                 preMAX = result;

              if(level == 1 && result > valueMAX)
                  valueMAX = result;



        }



        return result;


    }




    public int getScoreExtra(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget,boolean flagAttack) {

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        int kScore = 1;
        int countStep;
        int playerTarget;
        boolean flagSpace = false;
        boolean flagInterrupt = false;
        boolean flagInterrupt1 = false;
        boolean flagInterrupt2 = false;
        int countStep1 = 0;
        int countStep2 = 0;
        if(flagAttack){
            playerTarget = player;
            countStep = 1;
            score = kScore;
        }
        else{
            playerTarget = - player;
            countStep = 1;
            score = kScore;
        }

        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {
            if (cellBoard[indexRow][indexCol] == 0 ) {
                indexRow += indexRowTarget;
                indexCol += indexColTarget;
                if (indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                    if (cellBoard[indexRow][indexCol] == playerTarget) {

                        flagSpace = true;
                    }
            }

            if (indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {


                while (cellBoard[indexRow][indexCol] == playerTarget || cellBoard[indexRow][indexCol] == -playerTarget) {

                        score += kScore;
                    if (cellBoard[indexRow][indexCol] == playerTarget ){
                        countStep++;
                    }


                    indexRow += indexRowTarget;
                    indexCol += indexColTarget;


                    if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                        break;
                    else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget]) {


                        flagInterrupt1 = true;

                        break;

                    } else if(cellBoard[indexRow][indexCol] == 0 && !flagSpace){
                        if (indexRow + indexRowTarget < 0 || indexCol + indexColTarget < 0 || indexRow + indexRowTarget >= rowBoard || indexCol + indexColTarget >= colBoard)
                            break;
                        else
                        if(cellBoard[indexRow+indexRowTarget][indexCol+indexColTarget] == playerTarget){
                            indexRow += indexRowTarget;
                            indexCol += indexColTarget;
                            flagSpace = true;
                        }
                    }
                }
            }
        }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;
        countStep1 = countStep - 1;

        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {
            if (cellBoard[indexRow][indexCol] == 0 && !flagSpace) {
                indexRow -= indexRowTarget;
                indexCol -= indexColTarget;
                if (indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                    if (cellBoard[indexRow][indexCol] == playerTarget) {
                        flagSpace = true;
                    }
            }
            if (indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {


                while (cellBoard[indexRow][indexCol] == playerTarget || cellBoard[indexRow][indexCol] == -playerTarget) {

                       score += kScore;


                    if (cellBoard[indexRow][indexCol] == playerTarget ){
                        countStep++;

                    }

                    indexRow -= indexRowTarget;
                    indexCol -= indexColTarget;

                    if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                        break;
                    else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget]) {

                        flagInterrupt2 = true;
                        break;

                    } else if(cellBoard[indexRow][indexCol] == 0 && !flagSpace ){
                        if (indexRow - indexRowTarget < 0 || indexCol - indexColTarget < 0 || indexRow - indexRowTarget >= rowBoard || indexCol - indexColTarget >= colBoard)
                            break;
                        else
                        if(cellBoard[indexRow-indexRowTarget][indexCol-indexColTarget] == playerTarget){
                            indexRow -= indexRowTarget;
                            indexCol -= indexColTarget;
                            flagSpace = true;
                        }
                    }
                }
            }
        }

        countStep2 = countStep - countStep1 - 1;

        if(!flagInterrupt1 && !flagInterrupt2)
            flagInterrupt = false;
        else
            flagInterrupt = true;
       // if(flagAttack)
       //     score += countStep*10;

        if (countStep >= winTotal - 2) {
            if (countStep == winTotal - 2) {
                if (flagAttack && !flagInterrupt)
                    score += 100;

            }

            if (countStep == winTotal - 1) {
                if (flagAttack && !flagInterrupt)
                    score += 5000;
                if ((flagAttack && (flagInterrupt1 && !flagInterrupt2)) || (flagAttack && (!flagInterrupt1 && flagInterrupt2)))
                    score += 1000;

                if (!flagAttack && !flagInterrupt)
                    score += 1000;



            }

            if (countStep >= winTotal) {

                   score += 500000;
                   if(countStep >= winTotal && !flagAttack){
                       if((countStep1 >= winTotal - 2 && countStep2 != 0) || (countStep1 != 0 && countStep2 >= winTotal -2) || (countStep1>=2 && countStep2 >= 2))
                           score += 1000000;
                        if(((countStep1 >= 3 )&&(countStep2 >= 2))||((countStep1 >= 2)&&(countStep2>=3)))
                           score += 100000;
                   }
                   if(countStep == winTotal+1 && flagAttack)
                       score += 2000000;
                /*
                 if(countStep == winTotal && !flagAttack )
                     if((countStep1 == 1 && countStep2 == 3)||(countStep1 == 3 && countStep2 == 1)||(countStep1 == 2 && countStep2 == 2) )
                         score += 1000000;
                if(countStep >= winTotal + 1 && !flagAttack) {
                    if((countStep1 == winTotal -1 && countStep2 != 0) || (countStep1 != 0 && countStep2 == winTotal - 1))
                        score += 10000000;
                    if((countStep1 != 0 && countStep2 == winTotal -2) || (countStep1 == winTotal -2 && countStep2 != 0) )
                        score += 20000000;


                }*/

            }




        }

        if(flagSpace)
            score -= 50;

        if(flagAttack){
            amountAttack = countStep >= amountAttack ? countStep : amountAttack;

        } else {

            amountDefense = countStep >= amountDefense ? countStep   : amountDefense;
        }


        return score;

    }




}

