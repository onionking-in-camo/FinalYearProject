package actors;

import environment.Location;

public abstract class Entity {

    private Location location;

    public Entity(Location location) {
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }
}
