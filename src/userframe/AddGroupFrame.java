package userframe;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import data.Groupe;
import enums.GroupTypeEnum;
import serveur.InterfaceServeur;

public class AddGroupFrame extends AbstractUserFrame<Groupe> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4670410912746277859L;
	
	private final int width = 300;
	
	private final int height = 240;
	
	private JLabel groupNameLabel = new JLabel("Nom du groupe");
	
	private JTextField groupNameTextField = new JTextField();
	
	private JComboBox<GroupTypeEnum> groupTypeComboBox = new JComboBox<>(new GroupTypeEnum[] {GroupTypeEnum.AGENT_USER, GroupTypeEnum.CAMPUS_USER});

	public AddGroupFrame(InterfaceServeur parentFrame) {
		super(parentFrame, "Ajouter un groupe", new String[] {"Ajouter", "Annuler"}, parentFrame.getGroupsListModel());
		this.setConfirmListener(new ConfirmAddGroupListener(this));
		this.initialiser();
	}

	private void initialiser() {
		
		this.setSize(this.width, this.height);
		
		Point position = this.getLocation();
		
		this.setLocation((int)position.getX() - width / 2, (int)position.getY() - height / 2);
		
		this.addToMainPanel(InterfaceServeur.createFiller(20));
		
		this.addToMainPanel(groupNameLabel);
		
		this.addToMainPanel(Box.createVerticalGlue());
		
		this.addToMainPanel(groupNameTextField);
		
		this.addToMainPanel(Box.createVerticalGlue());
		
		this.setLabelStyle(this.groupNameLabel);
		
		this.addToMainPanel(Box.createVerticalGlue());
		
		this.addToMainPanel(this.groupTypeComboBox);
		
		this.addToMainPanel(InterfaceServeur.createFiller(20));
		
		this.groupNameTextField.setMaximumSize(new Dimension(160, 0));
		this.groupTypeComboBox.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.groupTypeComboBox.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.groupTypeComboBox.setPreferredSize(new Dimension(160, 25));
		this.groupTypeComboBox.setMaximumSize(new Dimension(160, 25));
	}
	
	private class ConfirmAddGroupListener implements ActionListener {

		private String errMsg;
		
		private AddGroupFrame userFrame;
		
		public ConfirmAddGroupListener(AddGroupFrame userFrame) {
			this.userFrame = userFrame;
		}
		
		public boolean isGroupValid(String groupName) {
			
			if(groupName.isEmpty()) {
				this.errMsg = "Groupe invalide";
				return false;
			}
			
			for(Groupe groupe : getParentFrame().getServer().getListeGroupes()) {
				if(groupe.toString().equals(groupName)) {
					this.errMsg = "Groupe déjà existant";
					return false;
				}
			}
			
			return true;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String groupName = groupNameTextField.getText().trim();
			
			GroupTypeEnum groupType = (GroupTypeEnum) groupTypeComboBox.getSelectedItem();
			
			if(! isGroupValid(groupName)) {
				JOptionPane.showMessageDialog(this.userFrame, this.errMsg, "Erreur d'ajout du groupe", JOptionPane.ERROR_MESSAGE);
				return;
			}
				
			Groupe groupe = new Groupe(groupName, groupType);
			
			getModel().addElement(groupe);
			
			getParentFrame().getServer().getListeGroupes().add(groupe);

			getParentFrame().setEnabled(true);
	
			this.userFrame.dispose();
		}
	}
}