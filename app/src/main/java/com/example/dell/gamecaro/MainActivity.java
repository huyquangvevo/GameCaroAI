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
    static final int rowBoard = 10;
    static final int colBoard = 10;
    final int winTotal = 5;
    static Cell[][] arrayCell ;// = new Cell[rowBoard][colBoard];
    static int[][] cellBoard; //= new int[rowBoard][colBoard];
    Cell preCell;
    boolean flagChoosen = true;
    boolean flagWin = false;
    int amountDefense = -1;
    int amountAttack = -1;

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

        //  Heuristic task = new Heuristic();
        //  task.execute();


    }


    private class Heuristic extends AsyncTask<Void, Void, Cell> {


        protected Cell doInBackground(Void... voids) {
            int maxScore = -1000;
            int score = 0;
            int defenseScore;
            int attackScore = 0;
            int resultScore;
            int maxDefense = -10000;
            int maxAttack = -10000;
            Cell resultCell = arrayCell[0][0];
            Cell resultDefense = arrayCell[0][0];
            Cell resultAttack = arrayCell[0][0];

            for(int i=0;i<rowBoard;i++)
                for(int j=0;j<colBoard;j++){
                    if(arrayCell[i][j].player == 0) {
                        // score = evaluateScore(arrayCell[i][j],maxAttack);
                        score = getMax(arrayCell[i][j],1,-100,100,true);
                        attackScore = score + extraScore(i,j,-1,1,1,true) + extraScore(i,j,-1,1,0,true) + extraScore(i,j,-1,1,-1,true) + extraScore(i,j,-1,0,1,true);

                        if(attackScore >= maxAttack){
                            resultAttack = arrayCell[i][j];
                            maxAttack = attackScore;
                            //  resultAttack.player = -1;
                            if(flagWin)
                                return resultAttack;
                        }


                    }
                }


            for(int i=0;i<rowBoard;i++)
                for(int j=0;j<colBoard;j++){
                    if(arrayCell[i][j].player  == 0){
                        // score = evaluateScore(arrayCell[i][j],maxDefense);
                        score = getMax(arrayCell[i][j],1,-100,100,false);
                        defenseScore = score + extraScore(i,j,-1,1,1,false) + extraScore(i,j,-1,1,0,false) + extraScore(i,j,-1,1,-1,false) + extraScore(i,j,-1,0,1,false);

                        if(defenseScore >= maxDefense){
                            resultDefense = arrayCell[i][j];
                            maxDefense = defenseScore;
                            //   resultDefense.player = -1;
                        }


                    }
                }


        /*
            for(int i=0;i<rowBoard;i++)
                for(int j=0;j<colBoard;j++){
                    if(arrayCell[i][j].player == 0){

                        attackScore = getMax(arrayCell[i][j],1,-100,100,true);
                        if(attackScore >= maxAttack){
                            resultAttack = arrayCell[i][j];
                            maxAttack = attackScore;
                        }

                        defenseScore = getMax(arrayCell[i][j],1,-100,100,false);
                        if(defenseScore >= maxDefense){
                            resultDefense = arrayCell[i][j];
                            maxDefense = defenseScore;
                        }

                    }
                }
        */
            resultCell.player = maxScore;
            //   return flagChoosen ? resultAttack : resultDefense;
            return amountDefense>=amountAttack ? resultDefense : resultAttack;
        }

        protected void onPostExecute(Cell c){



            arrayCell[c.rowCell][c.columnCell].player = -1;
            cellBoard[c.rowCell][c.columnCell] = -1;

            //  machinceCell.add(arrayCell[c.rowCell][c.columnCell]);
            arrayCell[c.rowCell][c.columnCell].setText("O");
            arrayCell[c.rowCell][c.columnCell].setTextColor(Color.BLACK);
            preCell = arrayCell[c.rowCell][c.columnCell];
            buttonNewGame.setText(c.player+" Ok Huy");
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


    public int extraScore(int rowTarget, int colTarget, int player, int indexRowTarget,int indexColTarget,int kWin) {

        int indexRow = rowTarget + indexRowTarget;
        int indexCol = colTarget + indexColTarget;
        int score = 0;
        int numDefense = kWin;
        int numAttack = winTotal - kWin;
        int countDefense = 0;
        int countAttack = 1;
        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while((cellBoard[indexRow][indexCol] == player)||(cellBoard[indexRow][indexCol] == - player)){
                if(cellBoard[indexRow][indexCol] == -player){
                    score += numDefense;
                    countDefense ++;

                }
                else {
                    score += numAttack;
                    countAttack ++;
                }

                if(countDefense == winTotal - 2 || countAttack == winTotal -2)
                    score += 2000;
                if(countDefense == winTotal -1 || countAttack == winTotal -1)
                    score += 4000;

                if(countDefense == winTotal -2)
                    score += numDefense;
                if(countDefense == winTotal -1)
                    score += 2*numDefense;

                if(countAttack == winTotal)
                    score += 10000;

                indexRow += indexRowTarget;
                indexCol += indexColTarget;

                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
                else if(cellBoard[indexRow][indexCol] == -cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget]){
                    if(countDefense == winTotal - 2 || countAttack == winTotal -2)
                        score -= 2000;
                    break;
                }

            }

        indexRow = rowTarget - indexRowTarget;
        indexCol = colTarget - indexColTarget;


        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
            while ((cellBoard[indexRow][indexCol] == player)||(cellBoard[indexRow][indexCol] == -player)){
                if(cellBoard[indexRow][indexCol] == -player){
                    score += numDefense;
                    countDefense ++;
                }
                else{
                    score += numAttack;
                    countAttack ++;
                }

                if(countDefense == winTotal -2 || countAttack == winTotal - 2 )
                    score += 2000;
                if(countDefense == winTotal -1 || countAttack == winTotal -1 )
                    score += 4000;

                if(countDefense == winTotal -2){
                    score += numDefense;
                    flagChoosen = false;
                }
                if(countDefense == winTotal -1){
                    score += 2*numDefense;
                    flagChoosen = false;
                }

                if(countAttack == winTotal)
                    score += 10000;



                indexRow -= indexRowTarget;
                indexCol -= indexColTarget;

                if(indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard )
                    break;
                else if(cellBoard[indexRow][indexCol] == -cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget]){
                    if(countDefense == winTotal -2 || countAttack == winTotal - 2)
                        score -= 2000;
                    if(countDefense == winTotal - 2)
                        flagChoosen = true;
                    break;
                }

            }



        return score;
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
            score = kScore+1;
        }
        else{
            playerTarget = - player;
            countStep = 0;
            score = kScore;
        }

        if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard) {
            if (cellBoard[indexRow][indexCol] == 0 && flagAttack) {
                indexRow += indexRowTarget;
                indexCol += indexColTarget;
                if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                    if(cellBoard[indexRow][indexCol] == playerTarget){
                        score -= kScore -1;
                        flagSpace = true;
                    }
            }

            if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                while (cellBoard[indexRow][indexCol] == playerTarget || cellBoard[indexRow][indexCol] == -playerTarget) {
                    score += kScore;
                    if(cellBoard[indexRow][indexCol] == player) score += kScore;
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
                                flagWin = flagChoosen;
                        }

                        if (countStep == winTotal - 2 && !flagAttack)
                            if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                                break;
                            else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget])
                                flagChange = false;

                        if (flagChange)
                            flagChoosen = flagAttack;
                    }


                    if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                        break;
                    else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow - indexRowTarget][indexCol - indexColTarget]) {

                        if (countStep >= winTotal - 2 && !flagAttack) {
                            score -=  1000;
                            //  flagChoosen = true;
                        }

                        if (countStep >= winTotal - 1)
                            flagChoosen = flagAttack;

                        break;
                    } else if(cellBoard[indexRow][indexCol] == 0 && flagAttack){
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
            if(cellBoard[indexRow][indexCol] == 0 && flagAttack){
                indexRow -= indexRowTarget;
                indexCol -= indexColTarget;
                if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                    if(cellBoard[indexRow][indexCol] == playerTarget){
                        score -= kScore -1;
                        flagSpace = true;
                    }
            }
            if(indexRow >= 0 && indexCol >= 0 && indexRow < rowBoard && indexCol < colBoard)
                while (cellBoard[indexRow][indexCol] == playerTarget || cellBoard[indexRow][indexCol] == -playerTarget) {
                    score += kScore;
                    if(cellBoard[indexRow][indexCol] == player) score += kScore;
                    if (cellBoard[indexRow][indexCol] == playerTarget)
                        countStep++;

                    indexRow -= indexRowTarget;
                    indexCol -= indexColTarget;

                    if (countStep >= winTotal - 2) {
                        score += (countStep - winTotal + 3) * 1000;
                        if (countStep == winTotal) {
                            score += kScore * 10000;
                            if(!flagSpace)
                                flagWin = flagChoosen;
                        }
                        boolean flagChange = true;

                        if (countStep == winTotal - 2 && !flagAttack)
                            if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                                break;
                            else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget])
                                flagChange = false;
                        if (flagChange)
                            flagChoosen = flagAttack;

                    }

                    if (indexRow < 0 || indexCol < 0 || indexRow >= rowBoard || indexCol >= colBoard)
                        break;
                    else if (cellBoard[indexRow][indexCol] == -cellBoard[indexRow + indexRowTarget][indexCol + indexColTarget]) {

                        if (countStep >= winTotal - 2 && !flagAttack) {
                            score -= 1000;
                            //   flagChoosen = true;
                        }

                        if (countStep >= winTotal - 1)
                            flagChoosen = flagAttack;

                        break;

                    } else if(cellBoard[indexRow][indexCol] == 0 && flagAttack){
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

        if(flagAttack){
            amountAttack = countStep >= amountAttack ? countStep : amountAttack;
        } else {
            amountDefense = countStep + 1 >= amountDefense ? countStep + 1 : amountAttack;
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

    public int preventDouble(){

        return 0;


    }

    public int getMaxScore(Cell cell,int preMax,int level){
        int sumScore = 0;
        int minScore = 100;
        int maxScore = 0;
        int score = 0;
        cellBoard[cell.rowCell][cell.columnCell] = -1;
        for(int i=0;i<rowBoard;i++)
            for(int j=0;j<colBoard;j++){
                if(cellBoard[i][j] == 0)
                    score = evaluateScore(arrayCell[i][j],preMax);
                if(score>=maxScore)
                    maxScore = score;

            }

        return maxScore;
    }


    public int getEntropy(Cell cell,int player,boolean flagAtt){

        int sumScore = 0;
        player = -player;
        //  int minScore = 100;
        //  cellBoard[cell.rowCell][cell.columnCell] = player;
        for(int k=0;k<rowBoard;k++) {
            for (int p = 0; p < colBoard; p++) {

                if(cellBoard[k][p] == -player)
                    sumScore += calculateScore(k,p,-player,1,1) + calculateScore(k,p,-player,1,0)
                            + calculateScore(k,p,-player,1,-1) + calculateScore(k,p,-player,0,1);
                if(cellBoard[k][p] == player)
                    sumScore = sumScore - calculateScore(k,p,player,1,1) - calculateScore(k,p,player,1,0)
                            - calculateScore(k,p,player,1,-1) - calculateScore(k,p,player,0,1);

                // int scorePlus = extraScore(k,p,-1,1,1,flagAtt) + extraScore(k,p,-1,1,0,flagAtt) + extraScore(k,p,-1,1,-1,flagAtt) + extraScore(k,p,-1,0,1,flagAtt);


            }
        }

        // cellBoard[cell.rowCell][cell.columnCell] = 0;
      /*  for(int k=0;k < machinceCell.size();k++){
            Cell c = machinceCell.get(k);
            sumScore += calculateScore(c.rowCell, c.columnCell, 1, 1, 1) + calculateScore(c.rowCell, c.columnCell, 1, 1, 0)
                    + calculateScore(c.rowCell, c.columnCell, 1, 1, -1) + calculateScore(c.rowCell, c.columnCell, 1, 0, 1);

        }

        for (int p = 0; p < playerCell.size(); p++) {
            Cell c = playerCell.get(p);
            sumScore = sumScore - calculateScore(c.rowCell, c.columnCell, -1, 1, 1) - calculateScore(c.rowCell, c.columnCell, -1, 1, 0)
                    - calculateScore(c.rowCell, c.columnCell, -1, 1, -1) - calculateScore(c.rowCell, c.columnCell, -1, 0, 1);
        }
        */
        return sumScore;



    }

    public int getMax(Cell cell,int level,int preMAX,int preMIN,boolean flagAttack){
        int result = 0;

        if(level == 3){
            int scoreCache = 0;
            int minScore = -1000;
            cellBoard[cell.rowCell][cell.columnCell] = -1;
            for(int i=0;i<rowBoard;i++)
                for (int j=0;j<colBoard;j++)
                    if(cellBoard[i][j] == 0){
                        cellBoard[i][j] = 1;
                        scoreCache = getEntropy(arrayCell[i][j],-1,flagAttack);
                        //     scoreCache -= extraScore(i,j,-1,1,1,flagAttack) + extraScore(i,j,-1,1,0,flagAttack) + extraScore(i,j,-1,1,-1,flagAttack) + extraScore(i,j,-1,0,1,flagAttack);
                        cellBoard[i][j] = 0;
                        if(scoreCache<=minScore){
                            minScore = scoreCache;
                            // preMAX = minScore;
                        }
                        if(minScore<preMAX){
                            cellBoard[cell.rowCell][cell.columnCell] = 0;
                            return minScore;
                        }
                    }

            cellBoard[cell.rowCell][cell.columnCell] = 0;
            result = minScore;
            //  return  minScore;
        }

        if(level == 2 ){
            int maxScore = -1000;
            int scoreCache = 0;
            cellBoard[cell.rowCell][cell.columnCell] = 1;
            for(int i=0;i<rowBoard;i++)
                for (int j=0;j<colBoard;j++){
                    if(cellBoard[i][j] == 0){
                        // cellBoard[i][j] = 1;
                        scoreCache = getMax(arrayCell[i][j],level +1,preMAX,preMIN,flagAttack);
                        //   scoreCache += extraScore(i,j,-1,1,1,flagAttack) + extraScore(i,j,-1,1,0,flagAttack) + extraScore(i,j,-1,1,-1,flagAttack) + extraScore(i,j,-1,0,1,flagAttack);

                        // scoreCache = getEntropy(arrayCell[i][j],-1,-1000);
                        // cellBoard[i][j] = 0;
                        if(scoreCache>=maxScore){
                            maxScore = scoreCache;
                            preMAX = maxScore;
                        }
                        if(maxScore>preMIN){
                            cellBoard[cell.rowCell][cell.columnCell] = 0;
                            return maxScore;
                        }
                    }
                }

            cellBoard[cell.rowCell][cell.columnCell] = 0;
            result = maxScore;
            //   return maxScore;
        }

        if(level == 1 ){


            int minScore = 1000;
            int scoreCache = 0;

            cellBoard[cell.rowCell][cell.columnCell] = -1;
            for(int i=0;i<rowBoard;i++)
                for (int j=0;j<colBoard;j++){
                    if(cellBoard[i][j] == 0){
                        //  cellBoard[i][j] = 1;
                        scoreCache = getMax(arrayCell[i][j],level+1,preMAX,preMIN,flagAttack);
                        //   scoreCache -= extraScore(i,j,-1,1,1,flagAttack) + extraScore(i,j,-1,1,0,flagAttack) + extraScore(i,j,-1,1,-1,flagAttack) + extraScore(i,j,-1,0,1,flagAttack);
                        //   cellBoard[i][j] = 0;
                        if(scoreCache<=minScore){
                            minScore = scoreCache;
                            preMIN = minScore;
                        }
                        if(minScore<preMAX){
                            cellBoard[cell.rowCell][cell.columnCell] = 0;
                            return minScore;
                        }
                    }
                }

            result = minScore;
            cellBoard[cell.rowCell][cell.columnCell] = 0;
            //  return minScore;
        }

        return result;
        //  return 0;
    }






}

