package data;

import java.time.LocalDateTime;

import enums.StatutEnum;

public class FilDeDiscussion implements Comparable<FilDeDiscussion>{
	
	public String titre;
	public int idFil;
	public String auteur;
	public LocalDateTime dateMsgRecent;
	public int idM;
	
	private StatutEnum statusFdd;
	
	private Groupe groupeCible;
	
	public FilDeDiscussion(String titre, String auteur, Groupe groupeCible) {
		this.titre = titre;
		this.auteur = auteur;
		this.groupeCible = groupeCible;
	}
	
	public FilDeDiscussion(String titre, String auteur, Groupe groupeCible, Message premierMsg) {
		this.titre = titre;
		this.auteur = auteur;
		this.groupeCible = groupeCible;
		this.dateMsgRecent = premierMsg.date;
	}
	
	public void setPremierMsg(Message premierMsg) {
		this.dateMsgRecent = premierMsg.date;
	}
	
	public StatutEnum getStatusFdd() {
		return statusFdd;
	}

	public void setStatusFdd(StatutEnum statusFdd) {
		this.statusFdd = statusFdd;
	}

	@Override
	public int compareTo(FilDeDiscussion o) {
		return o.idM-this.idM;
	}
	
	public String toString() {
		return this.titre;
	}
}