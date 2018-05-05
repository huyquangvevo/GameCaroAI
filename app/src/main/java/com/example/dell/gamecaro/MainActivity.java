package com.example.dell.gamecaro;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    LinearLayout chessBoardLayout;
    Button buttonNewGame;
    static final int rowBoard = 15;
    static final int colBoard = 15;
    final int winTotal = 5;
    static Cell[][] arrayCell ;
    static int[][] cellBoard;
    Cell preCell;
    boolean flagWin = false;
    int amountDefense = -1;
    int amountAttack = -1;
    Cell previewCell;
    Cell nextPlayer1;
    Cell nextPlayer2;
    int preMAX = -1000000000;
    int preMIN = 1000000000;

    ArrayList<Cell> playerCell;
    ArrayList<Cell> machinceCell;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayCell = new Cell[rowBoard][colBoard];
        cellBoard = new int[rowBoard][colBoard];

        playerCell = new ArrayList<Cell>();
        machinceCell = new ArrayList<Cell>();

        for(int i=0;i<rowBoard;i++)
            for(int j=0;j<colBoard;j++){
                //  Cell c = new Cell(MainActivity.this,i,j,0);
                //  arrayCell[i][j] = c;
                cellBoard[i][j] = 0;
            }

        chessBoardLayout = (LinearLayout) findViewById(R.id.chess_board_layout);
        buttonNewGame = (Button) findViewById(R.id.button_new_game);

        for(int j=0;j<rowBoard;j++) {
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);

            for (int i = 0; i < colBoard; i++) {
                Cell c = new Cell(this,j,i,0);
                c.setBackground(this.getResources().getDrawable(R.drawable.cell_effect));
                c.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                //  c.setTextScaleX((float) 1.5);
                c.setTextColor(Color.RED);

                c.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Cell thisCell = (Cell) view;
                        if(preCell != null)
                            preCell.setTextColor(Color.BLUE);
                        thisCell.setText("X");
                        // thisCell.player = 1;

                        arrayCell[thisCell.rowCell][thisCell.columnCell].player = 1;
                        cellBoard[thisCell.rowCell][thisCell.columnCell] = 1;

                        amountDefense = -1;
                        amountAttack = -1;
                        preMAX = -1000000000;
                        preMIN = 1000000000;

                        if(checkWinner(arrayCell[thisCell.rowCell][thisCell.columnCell])){
                            buttonNewGame.setText("Chien Thang");
                        } else {

                            Heuristic task = new Heuristic();
                            task.execute();
                            playerCell.add(new Cell(MainActivity.this, thisCell.rowCell, thisCell.columnCell, 1));
                            buttonNewGame.setText("AI Thinking...");


                        }
                    }
                });
                arrayCell[j][i] = c;
                l.addView(arrayCell[j][i]);
            }
            chessBoardLayout.addView(l);
        }




    }


    private class Heuristic extends AsyncTask<Void, Void, Cell> {


        protected Cell doInBackground(Void... voids) {
            int maxScore = -1000;
            int score = 0;
            int defenseScore;
            int attackScore = 0;
            int resultScore;
            int maxDefense = -100000;
            int maxAttack = -100000;
            Cell resultCell = arrayCell[0][0];
            Cell resultDefense = arrayCell[0][0];
            Cell resultAttack = arrayCell[0][0];

            for(int i=0;i<rowBoard;i++)
                for(int j=0;j<colBoard;j++){
                    if(arrayCell[i][j].player == 0) {
                      //  amountAttack = -1;
                      //  amountDefense = -1;
                        attackScore = getMax(arrayCell[i][j],1,true);
                      //  attackScore = getScoreExtra(i,j,-1,1,1,true) + getScoreExtra(i,j,-1,1,0,true) + getScoreExtra(i,j,-1,1,-1,true) + getScoreExtra(i,j,-1,0,1,true);

                        if(attackScore >= maxAttack){
                            resultAttack = arrayCell[i][j];
                            maxAttack = attackScore;
                        //    resultAttack.player = 10;

                        }

                    }
                }


            for(int i=0;i<rowBoard;i++)
                for(int j=0;j<colBoard;j++){
                    if(arrayCell[i][j].player  == 0){
                      //  amountAttack = -1;
                      //  amountDefense = -1;
                        defenseScore = getMax(arrayCell[i][j],1,false);
                      //  defenseScore = getScoreExtra(i,j,-1,1,1,false) + getScoreExtra(i,j,-1,1,0,false) + getScoreExtra(i,j,-1,1,-1,false) + getScoreExtra(i,j,-1,0,1,false);

                        if(defenseScore >= maxDefense){
                           resultDefense = arrayCell[i][j];
                            maxDefense = defenseScore;

                        }


                    }
                }


               return ((amountDefense>=amountAttack)&&(amountDefense>=winTotal-1)&&(maxDefense>=maxAttack)) ? resultDefense : resultAttack;
        }

        protected void onPostExecute(Cell c){



         //   arrayCell[c.rowCell][c.columnCell].player = -1;
         //   cellBoard[c.rowCell][c.columnCell] = -1;

            arrayCell[c.rowCell][c.columnCell].setText("O");
            arrayCell[c.rowCell][c.columnCell].setTextColor(Color.BLACK);
            preCell = arrayCell[c.rowCell][c.columnCell];



            arrayCell[c.rowCell][c.columnCell].player = -1;
            cellBoard[c.rowCell][c.columnCell] = -1;
                buttonNewGame.setText("Attack: "+amountAttack+" --- "+"Defense: "+amountDefense );
            if(checkWinner(arrayCell[c.rowCell][c.columnCell]))
                buttonNewGame.setText("May Thang");



        }


    }

    public int evaluateScore(Cell cell,int preMax){
        int sumScore = 0;
        int minScore = 100;
        cellBoard[cell.rowCell][cell.columnCell] = -1;
        for(int i=0;i<rowBoard;i++) {
            for (int j = 0; j < colBoard; j++) {
                if (cellBoard[i][j] == 0) {
                    cellBoard[i][j] = 1;
                    sumScore = calculateScore(cell.rowCell, cell.columnCell, 1, 1, 1) + calculateScore(cell.rowCell, cell.columnCell, 1, 1, 0)
                            + calculateScore(cell.rowCell, cell.columnCell, 1, 1, -1) + calculateScore(cell.rowCell, cell.columnCell, 1, 0, 1)
                            - calculateScore(i, j, -1, 1, 1) - calculateScore(i, j, -1, 1, 0) - calculateScore(i, j, -1, 1, -1) - calculateScore(i, j, -1, 0, 1);


                    for (int p = 0; p < machinceCell.size(); p++) {
                        Cell c = machinceCell.get(p);
                        sumScore += calculateScore(c.rowCell, c.columnCell, 1, 1, 1) + calculateScore(c.rowCell, c.columnCell, 1, 1, 0)
                                + calculateScore(c.rowCell, c.columnCell, 1, 1, -1) + calculateScore(c.rowCell, c.columnCell, 1, 0, 1);
                    }

                    for (int p = 0; p < playerCell.size(); p++) {
                        Cell c = playerCell.get(p);
                        sumScore = sumScore - calculateScore(c.rowCell, c.columnCell, -1, 1, 1) - calculateScore(c.rowCell, c.columnCell, -1, 1, 0)
                                - calculateScore(c.rowCell, c.columnCell, -1, 1, -1) - calculateScore(c.rowCell, c.columnCell, -1, 0, 1);
                    }


                    if (sumScore <= minScore)
                        minScore = sumScore;

                    cellBoard[i][j] = 0;
                    //  arrayCell[i][j].setText(sumScore+"k");

                    if (minScore < preMax){
                        cellBoard[cell.rowCell][cell.columnCell] = 0;
                        return minScore;
                    }
                }


                //break;
            }


        }

        cellBoard[cell.rowCell][cell.columnCell] = 0;
        return minScore;
    }



    public int calculateScore(int rowTarget, int colTarget, int prohibit, int indexRowTarget,int indexColTarget){

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while ((cellBoard[indexRow][indexCol] != prohibit) && (score < winTotal)){
                score += 1;
                indexRow = indexRow + indexRowTarget;
                indexCol = indexCol + indexColTarget;
                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while ((cellBoard[indexRow][indexCol] != prohibit ) && (score < 2*winTotal)){
                score +=1;
                indexRow -=  indexRowTarget;
                indexCol -=  indexColTarget;
                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
            }

        int result = 0;
        if(score>=winTotal && score<2*winTotal)
            result = 1;
        else if (score==winTotal*2)
            result = 2;

        return result;

        //   return score;
    }





    public int extraScore(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget,boolean flagAttack) {

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        int kScore = 5;
        int countStep;
        int playerTarget;
        boolean flagSpace = false;
        if(flagAttack){
            playerTarget = player;
            countStep = 1;
            score = kScore+2;//+1;
        }
        else{
            playerTarget = - player;
            countStep = 1;
            score = kScore+2;
        }

        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {
            if (cellBoard[indexRow][indexCol] == 0 /*&& flagAttack*/) {
                indexRow += indexRowTarget;
                indexCol += indexColTarget;
                if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                    if(cellBoard[indexRow][indexCol] == playerTarget){
                        score -= kScore - 1;
                       // countStep ++;
                        flagSpace = true;
                    }
            }

            if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                while (cellBoard[indexRow][indexCol] == playerTarget || cellBoard[indexRow][indexCol] == -playerTarget) {
                    score += kScore;
                //    if(cellBoard[indexRow][indexCol] == player)
                 //       score += kScore;
                    if (cellBoard[indexRow][indexCol] == playerTarget)
                        countStep++;

                    indexRow += indexRowTarget;
                    indexCol += indexColTarget;

                    if (countStep >= winTotal - 2) {
                        score +=  (countStep - winTotal + 3) * 1000;

                        boolean flagChange = true;
                        if (countStep == winTotal) {
                            score += kScore * 10000;
                            if(!flagSpace)
                                countStep += 100;
                               // flagWin = flagChoosen;
                        }

                        if (countStep == winTotal - 2 && !flagAttack)
                            if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                                break;
                            else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget])
                                flagChange = false;

                    //    if (flagChange)
                     //       flagChoosen = flagAttack;
                    }


                    if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                        break;
                    else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget]) {

                        if (countStep >= winTotal - 2 && !flagAttack) {
                            score -=  1000;
                            if(countStep == winTotal-1)
                                countStep --;
                            if(!flagSpace)
                                score += 500;
                           // countStep = countStep -1 ;
                            //  flagChoosen = true;
                        }

                       // if (countStep >= winTotal - 1 && !flagSpace)
                        //    score += 10000;


                        break;
                    } else if(cellBoard[indexRow][indexCol] == 0 /*&& flagAttack*/){
                        if (indexRow + indexRowTarget < 0 || indexCol + indexColTarget < 0 || indexRow + indexRowTarget >= rowBoard || indexCol + indexColTarget >= colBoard)
                            break;
                        else
                        if(cellBoard[indexRow+indexRowTarget][indexCol+indexColTarget] == playerTarget){
                            indexRow += indexRowTarget;
                            indexCol += indexColTarget;
                            score -= kScore-1;
                            flagSpace = true;
                        }
                    }

                }
        }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;

        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {
            if(cellBoard[indexRow][indexCol] == 0 /* &&  flagAttack */){
                indexRow -= indexRowTarget;
                indexCol -= indexColTarget;
                if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                    if(cellBoard[indexRow][indexCol] == playerTarget){
                        score -= kScore - 1;
                        flagSpace = true;
                      //  countStep ++;
                    }
            }
            if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                while (cellBoard[indexRow][indexCol] == playerTarget || cellBoard[indexRow][indexCol] == -playerTarget) {
                    score += kScore;
                   // if(cellBoard[indexRow][indexCol] == player)
                    //    score += kScore;
                    if (cellBoard[indexRow][indexCol] == playerTarget)
                        countStep++;

                    indexRow -= indexRowTarget;
                    indexCol -= indexColTarget;

                    if (countStep >= winTotal - 2) {
                        score += (countStep - winTotal + 3) * 1000;

                        if (countStep == winTotal) {
                            score += kScore * 10000;
                            if(!flagSpace)
                                //flagWin = flagChoosen;
                                countStep += 100;
                        }
                        boolean flagChange = true;

                        if (countStep == winTotal - 2 && !flagAttack)
                            if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                                break;
                            else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget])
                                flagChange = false;

                    }

                    if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                        break;
                    else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget]) {

                        if (countStep >= winTotal - 2 && !flagAttack) {
                           // if(!flagAttack)
                           if(countStep == winTotal-1)
                                countStep = countStep - 1;
                            score -= 1000;
                            if(!flagSpace)
                                score += 500;

                        }

                       // if (countStep >= winTotal - 1 && !flagSpace) {
                        //    score += 10000;

                            if(countStep == winTotal)
                                countStep += 100;
                        //}

                        break;

                    } else if(cellBoard[indexRow][indexCol] == 0 /*&& flagAttack*/){
                        if (indexRow - indexRowTarget < 0 || indexCol - indexColTarget < 0 || indexRow - indexRowTarget >= rowBoard || indexCol - indexColTarget >= colBoard)
                            break;
                        else
                        if(cellBoard[indexRow-indexRowTarget][indexCol-indexColTarget] == playerTarget){
                            indexRow -= indexRowTarget;
                            indexCol -= indexColTarget;
                            score -= kScore-1;
                            flagSpace = true;
                        }
                    }
                }
        }

       // if(flagSpace)
           // score -= kScore + 1;

        if(flagAttack){
            amountAttack = countStep >= amountAttack ? countStep : amountAttack;

        } else {

            amountDefense = countStep >= amountDefense ? countStep : amountDefense;
        }


        return score;

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

        if(level == 3){

            cellBoard[cell.rowCell][cell.columnCell] = -1;
                      int  scoreCache = getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,1,flagAttack)
                                + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,0,flagAttack)
                                + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,-1,1,flagAttack)
                                + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,0,1,flagAttack);

            cellBoard[cell.rowCell][cell.columnCell] = 0;
            return scoreCache;

        }

        if(level == 2 ){
            int maxScore = -40000000;
            int scoreCache = 0;
            cellBoard[cell.rowCell][cell.columnCell] = 1;
            for(int i=0;i<rowBoard;i++)
                for (int j=0;j<colBoard;j++){
                    if(cellBoard[i][j] == 0){
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

            cellBoard[cell.rowCell][cell.columnCell] = 0;
            if(maxScore<preMIN)
                preMIN = maxScore;
            result = maxScore;
        }

        if(level == 1 ){


            int minScore = 400000000;
            int scoreCache = 0;

            cellBoard[cell.rowCell][cell.columnCell] = -1;
            previewCell = cell;
            for(int i=0;i<rowBoard;i++)
                for (int j=0;j<colBoard;j++){
                    if(cellBoard[i][j] == 0){
                        scoreCache = getMax(arrayCell[i][j],level+1,flagAttack);
                        if(scoreCache<=minScore){
                            minScore = scoreCache;
                        }
                        if(scoreCache<preMAX){
                            cellBoard[cell.rowCell][cell.columnCell] = 0;
                            return  scoreCache;
                        }
                    }
                }

            result = minScore;
            result += getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,1,0,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,-1,1,flagAttack)
                    + getScoreExtra(previewCell.rowCell,previewCell.columnCell,-1,0,1,flagAttack);

            cellBoard[cell.rowCell][cell.columnCell] = 0;
            if(minScore>preMAX)
                preMAX = minScore;
        }



        return result;

    }




    public int getScoreExtra(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget,boolean flagAttack) {

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 1;
        int kScore = 10;
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
            score = kScore;//+1;
        }
        else{
            playerTarget = - player;
            countStep = 1;
            score = kScore;
        }

        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {
            if (cellBoard[indexRow][indexCol] == 0 /*&& !flagAttack*/) {
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

                    } else if(cellBoard[indexRow][indexCol] == 0 /*&& flagAttack*/){
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
            if (cellBoard[indexRow][indexCol] == 0 /*&& !flagAttack*/ && !flagSpace) {
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

                    } else if(cellBoard[indexRow][indexCol] == 0 ){
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

        if (countStep >= winTotal - 2) {
            if (countStep == winTotal - 2) {
                if (flagAttack && !flagInterrupt)
                    score += 1000;

                // countStep = 1;
            }

            if (countStep == winTotal - 1) {
                if (flagAttack && !flagInterrupt)
                    score += 50000;
                if ((flagAttack && (flagInterrupt1 && !flagInterrupt2)) || (flagAttack && (!flagInterrupt1 && flagInterrupt2)))
                    score += 10000;

                if (!flagAttack && !flagInterrupt)
                    score += 10000;

            }

            if (countStep >= winTotal) {
                    score += 1000000;
                if(countStep1 == winTotal + 1)
                    if((countStep1 == winTotal -1 && countStep2 == winTotal - 4) || (countStep1 == winTotal - 4 && countStep2 == winTotal - 1))
                        score += 10000000;
                    if((countStep1 == winTotal - 3 && countStep2 == winTotal -2) || (countStep1 == winTotal -2 && countStep2 == winTotal - 3) )
                        score += 20000000;
            }

            if(flagSpace)
                score -= 500;


        }
        /*

            if((countStep == winTotal -1  || countStep == winTotal -2  )&& !flagAttack){
                score -= (countStep - winTotal + 3 )*1000;
                countStep = 1;
            }

            if(countStep == winTotal && countStep1 != 0){
                score -= kScore*10000;
                countStep = 1;
            }



        }


        if(!flagSpace)
            score +=  10000;
        if(countStep<winTotal-2 && flagSpace && flagAttack)
            score = -10000;

       // if(flagInterrupt1 && flagInterrupt2 && countStep < winTotal && flagAttack)
        //    score = 0;

        if(countStep >= winTotal && !flagSpace)
            countStep += 100;

       if(!flagAttack && !flagInterrupt && countStep >= winTotal)
           countStep += 50;

            */

        if(flagAttack){
            amountAttack = countStep >= amountAttack ? countStep : amountAttack;

        } else {

            amountDefense = countStep >= amountDefense ? countStep   : amountDefense;
        }


        return score;

    }




}

