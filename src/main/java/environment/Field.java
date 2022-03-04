package environment;

import actors.Entity;

public abstract class Field {

    public abstract void place(Entity e, Location l);
    public abstract void clearLocation(Location l);
    public abstract Entity getObjectAt(Location l);

}
