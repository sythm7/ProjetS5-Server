package networking;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;


public class NetworkTreeMap implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1588417096585202680L;
	
	private TreeMap<String, TreeSet<NetworkFilDeDiscussion>> groupTreeMap = new TreeMap<>();
	
	public NetworkTreeMap() {
	}
	
	public NetworkTreeMap(TreeMap<String, TreeSet<NetworkFilDeDiscussion>> groupTreeMap) {
		this.groupTreeMap = groupTreeMap;
	}
	
	public void setGroupTreeMap(TreeMap<String, TreeSet<NetworkFilDeDiscussion>> groupTreeMap) {
		this.groupTreeMap = groupTreeMap;
	}
	
	public void addGroup(String groupName) {
		this.groupTreeMap.put(groupName, new TreeSet<>());
	}
	
	public void addFdd(String groupName, NetworkFilDeDiscussion networkFdd) {
		this.groupTreeMap.get(groupName).add(networkFdd);
	}
	
	public TreeMap<String, TreeSet<NetworkFilDeDiscussion>> getGroupTreeMap() {
		return this.groupTreeMap;
	}
}