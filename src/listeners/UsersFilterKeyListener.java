package listeners;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JTextField;

import data.User;
import userframe.GroupManagementFrame;

public class UsersFilterKeyListener implements KeyListener {
	
	private DefaultListModel<User> model;
	private DefaultListModel<User> filtreModel;
	private GroupManagementFrame groupManagementFrame = null;
	
	public UsersFilterKeyListener(DefaultListModel<User> model, DefaultListModel<User> filtreModel) {
		this.model = model;
		this.filtreModel = filtreModel;
	}

	public UsersFilterKeyListener(DefaultListModel<User> model, DefaultListModel<User> filtreModel, GroupManagementFrame groupManagementFrame) {
		this.model = model;
		this.filtreModel = filtreModel;
		this.groupManagementFrame = groupManagementFrame;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// Non necessaire
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Non necessaire
	}

	@Override
	public void keyReleased(KeyEvent e) {
		LinkedList<User> selectedUsers = new LinkedList<>();
		
		JTextField saisieTextField = (JTextField) e.getSource();
		
		String text = saisieTextField.getText().toLowerCase();
		
		for(Object o : this.model.toArray()) {
			User user = (User) o;
			
			String nomPrenom = user.getNom().toLowerCase() + " " + user.getPrenom().toLowerCase();
			
			if(nomPrenom.contains(text))
				selectedUsers.add(user);
		}
		
		this.filtreModel.removeAllElements();
		for(User user : selectedUsers) {
			if(groupManagementFrame != null) {
				if(groupManagementFrame.getSelectedGroupe().getGroupType() == user.getTypeUser())
					this.filtreModel.addElement(user);
				if(groupManagementFrame.getSelectedUsersList().contains(user)) //Si user déjà 
					this.filtreModel.removeElement(user);
			} else
				this.filtreModel.addElement(user);
		}
	}
}
