package jogo;

class Tiro {
    public int x, y, largura, altura, velocidade;
    
    public Tiro(int x, int y) {
        this.x = x - 2;
        this.y = y;
        this.largura = 4;
        this.altura = 10;
        this.velocidade = 12; 
    }
}
