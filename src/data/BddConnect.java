package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

import enums.GroupTypeEnum;

public class BddConnect {
	public static Connection con;
	
	public static void init() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/projet_s5", "root", "");
		} catch (SQLException e) {
			System.err.println("Erreur init base de données. Arrêt du programme.");
			System.exit(1);
		}
	}
	
	public static void clear(){
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/projet_s5", "root", "");
			PreparedStatement set=con.prepareStatement("DELETE FROM Message;");
			PreparedStatement set2=con.prepareStatement("DELETE FROM FilDeDiscussion;");
			PreparedStatement set3=con.prepareStatement("DELETE FROM ListUsers;");
			PreparedStatement set4=con.prepareStatement("DELETE FROM Groupe;");
			PreparedStatement set5=con.prepareStatement("DELETE FROM Utilisateur;");
			set.executeUpdate();
			set2.executeUpdate();
			set3.executeUpdate();
			set4.executeUpdate();
			set5.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static TreeMap<String, TreeSet<FilDeDiscussion>> initTreeGFMap(){
		TreeMap<String, TreeSet<FilDeDiscussion>> res = new TreeMap<>();
		try {
			Statement stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Groupe;");
			while (rs.next()) {
				res.put(rs.getString("nomG"), new TreeSet<FilDeDiscussion>());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static TreeMap<FilDeDiscussion, TreeSet<Message>> initTreeFMMap(){
		TreeMap<FilDeDiscussion, TreeSet<Message>> res = new TreeMap<>();
		try {
			Statement stmt = BddConnect.con.createStatement();
			Statement stmt2 = BddConnect.con.createStatement();
			Statement stmt3 = BddConnect.con.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion;");
			while (rs.next()) {
				String nomG = rs.getString("destG");
				int idF = rs.getInt("idF");
				String titre = rs.getString("titreF");
				String auteurF = rs.getString("auteurF");
				
				ResultSet rs1 = stmt2.executeQuery("SELECT * FROM Groupe WHERE nomG = '" + nomG + "';");
				rs1.next();
				String typeG = rs1.getString("typeG");
				
				ResultSet rs2 = stmt3.executeQuery("SELECT * FROM Message;");
				rs2.next();
				String contenu = rs2.getString("contenuM");
				LocalDateTime date = (LocalDateTime) rs2.getObject("dateM");
				if (typeG.equals(GroupTypeEnum.CAMPUS_USER.toString())){
					FilDeDiscussion f1 = new FilDeDiscussion(titre, auteurF, new Groupe(nomG, GroupTypeEnum.CAMPUS_USER), new Message(contenu, date, auteurF));
					f1.idFil=idF;
					rs2 = stmt3.executeQuery("SELECT * FROM Message WHERE filId = " + idF + " ORDER BY filId DESC;");
					rs2.next();
					f1.dateMsgRecent=rs2.getTimestamp("dateMessageR").toLocalDateTime();
					res.put(f1, new TreeSet<Message>());
				}
				else {
					FilDeDiscussion f1 = new FilDeDiscussion(titre, auteurF, new Groupe(nomG, GroupTypeEnum.AGENT_USER), new Message(contenu, date, auteurF));
					res.put(f1, new TreeSet<Message>());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static TreeMap<String, TreeSet<FilDeDiscussion>> getTreeGroupeFddFromBdd(){
		TreeMap<String, TreeSet<FilDeDiscussion>> res = initTreeGFMap();
		
		try {
			Statement stmt = BddConnect.con.createStatement();
			Statement stmt2 = BddConnect.con.createStatement();
			Statement stmt3 = BddConnect.con.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion;");
			while (rs.next()) {
				int idF = rs.getInt("idF");
				String nomG = rs.getString("destG");
				String titre = rs.getString("titreF");
				String auteurF = rs.getString("auteurF");
				
				ResultSet rs1 = stmt2.executeQuery("SELECT * FROM Groupe WHERE nomG = '" + nomG + "';");
				rs1.next();
				String typeG = rs1.getString("typeG");
				
				ResultSet rs2 = stmt3.executeQuery("SELECT * FROM Message;");
				rs2.next();
				String contenu = rs2.getString("contenuM");
				LocalDateTime date = (LocalDateTime) rs2.getObject("dateM");
				if (typeG.equals(GroupTypeEnum.CAMPUS_USER.toString())){
					FilDeDiscussion f1 = new FilDeDiscussion(titre, auteurF, new Groupe(nomG, GroupTypeEnum.CAMPUS_USER), new Message(contenu, date, auteurF));
					f1.idFil=idF;
					res.get(nomG).add(f1);
				}
				else {
					FilDeDiscussion f1 = new FilDeDiscussion(titre, auteurF, new Groupe(nomG, GroupTypeEnum.AGENT_USER), new Message(contenu, date, auteurF));
					res.get(nomG).add(f1);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static TreeMap<FilDeDiscussion, TreeSet<Message>> getTreeFddMessageFromBdd(){
		TreeMap<FilDeDiscussion, TreeSet<Message>> res = initTreeFMMap();
		
		try {
			Statement stmt = BddConnect.con.createStatement();
			for (FilDeDiscussion f : res.keySet()) {
				//System.out.println(f.idFil);
				ResultSet rs = stmt.executeQuery("SELECT * FROM MESSAGE WHERE filId = " + f.idFil + ";");
				while (rs.next()) {
					Message m = new Message(rs.getString("contenuM"), rs.getTimestamp("dateM").toLocalDateTime(), rs.getString("auteurM"));
					m.idM = rs.getInt("idM");
					res.get(f).add(m);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static ArrayList<User> getUsersConnectes(){
		ArrayList<User> res = new ArrayList<>();
		try {
			Statement stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Utilisateur WHERE statusU = true;");
			
			while (rs.next()) {
				if (rs.getString("typeU").equals("CAMPUS_USER")) {
					CampusUser u = new CampusUser(rs.getString("userName"), rs.getString("mdpU"), rs.getString("prenomU"), rs.getString("nomU"));
					res.add(u);
				}
				else {
					AgentUser u = new AgentUser(rs.getString("userName"), rs.getString("mdpU"), rs.getString("prenomU"), rs.getString("nomU"));
					res.add(u);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public static LinkedList<User> getUsers(){
		Statement stmt;
		LinkedList<User> list = new LinkedList<>();
		try {
			stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Utilisateur;");
			while (rs.next()) {
				if (rs.getString("typeU").equals("CAMPUS_USER")) {
					CampusUser u = new CampusUser(rs.getString("userName"), rs.getString("mdpU"), rs.getString("prenomU"), rs.getString("nomU"));
					list.add(u);
				}
				else {
					AgentUser u = new AgentUser(rs.getString("userName"), rs.getString("mdpU"), rs.getString("prenomU"), rs.getString("nomU"));
					list.add(u);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public static LinkedList<Groupe> getGroupes(){
		Statement stmt;
		LinkedList<Groupe> list = new LinkedList<>();
		try {
			stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Groupe;");
			while (rs.next()) {
				if (rs.getString("typeG").equals("CAMPUS_USER")) {
					Groupe g = new Groupe(rs.getString("nomG"), GroupTypeEnum.CAMPUS_USER);
					list.add(g);
				}
				else {
					Groupe g = new Groupe(rs.getString("nomG"), GroupTypeEnum.AGENT_USER);
					list.add(g);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public static ArrayList<User> getGroupeUsers(String nomG){
		Statement stmt;
		ArrayList<User> list = new ArrayList<>();
		try {
			stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM ListUsers WHERE nomGroupe = '" + nomG + "';");
			while (rs.next()) {
				Statement stmt2 = BddConnect.con.createStatement();
				ResultSet rs2 = stmt2.executeQuery("SELECT * FROM Utilisateur WHERE userName = '" + rs.getString("usernameID") + "';");
				while (rs2.next()) {
					if (rs2.getString("typeU").equals("CAMPUS_USER")) {
						CampusUser u = new CampusUser(rs2.getString("userName"), rs2.getString("mdpU"), rs2.getString("prenomU"), rs2.getString("nomU"));
						list.add(u);
					}
					else {
						AgentUser u = new AgentUser(rs2.getString("userName"), rs2.getString("mdpU"), rs2.getString("prenomU"), rs2.getString("nomU"));
						list.add(u);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	
	public static void deleteUser(User user) {
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("DELETE FROM utilisateur WHERE userName = ?;");
			set.setString(1, user.getIdentifiant());
			set.executeUpdate();
			
			set = BddConnect.con.prepareStatement("DELETE FROM listusers WHERE usernameID = ?;");
			set.setString(1, user.getIdentifiant());
			set.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getListGroupes() {
		ArrayList<String> listGroupes = new ArrayList<>(); 
		try {
			Statement set = BddConnect.con.createStatement();
			ResultSet rs = set.executeQuery("SELECT * FROM Groupe;");
			while (rs.next()) {
				listGroupes.add(rs.getString("nomG"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return listGroupes;
	}
	
	public static void updateMessageStatus(int idM, String status) {
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("UPDATE Message SET statusM = '" + status + "' WHERE idM = " + idM + ";");
			set.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updateFDDStatus(int idF, String status) {
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("UPDATE FilDeDiscussion SET statusF = '" + status + "' WHERE idF = " + idF + ";");
			set.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getNewIdM() {
		
		int idM = 0;
		
		try {
			Statement stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as a FROM Message;");
			rs.next();
			idM = rs.getInt("a") - 1;
			System.out.println("IDM = " + idM);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return idM;
	}
}
