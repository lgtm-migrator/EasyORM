package org.suntao.easyorm.configuration;

/**
 * mapper文件配置 存储文件的id以及位置
 * 
 * @author suntao
 * 
 */
public class MapperConfig {
	private String id;
	private String location;

	public MapperConfig(String id, String location) {
		super();
		this.id = id;
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public String getInfoStr() {
		return String.format("%s\t%s", this.id, this.location);
	}

	public String getLocation() {
		return location;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}