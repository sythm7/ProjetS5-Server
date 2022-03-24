package userframe;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import data.BddConnect;
import data.CampusUser;
import data.Groupe;
import data.User;
import enums.GroupTypeEnum;
import listeners.UsersFilterKeyListener;
import serveur.ClientManagerThread;
import serveur.CustomListRenderer;
import serveur.InterfaceServeur;
import serveur.Serveur;

public class GroupManagementFrame extends AbstractUserFrame<User> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -422668911362089024L;
	
	private int width = 600;
	
	private int height = 400;
	
	// Panel du centre
	private JPanel centerPanel = new JPanel(new GridLayout());
	//
	
	// Panel de gauche
	private JPanel leftPanel = new JPanel();
	
	private BoxLayout leftBoxLayout = new BoxLayout(this.leftPanel, BoxLayout.Y_AXIS);
	
	private JLabel usersFromGroupLabel = new JLabel("Utilisateurs du groupe");
	
	private JLabel groupNameLabel = new JLabel();
	
	private DefaultListModel<User> usersFromGroupModel = new DefaultListModel<>();
	
	private JList<User> usersFromGroupList = new JList<>(this.usersFromGroupModel);
	
	private JPanel leftButtonsPanel = new JPanel(new FlowLayout());
	
	private JButton deleteGroupButton = new JButton("<html>Supprimer<br>ce groupe</html>");
	
	private JButton deleteSelectedUserButton = new JButton("<html>Supprimer l'utilisateur<br>du groupe</html>");
	
	private JScrollPane usersFromGroupScrollPane = new JScrollPane(this.usersFromGroupList);
	//
	
	// Panel de droite
	private JPanel rightPanel = new JPanel();
	
	private BoxLayout rightBoxLayout = new BoxLayout(this.rightPanel, BoxLayout.Y_AXIS);
	
	private JLabel searchUserLabel = new JLabel("Rechercher un utilisateur");
	
	private JTextField searchUserTextField = new JTextField();
	
	private DefaultListModel<User> usersFilterModel = null;

	private JList<User> usersFilterList = new JList<>();
	
	private JScrollPane addUserToGroupScrollPane = new JScrollPane(this.usersFilterList);
	
	private JButton addUserToGroupButton = new JButton("<html>Ajouter l'utilisateur<br>au groupe</html>");
	//
	
	private ArrayList<User> usersListToDelete = new ArrayList<>();
	private ArrayList<User> usersListToAdd = new ArrayList<>();
	
	
	private Groupe selectedGroup;
	
	public GroupManagementFrame(InterfaceServeur parentFrame, Groupe selectedGroup) {
		super(parentFrame, "Gestion du groupe", new String[] {"OK", "Annuler"}, parentFrame.getUsersListModel());
		this.selectedGroup = selectedGroup;
		this.addUsersFromGroup();
		this.usersFilterModel = this.getUsersFilterModel();
		this.usersFilterList.setModel(this.usersFilterModel);
		this.setConfirmListener(new ConfirmButtonListener(this));
		this.initialiser();
	}
	
	private void initialiser() {
		
		this.setSize(this.width, this.height);
		
		Point position = this.getLocation();
		
		this.setLocation((int)position.getX() - width / 2, (int)position.getY() - height / 2);
		
		this.addToMainPanel(this.centerPanel);
		
		this.centerPanel.add(this.leftPanel);
		this.centerPanel.add(this.rightPanel);
		
		this.initLeftPanel();
		this.initRightPanel();
		
		this.initComponentsStyle();
		
		this.addTextFieldListener();
		this.addButtonsListeners();
	}
	
	private void initLeftPanel() {
		
		this.leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 10, 0));
		initGroupLabel();
		
		this.leftPanel.setLayout(this.leftBoxLayout);
		this.leftPanel.add(this.usersFromGroupLabel);
		this.leftPanel.add(InterfaceServeur.createFiller(10));
		this.leftPanel.add(this.groupNameLabel);
		this.leftPanel.add(InterfaceServeur.createFiller(10));
		this.leftPanel.add(this.usersFromGroupScrollPane);
		this.leftPanel.add(InterfaceServeur.createFiller(10));
		this.leftPanel.add(this.leftButtonsPanel);
		this.usersFromGroupScrollPane.setPreferredSize(new Dimension(240,160));
		this.usersFromGroupScrollPane.setMaximumSize(new Dimension(240,160));
		
		this.addUserToGroupScrollPane.setPreferredSize(new Dimension(240,160));
		this.addUserToGroupScrollPane.setMaximumSize(new Dimension(240,160));
		
		this.leftButtonsPanel.add(this.deleteGroupButton);
		this.leftButtonsPanel.add(this.deleteSelectedUserButton);
	}
	
	private void initGroupLabel() {
		
		String groupType = new String();
		if(this.selectedGroup.getGroupType() == GroupTypeEnum.AGENT_USER) {
			groupType = "AgentU";
			this.groupNameLabel.setText(this.selectedGroup.getNom() + " (" + groupType + ")");
		}else {
			groupType = "CampusU";
			this.groupNameLabel.setText(this.selectedGroup.getNom() + " (" + groupType + ")");
		}
	}

	private void initRightPanel() {
		
		this.rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 15));
		
		this.rightPanel.setLayout(this.rightBoxLayout);
		this.rightPanel.add(this.searchUserLabel);
		this.rightPanel.add(InterfaceServeur.createFiller(10));
		this.rightPanel.add(this.searchUserTextField);
		this.rightPanel.add(InterfaceServeur.createFiller(8));
		this.rightPanel.add(this.addUserToGroupScrollPane);
		this.rightPanel.add(InterfaceServeur.createFiller(14));
		this.rightPanel.add(this.addUserToGroupButton);	

		this.addUserToGroupButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		this.addUserToGroupButton.setPreferredSize(new Dimension(125, 35));
		this.addUserToGroupButton.setMaximumSize(new Dimension(125, 35));
		
		this.searchUserTextField.setMaximumSize(new Dimension(160, 20));
	}
	
	private void initComponentsStyle() {
		
		this.centerPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.leftPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.leftButtonsPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.rightPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.setLabelStyle(this.searchUserLabel);
		this.setLabelStyle(this.usersFromGroupLabel);
		this.setLabelStyle(this.groupNameLabel);
		this.groupNameLabel.setForeground(Color.red);
		this.usersFilterList.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.usersFromGroupList.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.usersFilterList.setCellRenderer(new CustomListRenderer<>(150, 11));
		this.usersFromGroupList.setCellRenderer(new CustomListRenderer<>(150, 11));
		this.addUserToGroupScrollPane.setHorizontalScrollBar(null);
		this.usersFromGroupScrollPane.setHorizontalScrollBar(null);
		
		this.initButtonStyle(this.addUserToGroupButton);
		this.initButtonStyle(this.deleteSelectedUserButton);
		this.initButtonStyle(this.deleteGroupButton);
	}
	
	private void initButtonStyle(JButton button) {
		button.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		button.setForeground(InterfaceServeur.COULEUR_TEXTE);
		button.setFont(new Font(InterfaceServeur.STYLE_TEXTE, Font.PLAIN, 11));
	}
	
	private void addButtonsListeners() {
		this.addUserToGroupButton.addActionListener(event -> {
			User selectedUser = this.usersFilterList.getSelectedValue();
			if(selectedUser == null) {
				JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à ajouter au groupe", "Erreur d'ajout au groupe", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
//			selectedGroup.addUser(selectedUser);
			this.usersFromGroupModel.addElement(selectedUser);
			this.usersFilterModel.removeElement(selectedUser);
			
			
			if(! selectedGroup.getUsersList().contains(selectedUser))
				this.usersListToAdd.add(selectedUser);
			
			this.usersListToDelete.remove(selectedUser);
			
			System.out.println("\nUsers qui vont etre ajoutés : " + usersListToAdd.toString());
			System.out.println("Users qui vont etre supprimés : " + usersListToDelete.toString() + "\n");
			
			
	
		});
		
		this.deleteSelectedUserButton.addActionListener(event -> {
			User selectedUser = this.usersFromGroupList.getSelectedValue();
			if(selectedUser == null) {
				JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à retirer du groupe", "Erreur de suppression de l'utilisateur", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(selectedGroup.getUsersList().contains(selectedUser))
				this.usersListToDelete.add(selectedUser);
			
			this.usersListToAdd.remove(selectedUser);
			
			System.out.println("\nUsers qui vont etre ajoutés : " + usersListToAdd.toString());
			System.out.println("Users qui vont etre supprimés : " + usersListToDelete.toString() + "\n");
			
//			selectedGroup.removeUser(selectedUser);
			this.usersFromGroupModel.removeElement(selectedUser);
			this.usersFilterModel.addElement(selectedUser);
		});
		
		this.deleteGroupButton.addActionListener(event -> {
			int choice = JOptionPane.showConfirmDialog(this, "Etes vous sur de vouloir supprimer ce groupe ?", "Confirmer la suppression du groupe", JOptionPane.YES_NO_OPTION);
			
			if(choice == JOptionPane.CANCEL_OPTION)
				return;

			selectedGroup.supprimerGroupe();
			
			for(ClientManagerThread cliManager : Serveur.openThreads) {
				cliManager.updateClientTree();
			}
			
			this.getParentFrame().getGroupsListModel().removeElement(this.selectedGroup);
			this.getParentFrame().getServer().getListeGroupes().remove(this.selectedGroup);
			this.getParentFrame().getManageGroupsButton().setVisible(false);
			this.getParentFrame().setEnabled(true);
			this.dispose();
		});
	}
	
	private DefaultListModel<User> getUsersFilterModel() {
		
		DefaultListModel<User> usersFilterModel = new DefaultListModel<>();
		for(Object user : this.getModel().toArray()) {
			if(! this.usersFromGroupModel.contains((User) user))
				if (user instanceof CampusUser) {
					if (this.selectedGroup.getGroupType()==GroupTypeEnum.CAMPUS_USER)
						usersFilterModel.addElement((User) user);
				}
				else {
					if (this.selectedGroup.getGroupType()==GroupTypeEnum.AGENT_USER)
						usersFilterModel.addElement((User) user);
				}
		}
		return usersFilterModel;
	}
	
	private void addTextFieldListener() {
		this.searchUserTextField.addKeyListener(new UsersFilterKeyListener(this.getModel(), this.usersFilterModel, this));
	}
	
	private void addUsersFromGroup() {

		for(User user : BddConnect.getGroupeUsers(this.selectedGroup.getNom())) {
			if (user instanceof CampusUser) {
				if (this.selectedGroup.getGroupType()==GroupTypeEnum.CAMPUS_USER)
					this.usersFromGroupModel.addElement(user);
			}
			else {
				if (this.selectedGroup.getGroupType()==GroupTypeEnum.AGENT_USER)
					this.usersFromGroupModel.addElement(user);
			}
		}
	}
	
	public Groupe getSelectedGroupe() {
		return this.selectedGroup;
	}
	
	public DefaultListModel<User> getSelectedUsersList() {
		return this.usersFromGroupModel;
	}
	
	private class ConfirmButtonListener implements ActionListener {

		GroupManagementFrame frame;
		
		public ConfirmButtonListener(GroupManagementFrame frame) {
			this.frame = frame;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			this.frame.getParentFrame().setEnabled(true);
			
//			if(this.frame.getSelectedGroupe() == null) {
//				for(User user : this.frame.getSelectedGroupe().getUsersList()) {
//					this.frame.selectedGroup.removeUser(user);
//					//update tree chez l'user
//				}
//				this.frame.dispose();
//				return;
//			}

//			LinkedList<User> userList = new LinkedList<>();
//			
//			Object[] selectedUsersList = this.frame.getSelectedUsersList().toArray();
			
//			for(Object user : selectedUsersList) {
//				userList.add((User) user);
//			}
			
			for(User user : usersListToAdd)
				this.frame.getSelectedGroupe().addUser(user);
			
			for(User user : usersListToDelete)
				this.frame.getSelectedGroupe().removeUser(user);
			
			for(ClientManagerThread cliManager : Serveur.openThreads)
				cliManager.updateClientTree();
			
//			this.frame.getSelectedGroupe().getUsersList().clear();
//			
//			this.frame.getSelectedGroupe().getUsersList().addAll(userList);
			
			this.frame.dispose();
		}
	}
}