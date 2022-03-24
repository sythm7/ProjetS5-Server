package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import java.util.TreeSet;

import enums.GroupTypeEnum;
import enums.StatutEnum;

public class CampusUser extends User {

	public CampusUser(String identifiant, String mdp, String prenom, String nom){
		super(identifiant, mdp, prenom, nom);
		ajouterUser(this);
		super.typeUser = GroupTypeEnum.CAMPUS_USER;
	}
	
	public void ajouterUser(CampusUser user){
		PreparedStatement set;
		try {
			Statement stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as a FROM Utilisateur WHERE userName = '" + user.identifiant + "';");
			rs.next();
			if (rs.getInt("a")==0) {
				set = BddConnect.con.prepareStatement("INSERT INTO Utilisateur VALUES(?, ?, ?, ?, ?, ?);");
				set.setString(1, user.identifiant);
			    set.setString(2, user.motDePasse);
			    set.setString(3, user.nom);
			    set.setString(4, user.prenom);
			    set.setBoolean(5, false);
			    set.setString(6, "CAMPUS_USER");
				set.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public TreeMap<FilDeDiscussion, TreeSet<Message>> initUserTreeFM(String nomG){
		TreeMap<FilDeDiscussion, TreeSet<Message>> res = new TreeMap<>();
		try {
			Statement stmt = BddConnect.con.createStatement();
			Statement stmt2 = BddConnect.con.createStatement();
			Statement stmt3 = BddConnect.con.createStatement();
			Statement stmt6 = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion WHERE destG = '" + nomG + "';");
			Statement stmt4 = BddConnect.con.createStatement();
			ResultSet rs4 = stmt4.executeQuery("SELECT COUNT(*) as a FROM ListUsers WHERE nomGroupe = '" + nomG + "' AND usernameID = '" + this.identifiant + "';");
			Statement stmt5 = BddConnect.con.createStatement();
			ResultSet rs5 = stmt5.executeQuery("SELECT COUNT(*) as b FROM FilDeDiscussion WHERE destG = '" + nomG + "' AND auteurF = '" + this.identifiant + "';");
			rs4.next();
			rs5.next();
			int a=rs4.getInt("a");
			int b = rs5.getInt("b");
			while (rs.next()) {
				if ((a==0 && b!=0)) {
					if (rs.getString("auteurF").equals(this.identifiant)) {
						FilDeDiscussion f1 = null;
						ResultSet rs2 = stmt2.executeQuery("SELECT * FROM MESSAGE WHERE filId = " + rs.getInt("idF") + " ORDER BY filId DESC;");
						rs2.next(); 
						ResultSet rs6 = stmt6.executeQuery("SELECT * FROM MESSAGE WHERE filId = " + rs.getInt("idF") + " ORDER BY idM DESC;");
						rs6.next();
						ResultSet rs3 = stmt3.executeQuery("SELECT * FROM Groupe WHERE nomG = '" + rs.getString("destG") + "';");
						rs3.next();
						if (rs3.getString("typeG").equals("CAMPUS_USER")){
							f1 = new FilDeDiscussion(rs.getString("titreF"), rs.getString("auteurF"), new Groupe(rs.getString("destG"), GroupTypeEnum.CAMPUS_USER), new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM")));
						}
						else {
							f1 = new FilDeDiscussion(rs.getString("titreF"), rs.getString("auteurF"), new Groupe(nomG, GroupTypeEnum.AGENT_USER), new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM")));
						}
						f1.idFil=rs.getInt("idF");
						f1.idM=rs6.getInt("idM");
						f1.setStatusFdd(StatutEnum.valueOf(rs.getString("statusF")));
						System.out.println("ICCCCCCCCCCCCCCCCCCCCCCCI : " + f1.titre + "     " + f1.idM);
						res.put(f1, new TreeSet<Message>());
						Message m = new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM"));
						m.setNomComplet(rs2.getString("prenomA") + " " + rs2.getString("nomA"));
						System.out.println(m.contenu + " " + m.getNomComplet());
						m.idM=rs2.getInt("idM");
						m.statusMsg=StatutEnum.valueOf(rs2.getString("statusM"));
						res.get(f1).add(m);
						while (rs2.next()){
							Message m2 = new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM"));
							m2.setNomComplet(rs2.getString("prenomA") + " " + rs2.getString("nomA"));
							System.out.println(m2.contenu + " " + m2.getNomComplet());
							m2.idM=rs2.getInt("idM");
							m2.statusMsg=StatutEnum.valueOf(rs2.getString("statusM"));
							//System.out.println();
							res.get(f1).add(m2);
						}
					}
				}
				if (a!=0) {
					FilDeDiscussion f1 = null;
					ResultSet rs2 = stmt2.executeQuery("SELECT * FROM MESSAGE WHERE filId = " + rs.getInt("idF") + " ORDER BY filId DESC;");
					rs2.next(); 
					ResultSet rs6 = stmt6.executeQuery("SELECT * FROM MESSAGE WHERE filId = " + rs.getInt("idF") + " ORDER BY idM DESC;");
					rs6.next();
					ResultSet rs3 = stmt3.executeQuery("SELECT * FROM Groupe WHERE nomG = '" + rs.getString("destG") + "';");
					rs3.next();
					if (rs3.getString("typeG").equals("CAMPUS_USER")){
						f1 = new FilDeDiscussion(rs.getString("titreF"), rs.getString("auteurF"), new Groupe(rs.getString("destG"), GroupTypeEnum.CAMPUS_USER), new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM")));
					}
					else {
						f1 = new FilDeDiscussion(rs.getString("titreF"), rs.getString("auteurF"), new Groupe(nomG, GroupTypeEnum.AGENT_USER), new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM")));
					}
					f1.idFil=rs.getInt("idF");
					f1.idM=rs6.getInt("idM");
					f1.setStatusFdd(StatutEnum.valueOf(rs.getString("statusF")));
					res.put(f1, new TreeSet<Message>());
					Message m = new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM"));
					m.setNomComplet(rs2.getString("prenomA") + " " + rs2.getString("nomA"));
					m.statusMsg=StatutEnum.valueOf(rs2.getString("statusM"));
					m.idM=rs2.getInt("idM");
					res.get(f1).add(m);
					while (rs2.next()){
						Message m2 = new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM"));
						m2.setNomComplet(rs2.getString("prenomA") + " " + rs2.getString("nomA"));
						m2.idM=rs2.getInt("idM");
						m2.statusMsg=StatutEnum.valueOf(rs2.getString("statusM"));
						res.get(f1).add(m2);
					}
				}
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public TreeMap<String, TreeMap<FilDeDiscussion, TreeSet<Message>>> getTreeUser(){
		TreeMap<String, TreeMap<FilDeDiscussion, TreeSet<Message>>> userTree = new TreeMap<>();
		
		try {
			Statement stmt = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT usernameID, nomGroupe FROM listusers WHERE usernameID = '" + this.identifiant + "' UNION SELECT auteurF, destG FROM fildediscussion WHERE auteurF = '" + this.identifiant + "';");
			while (rs.next()) {
				userTree.put(rs.getString("nomGroupe"), new TreeMap<FilDeDiscussion, TreeSet<Message>>());
			}
			
			for (String g : userTree.keySet()) {
				userTree.put(g, this.initUserTreeFM(g));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userTree;
	}
}
