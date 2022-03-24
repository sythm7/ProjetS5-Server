package userframe;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import data.BddConnect;
import data.User;
import listeners.UsersFilterKeyListener;
import serveur.CustomListRenderer;
import serveur.InterfaceServeur;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Dimension;
import java.awt.Point;

public class DelUserFrame extends AbstractUserFrame<User> {

	/**
	 * On doit generer un serial version ID quand on extends JFrame sinon warning
	 */
	private static final long serialVersionUID = 6543303102355027839L;
	
	private final int width = 360;
	
	private final int height = 380;
	
	private JLabel rechercherLabel = new JLabel("Rechercher");
	
	private JTextField saisieTextField = new JTextField();
	
	private DefaultListModel<User> filtreModel = null;

	private JList<User> filtreUsersJList = new JList<>();
	
	private JScrollPane scrollPane = new JScrollPane(this.filtreUsersJList);
	
	private ConfirmDelUserListener confirmDelUserListener = new ConfirmDelUserListener();
	
	public DelUserFrame(InterfaceServeur parentFrame) {
		super(parentFrame, "Supprimer utilisateur", new String[] {"Supprimer", "Retour"}, parentFrame.getUsersListModel());
		this.filtreModel = this.getFilterModel();
		this.filtreUsersJList.setModel(this.filtreModel);
		this.setConfirmListener(this.confirmDelUserListener);
		this.initialiser();
	}
	
	// Cree un frame qui agit comme une sorte de JOptionPane personnalise, avec une saisie de texte pour filtrer les utilisateurs, 
	// une liste d'utilisateurs selectionnables, deux boutons Supprimer et Retour. Frame toujours visible et rend le frame parentFrame non cliquable.
	private void initialiser() {
		
		this.setSize(width, height);
		
		Point position = this.getLocation();
		
		this.setLocation((int)position.getX() - width / 2, (int)position.getY() - height / 2);
		
		this.setLabelStyle(this.rechercherLabel);
		this.initMainPanel();
		this.initComponents();
		this.addTextFieldListener();
	}
	
	private void initMainPanel() {
		
		this.addToMainPanel(Box.createVerticalGlue());
		this.addToMainPanel(this.rechercherLabel);
		this.addToMainPanel(InterfaceServeur.createFiller(6));
		this.addToMainPanel(this.saisieTextField);
		this.addToMainPanel(Box.createVerticalGlue());
		this.addToMainPanel(this.scrollPane);
		this.addToMainPanel(Box.createVerticalGlue());
	}
	
	private void initComponents() {
		
		this.saisieTextField.setMaximumSize(new Dimension(160,0));
		this.filtreUsersJList.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.filtreUsersJList.setVisibleRowCount(5);
		this.filtreUsersJList.setCellRenderer(new CustomListRenderer<>(150, 12));
		this.scrollPane.setMaximumSize(new Dimension(300,0));
	}
	
	private void addTextFieldListener() {
		this.saisieTextField.addKeyListener(new UsersFilterKeyListener(this.getModel(), this.filtreModel));
	}
	
	private DefaultListModel<User> getFilterModel() {
		
		DefaultListModel<User> filterModel = new DefaultListModel<>();
		for(Object user : this.getModel().toArray()) {
			filterModel.addElement((User)user);
		}
		return filterModel;
	}
	
	private class ConfirmDelUserListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			User selectedUser = filtreUsersJList.getSelectedValue();
			User usersListSelectedUser = getParentFrame().getUsersList().getSelectedValue();
			removeModelElement(selectedUser);
			filtreModel.removeElement(selectedUser);
			getParentFrame().getServer().getListeUsers().remove(selectedUser);
			
			BddConnect.deleteUser(selectedUser);
			
			if(usersListSelectedUser != null && usersListSelectedUser.equals(selectedUser))
				getParentFrame().getSelectedUserScrollPane().setVisible(false);
		}
	}
}