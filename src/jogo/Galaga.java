package jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Galaga extends JPanel implements ActionListener, KeyListener {
    
 
    public int LARGURA = 800;
    public int ALTURA = 600;
    public int jogadorX = 375;
    public int jogadorY = 500;
    public int jogadorLargura = 50;
    public int jogadorAltura = 30;
    public int velocidadeJogador = 7;
    public ArrayList<Tiro> tiros = new ArrayList<>();
    public ArrayList<Inimigo> inimigos = new ArrayList<>();
    public ArrayList<TiroInimigo> tirosInimigos = new ArrayList<>();
    public boolean teclaEsquerda, teclaDireita, teclaEspaco;
    public Timer timer;
    public int pontuacao = 0;
    public int vidas = 3;
    public boolean gameOver = false;
    public Random random = new Random();
    public int contadorSpawn = 0;
    public int contadorTiroInimigo = 0;
    public int intervaloSpawn = 53; 
    public int tipoInimigoAtual = 0;
    public int maxInimigos = 10; 
    public int pontuacaoVitoria = 1000000;
    public boolean vitoria = false;
    private BufferedImage fundoImg;
    private BufferedImage jogadorImg;
    private BufferedImage tiroImg;
    private BufferedImage tiroInimigoImg;
    private BufferedImage inimigo1Img, inimigo2Img, inimigo3Img;
    
    public Galaga() {
        this.setPreferredSize(new Dimension(LARGURA, ALTURA));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        carregarImagens();
        timer = new Timer(16, this); // pra deixar em 60 fps
        timer.start();
    }
    
    private void carregarImagens() {
        try {
            fundoImg = ImageIO.read(new File("images/fundo.png"));
            jogadorImg = ImageIO.read(new File("images/jogador.png"));
            tiroImg = ImageIO.read(new File("images/tiro.png"));
            tiroInimigoImg = ImageIO.read(new File("images/tiro_inimigo.png"));
            inimigo1Img = ImageIO.read(new File("images/inimigo1.png"));
            inimigo2Img = ImageIO.read(new File("images/inimigo2.png"));
            inimigo3Img = ImageIO.read(new File("images/inimigo3.png"));
         } catch (IOException e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }
    }
    
    public void adicionarNovoInimigo() {
        boolean spawnEsquerda = random.nextBoolean(); // 0 = spawn esquerda 1 = spawn direita
        int startX, startY, targetX, targetY;
        if (spawnEsquerda) {
            startX = -50;
            startY = random.nextInt(200) + 50;
        } else {
            startX = LARGURA + 50;
            startY = random.nextInt(200) + 50;
        }
        targetX = random.nextInt(LARGURA - 80) + 40;
        targetY = random.nextInt(150) + 50;
        int tipoChance = random.nextInt(100);
        if (pontuacao < 30000) { 
            tipoInimigoAtual = (tipoChance < 70) ? 1 : 2; // 70% tipo 1, 30% tipo 2
        } else if (pontuacao < 100000) { 
            tipoInimigoAtual = (tipoChance < 40) ? 1 : (tipoChance < 75) ? 2 : 3; // 40% tipo 1, 35% tipo 2, 25% tipo 3
        } else {
            tipoInimigoAtual = (tipoChance < 20) ? 1 : (tipoChance < 55) ? 2 : 3; // 20% tipo 1, 35% tipo 2, 45% tipo 3
        }
        inimigos.add(new Inimigo(startX, startY, tipoInimigoAtual, targetX, targetY));
    }
    
    public int calcularIntervaloSpawn() {
        int intervaloBase = 55;
        int reducao = pontuacao / 8000; 
        return Math.max(12, intervaloBase - reducao);
    }
    
    public int calcularMaxInimigos() {
        int baseMax = 8;
        int aumento = pontuacao / 20000; 
        return Math.min(20, baseMax + aumento);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        desenhar(g);
    }
    
    public void desenhar(Graphics g) {
        if (fundoImg != null) {
            g.drawImage(fundoImg, 0, 0, LARGURA, ALTURA, this);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, Color.BLACK, 0, ALTURA, new Color(0, 0, 50));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, LARGURA, ALTURA);
        }
        if (jogadorImg != null) {
            g.drawImage(jogadorImg, jogadorX, jogadorY, jogadorLargura, jogadorAltura, this);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(jogadorX, jogadorY, jogadorLargura, jogadorAltura);
        }
        for (Tiro tiro : tiros) {
            if (tiroImg != null) {
                g.drawImage(tiroImg, tiro.x, tiro.y, tiro.largura, tiro.altura, this);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(tiro.x, tiro.y, tiro.largura, tiro.altura);
            }
        }
        for (Inimigo inimigo : inimigos) {
            BufferedImage imgInimigo = null;
            switch(inimigo.tipo) {
                case 1:
                    imgInimigo = inimigo1Img;
                    break;
                case 2:
                    imgInimigo = inimigo2Img;
                    break;
                case 3:
                    imgInimigo = inimigo3Img;
                    break;
            }
            
            if (imgInimigo != null) {
                g.drawImage(imgInimigo, inimigo.x, inimigo.y, inimigo.largura, inimigo.altura, this);
            } else {
                switch(inimigo.tipo) {
                    case 1:
                        g.setColor(Color.RED);
                        break;
                    case 2:
                        g.setColor(Color.MAGENTA);
                        break;
                    case 3:
                        g.setColor(Color.CYAN);
                        break;
                    default:
                        g.setColor(Color.RED);
                }
                g.fillRect(inimigo.x, inimigo.y, inimigo.largura, inimigo.altura);
            }
        }
        for (TiroInimigo tiro : tirosInimigos) {
            if (tiroInimigoImg != null) {
                g.drawImage(tiroInimigoImg, tiro.x, tiro.y, tiro.largura, tiro.altura, this);
            } else {
                g.setColor(Color.ORANGE);
                g.fillRect(tiro.x, tiro.y, tiro.largura, tiro.altura);
            }
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Pontuação: " + pontuacao, 10, 30);
        g.drawString("Vidas: " + vidas, 10, 60);
        
        if (gameOver) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, ALTURA/2 - 100, LARGURA, 200);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", LARGURA/2 - 120, ALTURA/2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("Pressione R para reiniciar", LARGURA/2 - 100, ALTURA/2 + 50);
            g.drawString("Pontuação final: " + pontuacao, LARGURA/2 - 80, ALTURA/2 + 80);
        }
        
        if (vitoria) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, ALTURA/2 - 100, LARGURA, 200);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.GREEN);
            g.drawString("VITÓRIA!", LARGURA/2 - 100, ALTURA/2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("PONTUAÇÃO MÁXIMA!", LARGURA/2 - 100, ALTURA/2 + 50);
            g.drawString("Pressione R para reiniciar", LARGURA/2 - 100, ALTURA/2 + 80);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !vitoria) {
            atualizar();
        }
        repaint();
    }
    
    public void atualizar() {
        if (teclaEsquerda && jogadorX > 0) {
            jogadorX -= velocidadeJogador;
        }
        if (teclaDireita && jogadorX < LARGURA - jogadorLargura) {
            jogadorX += velocidadeJogador;
        }
        for (int i = tiros.size() - 1; i >= 0; i--) {
            Tiro tiro = tiros.get(i);
            tiro.y -= tiro.velocidade;
            if (tiro.y < 0) {
                tiros.remove(i);
            }
        }
        for (int i = tirosInimigos.size() - 1; i >= 0; i--) {
            TiroInimigo tiro = tirosInimigos.get(i);
            tiro.y += tiro.velocidade;
            if (tiro.y > ALTURA) {
                tirosInimigos.remove(i);
            }
        }
        if (pontuacao >= pontuacaoVitoria) {
            vitoria = true;
        }
        
        intervaloSpawn = calcularIntervaloSpawn();
        maxInimigos = calcularMaxInimigos();
        
       
        if (inimigos.size() < maxInimigos) {
            contadorSpawn++;
            if (contadorSpawn >= intervaloSpawn) {
                adicionarNovoInimigo();
                contadorSpawn = 0;
            }
        }
        
        for (int i = inimigos.size() - 1; i >= 0; i--) {
            Inimigo inimigo = inimigos.get(i);
            inimigo.atualizar();
        }
        
        boolean mudarDirecao = false;
        for (Inimigo inimigo : inimigos) {
            if (inimigo.emFormacao) {
                int velocidade = 1 + inimigo.tipo; 
                inimigo.x += inimigo.direcao * velocidade;
                if (inimigo.x <= 0 || inimigo.x >= LARGURA - inimigo.largura) {
                    mudarDirecao = true;
                }
            }
        }
        
        if (mudarDirecao) {
            for (Inimigo inimigo : inimigos) {
                if (inimigo.emFormacao) {
                    inimigo.direcao *= -1;
                    inimigo.y += 20 + (inimigo.tipo * 5); 
                }
            }
        }
        
        for (int i = inimigos.size() - 1; i >= 0; i--) {
            Inimigo inimigo = inimigos.get(i);
            if (inimigo.emFormacao && inimigo.y > ALTURA) {
                inimigo.y = -inimigo.altura;
                inimigo.x = random.nextInt(LARGURA - inimigo.largura);
                inimigo.targetX = inimigo.x;
                inimigo.targetY = random.nextInt(150) + 50;
                inimigo.emFormacao = false; 
            }
        }
        
        contadorTiroInimigo++;
        int frequenciaTiro = Math.max(15, 40 - (pontuacao / 4000)); 
        if (contadorTiroInimigo > frequenciaTiro && !inimigos.isEmpty()) {
            Inimigo atirador = inimigos.get(random.nextInt(inimigos.size()));
            int chanceTiro = Math.min(70, 40 + (pontuacao / 8000));
            if (atirador.emFormacao && (atirador.tipo >= 2 || random.nextInt(100) < chanceTiro)) {
                tirosInimigos.add(new TiroInimigo(atirador.x + atirador.largura/2, atirador.y + atirador.altura));
            }
            contadorTiroInimigo = 0;
        }
        
       // colisão tiros do player com inimigos
        for (int i = tiros.size() - 1; i >= 0; i--) {
            Tiro tiro = tiros.get(i);
            for (int j = inimigos.size() - 1; j >= 0; j--) {
                Inimigo inimigo = inimigos.get(j);
                if (colidiu(tiro.x, tiro.y, tiro.largura, tiro.altura,
                           inimigo.x, inimigo.y, inimigo.largura, inimigo.altura)) {
                    tiros.remove(i);
                    inimigos.remove(j);
                    pontuacao += inimigo.tipo * 100;
                    break;
                }
            }
        }
        // colisão tiros inimigos com o player
        for (int i = tirosInimigos.size() - 1; i >= 0; i--) {
            TiroInimigo tiro = tirosInimigos.get(i);
            if (colidiu(tiro.x, tiro.y, tiro.largura, tiro.altura,
                       jogadorX, jogadorY, jogadorLargura, jogadorAltura)) {
                tirosInimigos.remove(i);
                vidas--;
                if (vidas <= 0) {
                    gameOver = true;
                }
                break;
            }
        }
        
        // colisão dos inimigos com o play
        for (Inimigo inimigo : inimigos) {
            if (inimigo.emFormacao && 
                colidiu(inimigo.x, inimigo.y, inimigo.largura, inimigo.altura,
                       jogadorX, jogadorY, jogadorLargura, jogadorAltura)) {
                gameOver = true;
                break;
            }
        }
    }
    
    public boolean colidiu(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }
    
    public void keyPressed(KeyEvent e) {
        int codigo = e.getKeyCode();
        
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            teclaEsquerda = true;
        }
        if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            teclaDireita = true;
        }
        if (codigo == KeyEvent.VK_SPACE && !gameOver) {
            teclaEspaco = true;
            if (tiros.size() < 5) {
                tiros.add(new Tiro(jogadorX + jogadorLargura/2, jogadorY));
            }
        }
        if (codigo == KeyEvent.VK_R && (gameOver || vitoria)) {
            reiniciarJogo();
        }
    }
    
    public void keyReleased(KeyEvent e) {
        int codigo = e.getKeyCode();
        
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            teclaEsquerda = false;
        }
        if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            teclaDireita = false;
        }
        if (codigo == KeyEvent.VK_SPACE) {
            teclaEspaco = false;
        }
    }
    
    public void keyTyped(KeyEvent e) {}
    
    public void reiniciarJogo() {
        jogadorX = 375;
        jogadorY = 500;
        tiros.clear();
        inimigos.clear();
        tirosInimigos.clear();
        pontuacao = 0;
        vidas = 3;
        gameOver = false;
        vitoria = false;
        contadorSpawn = 0;
        intervaloSpawn = 55;
        tipoInimigoAtual = 0;
        maxInimigos = 8;
    }
}