package networking;

import java.io.Serializable;
import java.time.LocalDateTime;

import enums.StatutEnum;

public class NetworkMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8635908721282746155L;
	
	private String auteur;
	
	public int idM;
	
	private int idF;
	
	private LocalDateTime date;
	
	private String contenu;
	
	private String nomComplet;
	
	private String[] path;
	
	private StatutEnum statusMsg = StatutEnum.EN_ATTENTE;
	
	public NetworkMessage(String contenu, LocalDateTime date, String auteur) {
		this.auteur = auteur;
		this.date = date;
		this.contenu = contenu;
	}
	
	public NetworkMessage(String contenu, LocalDateTime date, String auteur, StatutEnum statusMsg) {
		this.auteur = auteur;
		this.contenu = contenu;
		this.date = date;
		this.statusMsg = statusMsg;
	}

	public String getAuteur() {
		return auteur;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public String getContenu() {
		return contenu;
	}
	
	public String getNomComplet() {
		return nomComplet;
	}
	
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
	
	public String[] getPath() {
		return this.path;
	}
	
	public void setPath(String[] path) {
		this.path = path;
	}
	
	public StatutEnum getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(StatutEnum statusMsg) {
		this.statusMsg = statusMsg;
	}
	
	public int getIdF() {
		return idF;
	}

	public void setIdF(int idF) {
		this.idF = idF;
	}
	
	public String toString() {
		return this.auteur + "(" + this.date + ") -> " + this.contenu;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof NetworkMessage))
			return false;
		
		NetworkMessage msg = (NetworkMessage) obj;
		
		return msg.auteur.equals(this.auteur) && msg.contenu.equals(this.contenu) && msg.date.equals(this.date);
	}
	
	@Override
	public int hashCode() {
		return (this.auteur.hashCode() + this.date.hashCode() + this.contenu.hashCode()) * 31;
	}	
}