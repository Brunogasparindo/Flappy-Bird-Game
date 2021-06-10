package academy.learnprogramming;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird implements ActionListener, MouseListener, KeyListener {

    public static FlappyBird flappyBird;
    public final int WIDTH = 800, HEIGHT = 800, BOTTOM = 120, GRASS_HEIGHT = 20, BIRD_SIZE = 20;
    public final int SPEED = 10, JUMP = 10;
    public Renderer renderer;

    public Rectangle bird;
    public ArrayList<Rectangle> columns;
    public boolean gameOver, started;
    public int ticks, yMotion, score, highestScore;
    public Random random;

    // Constructor
    public FlappyBird () {
        JFrame jFrame = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        random = new Random();
        jFrame.add(renderer);

        jFrame.setTitle("Flappy Bird");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.addMouseListener(this);
        jFrame.addKeyListener(this);
        jFrame.setResizable(false);
        jFrame.setVisible(true);

        columns = new ArrayList<>();
        highestScore = 0;
        startGame();

        timer.start();
    }

    public void paintColumn(Graphics g, Rectangle column) {

        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void addColumn(boolean start) {
        int space = 300; // space between the up and down part of the column already considering the 120 bottom height (at the end the space will be only 180)
        int width = 100; // width of the column itself
        int height = 50 + random.nextInt(300); // height of the column itself (maximum 350)

        if (start)
        {
            //by the second time that addColumn() is called, the column.size() is equals to 2
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - BOTTOM - height, width, height)); // column's bottom
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space)); // column's top
        }
        else
        {
            // columns.get(columns.size() - 1).x    >>>> gets the last element's x and adds 600
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - BOTTOM - height, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }

    }

    private void startGame() {
        bird = new Rectangle(WIDTH/2 - 10, HEIGHT/2 - 10, BIRD_SIZE, BIRD_SIZE);
        columns.clear();
        yMotion = 0;
        score = 0;

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        gameOver = false;
    }

    public void handleMouseClickedOrSpacePressed() {
        if (gameOver) {
            startGame();
            return;
        }

        if (!started) {
            started = true;
            return;
        }

        if (yMotion > 0) {
            yMotion = 0;
        }

        // JUMP
        yMotion -= JUMP;

    }

    // implementation of the INTERFACE ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {
        ticks++;

        if (started) {
            // Move the columns to the left
            for (Rectangle column : columns) {
                column.x -= SPEED;
            }

            // Move the bird down
            if (ticks % 2 == 0 && yMotion <15) {
                yMotion += 2;
            }
            bird.y += yMotion;

            // remove old and add new column
            for (int i=0; i< columns.size(); i++) {
                Rectangle column = columns.get(i);
                if (column.x + column.width < 0) {
                    columns.remove(column); // remove old
                    addColumn(false); // add new
                }
            }

            // check collision + check score
            for (Rectangle column : columns) {

                // check score
                if (column.y == 0 && bird.x == (column.x + column.width)) {
                    score++;
                    if (highestScore < score) {
                        highestScore++;
                    }
                }

                // check collision [1/2]
                if (column.intersects(bird)) {
                    gameOver = true;

                    // column moves the bird if the bird dies
                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;
                    }
                }
            }
            // check collision [2/2]
            if ((bird.y + bird.height) > (HEIGHT - BOTTOM) || bird.y < 0) {
                bird.y = HEIGHT - BOTTOM - bird.height; // leave the bird on the floor when it dies
                gameOver = true;
            }
        }

        renderer.repaint(); // CALL * 1 calling
    }

    // CALL * 1 called
    public void repaint(Graphics g) {
        // fill background color
        g.setColor(Color.cyan);
        g.fillRect(0,0,WIDTH, HEIGHT);

        // fill background color (floor)
        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT-BOTTOM, WIDTH, BOTTOM);

        // fill background color (grass)
        g.setColor(Color.green);
        g.fillRect(0, HEIGHT-BOTTOM, WIDTH, GRASS_HEIGHT);

        // draw bird
        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        // draw columns
        for (Rectangle column : columns) {
            paintColumn(g, column);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 100));

        // Start
        if (!started) {
            g.drawString("Click to start", 75, HEIGHT/2 - 50);
        }

        // Game Over
        if (gameOver) {
            g.drawString("Game Over!", 100, HEIGHT/2 - 50);
        }

        // Score
        if (started && !gameOver) {
            g.setFont(new Font("Arial", Font.PLAIN, 25));
            g.drawString("Highest Score: " + highestScore, WIDTH/2 + 150, 40);

            g.setFont(new Font("Arial", Font.PLAIN, 25));
            g.drawString("Current Score: " + score, WIDTH/2 - 370, 40);
        }

    }

    public static void main(String[] args) {
        flappyBird = new FlappyBird();
    }

    // implementation of the INTERFACE MouseListener
    @Override
    public void mouseClicked(MouseEvent e) {
        handleMouseClickedOrSpacePressed();
    }

    // implementation of the INTERFACE KeyListener
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            handleMouseClickedOrSpacePressed();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }
}