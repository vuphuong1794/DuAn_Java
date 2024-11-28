package game.obj;

import game.obj.sound.sound;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import javax.swing.ImageIcon;

public class Gun {

    private String name;             // Name of the gun
    private int ammoCapacity;        // Maximum ammo capacity
    private int currentAmmo;         // Current ammo count
    private int reloadTime;          // Time to reload (in ms)
    private boolean isReloading;     // Reload status
    private List<Bullet> bullets;    // Bullets shot by the gun
    private final Image image;       // Gun image
    private final Area gunShape;     // Gun shape for collision or rendering
    public static final double Gun_size = 1;
    private sound Sound;

    // Constructor
    public Gun(String name, int ammoCapacity, int reloadTime, float playerX, float playerY) {
        this.name = name;
        this.ammoCapacity = ammoCapacity;
        this.currentAmmo = ammoCapacity; // Start with a full clip
        this.reloadTime = reloadTime;
        this.isReloading = false;
        this.bullets = new ArrayList<>();
        this.image = loadImageByName(name); // Dynamically load the gun image based on the gun's name
        this.Sound = new sound();
        // Define the gun shape (example shape, scaled dynamically)
        double scaleFactor = Gun_size / 30; // Scale relative to the reference size (10.0)
        Path2D p = new Path2D.Double();
        p.moveTo(playerX, playerY);
        p.lineTo(30 * scaleFactor, -10 * scaleFactor);
        p.lineTo(60 * scaleFactor, 0);
        p.lineTo(30 * scaleFactor, 10 * scaleFactor);
        p.closePath();
        this.gunShape = new Area(p);
    }

    // Method to dynamically load an image based on the gun's name
    private Image loadImageByName(String gunName) {
        String path = ""; // Initialize the path as empty

        // Choose the image path based on the gun's name
        switch (gunName.toLowerCase()) {
            case "rifle":
                path = "/game/image/rifle.png";
                break;
            case "sniper":
                path = "/game/image/sniper.png";
                break;
            case "grenade":
                System.out.println("Gun image not found for name: " + gunName + ". Using default image.");
                path = "/game/image/grenade.png";
                break;
            default:
                System.out.println("Gun image not found for name: " + gunName + ". Using default image.");
                path = "/game/image/pistol.png"; // Fallback to a default gun image
                break;
        }
        // Attempt to load the image
        try {
            return new ImageIcon(getClass().getResource(path)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if the image could not be loaded
        }

    }

    // Method
    // to draw the gun
    public void drawGun(Graphics2D g2, double x, double y, float angle,String name) {
        if (image == null) {
            System.out.println("Gun image not loaded!");
            return;
        }

        AffineTransform oldTransform = g2.getTransform();

        // Move to the player's position and rotate the gun
        g2.translate(x, y); // Position at player's location
        if (name.equals("pistol")) {
            g2.rotate(Math.toRadians(angle + 90)); // Rotate gun based on player's angle
            g2.translate(5, -40);
        }
        else if (name.equals("rifle")) {
            g2.rotate(Math.toRadians(angle - 90)); // Rotate gun based on player's angle
            g2.translate(-75, 40);
        }
        else if (name.equals("sniper")) {
            g2.rotate(Math.toRadians(angle -90)); // Rotate gun based on player's angle
            g2.translate(-70, 40);
        }
        else if (name.equals("grenade")) {
            g2.rotate(Math.toRadians(angle - 90)); // Rotate gun based on player's angle
            g2.translate(-70, 40);
        }

        double scaleFactor = Gun_size / 40.0; // Scale relative to reference size (10.0)
        g2.scale(scaleFactor, scaleFactor);

        // Draw the gun image
        int gunWidth = image.getWidth(null);
        int gunHeight = image.getHeight(null);

        // Position the gun relative to the player
        int gunOffsetX = 30; // Adjust offset to place the gun in front of the player
        g2.drawImage(image, gunOffsetX, -gunHeight / 2, null); // Center vertically

        // Restore the original transformation
        g2.setTransform(oldTransform);
    }

    // Method to shoot a bullet
    public Bullet shoot(float x, float y, float angle, float offsetX, float offsetY, String nameGun) {
        if (isReloading) {
            System.out.println(name + " is reloading!");
            return null;
        }

        if (currentAmmo > 0) {
            currentAmmo--;
            System.out.println("Bang! " + name + " fired. Ammo left: " + currentAmmo);
            Sound.soundShoot();
            // Create a new bullet at the gun's position and angle
            Bullet bullet = new Bullet(x, y, angle,offsetX,offsetY,nameGun);
            return bullet;
        } else {
            System.out.println("Click! Out of ammo.");
            return null;
        }
    }

    // Method to reload the gun
    public void reload() {
        if (isReloading) {
            System.out.println(name + " is already reloading!");
            return;
        }

        System.out.println("Reloading " + name + "...");
        isReloading = true;

        // Simulate reload time using a separate thread
        new Thread(() -> {
            try {
                Thread.sleep(reloadTime); // Wait for the reload time
                currentAmmo = ammoCapacity; // Refill ammo
                isReloading = false;
                System.out.println(name + " reloaded! Ammo: " + currentAmmo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Getters and utility methods

    /**
     * Get the current ammo count.
     */
    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public void addCurrentAmmo(int ammo) {
        currentAmmo += ammo;
    }

    /**
     * Get the bullets shot by the gun.
     */
    public List<Bullet> getBullets() {
        return bullets;
    }

    /**
     * Check if the gun is currently reloading.
     */
    public boolean isReloading() {
        return isReloading;
    }

    /**
     * Get the name of the gun.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the gun's shape for collision detection.
     */
    public Area getGunShape() {
        return gunShape;
    }
}