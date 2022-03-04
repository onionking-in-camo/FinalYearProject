package actors;

import environment.Location;

public abstract class EntityGenerator<Entity> {

    public Entity generate(Location l) {
        return generateEntity(l);
    }

    protected abstract Entity generateEntity(Location l);
}
