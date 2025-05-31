package jogo;

class TiroInimigo {
    public int x, y, largura, altura, velocidade;
    
    public TiroInimigo(int x, int y) {
        this.x = x - 2;
        this.y = y;
        this.largura = 3;
        this.altura = 8;
        this.velocidade = 10;
    }
}
