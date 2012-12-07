package gameLife;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Simulator of Conway's Game of Life in a scalable resizable window.
 * Allows for user input to change which cells are alive and active.
 * 
 * @author Jeffrey Brock
 * @version 0.2, 7/8/2011
 * 
 * .3 graphics updates, error handling on mouse events, slider to choose play speed.
 */

public class gameLife extends JFrame implements MouseListener, ActionListener, MouseMotionListener, ChangeListener
{
	//size is the number of cells in the x and y direction, scale is the size of each cell in pixels
	int size = 40, scale = 15, speed = 100;
	
	//grid to record cells that are alive
	boolean grid[][] = new boolean[size][size];
	boolean running = false, changeTrue = true;
	JButton nextStep, clear, play, stop;
	JSlider slider;
	JLabel sliderLabel;
	BufferedImage gridImage;
	
	public gameLife()
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
		
		sliderLabel = new JLabel("Play Speed:");
		sliderLabel.setBounds(370,5,70,25);
		getContentPane().add(sliderLabel);
		
		// add slider to determine play speed
		slider = new JSlider(JSlider.HORIZONTAL,20,500, speed);
		slider.setBounds(440, 5, 150, 35);
		slider.setMinorTickSpacing(60);
		slider.setPaintTicks(true);
		getContentPane().add(slider);
		slider.addChangeListener(this);
		
		setVisible(true);
	}
	
	public void paint(Graphics g)
	{
		//create grid and check for any filled in cells
		super.paint(g);
		Graphics2D gridGraph = (Graphics2D) g;
		
		//If not yet created, creates an image of the graph (to avoid remaking gridlines every paint call)
		if(gridImage == null)
		{
			gridImage = (BufferedImage)(this.createImage(this.getWidth(), this.getHeight()));
			gridGraph = gridImage.createGraphics();
			gridGraph.setColor(Color.black);
			for(int i = 0; i < size; i++)
			{
				for (int j = 0; j < size; j++)
				{
					//draw grid square
					gridGraph.drawRect((i*scale), (j*scale), scale, scale);
				}
			}
			
		}
		
		g.drawImage(gridImage, 9, 59, null);
		
		//Fills in cells that are alive, checking with the grid[]
		g.setColor(Color.black);
		for(int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				//check if filled in or not
				if(grid[i][j] == true)
					g.fillRect((i*scale)+11, (j*scale)+61, scale-3, scale-3);
				else
					g.clearRect((i*scale)+11, (j*scale)+61, scale-3, scale-3);
			}
		}
		
		//run next iteration if playing continuously
		if(running)
			updateGrid();
	}
	
	//overrides default update, so that the screen is not cleared with each repaint
	public void update(Graphics g)
	{
		paint(g);
	}
	
	public void mousePressed(MouseEvent evet)
	{
		// Uses mousePressed instead of mouseClicked to be quicker and more responsive
		// Catch user input by mouse click to fill in cells
		// add invalid number error catch
		if(evet.getX()<0 || evet.getY()<0 || evet.getX()>(size*scale)+6 || evet.getY()>(size*scale)+55)
			return;
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
	}
	public void mouseDragged(MouseEvent evet)
	{
		// Allows dragging to determine cell values
		if(evet.getX()<0 || evet.getY()<0 || evet.getX()>(size*scale)+6 || evet.getY()>(size*scale)+55)
			return;
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
	
	public void stateChanged(ChangeEvent evet) {
		// catch slider changes to change speed
		JSlider catcher = (JSlider) evet.getSource();
		speed = catcher.getValue();
	}
	
	public void updateGrid()
	{
		try {
			Thread.sleep(speed);
		} catch (InterruptedException e) {
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
		new gameLife();
	}
	
	//necessary implemented classes
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) { repaint();}
	public void mouseMoved(MouseEvent arg0) {}
}
