package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import enums.GroupTypeEnum;

public class Groupe {
	
	private String nomG;
	
	private GroupTypeEnum groupType;
	
	// Dans l'UML pour le constructeur j'ai écrit :
	public Groupe(String nom, GroupTypeEnum groupType){
		this.groupType = groupType;
		this.nomG = nom;
		this.ajouterGroupe();
	}
	
	public String getNom() {
		return this.nomG;
	}
	
	public GroupTypeEnum getGroupType() {
		return this.groupType;
	}
	
	public ArrayList<User> getUsersList() {
		return BddConnect.getGroupeUsers(this.nomG);
	}
	
	public String toString() {
		if(groupType == GroupTypeEnum.AGENT_USER)
			return this.nomG + "<br> <b STYLE=\"font-size: 0.8em; color: rgb(41, 128, 185);\">(AgentU)</b>";
		else
			return this.nomG + "<br> <b STYLE=\"font-size: 0.8em; color: rgb(184, 27, 72);\">(CampusU)</b>";
	}
	
	public void addUser(User user){
		PreparedStatement set;
		try {
			Statement stmt=BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as a FROM ListUsers WHERE nomGroupe = '" + this.nomG +"' AND usernameID = '" + user.identifiant + "';");
			rs.next();
			if (rs.getInt("a")==0) {
				set = BddConnect.con.prepareStatement("INSERT INTO ListUsers VALUES(?, ?);");
				set.setString(1, user.identifiant);
			    set.setString(2, this.nomG);
				set.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeUser(User user){
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("DELETE FROM ListUsers WHERE usernameID = ?;");
			set.setString(1, user.identifiant);
			set.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public void ajouterGroupe(){
		PreparedStatement set;
		try {
			Statement stmt=BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as a FROM Groupe WHERE nomG = '" + this.nomG +"';");
			rs.next();
			if (rs.getInt("a")==0) {
				set = BddConnect.con.prepareStatement("INSERT INTO Groupe VALUES(?, ?);");
				set.setString(1, this.nomG);
				if (this.groupType.equals(GroupTypeEnum.CAMPUS_USER))
					set.setString(2, "CAMPUS_USER");
				else
					set.setString(2, "AGENT_USER");
				set.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  
	}
	
	public void supprimerGroupe() {
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("DELETE FROM listUsers WHERE nomGroupe = ?;");
			set.setString(1, this.nomG);
			set.executeUpdate();
			
			set = BddConnect.con.prepareStatement("DELETE FROM Groupe WHERE nomG = ?;");
			set.setString(1, this.nomG);
			set.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
