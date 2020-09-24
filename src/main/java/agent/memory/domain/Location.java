package agent.memory.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Location extends Entity {

	private String path;
	//Type: local, remote, docker etc
    private String type;
    private int port;
    //Fix for local locations: say if it is a sub-location
    // i.e. a different port on the same location as an application
    private boolean sublocation;

    public Location() {
    	// Empty constructor required as of Neo4j API 2.0.5
    }
    
    /*
     * For testing
     */
    public Location(String simpleLocation) {
    	this.path = simpleLocation;
    	this.type = "simple";
    	this.port = 0;
		this.sublocation = false;
    }
	
    public Location(String path, String type, int port, boolean sublocation) {
		this.path = path;
		this.type = type;
		this.port = port;
		this.sublocation = sublocation;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public String getPath() {
		return path;
	}

	public String getType() {
		return type;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return "Type:" + type + " Path:" + path + ":" + port;
	}
}
