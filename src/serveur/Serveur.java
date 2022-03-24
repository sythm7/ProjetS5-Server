package serveur;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import data.BddConnect;
import data.FilDeDiscussion;
import data.Groupe;
import data.User;

public class Serveur {
	
	private static final int LISTENING_PORT = 6969;
	
	private ServerSocket listener = null;
	
	public ServerSocket getListener() {
		return listener;
	}

	private ObjectInputStream inputStream = null;
	
	private ObjectOutputStream outputStream = null;
	
	public static ArrayList<ClientManagerThread> openThreads = new ArrayList<>();
	
	public static HashMap<Integer, HashSet<User>> hashRecu = new HashMap<>();
	
	public static HashMap<Integer, HashSet<User>> hashLu = new HashMap<>();
	
	// On devra ajouter les utilisateurs à la liste depuis la base de données.
	
	private LinkedList<User> listeUtilisateurs = new LinkedList<>();
	
	private LinkedList<Groupe> listeGroupes = new LinkedList<>();
	
	public void ajouterFilDeDiscussion(FilDeDiscussion fdd) {
		
	}
	
	public void connecterUser(String identifiant, String motDePasse, String nom) {
		
	}
	
	public void deconnecterUser(String identifiant, String motDePasse, String nom) {
		
	}
	
	public void diffuserFilDeDiscussion(FilDeDiscussion filDeDiscussion) {
		
	}
	
	// Méthode que j'ai rajoutée pour ajouter les utilisateurs à la liste en lisant la base de données
	public void ajouterUsers() {
		this.listeUtilisateurs = BddConnect.getUsers();
	}
	
	public void ajouterGroups() {
		this.listeGroupes = BddConnect.getGroupes();
	}
	
	public LinkedList<User> getListeUsers() {
		return this.listeUtilisateurs;
	}
	
	public LinkedList<Groupe> getListeGroupes() {
		return this.listeGroupes;
	}
	
	public boolean connectionPossible(String identifiant, String mdp) {
		try {
			PreparedStatement set = BddConnect.con.prepareStatement("SELECT COUNT(*) as a FROM Utilisateur WHERE userName = ? AND mdpU = ?;");
			set.setString(1, identifiant);
			set.setString(2, mdp);
			ResultSet rs = set.executeQuery();
			rs.next();
			int a = rs.getInt("a");
			return a==1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void waitForConnections() {
		
		Socket connectionSocket = null;
		this.inputStream = null;
		this.outputStream = null;
		
		try {
			listener = new ServerSocket(Serveur.LISTENING_PORT);
		}
		
		catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur d'ouverture du socket", "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		System.out.println("A l'ecoute sur le port " + Serveur.LISTENING_PORT);
		
		while(InterfaceServeur.isOpen) {
			try {
				System.out.println("En attente d'une connexion...");

	           // Accept client connection request
	           // Get new Socket at connectionSocket.
	           
	           connectionSocket = listener.accept();
	           System.out.println("Client accepté !");

	           // Open input and output streams
	           
	           InputStream input = connectionSocket.getInputStream();
	           OutputStream output = connectionSocket.getOutputStream();
	           
	           outputStream = new ObjectOutputStream(output);
	           
	           inputStream = new ObjectInputStream(input);
	           
	           ClientManagerThread clientManager = new ClientManagerThread(this);
	           Serveur.openThreads.add(clientManager);
	           clientManager.start();
			}
			catch (IOException e) {
				System.out.println("SocketListener fermé");
			}
		}
		
		for(User user : BddConnect.getUsersConnectes())
			user.deconnecter();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		
		//BddConnect.clear();
		
		BddConnect.init();
		
		Serveur serveur = new Serveur();
		
		InterfaceServeur interfaceServeur = new InterfaceServeur(serveur);
		
		// Rajoute de l'anti-aliasing au texte (moins pixellisé)
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");
		
		serveur.ajouterUsers();
		serveur.ajouterGroups();
		
		SwingUtilities.invokeLater(() -> {
				interfaceServeur.initialiser();
		});
		
		serveur.waitForConnections();
	}

	public ObjectInputStream getObjectInputStream() {
		return inputStream;
	}

	public ObjectOutputStream getObjectOutputStream() {
		return outputStream;
	}
}