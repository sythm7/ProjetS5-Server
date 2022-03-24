package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import data.AgentUser;
import data.CampusUser;
import data.User;
import serveur.InterfaceServeur;

public class UsersListMouseListener implements MouseListener {
	
	private InterfaceServeur frame;
	
	public UsersListMouseListener(InterfaceServeur frame) {
		this.frame = frame;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.update(e);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// Pas de necessite d'utilisation
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// Pas de necessite d'utilisation
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// Pas de necessite d'utilisation
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// Pas de necessite d'utilisation
	}
	
	private void update(MouseEvent e) {
		@SuppressWarnings("unchecked")
		JList<User> listeUsers = (JList<User>) e.getComponent();
		this.frame.setCurrentUser(listeUsers.getSelectedValue());
		
		User user = this.frame.getCurrentUser();
		
		int width = (int) (frame.getSize().getWidth() - InterfaceServeur.SCROLLPANE_WIDTH - 20);
		
		int labelWidth = (int) (width * 0.75);
		
		int defaultWidth = (InterfaceServeur.DEFAULT_WIDTH - InterfaceServeur.SCROLLPANE_WIDTH - 20);
		
		float labelSize;
		
		if(width > defaultWidth * 1.4f)
			labelSize = 1.15f;
		else if(width < (defaultWidth / 1.4f))
			labelSize = 0.55f;
		else
			labelSize =  ((float) width / defaultWidth) * 0.75f;
		
		if(user != null) {
			String usernameText = "<html><h1 align='center' STYLE=\"color: rgb(127, 226, 196); font-size: " + labelSize + "em; width: " + labelWidth + "px;\">" + user + "<br>";
			usernameText += user instanceof AgentUser ? "<h2 align='center'>(Agent User)</h2></p></html>" : "<h2 align='center'>(Campus User)</h2></p></html>";
			
			this.frame.setUsernameLabel(usernameText);
			if(! this.frame.getSelectedUserScrollPane().isVisible() ) {
				this.frame.getSelectedUserScrollPane().setVisible(true);
				this.frame.getContentPane().validate();
				this.frame.getContentPane().repaint();		
			}
		}
		this.frame.clearPasswordField();
	}
}