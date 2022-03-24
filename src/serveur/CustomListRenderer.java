package serveur;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import java.awt.Component;
import java.awt.Font;
import java.awt.Color;

public class CustomListRenderer<T> implements ListCellRenderer<T> {
	
	private int width;
	
	private int fontSize;
	
	private JLabel label;
	
	private Color selectedColor = new Color(54, 60, 70);

	public CustomListRenderer(int width, int fontSize) {
		this.width = width;
		this.fontSize = fontSize;
	}
	
	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
		
		JPanel panel = new JPanel();
		
		panel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		
		if (isSelected)
	        panel.setBackground(this.selectedColor);
		
		String message = "<html><h1 STYLE=\"font-size: 1em; width: %1spx; text-align: center; margin: 5px;\">" + value.toString() + "</h1></html>";
		
		message = String.format(message, this.width);
		
		this.label = new JLabel(message);
		this.label.setVerticalAlignment(SwingConstants.CENTER);
		this.label.setFont(new Font(InterfaceServeur.STYLE_TEXTE, Font.PLAIN, this.fontSize));
		this.label.setForeground(InterfaceServeur.COULEUR_TEXTE);
		
		panel.add(this.label);
		
		return panel;
	}
}