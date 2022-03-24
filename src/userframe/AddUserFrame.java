package userframe;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import data.AgentUser;
import data.CampusUser;
import data.User;
import serveur.InterfaceServeur;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddUserFrame extends AbstractUserFrame<User> {

	/**
	 * On doit generer un serial version ID quand on extends JFrame sinon warning
	 */
	private static final long serialVersionUID = -823367268563273198L;
	
	private final int width = 300;
	
	private final int height = 400;
	
	private final int fillerSize = 12;
	
	private JLabel idLabel = new JLabel("Identifiant");
	
	private JTextField idTextField = new JTextField();
	
	private JLabel nomLabel = new JLabel("Nom");
	
	private JTextField nomTextField = new JTextField();
	
	private JLabel prenomLabel = new JLabel("Prénom");
	
	private JTextField prenomTextField = new JTextField();
	
	private JLabel mdpLabel = new JLabel("Mot de passe");
	
	private JPasswordField passwordField = new JPasswordField();
	
	private JComboBox<String> choixUserComboBox = new JComboBox<>(new String[] {"Agent User", "Campus User"});
	
	private ConfirmAddUserListener confirmAddUserListener = new ConfirmAddUserListener(this);
	
	public AddUserFrame(InterfaceServeur parentFrame) {
		super(parentFrame, "Ajouter utilisateur", new String[] {"Ajouter", "Annuler"}, parentFrame.getUsersListModel());
		this.setConfirmListener(confirmAddUserListener);
		this.initialiser();
	}
	
	// Cree un frame qui agit comme une sorte de JOptionPane personnalise, avec 3 saisies de texte, deux boutons Confirmer et Annuler, toujours visible et rend le frame parentFrame non cliquable. 
	private void initialiser() {
		
		this.setSize(width, height);
		
		Point position = this.getLocation();
		
		this.setLocation((int)position.getX() - width / 2, (int)position.getY() - height / 2);
		
		this.initMainPanel();
		this.initComponentsSize();
		this.initComponentsStyle();
	}
	
	private void initMainPanel() {
		
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.idLabel);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.idTextField);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.nomLabel);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.nomTextField);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.prenomLabel);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.prenomTextField);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.mdpLabel);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
		this.addToMainPanel(this.passwordField);
		this.addToMainPanel(InterfaceServeur.createFiller(20));
		this.addToMainPanel(this.choixUserComboBox);
		this.addToMainPanel(InterfaceServeur.createFiller(this.fillerSize));
	}
	
	private void initComponentsStyle() {
		
		this.setLabelStyle(this.idLabel);
		this.setLabelStyle(this.mdpLabel);
		this.setLabelStyle(this.nomLabel);
		this.setLabelStyle(this.prenomLabel);
		this.choixUserComboBox.setBackground(InterfaceServeur.COULEUR_COMPOSANTS);
		this.choixUserComboBox.setForeground(InterfaceServeur.COULEUR_TEXTE);
	}
	
	
	private void initComponentsSize() {
		
		this.idTextField.setMaximumSize(new Dimension(160,0));
		this.nomTextField.setMaximumSize(new Dimension(160,0));
		this.prenomTextField.setMaximumSize(new Dimension(160,0));
		this.passwordField.setMaximumSize(new Dimension(160,0));
		this.choixUserComboBox.setMaximumSize(new Dimension(160,0));
	}
	
	
	private class ConfirmAddUserListener implements ActionListener {
		
		private AddUserFrame userFrame;
		
		private String errMsg;
		
		public ConfirmAddUserListener(AddUserFrame userFrame) {
			this.userFrame = userFrame;
		}
		
		private boolean isIdentifiantUnique(String identifiant) {
			for(User user : getParentFrame().getServer().getListeUsers()) {
				if(user.getIdentifiant().equals(identifiant))
					return false;
			}
			return true;
		}
		
		private boolean isUserValid(String identifiant, String prenom, String nom, String motDePasse) {
			if(identifiant.isEmpty()) {
				this.errMsg = "Identifiant invalide";
				return false;
			}
			
			if(! this.isIdentifiantUnique(identifiant)) {
				this.errMsg = "Identifiant déjà utilisé, veuillez en choisir un autre";
				return false;
			}
			
			if(nom.isEmpty()) {
				this.errMsg = "Nom invalide";
				return false;
			}
			
			if(prenom.isEmpty()) {
				this.errMsg = "Prénom invalide";
				return false;
			}
			
			if(motDePasse.length() < 5 || motDePasse.contains(" ")) {
				this.errMsg = "Mot de passe invalide (5 caractères minimum, pas d'espace)";
				return false;
			}
			
			return true;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String identifiant = idTextField.getText().trim();
			String nom = nomTextField.getText().trim();
			String prenom = prenomTextField.getText().trim();
			String motDePasse = String.copyValueOf(passwordField.getPassword());
			String selectedUser = choixUserComboBox.getSelectedItem().toString().trim();
			
			if(! isUserValid(identifiant, prenom, nom, motDePasse))
				JOptionPane.showMessageDialog(userFrame, errMsg, "Erreur ajout utilisateur", JOptionPane.ERROR_MESSAGE);
			
			else {
				User nouvelUser;
				nouvelUser = selectedUser.equals("Agent User") ? new AgentUser(identifiant, motDePasse, prenom, nom) : new CampusUser(identifiant, motDePasse, prenom, nom);
				getParentFrame().getServer().getListeUsers().add(nouvelUser);
				addModelElement(nouvelUser);
				getParentFrame().setEnabled(true);
				dispose();
				
			}
		}
	}
}
