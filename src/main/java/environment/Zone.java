package environment;

import java.util.Set;

public interface Zone {
    public void registerZone(Set<Location> zoneLocations);
    public void deregisterZone(Set<Location> zoneLocations);
    public Set<Location> getZone();
}
