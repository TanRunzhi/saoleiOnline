package com.gwideal.biz.manager;

import com.gwideal.biz.entity.Cell;
import com.gwideal.biz.entity.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service("TableMng")
public class TableMng {

  private final static Logger logger = LogManager.getLogger();

  public static void resetTable(Table table , int row , int col , int boom){
    Cell[][] tableArr = new Cell[row][col];
    for(int r = 0 ; r < row ; r++ ){
      for(int c = 0 ; c < col ; c++ ){
        Cell cell = new Cell();
        cell.setRow(r);
        cell.setCol(c);
        tableArr[r][c] = cell;
      }
    }
    // set boom
    int setBoom = 0;
    int p = 100 * boom / (row * col);
    while(setBoom < boom){
      for(int r = 0 ; r < row ; r++ ){
        for(int c = 0 ; c < col ; c++ ){
          if(setBoom < boom && !tableArr[r][c].isHasBoom() ){
            double rand = Math.random() * 100 ;
            if(rand < p){
              tableArr[r][c].setHasBoom(true);
              setBoom ++ ;
            }
          }
        }
      }
    }
    // init num
    for(int r = 0 ; r < row ; r++ ){
      for(int c = 0 ; c < col ; c++ ){
        Cell cell = tableArr[r][c];
        if(!cell.isHasBoom()){
          int num = 0;
          for(int r0 = r-1<0?0:r-1 ; r0 <= r+1 && r0 < row  ; r0++ ) {
            for (int c0 = c-1<0?0:c-1 ; c0 <= c+1 && c0 < col ; c0++) {
              num += (tableArr[r0][c0].isHasBoom()) ? 1 : 0;
            }
          }
          cell.setNum(num);
        }
      }
    }
    //
    table.setCells(tableArr);
  }

  public static List<Cell> changeTable(Table table , int row , int col , int player , int button){
    List<Cell> cells = new ArrayList<>();
    Cell cell = table.getCells()[row][col];
    cells.add(cell);
    cell.setPlayer(player);
    if(button == 0){
      if(cell.isHasBoom()){
        cell.setState(-1);
      }else if(cell.getNum() == 0){
        cell.setState(2);
        cells.addAll(getCellAround0(table,row,col,player));
      }else{
        cell.setState(2);
      }
    }else if(button == 2){
      cell.setState(1);
    }
    return cells;
  }

  private static List<Cell> getCellAround0(Table table , int row , int col, int player ){
    List<Cell> cells = new ArrayList<>();
    Map<String,Cell> map =  getCellAround0RE(table,row,col,player);
    for(Map.Entry<String,Cell> entry : map.entrySet()){
      cells.add(entry.getValue());
    }
    return cells;
  }

  private static Map<String,Cell> getCellAround0RE(Table table , int row , int col, int player ){
    Map<String,Cell> map = new HashMap<>();
    for(int r0 = row -1 < 0 ? 0 : row -1 ; r0 <= row +1 && r0 < table.getCells().length  ; r0++ ) {
      for (int c0 = col - 1 < 0 ? 0 : col -1 ; c0 <= col + 1  && c0 < table.getCells()[0].length ; c0++) {
        if( r0 != row && c0 != col){
          if(map.containsKey(r0 + "_" + c0)){

          }else{
            Cell cell = table.getCells()[r0][c0];
            if(0 == cell.getState()){
              cell.setPlayer(player);
              cell.setState(2);
              map.put(r0 + "_" + c0,cell);
              if(0 == cell.getNum()){
                map.putAll(getCellAround0RE(table , r0 , c0,  player ));
              }
            }
          }
        }
      }
    }
    return map;
  }


}
