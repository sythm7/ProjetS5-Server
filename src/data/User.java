package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import enums.GroupTypeEnum;
import enums.StatutEnum;


// Sur l'UML j'ai oublie de mettre User en italique pour preciser que c'est une classe abstraite
// On ne pourra instancier que des CampusUser, ou des AgentUser, donc ca parait logique.
public abstract class User {
	
	protected String identifiant;
	protected String motDePasse;
	protected String nom;
	protected String prenom;
	protected GroupTypeEnum typeUser;
	protected Connection con;
	
	// Non utilisé pour l'instant
	private Set<Groupe> listeGroupes = new HashSet<>();
	
	boolean estConnecte = false;
	
	// Petite erreur de ma part d'avoir precise dans le UML que le mdp en argument du constructeur est de type int, c'est corrige.
	protected User(String identifiant, String mdp, String prenom, String nom) {
		this.identifiant = identifiant;
		this.motDePasse = mdp;
		this.nom = nom;
		this.prenom = prenom;
	}
	
	public void envoyerMsg(Message msg, FilDeDiscussion fil){
		Statement stmt;
		try {
			stmt = BddConnect.con.createStatement();
			int idF = fil.idFil;
			
			// Pour savoir à quel idF on a à faire
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as a FROM Message WHERE filId = " + idF +";");
			rs.next();
			int indexM = rs.getInt("a");
			
			// Pour savoir à quel idF on a à faire
			ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as a FROM Message;");
			rs1.next();
			int idM = rs1.getInt("a");
			
			//Mise en place des données dans la BDD
			PreparedStatement setM = BddConnect.con.prepareStatement("INSERT INTO Message VALUES(?, ?, ?, ?, ?, ?, NOW(), ?, ?);");
			setM.setInt(1, idM);
			setM.setInt(2, indexM);
			setM.setString(3, this.identifiant);
			setM.setString(4, this.prenom);
			setM.setString(5, this.nom);
			setM.setString(6, msg.contenu);
			setM.setInt(7, idF);
			setM.setString(8, "EN_ATTENTE");
			setM.executeUpdate();
			
			PreparedStatement set = BddConnect.con.prepareStatement("UPDATE FilDeDiscussion SET dateMessageR = NOW() WHERE idF = " + idF +";");
			set.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPrenom(){
		return this.prenom;
	}
	
	public FilDeDiscussion creerFilDeDiscussion(String titre, Groupe groupeCible, Message premierMsg){
		Statement stmt;
		FilDeDiscussion f = null;
		try {
			stmt = BddConnect.con.createStatement();
			// Pour savoir à quel idF on a à faire
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as a FROM FilDeDiscussion;");
			rs.next();
			int idF = rs.getInt("a");
			
			//Mise en place des données dans la BDD
			PreparedStatement setF = BddConnect.con.prepareStatement("INSERT INTO FilDeDiscussion VALUES(?, ?, ?, ?, NOW(), ?);");
			setF.setInt(1, idF);
			setF.setString(2, this.identifiant);
			setF.setString(3, titre);
			setF.setString(4, groupeCible.getNom());
			setF.setString(5, StatutEnum.EN_ATTENTE.toString());
			
			ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) as a FROM Message;");
			rs1.next();
			int idM = rs1.getInt("a");
			
			PreparedStatement setM = BddConnect.con.prepareStatement("INSERT INTO Message VALUES(?, ?, ?, ?, ?, ?, NOW(), ?, ?);");
			setM.setInt(1, idM);
			setM.setInt(2, 0);
			setM.setString(3, this.identifiant);
			setM.setString(4, this.prenom);
			setM.setString(5, this.nom);
			setM.setString(6, premierMsg.contenu);
			setM.setInt(7, idF);
			setM.setString(8, "EN_ATTENTE");
			
			setF.executeUpdate();
			setM.executeUpdate();
			f = new FilDeDiscussion(titre, this.identifiant, groupeCible, premierMsg);
			f.idFil=idF;
			f.idM=idM;
		} catch (SQLException e){
			e.printStackTrace();
		}
		TreeMap<String, TreeMap<FilDeDiscussion, TreeSet<Message>>> test = this.getTreeUser();
		System.out.println(" FDD CREE " + f.titre);
		System.out.println(" ARBRE ");
		for (String g : test.keySet()) {
			System.out.println(" - " + g + " : ");
			TreeMap<FilDeDiscussion, TreeSet<Message>> arbre = test.get(g);
			for (FilDeDiscussion f1 : arbre.keySet()) {
				System.out.println("     - " + f1.titre + " : ");
				for (Message m : arbre.get(f1))
					System.out.println("          " + m.idM + " : " + m.contenu);
			}
		}
		return f;
	}
	
	public void connecter(){
		this.estConnecte = true;
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("UPDATE Utilisateur SET statusU = true WHERE userName = ?;");
			set.setString(1, this.identifiant);
			set.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deconnecter(){
		this.estConnecte = false;
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("UPDATE Utilisateur SET statusU = false WHERE userName = ?;");
			set.setString(1, this.identifiant);
			set.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getNom() {
		return this.nom;
	}
	
	public String getMotDePasse() {
		return this.motDePasse;
	}
	
	public void setMotDePasse(String motDePasse){
		this.motDePasse = motDePasse;
		PreparedStatement set;
		try {
			set = BddConnect.con.prepareStatement("UPDATE Utilisateur SET mdpU = ? WHERE userName = ?;");
			set.setString(1, this.motDePasse);
			set.setString(2, this.identifiant);
			set.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getIdentifiant() {
		return this.identifiant;
	}
	
	public String toString() {
		return this.nom + " " + this.prenom;
	}
	
	public boolean equals(Object o) {
		if(! (o instanceof User))
			return false;
		
		User user = (User) o;
		
		return this.identifiant.equals(user.identifiant) ;
	}
	
	public int hashCode() {
		return (this.identifiant.hashCode())*31;
	}
	
	public TreeMap<FilDeDiscussion, TreeSet<Message>> initUserTreeFM(String nomG){
		TreeMap<FilDeDiscussion, TreeSet<Message>> res = new TreeMap<>();
		try {
			Statement stmt = BddConnect.con.createStatement();
			Statement stmt2 = BddConnect.con.createStatement();
			Statement stmt3 = BddConnect.con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion WHERE destG = '" + nomG + "' OR auteurF = '" + this.identifiant + "';");
			while (rs.next()) {
				FilDeDiscussion f1 = null;
				//System.out.println(rs.getString("destG"));
				ResultSet rs2 = stmt2.executeQuery("SELECT * FROM MESSAGE WHERE filId = " + rs.getInt("idF") + " ORDER BY filId DESC;");
				rs2.next(); 
				ResultSet rs3 = stmt3.executeQuery("SELECT * FROM Groupe WHERE nomG = '" + rs.getString("destG") + "';");
				rs3.next();
				if (rs3.getString("typeG").equals("CAMPUS_USER")){
					f1 = new FilDeDiscussion(rs.getString("titreF"), rs.getString("auteurF"), new Groupe(rs.getString("destG"), GroupTypeEnum.CAMPUS_USER), new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM")));
				}
				else {
					f1 = new FilDeDiscussion(rs.getString("titreF"), rs.getString("auteurF"), new Groupe(rs.getString("nomG"), GroupTypeEnum.AGENT_USER), new Message(rs2.getString("contenuM"), rs.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM")));
				}
				f1.idFil=rs.getInt("idF");
				f1.idM=rs2.getInt("idM");
				System.out.println("IIIIIIIIIICCCCCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIIIIIIIII ");
				res.put(f1, new TreeSet<Message>());
				Message m = new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM"));
				m.idM=rs2.getInt("idM");
				res.get(f1).add(m);
				while (rs2.next()) {
					Message m2 = new Message(rs2.getString("contenuM"), rs2.getTimestamp("dateM").toLocalDateTime(), rs2.getString("auteurM"));
					m2.idM=rs2.getInt("idM");
					//System.out.println();
					res.get(f1).add(m2);
				}
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
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
				System.out.println(rs.getString("nomGroupe"));
				userTree.put(rs.getString("nomGroupe"), new TreeMap<FilDeDiscussion, TreeSet<Message>>());
			}
			for (String g : userTree.keySet()){
				System.out.println(g);
				userTree.put(g, this.initUserTreeFM(g));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userTree;
	}
	
	public GroupTypeEnum getTypeUser() {
		return this.typeUser;
	}
}
