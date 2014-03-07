import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.io.*;
import java.lang.Math;
import java.util.*;

/*
alle game mechanics staan in Board.java
*/

public class Othello {
	
	private Board mainBoard;
	private char human;
	private char computer;
	private boolean compFlag = false;
	
	public Othello(){
		mainBoard = new Board();
		human = 'b';
		computer = 'w';
		mainBoard.board[3][3] = 'w';
		mainBoard.board[3][4] = 'b';
		mainBoard.board[4][3] = 'b';
		mainBoard.board[4][4] = 'w';
	}

	public static void main(String[] args){
		Othello game = new Othello();
		
		if(args.length>0){
			if(args[0].equals("c"))
				game.compFlag = true;
		}
				
		//deze while loop kan een stuk beter georganiseerd
		while(!game.mainBoard.endGame()){
			if(game.mainBoard.hasTurn == game.human && game.compFlag){
				int[] move = game.bestMove(game.mainBoard, 1, false);
				if(move != null){
                    System.out.printf("%d, %d",move[0],move[1]);
					game.mainBoard.put(move[0],move[1]);
                    try{Thread.sleep(100);}catch(Exception e){System.out.println(e.toString());}
				}else{
					game.mainBoard.pass();
				}
				game.mainBoard.repaint();
			}
			
			if(game.mainBoard.hasTurn == game.computer){
				int[] move = game.bestMove(game.mainBoard, 6, true);
				if(move != null){
                    System.out.printf("%d, %d",move[0],move[1]);
					game.mainBoard.put(move[0],move[1]);
                    try{Thread.sleep(100);}catch(Exception e){System.out.println(e.toString());}
				}else{
					game.mainBoard.pass();
				}
				game.mainBoard.repaint();
			}
			
			try{Thread.sleep(100);}
			catch(Exception e){System.out.println(e.toString());}
		}
		
		if(game.mainBoard.win(game.computer))
			System.out.println(game.computer);
		else
			System.out.println(game.human);
	}
	
    private int[] bestMove(Board mainBoard, int depth, boolean computer) {
        int a = -10000;
        int anew;
        int[] bestMove = null;

        if(depth <= 0 || mainBoard.endGame()) {
            return null;
        }
        
        ArrayList<int[]> nextmoves = mainBoard.legalPositions();

        if (!nextmoves.isEmpty()) {        
            for(int[] nextmove : nextmoves) {
                Board nextBoard = new Board(mainBoard);
                nextBoard.put(nextmove);

                anew = alphabeta(nextBoard, depth-1, a, 1000, computer);
                if(a < anew) {
                    bestMove = nextmove;
                }

                a = Math.max(a, anew);
            }
            return bestMove;
        }else return null;
    }

    private int alphabeta(Board board, int depth, int a, int b, boolean computer) {
        if(depth == 0 || board.endGame()) {
			if(!computer)
				return board.score(this.human);
            return board.score(this.computer);
        }
        
        ArrayList<int[]> nextmoves = board.legalPositions();
        if(!nextmoves.isEmpty()){
            if(computer) {
                for(int[] nextmove : nextmoves) {
                    Board nextBoard = new Board(mainBoard);
                    nextBoard.put(nextmove);

                    a = Math.max(a, alphabeta(nextBoard, depth-1, a, b, false));
                    if(b<a) {
                        break;
                    }
                }
                return a;
            }else {
                for(int[] nextmove : nextmoves) {
                    Board nextBoard = new Board(mainBoard);
                    nextBoard.put(nextmove);

                    b = Math.min(b, alphabeta(board,depth-1, a, b, true));
                    if(b<a) {
                        break;
                    }
                }
                return b;
            }
        }else if(computer) {
            Board nextBoard = new Board(mainBoard);
            nextBoard.pass();

            a = Math.max(a, alphabeta(nextBoard, depth-1, a, b, false));

            return a;
        }else {
            Board nextBoard = new Board(mainBoard);
            nextBoard.pass();

            b = Math.min(b, alphabeta(board,depth-1, a, b, true));

            return b;
        }
	}
}
