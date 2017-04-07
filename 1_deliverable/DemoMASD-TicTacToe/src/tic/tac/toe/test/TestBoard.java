package tic.tac.toe.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import tic.tac.toe.logic.Board;
import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.TicTacToeException;

/**
 * Tests for Board class. Uses JUnit framework.
 * 
 * @author sprkrd
 *
 */
public class TestBoard {
	
	private static Board createExampleBoard1() throws TicTacToeException {
		Board board = new Board();
		board.getCell(0, 0).setContent('X');
		board.getCell(2, 0).setContent('O');
		board.getCell(1, 1).setContent('X');
		board.getCell(0, 2).setContent('O');
		board.getCell(2, 2).setContent('X');
		return board;
	}
	
	private static Board createExampleBoard2() throws TicTacToeException {
		Board board = new Board();
		board.getCell(0, 0).setContent('O');
		board.getCell(0, 1).setContent('X');
		board.getCell(1, 0).setContent('O');
		board.getCell(0, 2).setContent('X');
		board.getCell(2, 0).setContent('O');
		return board;
	}
	
	private static Board createExampleBoard3() throws TicTacToeException {
		Board board = new Board();
		board.getCell(0, 0).setContent('O');
		board.getCell(1, 0).setContent('X');
		board.getCell(0, 1).setContent('O');
		board.getCell(1, 2).setContent('X');
		board.getCell(0, 2).setContent('O');
		return board;
	}
	
	@Test
	public void testClone() throws TicTacToeException {
		Board board1 = new Board();
		Board board2 = null;
		try {board2 = (Board)board1.clone();}
		catch (CloneNotSupportedException e) {/*Not gonna happen*/}
		board1.getCell(0, 0).setContent('X');
		board2.getCell(0, 0).setContent('O');
		assertEquals(board1.getCell(0, 0).getContent(), 'X');
		assertEquals(board2.getCell(0, 0).getContent(), 'O');
	}
	
	@Test
	public void testCountDiagonal() throws TicTacToeException {
		Board board = createExampleBoard1();
		assertEquals(board.countDiagonal('X', true), 3);
		assertEquals(board.countDiagonal('X', false), 1);
		assertEquals(board.countDiagonal('O', true), 0);
		assertEquals(board.countDiagonal('O', false), 2);
	}
	
	@Test
	public void testCountVertical() throws TicTacToeException {
		Board board = createExampleBoard2();
		assertEquals(board.countVertical('X', 0), 0);
		assertEquals(board.countVertical('O', 0), 3);
	}
	
	@Test
	public void testCountHorizontal() throws TicTacToeException {
		Board board = createExampleBoard3();
		assertEquals(board.countHorizontal('X', 0), 0);
		assertEquals(board.countHorizontal('O', 0), 3);
	}
	
	@Test
	public void testWinners() throws TicTacToeException {
		assertEquals(createExampleBoard1().getWinner(), 'X');
		assertEquals(createExampleBoard2().getWinner(), 'O');
		assertEquals(createExampleBoard3().getWinner(), 'O');
	}
	
	@Test
	public void testAvailableCells() throws TicTacToeException {
		Board board = createExampleBoard1();
		board.getCell(1, 1).reset();
		List<Cell> available = board.getEmptyCells();
		assertEquals(available.size(), 5);
		assertTrue(available.contains(new Cell(0, 1)));
		assertTrue(available.contains(new Cell(1, 0)));
		assertTrue(available.contains(new Cell(1, 1)));
		assertTrue(available.contains(new Cell(1, 2)));
		assertTrue(available.contains(new Cell(2, 1)));
	}
	
	@Test
	public void testWinningMove() throws TicTacToeException {
		Board board = createExampleBoard1();
		board.getCell(1, 1).reset();
		assertTrue(board.isWinningMove('X', board.getCell(1, 1)));
		assertFalse(board.isWinningMove('X', board.getCell(2, 1)));
	}
	
	@Test
	public void testToString() throws TicTacToeException {
		Board board = createExampleBoard1();
		assertEquals(board.toString(), "X| |O\n-----\n |X| \n-----\nO| |X");
	}

}
