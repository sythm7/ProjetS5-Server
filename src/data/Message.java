package data;

import java.time.LocalDateTime;

import enums.StatutEnum;

public class Message implements Comparable<Message>{

	String contenu;
	public int idM;
	LocalDateTime date;
	
	private String auteur;
	
	private String nomComplet;

	public StatutEnum statusMsg = StatutEnum.EN_ATTENTE;
	
	public Message(String contenu, LocalDateTime date, String auteur) {
		this.auteur = auteur;
		this.contenu = contenu;
		this.date = date;
	}
	
	public String getContenu() {
		return this.contenu;
	}
	
	public LocalDateTime getDate() {
		return this.date;
	}
	
	public String getAuteur() {
		return this.auteur;
	}
	
	public StatutEnum getStatusMsg() {
		return this.statusMsg;
	}
	
	public void setStatusEnum(StatutEnum statusMsg) {
		this.statusMsg = statusMsg;
	}

	@Override
	public int compareTo(Message o) {
		return this.idM-o.idM;
	}

	public String getNomComplet() {
		return nomComplet;
	}
	
	public void setNomComplet(String nomComplet) {
		this.nomComplet = nomComplet;
	}
}