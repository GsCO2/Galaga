package jogo;

class Inimigo {
    public int x, y, largura, altura, direcao, tipo;
    public int targetX, targetY;
    public boolean emFormacao = false;
    public double velocidadeMovimento = 3.5;

    public Inimigo(int x, int y, int tipo, int targetX, int targetY) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.targetX = targetX;
        this.targetY = targetY;
        this.direcao = 1;

        switch(tipo) {
            case 1: 
                this.largura = 30;
                this.altura = 25;
                break;
            case 2:
                this.largura = 35;
                this.altura = 30;
                break;
            case 3: 
                this.largura = 45;
                this.altura = 35;
                break;
            default:
                this.largura = 30;
                this.altura = 25;
        }
        
        this.velocidadeMovimento = 4.0 + (tipo * 0.5); 
    }

    public void atualizar() {
        if (!emFormacao) {
            double dx = targetX - x;
            double dy = targetY - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia > velocidadeMovimento) {
                x += (dx / distancia) * velocidadeMovimento;
                y += (dy / distancia) * velocidadeMovimento;
            } else {
                x = targetX;
                y = targetY;
                emFormacao = true;
            }
        }
    }
}