package serveur;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import data.AgentUser;
import data.BddConnect;
import data.CampusUser;
import data.FilDeDiscussion;
import data.Groupe;
import data.Message;
import data.User;
import enums.GroupTypeEnum;
import enums.StatutEnum;
import networking.NetworkMessage;
import networking.NetworkFilDeDiscussion;
import networking.NetworkHashMap;

public class ClientManagerThread extends Thread {
	
	private User targetUser;
	
	private ObjectInputStream input;
	
	private ObjectOutputStream output;
	
	private Serveur server;

	private NetworkHashMap networkHashMap = new NetworkHashMap();
	
	public ClientManagerThread(Serveur server) {
		this.server = server;
		this.input = server.getObjectInputStream();
		this.output = server.getObjectOutputStream();
	}
	
	@Override
	public void run() {
		
		boolean isConnected = true;
		while(isConnected) {
			
			Object receivedObj = null;
			try {
				receivedObj = input.readUnshared();
				
				this.processPacket(receivedObj);
				
			} catch (ClassNotFoundException | IOException e) {
				isConnected = false;
			}
		}
		
		if(targetUser != null)
			this.targetUser.deconnecter();
		
		System.out.println("Le client s'est déconnecté");
		Serveur.openThreads.remove(this);
	}
	
	// Envoyer toutes les donnees que doit recevoir le client lors de la connexion
	public void initClientData() {
		initUserTree();
		this.sendObject(this.networkHashMap);
		
	}
	
	// Traiter le paquet recu
	public void processPacket(Object obj) {
		
		if(obj instanceof String[]) {
			
			String[] list = (String[]) obj;
			
			if (list[0].equals("idF_Recu")) {
				gererHashRecu(list);
			}
			else if (list[0].equals("idF_Lu")) {
				gererHashLu(list);
			}
			else
				this.verifyIdentifiers((String[]) obj);
		}
		
		if(obj instanceof NetworkMessage) {
			NetworkMessage networkMessage = (NetworkMessage) obj;
			this.sendMsgToAll(networkMessage);
		}
		
		if(obj instanceof String) {
			String receivedString = (String) obj;
			if(receivedString.equals("init"))
				this.initClientData();
			if(receivedString.equals("getGroupsList"))
				this.sendGroupsList();
		}
		
		if(obj instanceof NetworkFilDeDiscussion) {
			NetworkFilDeDiscussion networkFdd = (NetworkFilDeDiscussion) obj;
			System.out.println("NetworkFDD recu : " + networkFdd);
			this.sendFddToAll(networkFdd);
		}
	}
	
	//########## HASH RECU TRAITEMENT ###########
	
	public void gererHashRecu(String[] list) {
		for (String idF_temp : list) {
			Integer idF=-1;
			if (!idF_temp.equals("idF_Recu")) {
				idF = Integer.parseInt(idF_temp);
				if (!Serveur.hashRecu.get(idF).contains(this.targetUser))
					Serveur.hashRecu.get(idF).add(this.targetUser);
			}
			if (idF!=-1) {
				try {
					Statement stmt = BddConnect.con.createStatement();
					ResultSet rs;
					rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion WHERE idF = " + idF + ";");
					rs.next();
					String groupe = rs.getString("destG");
					String auteur = rs.getString("auteurF");
					
					ArrayList<User> listeUsers = BddConnect.getGroupeUsers(groupe);
					if (!listeUsers.contains(this.targetUser)) {
						listeUsers.add(this.targetUser);
					}
					if (Serveur.hashRecu.get(idF).containsAll(listeUsers)) {
						BddConnect.updateFDDStatus(idF, StatutEnum.NON_LU_PAR_TOUS.toString());
						Statement stmt2 = BddConnect.con.createStatement();
						ResultSet rs2 = stmt2.executeQuery("SELECT * FROM Message WHERE filId = " + idF + ";");
						while (rs2.next()) {
							BddConnect.updateMessageStatus(rs2.getInt("idM"), StatutEnum.NON_LU_PAR_TOUS.toString());
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (Integer idF : Serveur.hashRecu.keySet()) {
			System.out.println("IdF : " + idF);
			for (User u : Serveur.hashRecu.get(idF)) {
				try {
					Statement stmt = BddConnect.con.createStatement();
					ResultSet rs;
					rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion WHERE idF = " + idF + ";");
					rs.next();
					String groupe = rs.getString("destG");
					String auteur = rs.getString("auteurF");
					
					ArrayList<User> listeUsers = BddConnect.getGroupeUsers(groupe);
					System.out.println(Serveur.hashLu.get(idF).containsAll(listeUsers));
					System.out.println("             - " + u.getIdentifiant());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("HASH RECU");
		
		for(Integer i : Serveur.hashRecu.keySet()) {
			System.out.println("- " + i);
			for(User user : Serveur.hashRecu.get(i)) {
				System.out.println(user.getIdentifiant());
			}
		}
	}
	
	
	//########## HASH LU TRAITEMENT ###########
	
	public void gererHashLu(String[] list) {
		for (String idF_temp : list) {
			Integer idF=-1;
			if (!idF_temp.equals("idF_Lu")) {
				idF = Integer.parseInt(idF_temp);
				
				if (!Serveur.hashLu.get(idF).contains(this.targetUser))
					Serveur.hashLu.get(idF).add(this.targetUser);
			}
			if (idF!=-1) {
				try {
					Statement stmt = BddConnect.con.createStatement();
					ResultSet rs;
					rs = stmt.executeQuery("SELECT * FROM FilDeDiscussion WHERE idF = " + idF + ";");
					rs.next();
					String groupe = rs.getString("destG");
					String auteur = rs.getString("auteurF");
						
					ArrayList<User> listeUsers = BddConnect.getGroupeUsers(groupe);
					if (!listeUsers.contains(this.targetUser)) {
						listeUsers.add(this.targetUser);
					}
					if (Serveur.hashLu.get(idF).containsAll(listeUsers)) {
						BddConnect.updateFDDStatus(idF, StatutEnum.LU_PAR_TOUS.toString());
						Statement stmt2 = BddConnect.con.createStatement();
						ResultSet rs2 = stmt2.executeQuery("SELECT * FROM Message WHERE filId = " + idF + ";");
						while (rs2.next()) {
							BddConnect.updateMessageStatus(rs2.getInt("idM"), StatutEnum.LU_PAR_TOUS.toString());
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	public void sendGroupsList() {
		ArrayList<String> groupsList = BddConnect.getListGroupes();
		this.sendObject(groupsList);
	}
	
	public void sendObject(Object obj) {
		
		try {
			this.output.writeUnshared(obj);
			this.output.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFddToAll(NetworkFilDeDiscussion networkFdd) {
        
        NetworkMessage networkMsg = networkFdd.getListeMessages().iterator().next();
        
        Groupe group = null;
        
        String groupName = networkFdd.getNomGroupe();
        
        for(Groupe groupFromList : server.getListeGroupes()) {
            if(groupFromList.getNom().equals(groupName))
                group = groupFromList;
        }
        
        this.targetUser.creerFilDeDiscussion(networkFdd.getTitre(), group, new Message(networkMsg.getContenu(), networkMsg.getDate(), networkMsg.getAuteur()));
        
        int idM = BddConnect.getNewIdM();
        
        BddConnect.updateMessageStatus(idM, StatutEnum.RECU_SERVEUR.toString());
        
        for(ClientManagerThread cliManager : Serveur.openThreads) {
            if(cliManager.getTargetUser() != null)
                cliManager.updateClientTree();
        }
    }
	
	public void sendMsgToAll(NetworkMessage networkMsg) {
		
		FilDeDiscussion fdd = new FilDeDiscussion(null, null, null);
		fdd.idFil = networkMsg.getIdF();
		
		Message message = new Message(networkMsg.getContenu(), networkMsg.getDate(), networkMsg.getAuteur());
		message.setNomComplet(networkMsg.getNomComplet());
		this.targetUser.envoyerMsg(message, fdd);
		
		int idM = BddConnect.getNewIdM();
		
		BddConnect.updateMessageStatus(idM, StatutEnum.RECU_SERVEUR.toString());

		for(ClientManagerThread cliManager : Serveur.openThreads) {
			if(cliManager.getTargetUser() != null)
				cliManager.updateClientTree();
		}
	}
	
	public void updateClientTree() {
		this.initUserTree();
		this.sendObject(this.networkHashMap);
	}
	
	public void verifyIdentifiers(String[] identifiers) {

			boolean isValid = server.connectionPossible(identifiers[0], identifiers[1]);
			
			String[] verification;
			
			String userData[] = new String[6];
			
			try {
				Statement stmt = BddConnect.con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Utilisateur WHERE userName = '" + identifiers[0] + "' ;");
				rs.next();
				
				userData[0] = rs.getString("userName");
				userData[1] = rs.getString("mdpU");
				userData[2] = rs.getString("prenomU");
				userData[3] = rs.getString("nomU");
				userData[4] = rs.getString("statusU");
				userData[5] = rs.getString("typeU");
				
				if(userData[4].equals(GroupTypeEnum.AGENT_USER))
					this.targetUser = new AgentUser(userData[0], userData[1], userData[2], userData[3]);
				else
					this.targetUser = new CampusUser(userData[0], userData[1], userData[2], userData[3]);

				if(isValid) {
					verification = new String[] {"accepted", this.targetUser.getPrenom(), this.targetUser.getNom()};
					this.targetUser.connecter();
				}
				else
					verification = new String[] {"", null, null};
				
			} catch (SQLException e) {
				e.printStackTrace();
				verification = new String[] {"", null, null};
			}
			
			try {
				output.writeObject(verification);
			} catch (IOException e) {
				System.out.println("Erreur de réponse vérif identifiants");
			}
	}
	
	public User getTargetUser() {
		return this.targetUser;
	}
	
	public void initUserTree() {
		
		this.networkHashMap.getGroupHashMap().clear();
		// Ceci est un test
		TreeMap<String, TreeMap<FilDeDiscussion, TreeSet<Message>>> tree = this.targetUser.getTreeUser();
		
		for(String g : tree.keySet()) {
			this.networkHashMap.addGroup(g);
			System.out.println("- " + g);
			for(FilDeDiscussion f : tree.get(g).keySet()) {
				System.out.println(" 	- " + f.titre);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				NetworkFilDeDiscussion fddTest = new NetworkFilDeDiscussion(f.titre, f.auteur, g);
				fddTest.setStatusFdd(f.getStatusFdd());
				
				this.networkHashMap.addFdd(fddTest);
				// on peut prendre l'auteur du fil de discussion pour le premier message
				
				for(Message m : tree.get(g).get(f)) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(" 		- " + m.getContenu() + " NOM = " + m.getNomComplet());
					NetworkMessage n = new NetworkMessage(m.getContenu(), m.getDate(), m.getAuteur());
					n.setNomComplet(m.getNomComplet());
					n.setStatusMsg(m.getStatusMsg());
					n.idM=m.idM;
					fddTest.addMsg(n);
				}
				fddTest.idF=f.idFil;

				if (! Serveur.hashRecu.containsKey(fddTest.idF))
					Serveur.hashRecu.put(fddTest.idF, new HashSet<User>());
				if (! Serveur.hashLu.containsKey(fddTest.idF))
					Serveur.hashLu.put(fddTest.idF, new HashSet<User>());
			}
		}
		
		System.out.println(" FDD CREE CLIENT MANAGER");
		System.out.println(" ARBRE ");
		for (String g : tree.keySet()) {
			System.out.println(" - " + g + " : ");
			for (NetworkFilDeDiscussion f1 : networkHashMap.getGroupHashMap().get(g)) {
				System.out.println("     - " + f1.getTitre() + " : ");
				for (NetworkMessage m : f1.getListeMessages())
					System.out.println("          " + m.idM + " : " + m.getContenu());
			}
		}
		System.out.println("--Fin--");
	}
}
