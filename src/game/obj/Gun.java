package game.obj;

import java.util.ArrayList;
import java.util.List;

public class Gun {

    private String name;             // Name of the gun
    private int ammoCapacity;        // Maximum ammo capacity
    private int currentAmmo;         // Current ammo count
    private int reloadTime;          // Time to reload (in ms)
    private boolean isReloading;     // Reload status
    private List<Bullet> bullets;    // Bullets shot by the gun

    public Gun(String name, int ammoCapacity, int reloadTime) {
        this.name = name;
        this.ammoCapacity = ammoCapacity;
        this.currentAmmo = ammoCapacity; // Start with a full clip
        this.reloadTime = reloadTime;
        this.isReloading = false;
        this.bullets = new ArrayList<>();
    }

    // Shoot a bullet
    public Bullet shoot(float x, float y, float angle) {
        if (isReloading) {
            System.out.println(name + " is reloading!");
            return null;
        }

        if (currentAmmo > 0) {
            currentAmmo--;
            System.out.println("Bang! " + name + " fired. Ammo left: " + currentAmmo);
            Bullet bullet = new Bullet(x, y, angle); // Create a new bullet at given position and angle
            bullets.add(bullet);
            return bullet;
        } else {
            System.out.println("Click! Out of ammo.");
            return null;
        }
    }

    // Reload the gun
    public void reload() {
        if (isReloading) {
            System.out.println(name + " is already reloading!");
            return;
        }

        System.out.println("Reloading " + name + "...");
        isReloading = true;

        // Simulate reload time with a separate thread
        new Thread(() -> {
            try {
                Thread.sleep(reloadTime); // Wait for reload time
                currentAmmo = ammoCapacity; // Refill ammo
                isReloading = false;
                System.out.println(name + " reloaded! Ammo: " + currentAmmo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Get the current ammo count
    public int getCurrentAmmo() {
        return currentAmmo;
    }

    // Get all bullets shot by the gun
    public List<Bullet> getBullets() {
        return bullets;
    }

    // Check if the gun is reloading
    public boolean isReloading() {
        return isReloading;
    }

    // Get gun name
    public String getName() {
        return name;
    }
}