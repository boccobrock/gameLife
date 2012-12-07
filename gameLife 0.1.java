package gameLife;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Simulator of Conway's Game of Life in a scalable resizable window.
 * Allows for user input to change which cells are alive and active.
 * 
 * @author Jeffrey Brock
 * @version 0.1
 */
public class Window extends JFrame implements MouseListener, ActionListener, MouseMotionListener
{
	//size is the number of cells in the x and y direction, scale is the size of each cell in pixels
	int size = 50, scale = 15;
	
	//grid to record cells that are alive
	boolean grid[][] = new boolean[size][size];
	boolean running = false, changeTrue = true;
	JButton nextStep, clear, play, stop;
	
	public Window()
	{
		//create grid array empty
		initializeGrid(grid);
		
		//create window
		this.setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize((size*scale)+20,(size*scale)+80);
		setLocation(150,100);
		setTitle("Game of Life");
		getContentPane().setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		//add play button
		play = new JButton("Play");
		play.setBounds(10,5,60,25);
		getContentPane().add(play);
		play.addActionListener(this);
		
		//add stop button
		stop = new JButton("Stop");
		stop.setBounds(80,5,60,25);
		getContentPane().add(stop);
		stop.addActionListener(this);
		
		//add run step button
		nextStep = new JButton("Next Step");
		nextStep.setBounds(150,5,100,25);
		getContentPane().add(nextStep);
		nextStep.addActionListener(this);
		
		//add reset button
		clear = new JButton("Clear Cells");
		clear.setBounds(260,5,100,25);
		getContentPane().add(clear);
		clear.addActionListener(this);
		
		setVisible(true);
	}
	
	public void paint(Graphics g)
	{
		//create grid and check for any filled in cells
		super.paint(g);
		g.setColor(Color.black);
		for(int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				//check if filled in or not
				if(grid[i][j] == true)
					g.fillRect((i*scale)+10, (j*scale)+60, scale, scale);
				else
					g.clearRect((i*scale)+10, (j*scale)+60, scale, scale);
				
				//draw grid square
				if(!running)
					g.drawRect((i*scale)+10, (j*scale)+60, scale, scale);
			}
		}
		
		if(running)
			updateGrid();
	}
	
	public void mousePressed(MouseEvent evet)
	{
		// Uses mousePressed instead of mouseClicked to be quicker and more responsive
		// Catch user input by mouse click to fill in cells
		if(grid[((evet.getX()-10)/scale)][((evet.getY()-60)/scale)])
		{
			changeTrue = false;
			grid[((evet.getX()-10)/scale)][((evet.getY()-60)/scale)] = false;
		}
		else
		{
			changeTrue = true;
			grid[((evet.getX()-10)/scale)][((evet.getY()-60)/scale)] = true;
		}
		repaint();
	}
	public void mouseDragged(MouseEvent evet)
	{
		// Allows dragging to determine cell values
		if(changeTrue == true)
			grid[((evet.getX()-10)/scale)][((evet.getY()-60)/scale)] = true;
		else
			grid[((evet.getX()-10)/scale)][((evet.getY()-60)/scale)] = false;
		repaint();
	}
	
	public void actionPerformed(ActionEvent evet)
	{
		// Add functionality to the clear and nextStep buttons, allowing the cells to reproduce
		if(evet.getSource() == clear)
		{
			initializeGrid(grid);
			repaint();
		}
		if(evet.getSource() == nextStep)
		{
			grid = calculateNext(grid);
			repaint();
		}
		if(evet.getSource() == play)
		{
				running = true;
				updateGrid();
		}
		if(evet.getSource() == stop)
		{
			running = false;
		}
	}
	
	public void updateGrid()
	{
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		grid = calculateNext(grid);
		repaint();

	}
	
	public boolean[][] initializeGrid(boolean[][] grid)
	{
		//simply run through array setting all to false, signifying all empty cells
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				grid[i][j] = false;
			}
		}
		return grid;
	}
	
	public boolean[][] calculateNext(boolean[][] grid)
	{
		//calculates the next step, using 4 rules to determine if a cell is living or not. then return new grid to be displayed.
		//make newGrid, a copy of grid, to be manipulated and save the changes.
		boolean newGrid[][] = new boolean[size][size];
		
		//calculate living neighbors of each cell
		int neighbors = 0;
		for(int i = 0; i < grid.length; i++)
		{
			for(int j = 0; j < grid[0].length; j++)
			{
				//check to see if neighbor exists and then see if it is alive, adds up number of alive neighboring cells
				if(!(i-1<0) && !(j-1<0) && grid[i-1][j-1] == true)
					neighbors++;
				if(!(i-1<0) && grid[i-1][j] == true)
					neighbors++;
				if(!(j-1<0) && grid[i][j-1] == true)
					neighbors++;
				if(!(i-1<0) && !(j+1>(size-1)) && grid[i-1][j+1] == true)
					neighbors++;
				if(!(j-1<0) && !(i+1>(size-1)) && grid[i+1][j-1] == true)
					neighbors++;
				if(!(i+1>(size-1)) && grid[i+1][j] == true)
					neighbors++;
				if(!(j+1>(size-1)) && grid[i][j+1] == true)
					neighbors++;
				if(!(i+1>(size-1)) && !(j+1>(size-1)) && grid[i+1][j+1] == true)
					neighbors++;
				
				//System.out.println(neighbors);
				//first rule, any living cell with less than 2 neighbors dies
				if(grid[i][j] == true && neighbors < 2)
					newGrid[i][j] = false;
				//second rule, any living cell with 2 or 3 neighbors lives on
				if(grid[i][j] == true && neighbors < 4 && neighbors > 1)
					newGrid[i][j] = true;
				//third rule, any living cell with more than 3 neighbors dies
				if(grid[i][j] == true && neighbors > 3)
					newGrid[i][j] = false;
				//fourth rule, any dead cell with 3 neighbors becomes alive
				if(grid[i][j] == false && neighbors == 3)
					newGrid[i][j] = true;
				
				neighbors=0;
			}
		}
		
		return newGrid;
	}
	
	public static void main(String args[])
	{
		//open window with grid
		new Window();
	}
	
	//necessary implemented classes
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) { repaint();}
	public void mouseMoved(MouseEvent arg0) {}
}
