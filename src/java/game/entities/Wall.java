package game.entities;
import java.awt.*;

//Classe pour les murs
public class Wall extends StaticEntity {
    public Wall(int xPos, int yPos) {
        super(8, xPos, yPos);
    }
    
    @Override
    public void render(Graphics2D g) {
        // 어두운 파란 벽
        g.setColor(new Color(0, 0, 150));
        g.fillRect(this.xPos, this.yPos, this.size, this.size);
    }
}
