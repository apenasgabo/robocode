package Lol;

import robocode.*;
import java.awt.Color;

public class YASUO extends AdvancedRobot {

    boolean movingForward;
    boolean scanningForEnemies;

    public void run() {
        setColors(Color.black, Color.black, Color.white, Color.white, Color.magenta);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Inicia o radar
        scanningForEnemies = true;
        setTurnRadarRight(Double.POSITIVE_INFINITY);

        while (true) {
            moveAndTurn(); // Movimenta e vira
            execute();
        }
    }

    private void moveAndTurn() {
        setAhead(40000);
        movingForward = true;
        turnRight(90);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Mira e dispara
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
        double bearingFromGun = robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

        setTurnGunRightRadians(bearingFromGun);
        if (Math.abs(bearingFromGun) <= 0.05 && getGunHeat() == 0 && getEnergy() > 1) {
            fire(Math.min(3.0, getEnergy() - 0.1));
        }

        // Ajusta o radar para o alvo atual
        double radarTurn = absoluteBearing - getRadarHeadingRadians();
        setTurnRadarRightRadians(2.0 * robocode.util.Utils.normalRelativeAngle(radarTurn));

        execute();
    }

    public void onHitWall(HitWallEvent e) {
        // Lida com colisões com as paredes
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
        // Evita colisões com outros robôs
        if (e.isMyFault()) {
            if (movingForward) {
                setBack(100);
                movingForward = false;
            } else {
                setAhead(100);
                movingForward = true;
            }
        }
        execute();
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // Se o alvo atual morreu, comece a procurar por outro
        if (e.getName().equals(getName())) {
            scanningForEnemies = true;
            setTurnRadarRight(Double.POSITIVE_INFINITY);
        }
    }
}
