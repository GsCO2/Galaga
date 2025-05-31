package jogo;

class Inimigo {
    public int x, y, largura, altura, direcao, tipo;
    public int targetX, targetY;
    public boolean emFormacao = false;
    public double velocidadeMovimento = 3.5;
    
    public boolean fazendoDive = false;
    public int posicaoOriginalX, posicaoOriginalY;
    public int faseDiv = 0;
    public int contadorDive = 0;
    public int targetDiveX;
    
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
        if (!emFormacao && !fazendoDive) {
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
    
    public void iniciarDive(int jogadorX) {
        fazendoDive = true;
        posicaoOriginalX = x;
        posicaoOriginalY = y;
        targetDiveX = jogadorX;
        faseDiv = 0;
        contadorDive = 0;
    }
    
    public void atualizarDive() {
        contadorDive++;
        double velocidadeDive = 8.0 + (tipo * 1.5); 
        
        if (faseDiv == 0) { 
            double dx = targetDiveX - x;
            double dy = 450 - y; 
            double distancia = Math.sqrt(dx * dx + dy * dy);
            
            if (distancia > velocidadeDive) {
                x += (dx / distancia) * velocidadeDive;
                y += (dy / distancia) * velocidadeDive;
            } else {
                faseDiv = 1; 
            }
            
            if (contadorDive > 80 || y > 480) { 
                faseDiv = 1;
            }
        } else { 
            double dx = posicaoOriginalX - x;
            double dy = posicaoOriginalY - y;
            double distancia = Math.sqrt(dx * dx + dy * dy);
            
            if (distancia > velocidadeDive * 1.2) { 
                x += (dx / distancia) * velocidadeDive * 1.2;
                y += (dy / distancia) * velocidadeDive * 1.2;
            } else {
                x = posicaoOriginalX;
                y = posicaoOriginalY;
                fazendoDive = false;
                faseDiv = 0;
                contadorDive = 0;
            }
        }
    }
}