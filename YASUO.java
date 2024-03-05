package Lol;
import robocode.*;
import java.awt.Color;

public class YASUO extends AdvancedRobot {
    
    boolean movingForward;

    public void run() {
        // Configurações iniciais
		setColors (Color.black,Color.black,Color.white,Color.white,Color.magenta);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Gira o radar para procurar adversários
		setTurnRadarRight(Double.POSITIVE_INFINITY); 

        // Loop principal
        while (true) {
            setAhead(40000); // Move para frente
            movingForward = true;
            execute(); // Executa ações pendentes
            turnRight(90); // Gira 90 graus para a direita
            execute(); // Executa ações pendentes
			turnGunRight(30);
			execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula o ângulo para mirar no oponente
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double bearingFromGun = robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

        // Se a diferença do ângulo entre o canhão e o oponente for pequena, atira
        if (Math.abs(bearingFromGun) <= 0.05) {
            setTurnGunRightRadians(bearingFromGun); // Mira
            if (getGunHeat() == 0 && getEnergy() > 1) { // Verifica se o canhão pode disparar
                fire(Math.min(3.0, getEnergy() - 0.1)); // Atira com a força máxima possível
            }
        } else {
            setTurnGunRightRadians(bearingFromGun); // Mira
        }
		

        // Mantém o radar girando para procurar outros adversários
        double radarTurn = absoluteBearing - getRadarHeadingRadians();
        setTurnRadarRightRadians(2.0 * robocode.util.Utils.normalRelativeAngle(radarTurn));

        // Executa ações pendentes
        execute();
    }

    public void onHitWall(HitWallEvent e) {
        // Muda de direção quando atinge uma parede
        if (movingForward) {
            setBack(100);
            movingForward = false;
        } else {
            setAhead(100);
            movingForward = true;
        }
        execute();
    }

    public void onHitRobot(HitRobotEvent e) {
        // Recua e gira para evitar colisões com outros robôs
        if (e.isMyFault()) {
            setBack(100);
        }
    }
}

