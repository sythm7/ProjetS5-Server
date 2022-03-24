package networking;

import java.io.Serializable;
import java.util.LinkedHashSet;

import enums.StatutEnum;

public class NetworkFilDeDiscussion implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4922020159476953160L;
    
    private String titre;
    
    private String auteur;
    
    private LinkedHashSet<NetworkMessage> listeMessages = new LinkedHashSet<>();
    
    private StatutEnum statusFdd;
    
    private String nomGroupe;
    
    public int idF;
    
    public int index=0;
    
    public NetworkFilDeDiscussion(String titre, String auteur, String nomGroupe) {
        this.titre = titre;
        this.auteur = auteur;
        this.nomGroupe = nomGroupe;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public LinkedHashSet<NetworkMessage> getListeMessages() {
        return listeMessages;
    }

    public String getNomGroupe() {
        return nomGroupe;
    }
    
    public StatutEnum getStatusFdd() {
		return statusFdd;
	}

	public void setStatusFdd(StatutEnum statusFdd) {
		this.statusFdd = statusFdd;
	}
    
    public void addMsg(NetworkMessage networkMsg) {
        this.listeMessages.add(networkMsg);
        if (this.index<networkMsg.idM)
            this.index=networkMsg.idM;
    }
    
    public String toString() {
        return this.titre;
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof NetworkFilDeDiscussion))
            return false;
        
        NetworkFilDeDiscussion networkFdd = (NetworkFilDeDiscussion) obj;
        
        return this.idF == networkFdd.idF;
    }
    
    public int hashCode() {
        return (this.idF + this.index + this.nomGroupe.hashCode() + this.titre.hashCode()) * 31;
    }
}