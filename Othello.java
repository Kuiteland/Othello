import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.io.*;
import java.lang.Math;
import java.util.*;

/* Othello klasse is de hoofd klasse om othello te spelen
 * vanuit de main wordt er gespeeld met een Othello object
 * en daarbij behorende andere klassen
 */

public class Othello {
	
	// velden voor het bord en de spelers (computer of mens)
	private Board mainBoard;
	private char human;
	private char computer;
	private boolean compFlag = false;
	
	// constructor voor de klasse
	// set het board en de spelers
	public Othello(){
		mainBoard = new Board();
		human = 'b';
		computer = 'w';
		mainBoard.board[3][3] = 'w';
		mainBoard.board[3][4] = 'b';
		mainBoard.board[4][3] = 'b';
		mainBoard.board[4][4] = 'w';
	}

	/*
		main methode wordt gestart vanuit de console
		op de manier Othello [(depth | c | c depth1 depth2)]
		als er geen argumenten zijn worden standaard waardes gebruikt
		als enkel depth gezet wordt is dat de zoekdiepte van de computer
		als enkel c wordt gekozen speelt de computer voor de mens
		als de andere optie gekozen wordt speelt de computer voor de mens
		en is depth1 de diepte van de computer voor de mense en depth2 voor
		de normale computer
	*/
	public static void main(String[] args){
		//Othello object gemaakt en standaard waardes geinitialiseerd
		Othello game = new Othello();
		int deptHuman = 5;
		int deptComp = 5;
		int wait = 0;
		
		// één argument is of de computer laten spelen voor de mense
		// of zet de zoekdiepte van de computer
		if(args.length==1){
			if(args[0].equals("c")) {
				game.compFlag = true;
			}else {
				deptComp = Integer.parseInt(args[0]);
			}
		}

		// drie argumenten zet een computer voor de mens en de diepte
		// voor die computer en de diepte voor de normale computer
		if(args.length==3 && args[0].equals("c")) {
			game.compFlag = true;
			try{
				deptHuman = Integer.parseInt(args[1]);
				deptComp = Integer.parseInt(args[2]);
				if(deptHuman <= 0 || deptComp <= 0){
					deptHuman = 5;
					deptComp = 5;
				}
			}catch(Exception e){System.out.println(e.toString());}
		}

		// while loop waarin het spel wordt gespeeld
		// door gebruik vaan het Othello object en een bijbehorend Board
		while(!game.mainBoard.endGame()){
			game.mainBoard.passes = 0;
			//if statement die afhandelt als de computer voor de mens speelt
			if(game.compFlag && game.mainBoard.hasTurn == game.human){
				int[] move = game.bestMove(game.mainBoard, deptHuman, game.human);
				if(move != null){
                    System.out.printf("%d,%d: ",move[0],move[1]);
					game.mainBoard.put(move[0],move[1]);
                    try{Thread.sleep(100);}catch(Exception e){System.out.println(e.toString());}
				}else{
					game.mainBoard.pass(true);
				}
				// na de verandering wordt het bord opnieuw getekend 
				game.mainBoard.repaint();
			}
			// optionele wachttijd
			try{Thread.sleep(wait);}
			catch(Exception e){System.out.println(e.toString());}
			
			//if statement waarin de computer speelt
			if(game.mainBoard.hasTurn == game.computer){
				//bepaald de beste move
				int[] move = game.bestMove(game.mainBoard, deptComp, game.computer);
				// als er een move is wordt die gedaan anders wordt er gepassed
				if(move != null){
                    System.out.printf("%d,%d: ",move[0],move[1]);
					game.mainBoard.put(move[0],move[1]);
                    try{Thread.sleep(100);}catch(Exception e){System.out.println(e.toString());}
				}else{
					//pass omdat er geen zetten zijn
					game.mainBoard.pass(true);
				}
				// na de verandering wordt het bord opnieuw getekend 
				game.mainBoard.repaint();				
			}
			
			//optionele wachttijd
			try{Thread.sleep(wait);}
			catch(Exception e){System.out.println(e.toString());}
		}
		
		//print de winnaar zodra het spel is geindigd
		if(game.mainBoard.count(game.computer)>game.mainBoard.count(game.human))
			System.out.println(game.computer);
		else
			System.out.println(game.human);
	}
	
	
	// algoritme om de beste move te vinden, gebaseerd op alpha-beta pruning
	// Dit is de aanroep methode, die vervolgens ab-pruning doet
    private int[] bestMove(Board mainBoard, int depth, char player) {
        int a = -1000;
        int anew;
        int[] bestMove = null;

		//moves worden bepaald
        ArrayList<int[]> nextmoves = mainBoard.legalPositions();

		//als er moves zijn word voor elke move ab-pruning gedaan
		//anders wordt null teruggegeven
        if (!nextmoves.isEmpty()) {        
            for(int[] nextmove : nextmoves) {
                Board nextBoard = new Board(mainBoard);
                nextBoard.put(nextmove);

                anew = alphabeta(nextBoard, depth-1, a, 1000, false, player);
				//als dez move beter is dan een vorige is dit de beste move
                if(anew > a) {
                    bestMove = nextmove;
					a = anew;
                }
            }
            return bestMove;
        }else return null;
    }

	// alpha-beta Pruning, werkt zich recursief depth first tot diepte 0 is bereikt
	// bepaald dan een score
    private int alphabeta(Board board, int depth, int a, int b, boolean computer, char player) {
		// eindspel situatie of een diepte van 0, score wordt teruggegeven
        if (board.endGame())
			return 700;
		if(depth == 0) {
			int score = board.score(player);
			score -= board.score(board.getAnti(player));
			return score;
        }
		
        ArrayList<int[]> nextmoves = board.legalPositions();
		// speler aan zet en de moves bepalen wat wordt uitgevoerd
		// speler wil maximaliseren, tegenspeler minimaal
		// bij geen zetten wordt een pass gedaan en verder ab pruning gedaan
        if(!nextmoves.isEmpty()){
            if(computer) {
                for(int[] nextmove : nextmoves) {
                    Board nextBoard = new Board(board);
                    nextBoard.put(nextmove);

                    a = Math.max(a, alphabeta(nextBoard, depth-1, a, b, false, player));
                    if(b<=a) {
                        break;
                    }
                }
                return a;
            }else {
                for(int[] nextmove : nextmoves) {
                    Board nextBoard = new Board(board);
                    nextBoard.put(nextmove);

                    b = Math.min(b, alphabeta(nextBoard,depth-1, a, b, true, player));
                    if(b<=a) {
                        break;
                    }
                }
                return b;
            }
        }else if(computer) {
            Board nextBoard = new Board(board);
            nextBoard.pass(true);

            a = Math.max(a, alphabeta(nextBoard, depth-1, a, b, false, player));

            return a;
        }else {
            Board nextBoard = new Board(board);
            nextBoard.pass(true);

            b = Math.min(b, alphabeta(nextBoard,depth-1, a, b, true, player));

            return b;
        }
	}
}
