import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class RacingCarGame extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private int carX = 250, carY = 500;
    private int speed = 8;
    private int roadWidth = 300;
    private int score = 0;
    private int highScore = 0;
    private boolean gameRunning = false;
    private boolean gameOver = false;
    private ArrayList<java.awt.Rectangle> obstacles = new ArrayList<>();
    private ArrayList<Integer> roadMarks = new ArrayList<>();
    private Random random = new Random();

    public RacingCarGame() {
        timer = new Timer(20, this);
        setPreferredSize(new Dimension(600, 700));
        setFocusable(true);
        addKeyListener(this);
        initGame();
    }

    private void initGame() {
        carX = 250;
        carY = 500;
        score = 0;
        obstacles.clear();
        roadMarks.clear();
        for(int i = 0; i < 5; i++) {
            roadMarks.add(i * 150);
        }
        gameRunning = true;
        gameOver = false;
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw road
        g.setColor(Color.DARK_GRAY);
        g.fillRect((getWidth()-roadWidth)/2, 0, roadWidth, getHeight());
        
        // Draw road markings
        g.setColor(Color.YELLOW);
        for(int mark : roadMarks) {
            g.fillRect(getWidth()/2 - 5, mark, 10, 50);
        }
        
        // Draw car
        drawCar(g, carX, carY);
        
        // Draw obstacles
        g.setColor(Color.BLUE);
        for(java.awt.Rectangle obstacle : obstacles) {
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }
        
        // Draw UI elements
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("High Score: " + highScore, 20, 60);
        
        if(gameOver) {
            g.setColor(new Color(255, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", 200, 300);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to restart", 210, 350);
        }
    }

    private void drawCar(Graphics g, int x, int y) {
        // Car body
        g.setColor(Color.RED);
        g.fillRect(x, y, 50, 30);
        g.fillRect(x+10, y-20, 30, 20);
        
        // Wheels
        g.setColor(Color.BLACK);
        g.fillOval(x-5, y+20, 20, 20);
        g.fillOval(x+35, y+20, 20, 20);
        
        // Windows
        g.setColor(Color.CYAN);
        g.fillRect(x+15, y-15, 20, 10);
    }

    public void actionPerformed(ActionEvent e) {
        if(gameRunning) {
            // Move road markings
            for(int i = 0; i < roadMarks.size(); i++) {
                roadMarks.set(i, roadMarks.get(i) + speed);
                if(roadMarks.get(i) > getHeight()) {
                    roadMarks.remove(i);
                    roadMarks.add(0, -50);
                }
            }
            
            // Generate obstacles
            if(random.nextInt(100) < 3) {
                int obstacleWidth = 50 + random.nextInt(50);
                int obstacleX = (getWidth()-roadWidth)/2 + random.nextInt(roadWidth - obstacleWidth);
                obstacles.add(new java.awt.Rectangle(obstacleX, -50, obstacleWidth, 30));
            }
            
            // Move obstacles
            for(java.awt.Rectangle obstacle : obstacles) {
                obstacle.y += speed;
                if(obstacle.y > getHeight()) {
                    obstacles.remove(obstacle);
                    break;
                }
                
                // Collision detection
                if(carX < obstacle.x + obstacle.width &&
                   carX + 50 > obstacle.x &&
                   carY < obstacle.y + obstacle.height &&
                   carY + 30 > obstacle.y) {
                    gameOver();
                }
            }
            
            score++;
            if(score > highScore) highScore = score;
        }
        repaint();
    }

    private void gameOver() {
        gameRunning = false;
        gameOver = true;
        timer.stop();
    }

    public void keyPressed(KeyEvent e) {
        if(gameRunning) {
            if(e.getKeyCode() == KeyEvent.VK_LEFT && carX > (getWidth()-roadWidth)/2) {
                carX -= speed * 2;
            }
            if(e.getKeyCode() == KeyEvent.VK_RIGHT && carX < (getWidth()+roadWidth)/2 - 50) {
                carX += speed * 2;
            }
        }
        
        if(gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            initGame();
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Racing Car Game");
        RacingCarGame game = new RacingCarGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}