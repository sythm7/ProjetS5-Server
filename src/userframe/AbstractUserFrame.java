package userframe;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import serveur.InterfaceServeur;

public abstract class AbstractUserFrame<E> extends JFrame {

	/**
	 * On doit generer un serial version ID quand on extends JFrame sinon warning
	 */
	private static final long serialVersionUID = 8491618087986280009L;
	
	private String frameTitle;

	private JPanel mainPanel = new JPanel();
	
	private JPanel buttonsPanel = new JPanel(new FlowLayout());
	
	private InterfaceServeur parentFrame;
	
	private JButton confirmButton = new JButton();
	
	private JButton cancelButton = new JButton();
	
	private String[] buttonsTexts = null;
	
	private DefaultListModel<E> model;

	private ActionListener confirmListener = null;
	
	protected AbstractUserFrame(InterfaceServeur parentFrame, String frameTitle, String[] buttonsTexts, DefaultListModel<E> model) {
		this.frameTitle = frameTitle;
		this.parentFrame = parentFrame;
		this.buttonsTexts = buttonsTexts;
		this.model = model;
		this.initialiser();
	}
	
	private void initialiser() {
		
		this.setTitle(this.frameTitle);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(this.parentFrame);
		
		this.setAlwaysOnTop(true);
		
		this.confirmButton.setText(buttonsTexts[0]);
		this.cancelButton.setText(buttonsTexts[1]);
		this.parentFrame.setEnabled(false);
		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));
		this.mainPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.mainPanel.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.confirmButton.setBackground(InterfaceServeur.COULEUR_COMPOSANTS);
		this.cancelButton.setBackground(InterfaceServeur.COULEUR_COMPOSANTS);
		this.confirmButton.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.cancelButton.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.buttonsPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.buttonsPanel.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.buttonsPanel.add(this.confirmButton);
		this.buttonsPanel.add(this.cancelButton);
		
		this.add(this.mainPanel);
		
		this.addCloseButtonListener();
	}
	
	protected void setLabelStyle(JLabel label) {
		label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		label.setFont(new Font(InterfaceServeur.STYLE_TEXTE, Font.PLAIN, 15));
		label.setForeground(InterfaceServeur.COULEUR_TEXTE);
	}
	
	private void addButtonsListeners() {
			
		this.confirmButton.addActionListener(this.confirmListener);
		
		this.cancelButton.addActionListener(event -> {
			this.parentFrame.setEnabled(true);
			this.dispose();
		});
	}
	
	// Re-active le frame parentFrame avant de fermer celui ci
	private void addCloseButtonListener() {
		
		JFrame frame = this;
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        parentFrame.setEnabled(true);
		        frame.dispose();
		    }
		});
	}
	
	public void addToMainPanel(Component component) {
		this.mainPanel.add(component);
	}
	
	public void addToButtonsPanel(Component component) {
		this.buttonsPanel.add(component);
	}
	
	public void addModelElement(E element) {
		this.model.addElement(element);
	}
	
	public void removeModelElement(E element) {
		this.model.removeElement(element);
	}
	
	public DefaultListModel<E> getModel() {
		return this.model;
	}
	
	public InterfaceServeur getParentFrame() {
		return this.parentFrame;
		
	}
	
	public void deleteCancelButton() {
		this.buttonsPanel.remove(this.cancelButton);
	}

	public void setConfirmListener(ActionListener confirmListener) {
		this.confirmListener = confirmListener;
	}
	
	public void confirmAndShow() {
		this.mainPanel.add(this.buttonsPanel);
		this.addButtonsListeners();
		this.setVisible(true);
	}
}
