package agent.memory.domain;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Location extends Entity {

	private String path;
	//Type: local, remote, docker etc
    private String type;
    private int port;

    public Location() {
    		// Empty constructor required as of Neo4j API 2.0.5
    }
	
    public Location(String path, String type, int port) {
		this.path = path;
		this.type = type;
		this.port = port;
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
		return "Type: " + type + " Path:" + path + ":" + port;
	}
}
