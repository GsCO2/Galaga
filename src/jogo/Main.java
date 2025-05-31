package jogo;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
        JFrame frame = new JFrame("Galaga");
        Galaga galaga = new Galaga();
        
        frame.add(galaga);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        galaga.requestFocusInWindow();
	}
	
	
}
