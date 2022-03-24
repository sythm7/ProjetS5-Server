package networking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class NetworkHashMap implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1588417096585202680L;
	
	private LinkedHashMap<String, LinkedHashSet<NetworkFilDeDiscussion>> groupHashMap = new LinkedHashMap<>();
	
	public NetworkHashMap() {
	}
	
	public NetworkHashMap(LinkedHashMap<String, LinkedHashSet<NetworkFilDeDiscussion>> groupHashMap) {
		this.groupHashMap = groupHashMap;
	}
	
	public void setGroupTreeMap(LinkedHashMap<String, LinkedHashSet<NetworkFilDeDiscussion>> groupHashMap) {
		this.groupHashMap = groupHashMap;
	}
	
	public void addGroup(String groupName) {
		this.groupHashMap.put(groupName, new LinkedHashSet<>());
	}
	
	public void addFdd(NetworkFilDeDiscussion networkFdd) {
		this.groupHashMap.get(networkFdd.getNomGroupe()).add(networkFdd);
	}
	
	public void addMsg(NetworkMessage networkMsg) {
		String path[] = networkMsg.getPath();
		
		HashSet<NetworkFilDeDiscussion> networkFddSet = this.groupHashMap.get(path[0]);
		
		for(NetworkFilDeDiscussion networkFdd : networkFddSet) {
			if(networkFdd.getTitre().equals(path[1]))
				networkFdd.addMsg(networkMsg);
		}
	}
	
	public LinkedHashMap<String, LinkedHashSet<NetworkFilDeDiscussion>> getGroupHashMap() {
		return this.groupHashMap;
	}
}