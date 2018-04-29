package com.example.dell.gamecaro;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Dell on 3/19/2018.
 */

public class Cell extends android.support.v7.widget.AppCompatTextView {

    public int rowCell;
    public int columnCell;
    public int player;
    public int scoreCell;

    public Cell(Context context) {
        super(context);
    }

    public Cell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Cell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public  Cell(Context context,int rowCell,int columnCell,int player){
        super(context);
        this.rowCell = rowCell;
        this.columnCell = columnCell;
        this.player = player;

    }

    public void setPlayer(int i){
        this.player = i;
    }

}
