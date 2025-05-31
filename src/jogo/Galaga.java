package jogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
    public boolean telaInicio = true;
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
        timer = new Timer(16, this); 
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
        boolean spawnEsquerda = random.nextBoolean(); 
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
        if (pontuacao < 3000) { 
            tipoInimigoAtual = (tipoChance < 70) ? 1 : 2;
        } else if (pontuacao < 15000) { 
            tipoInimigoAtual = (tipoChance < 40) ? 1 : (tipoChance < 75) ? 2 : 3; 
        } else {
            tipoInimigoAtual = (tipoChance < 20) ? 1 : (tipoChance < 55) ? 2 : 3;
        }
        inimigos.add(new Inimigo(startX, startY, tipoInimigoAtual, targetX, targetY));
    }
    
    public int calcularIntervaloSpawn() {
        int intervaloBase = 30;
        int reducao = pontuacao / 2000; 
        return Math.max(12, intervaloBase - reducao);
    }
    
    public int calcularMaxInimigos() {
        int baseMax = 18;
        int aumento = pontuacao / 2000; 
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
        
        if (telaInicio) {
            g.setFont(new Font("Arial", Font.BOLD, 72));
            g.setColor(Color.YELLOW);
            FontMetrics fm = g.getFontMetrics();
            String titulo = "GALAGA";
            int x = (LARGURA - fm.stringWidth(titulo)) / 2;
            int y = ALTURA / 2 - 50;
            g.drawString(titulo, x, y);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.WHITE);
            fm = g.getFontMetrics();
            String instrucao = "PRESSIONE ESPAÇO PARA INICIAR";
            x = (LARGURA - fm.stringWidth(instrucao)) / 2;
            y = ALTURA / 2 + 50;
            g.drawString(instrucao, x, y);
            return;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String scoreText = "SCORE: " + pontuacao;
        FontMetrics fm = g2d.getFontMetrics();
        int scoreX = (LARGURA - fm.stringWidth(scoreText)) / 2;
        int scoreY = 30;
        g2d.setColor(Color.BLACK);
        g2d.drawString(scoreText, scoreX-1, scoreY-1);
        g2d.drawString(scoreText, scoreX+1, scoreY-1);
        g2d.drawString(scoreText, scoreX-1, scoreY+1);
        g2d.drawString(scoreText, scoreX+1, scoreY+1);
        g2d.setColor(Color.RED);
        g2d.drawString(scoreText, scoreX, scoreY);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("VIDAS:", 10, ALTURA - 60);
        
        for (int i = 0; i < vidas; i++) {
            int navesX = 70 + (i * 35);
            int navesY = ALTURA - 75;
            if (jogadorImg != null) {
                g.drawImage(jogadorImg, navesX, navesY, 25, 18, this);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(navesX, navesY, 25, 18);
            }
        }
        
        if (gameOver) {
            Graphics2D g2dOver = (Graphics2D) g;
            g2dOver.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2dOver.setColor(Color.BLACK);
            g2dOver.fillRect(0, ALTURA/2 - 100, LARGURA, 200);
            g2dOver.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", LARGURA/2 - 120, ALTURA/2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("Pontuação final: " + pontuacao, LARGURA/2 - 80, ALTURA/2 + 50);
            g.drawString("Pressione ESPAÇO para continuar", LARGURA/2 - 120, ALTURA/2 + 80);
        }
        
        if (vitoria) {
            Graphics2D g2dVit = (Graphics2D) g;
            g2dVit.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2dVit.setColor(Color.BLACK);
            g2dVit.fillRect(0, ALTURA/2 - 100, LARGURA, 200);
            g2dVit.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.GREEN);
            g.drawString("VITÓRIA!", LARGURA/2 - 100, ALTURA/2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("PONTUAÇÃO MÁXIMA!", LARGURA/2 - 100, ALTURA/2 + 50);
            g.drawString("Pressione ESPAÇO para continuar", LARGURA/2 - 120, ALTURA/2 + 80);
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !vitoria && !telaInicio) {
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
            if (inimigo.emFormacao && !inimigo.fazendoDive) {
                int velocidade = 1 + inimigo.tipo; 
                inimigo.x += inimigo.direcao * velocidade;
                if (inimigo.x <= 0 || inimigo.x >= LARGURA - inimigo.largura) {
                    mudarDirecao = true;
                }
            }
        }
        
        if (mudarDirecao) {
            for (Inimigo inimigo : inimigos) {
                if (inimigo.emFormacao && !inimigo.fazendoDive) {
                    inimigo.direcao *= -1;
                }
            }
        }
        
        for (Inimigo inimigo : inimigos) {
            if (inimigo.emFormacao && !inimigo.fazendoDive && random.nextInt(200) < 2) { // 1.5% chance por frame (muito mais frequente)
                inimigo.iniciarDive(jogadorX + jogadorLargura/2);
            }
            if (inimigo.fazendoDive) {
                inimigo.atualizarDive();
            }
        }
        
        contadorTiroInimigo++;
        int frequenciaTiro = Math.max(5, 15 - (pontuacao / 3000)); // Muito mais frequente
        if (contadorTiroInimigo > frequenciaTiro && !inimigos.isEmpty()) {
            Inimigo atirador = inimigos.get(random.nextInt(inimigos.size()));
            int chanceTiro = Math.min(90, 70 + (pontuacao / 5000)); // Chance maior de tiro
            if (atirador.emFormacao && !atirador.fazendoDive && (atirador.tipo >= 1 || random.nextInt(100) < chanceTiro)) {
                tirosInimigos.add(new TiroInimigo(atirador.x + atirador.largura/2, atirador.y + atirador.altura));
            }
            contadorTiroInimigo = 0;
        }
        
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
        if (codigo == KeyEvent.VK_SPACE) {
            if (telaInicio) {
                telaInicio = false;
            } else if (gameOver || vitoria) {
                telaInicio = true;
                reiniciarJogo();
            } else {
                teclaEspaco = true;
                if (tiros.size() < 5) {
                    tiros.add(new Tiro(jogadorX + jogadorLargura/2, jogadorY));
                }
            }
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