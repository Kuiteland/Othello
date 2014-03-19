import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.io.*;
import java.lang.Math;
import java.util.*;

/* Het Board object bevat 2 gedeeltes over een othello bord
 * een aantal methodes en velden om bewerkingen op het bord 
 * te doen en info over het spel
 * het andere gedeelte creert een GUI voor hte bord en handelt
 * input van de user af.
 */
public class Board extends JFrame{

	//vaste waardes voor dimensies van de GUI en het bord
	private static final int HI = 430;
	private static final int WID = 400;
	private static final int SIDE = 100;
	private static final int ROWS = 8;
	private static final int COL = 8;
	
	//waardes die info over het spel bevatten
	private char parity;
	int passes;
	char hasTurn;
	char noTurn;
	int turn;
	
	//het bord wordt gerepresenteerd als een matrix
	public char[][] board = new char[ROWS][COL];

	// twee constructors
	// Voor het hoofd board wat een frame aanmaakt
	public Board(){
		addMouseListener(new Mouse());
		setTitle("Othello");
		setSize(WID + SIDE, HI);
		setResizable(false);
		setVisible(true);
		getContentPane().setBackground(Color.GREEN);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		passes = 0;
		parity = 'w';
		hasTurn = 'b';
		noTurn = 'w';
		turn = 1;
	}
	
	// contructor voor enkel de informatie van een board
	// kopieert de info van een gegeven board
	public Board(Board inBoard){
		hasTurn = inBoard.hasTurn;
		noTurn = inBoard.noTurn;
		turn = inBoard.turn;
		parity = inBoard.parity;
		passes = inBoard.passes;
		
		for(int i = 0;i<COL;i++){
			for(int j = 0;j<ROWS;j++){
				board[j][i] = inBoard.board[j][i];
			}
		}
	}
	
	// methode die een muisklik in het scherm afhandeld
	// klikken op het bord zijn om een zet te doen
	// klikken in het gebied van het rode vlak doen een pass
	public void mouseClick(int x, int y){
		if(hasTurn == 'b');
		if(x < WID){
			int column = (int)Math.floor(x/50);
			int row = (int)Math.floor((y-30)/50);
			put(row, column);
			System.out.println("pressed: " + row + column);
		}
		else if((x>= WID + SIDE/2 - WID/16 && x <WID + SIDE/2 + WID/16) && (
				y >= 2*HI/3 && y < 2*HI/3 + WID/8) && legalPositions().size() == 0){
			pass(true);
		}
		repaint();
	}
	
	// methode die een zet op het bord omzet naar coordinaten
	// en doorgeeft naar de volgende pass methode
	public void put(int[] move){
		put(move[0],move[1]);
	}
	
	//methode die checked of een zet legaal is en dan de zet doet
	// vervolgens wordt pass aangeroepen om naar de volgende zet te gaan
	public void put(int row, int column){
		if(legalMove(row, column, hasTurn)){
			board[row][column] = hasTurn;
			updateBoard(row, column, hasTurn);
			pass();
		}
	}
	
	// pass methode die de parity flipt als er daadwerkelijk in het spel gepassed word
	// vervolgens word de pass om de beurt te flippen aangeroepen
	public void pass(boolean b){
		if (b==true){
			flipParity();
			passes++;
		}
		pass();
	}
	
	// deze methode zet alle waarden goed voor de volgende beurt en draait de beurt om
	public void pass(){
		char placeholder;
		placeholder = hasTurn;
		hasTurn = noTurn;
		noTurn = placeholder;
		turn++;
	}
	
	/*
	De methods om een legal move te vinden + de bijbehorende check next
	deze lijken erg op updateBoard en updateNext
	*/	
	public boolean legalMove(int row, int column, char turn){
		if(!(board[row][column] == '\u0000'))
			return false;
		for(int i = -1;i<2;i++){
			for(int j = -1;j<2;j++){
				if(checkNext(row, i, column, j, turn, false))
					return true;
			}
		}
		return false;
	}
	
	// werkt recursief alle posities op directies vanaf een bepaald punt af
	public boolean checkNext(int row, int i, int column, int j,
										char turn, boolean flag){
		row += i;
		column += j;
		if(row == -1 || row == 8 || column == -1 || column == 8){
			return false;
		}
		else if(board[row][column] == turn && flag){
			return true;
		}
		else if(board[row][column] == '\u0000' || board[row][column] == turn)
		{
			return false;
		}
		else{
			return checkNext(row, i, column, j, turn, true);
		}
	}
	
	// update de bord matrix
	public void updateBoard(int row, int column, char turn){
		for(int i = -1;i<2;i++){
			for(int j = -1;j<2;j++){
				updateNext(row, i, column, j, turn, false);
			}
		}
	}

	// update elk vakje in de bord matrix om te kijken of deze na een zet gedraaid wordt
	public boolean updateNext(int row, int i, int column, int j,
										char turn, boolean flag){
		row += i;
		column += j;
		if(row == -1 || row == 8 || column == -1 || column == 8){
			return false;
		}
		else if(board[row][column] == turn && flag){
			return true;
		}
		else if(board[row][column] == '\u0000' || board[row][column] == turn)
		{
			return false;
		}
		else{
			if(updateNext(row, i, column, j, turn, true)){
				board[row][column] = turn;
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	// zoekt de mogelijke zetten op het bord voor de speler die aan zet is
	// geeft deze terug in een arraylist
	public ArrayList<int[]> legalPositions(){
		ArrayList<int[]> positions = new ArrayList<int[]>();
		for(int i = 0;i<COL;i++){
			for(int j = 0;j<ROWS;j++){
				if(legalMove( j, i, hasTurn))
					positions.add(new int[]{j,i});
			}
		}
		return positions;
	}
	
	// zet de parity van het spel om
	// ofwel de andere speler krijgt het voordeel
	public void flipParity() {
		parity = getAnti(parity);
	}
	
	// hieronder enkele methods voor scoring en wincondition
	// worden nog niet gebruikt
	public int score(char color){
		int score = 0;	
		
		score += sides(color);
		score += corners(color);
		//deze score is alleen in het begin van het spel belangrijk
		if(turn<=16) {
			middleSquare(color);
		}
		//deze score is minder belangrijk aan het einde van het spel
		if (turn<=40) {
			score += cSquares(color);
			score += xSquares(color);
		}else {
			score += 0.5*cSquares(color);
			score += 0.5*xSquares(color);
		}
		score += stable(color);
		score += moves(color);
		
		if (parity == color) score++;
		
		return score;
	}
	
	// posities op het midden van het bord zijn een betere score
	private int middleSquare(char color) {
		int score = 0;
		return score;
	}
	
	// meer zetten is een betere score
	private int moves(char color) {
		int score = 0;
		int n = legalPositions().size();
		if (hasTurn == color) {
			score += n;
		}else {
			score -= n;
		}
		
		return score;
	}
	
	// methode die het aantal stable discs telt en daar een score aan geeft
	private int stable(char color) {
		int score = 0;
		return score;
	}
	
	// telt het aantal zijvlakken die de speler in bezit heeft en geeft hier
	// een score voor
	private int sides(char color) {
		int score = 0;
		int sidevalue = 4;
		
		for(int i=0; i < 8; i++) {
			if (board[i][0] == color)
				score += sidevalue;
			if (board[i][7] == color)
				score += sidevalue;
			if (board[0][i] == color)
				score += sidevalue;
			if (board[7][i] == color)
				score += sidevalue;
		}
		
		return score;	
	}
	
	//telt de hoeken die de speler heeft en geeft hier een score voor
	private int corners(char color) {
		int score = 0;
		int cornervalue = 20;
		
		if(board[0][0] == color)
			score += cornervalue;
		if(board[7][7] == color)
			score += cornervalue;
		if(board[0][7] == color)
			score += cornervalue;
		if(board[7][0] == color)
			score += cornervalue;
			
		return score;
	}
	
	//telt de vlakken recht naast de hoeken die de speler heeft 
	// en geeft hier een negatieve score aan
	private int cSquares(char color){
		int score = 0;
		int cvalue = -5;
		
		if(!(board[0][0] == color)){
			if(board[0][1] == color)
				score += cvalue;
			if(board[1][0] == color)
				score += cvalue;
		}
		if(!(board[0][7] == color)){
			if(board[0][6] == color)
				score += cvalue;
			if(board[1][7] == color)
				score += cvalue;
		}
		if(!(board[7][0] == color)){
			if(board[7][1] == color)
				score += cvalue;
			if(board[6][0] == color)
				score += cvalue;
		}
		if(!(board[7][7] == color)){
			if(board[7][6] == color)
				score += cvalue;
			if(board[6][7] == color)
				score += cvalue;
		}
		return score;
	}
	
	//telt de vlakken schuin naast de hoeken die de speler heeft 
	// en geeft hier een negatieve score aan
	private int xSquares(char color){
		int score = 0;
		int xvalue = -5;
		
		if(!(board[0][0] == color)){
			if(board[1][1] == color)
				score += xvalue;
		}
		if(!(board[7][0] == color)){
			if(board[6][1] == color)
				score += xvalue;
		}
		if(!(board[0][7] == color)){
			if(board[1][6] == color)
				score += xvalue;
		}
		if(!(board[7][7] == color)){
			if(board[6][6] == color)
				score += xvalue;
		}
		return score;
	}
			
	//telt het aantal stenen die de speler heeft
	public int count(char color){
		int count = 0;
		for(int i = 0;i<COL;i++){
			for(int j = 0;j<ROWS;j++){
				if(board[j][i] == color)
					count++;
			}
		}
		return count;
	}
	
	// checked of er een winsituatie is
	public boolean win(char color){
		char anti = getAnti(color);
		int count1 = count(color);
		int count2 = count(anti);
		if(count1 + count2 == ROWS * COL){
			if(count1>count2)
				return true;
		}
		return false;
	}
	
	// checked of er een eindspelsituatie is
	public boolean endGame(){
		if(passes == 2 || win(hasTurn) || win(noTurn)){
			return true;
		}
		return false;
	}
	
	// geeft de tegengestelde kleur
	public char getAnti(char color){
		if(color == hasTurn)
			return noTurn;
		else
			return hasTurn;
	}
	
	// paint het hele boord met informatie uit het character array
	public void paint(Graphics g){
		super.paint(g);
		int currentLine = 1;
		int x, y;
		int size = WID/8;
		int turnPlaceX = WID + SIDE/2 - size/2;
		int turnPlaceY = HI/3;
		for(int i = 0;i<COL;i++){
			x =(WID/8) * currentLine;
			g.drawLine(x,0, x, HI);
			currentLine++;
		}
		currentLine = 1;
		for(int i = 0;i<ROWS;i++){
			y =((HI-30)/8) * currentLine + 30;
			g.drawLine(0,y, WID, y);
			currentLine++;
		}
		
		if(hasTurn == 'b'){
			g.setColor(Color.BLACK);
			g.fillOval(turnPlaceX,turnPlaceY,size,size);
		}
		else{
			g.setColor(Color.WHITE);
			g.fillOval(turnPlaceX,turnPlaceY,size,size);
		}
		
		g.setColor(Color.RED);
		g.fillRect(turnPlaceX,turnPlaceY*2,size,size);
		
		for(int i = 0;i<COL;i++){
			for(int j = 0;j<ROWS;j++){
				if(board[j][i] == 'b'){
					x = size * i;
					y = size * j + 30;
					g.setColor(Color.BLACK);
					g.fillOval(x+5,y+5,size-10,size-10);
				}
				if(board[j][i] == 'w'){
					x = size * i;
					y = size * j + 30;
					g.setColor(Color.WHITE);
					g.fillOval(x+5,y+5,size-10,size-10);
				}
			}
		}
		g.setColor(Color.BLACK);
	}
	
	// mouse adapter haalt de x en y coördinaten van een klik
	public class Mouse extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e){
			mouseClick(e.getX(),e.getY());
		}
	}
}
