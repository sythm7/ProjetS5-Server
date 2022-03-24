package serveur;

import javax.swing.Box;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.awt.Font;
import java.awt.Component;
import java.awt.Color;

import data.AgentUser;
import data.Groupe;
import data.User;
import listeners.UsersListMouseListener;
import userframe.AddGroupFrame;
import userframe.AddUserFrame;
import userframe.DelUserFrame;
import userframe.GroupManagementFrame;

public class InterfaceServeur extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5676798506746339445L;

	public static boolean isOpen = true;
	
	public static final Color COULEUR_INTERFACE = new Color(33, 37, 43);
	
	public static final Color COULEUR_TEXTE = new Color(221, 228, 241);
	
	public static final Color COULEUR_COMPOSANTS = new Color(53, 59, 68);
	
	public static final String STYLE_TEXTE = "Arial";
	
	public static final int DEFAULT_WIDTH = 800;
	
	public static final int DEFAULT_HEIGHT = 600;
	
	private final int MENU_SIZE = 3;
	
	public static final int SCROLLPANE_WIDTH = 220;
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu menu = new JMenu("Menu");
	
	private JMenuItem menuItemsTab[] = new JMenuItem[MENU_SIZE];
	
	private JPanel mainPanel = new JPanel(new BorderLayout());
	
	private DefaultListModel<User> usersListModel = new DefaultListModel<>();
	
	private JList<User> usersList = new JList<>(this.usersListModel);
	
	private JPanel leftPanel = new JPanel();
	
	private DefaultListModel<Groupe> groupListModel = new DefaultListModel<>();
	
	private JList<Groupe> groupsList = new JList<>(this.groupListModel);
	
	private JScrollPane groupsScrollPane = new JScrollPane(this.groupsList);
	
	private BoxLayout leftBoxLayout = new BoxLayout(this.leftPanel, BoxLayout.Y_AXIS);
	
	private GridLayout gridLayout = new GridLayout();
	
	private JPanel groupGestionPanel = new JPanel(this.gridLayout);
	
	private JButton manageGroupsButton = new JButton("Gérer le groupe sélectionné");
	
	private JScrollPane usersScrollPane = new JScrollPane(this.usersList);
	
	private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.usersScrollPane, this.groupsScrollPane);
	
	private JPanel splitPanePanel = new JPanel(this.gridLayout);
	
	private BorderLayout borderLayout = new BorderLayout();
	
	private JPanel centerPanel = new JPanel(this.borderLayout);
	
	private JPanel childCenterPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
	
	private JLabel userNameLabel = new JLabel();
	
	private JPanel childCenterPanel2 = new JPanel();
	
	private BoxLayout boxLayoutCenter = new BoxLayout(this.childCenterPanel2, BoxLayout.Y_AXIS);
	
	private JScrollPane selectedUserScrollPane = new JScrollPane(this.centerPanel);
	
	private JLabel mdpModifLabel = new JLabel("Modifier le mot de passe");
	
	private JPasswordField mdpPasswordField = new JPasswordField();
	
	private JButton confirmerMdpButton = new JButton("OK");
	
	private User userCourant;
	
	private Serveur server;
	
	public InterfaceServeur(Serveur server) {
		this.server = server;
	}
	
	public void initialiser() {
		
		this.setTitle("ProjetS5 - Serveur");
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(InterfaceServeur.DEFAULT_WIDTH, InterfaceServeur.DEFAULT_HEIGHT);
		this.setLocationRelativeTo(null);
		this.setMinimumSize(new Dimension(500,400));
		
		this.addAllServerUsers();
		this.addAllServerGroups();
		
		this.initMainPanel();
		this.initLeftPanel();
		
		this.initComponents();
		this.initComponentsStyle();
		
		this.childCenterPanel1.add(this.userNameLabel);
		
		this.centerPanel.add(childCenterPanel1, BorderLayout.PAGE_START);
		
		this.childCenterPanel2.setLayout(this.boxLayoutCenter);
		this.childCenterPanel2.add(Box.createVerticalGlue());
		this.childCenterPanel2.add(this.mdpModifLabel);
		this.childCenterPanel2.add(InterfaceServeur.createFiller(15));
		this.childCenterPanel2.add(this.mdpPasswordField);		
		this.childCenterPanel2.add(InterfaceServeur.createFiller(15));		
		this.childCenterPanel2.add(this.confirmerMdpButton);		
		this.childCenterPanel2.add(Box.createVerticalGlue());		
		this.centerPanel.add(this.childCenterPanel2, BorderLayout.CENTER);
		
		this.groupGestionPanel.add(this.manageGroupsButton);
		this.groupGestionPanel.setPreferredSize(new Dimension(InterfaceServeur.SCROLLPANE_WIDTH, 50));
		this.groupGestionPanel.setMaximumSize(new Dimension(InterfaceServeur.SCROLLPANE_WIDTH, 50));
		this.leftPanel.setPreferredSize(new Dimension(InterfaceServeur.SCROLLPANE_WIDTH, Integer.MAX_VALUE));
		this.leftPanel.setMaximumSize(new Dimension(InterfaceServeur.SCROLLPANE_WIDTH, Integer.MAX_VALUE));
		
		this.usersScrollPane.setHorizontalScrollBar(null);
		this.groupsScrollPane.setHorizontalScrollBar(null);
		this.selectedUserScrollPane.setHorizontalScrollBar(null);
		
		this.usersScrollPane.setMinimumSize(new Dimension());
		this.groupsScrollPane.setMinimumSize(new Dimension());
		
		this.splitPanePanel.add(this.splitPane);
		
		this.addSelectionUserListener();
		this.addSelectionGroupListener();
		this.addFrameResizeListener();
		this.addButtonsListeners();
		this.addCloseButtonListener();
		
		this.creerMenu();
		
		this.add(this.mainPanel);
		
		this.selectedUserScrollPane.setVisible(false);
		this.setVisible(true);
	}
	
	private void addAllServerUsers() {
		for(User user : this.server.getListeUsers()) {
			this.ajouterUser(user);
		}
	}
	
	private void addAllServerGroups() {
		for(Groupe groupe : this.server.getListeGroupes()) {
			this.addGroup(groupe);
		}
	}
	
	private void initMainPanel() {
		
		this.mainPanel.add(this.leftPanel, BorderLayout.LINE_START);
		
		this.mainPanel.add(this.selectedUserScrollPane, BorderLayout.CENTER);
	}
	
	private void initLeftPanel() {
		
		this.leftPanel.setLayout(this.leftBoxLayout);
		
		this.leftPanel.add(this.splitPanePanel);
		
		this.leftPanel.add(this.groupGestionPanel);
	}
	
	private void initComponentsStyle() {
		
		this.mainPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.groupGestionPanel.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.childCenterPanel1.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.childCenterPanel2.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.confirmerMdpButton.setBackground(InterfaceServeur.COULEUR_COMPOSANTS);
		this.confirmerMdpButton.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.usersScrollPane.getHorizontalScrollBar().setBackground(InterfaceServeur.COULEUR_COMPOSANTS);	
		this.usersScrollPane.getVerticalScrollBar().setBackground(InterfaceServeur.COULEUR_COMPOSANTS);
		this.groupsScrollPane.getHorizontalScrollBar().setBackground(InterfaceServeur.COULEUR_COMPOSANTS);	
		this.groupsScrollPane.getVerticalScrollBar().setBackground(InterfaceServeur.COULEUR_COMPOSANTS);
		this.selectedUserScrollPane.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.usersList.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.usersList.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.menuBar.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.menu.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.manageGroupsButton.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.manageGroupsButton.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.groupsList.setBackground(InterfaceServeur.COULEUR_INTERFACE);
		this.groupsList.setForeground(InterfaceServeur.COULEUR_TEXTE);
	}
	
	private void initComponents() {
		
		this.userNameLabel.setHorizontalTextPosition(JLabel.CENTER);
		this.userNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.userNameLabel.setFont(new Font(InterfaceServeur.STYLE_TEXTE, Font.PLAIN,  40));
		this.userNameLabel.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.userNameLabel.setVerticalAlignment(JLabel.CENTER);
		this.mdpModifLabel.setFont(new Font(InterfaceServeur.STYLE_TEXTE, Font.BOLD,  20));
		this.mdpModifLabel.setForeground(InterfaceServeur.COULEUR_TEXTE);
		this.mdpModifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.splitPane.setDividerLocation(300);
		this.splitPane.setResizeWeight(0.5);
		this.mdpPasswordField.setMaximumSize(new Dimension(140,20));
		this.confirmerMdpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.usersList.setFont(new Font(InterfaceServeur.STYLE_TEXTE, Font.PLAIN, 20));
		this.manageGroupsButton.setVisible(false);
	}
	
	private void creerMenu() {
		this.menuItemsTab[0] = new JMenuItem("Ajouter un utilisateur");
		this.menuItemsTab[1] = new JMenuItem("Supprimer un utilisateur");
		this.menuItemsTab[2] = new JMenuItem("Ajouter un groupe");
		
		this.createMenuItemListeners();
		
		for(int i = 0; i < MENU_SIZE; i++) {
			this.menuItemsTab[i].setBackground(InterfaceServeur.COULEUR_INTERFACE);
			this.menuItemsTab[i].setForeground(InterfaceServeur.COULEUR_TEXTE);
			this.menu.add(this.menuItemsTab[i]);
		}
		
		this.menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}
	
	private void addButtonsListeners() {
		this.confirmerMdpButton.addActionListener(event -> {
			String motDePasse = String.copyValueOf(mdpPasswordField.getPassword());
			if(motDePasse.length() < 5)
				JOptionPane.showMessageDialog(this, "Mot de passe invalide (5 caractères minimum requis)", "Erreur modification mot de passe", JOptionPane.ERROR_MESSAGE);
			else {
				userCourant.setMotDePasse(motDePasse);
				JOptionPane.showMessageDialog(this, "Le mot de passe a été modifié avec succès", "Modification mot de passe", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		this.manageGroupsButton.addActionListener(event -> {
			Groupe selectedGroup = this.groupsList.getSelectedValue();
			
			GroupManagementFrame groupManagementFrame = new GroupManagementFrame(this, selectedGroup);
			groupManagementFrame.confirmAndShow();
			
		});
	}
	
	private void addCloseButtonListener() {
		
		JFrame frame = this;
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        frame.dispose();
		        InterfaceServeur.isOpen = false;
		        try {
					server.getListener().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
	}
	
	private void addSelectionUserListener() {
		this.usersList.addMouseListener(new UsersListMouseListener(this));
	}
	
	private void addSelectionGroupListener() {
		this.groupsList.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(groupsList.getSelectedValue() != null)
					manageGroupsButton.setVisible(true);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
			
		});
	}
	
	private void createMenuItemListeners() {
		this.menuItemsTab[0].addActionListener(event -> {
			AddUserFrame addUserFrame = new AddUserFrame(this);
			addUserFrame.confirmAndShow();
		});
		this.menuItemsTab[1].addActionListener(event -> {
			DelUserFrame delUserFrame = new DelUserFrame(this);
			delUserFrame.confirmAndShow();
		});
		this.menuItemsTab[2].addActionListener(event -> {
			AddGroupFrame addGroupFrame = new AddGroupFrame(this);
			addGroupFrame.confirmAndShow();
		});
	}
	
	private void addFrameResizeListener() {
		this.addComponentListener(new ResizeFrameListener());
	}
	
	public void ajouterUser(User user) {
			this.usersListModel.addElement(user);
	}
	
	public void supprimerUser(User user) {
		this.usersListModel.removeElement(user);
	}
	
	public void addGroup(Groupe groupe) {
		this.groupListModel.addElement(groupe);
	}
	
	public DefaultListModel<User> getUsersListModel() {
		return this.usersListModel;
	}
	
	public DefaultListModel<Groupe> getGroupsListModel() {
		return this.groupListModel;
	}
	
	public JPanel getCenterPanel() {
		return this.centerPanel;
	}
	
	public JScrollPane getSelectedUserScrollPane() {
		return this.selectedUserScrollPane;
	}
	
	public JList<User> getUsersList() {
		return this.usersList;
	}
	
	public JList<Groupe> getGroupsList() {
		return this.groupsList;
	}
	
	public void setCurrentUser(User user) {
		this.userCourant = user;
	}
	
	public User getCurrentUser() {
		return this.userCourant;
	}
	
	public JButton getManageGroupsButton() {
		return this.manageGroupsButton;
	}
	
	public void setUsernameLabel(String text) {
		this.userNameLabel.setText(text);
	}
	
	public void clearPasswordField() {
		this.mdpPasswordField.setText("");
	}
	
	public static Component createFiller(int ySize) {
		
		return Box.createRigidArea(new Dimension(0, ySize));
	}
	
	public Serveur getServer() {
		return this.server;
	}
	
	private class ResizeFrameListener extends ComponentAdapter {
		
		private boolean isTimerDone = false;
		
		// Timer pour ralentir le render toutes les 50 ms
		private Timer timer = new Timer(50, e -> {
			isTimerDone = true;
		});
		
		public ResizeFrameListener() {
			timer.start();
		}
		
		@Override
        public void componentResized(ComponentEvent e) {
			
			// Ralentit le render en autorisant un render toutes les 50ms (Pour �viter de consommer trop de ressources)
			if(this.isTimerDone) {
				
				int width = (int) (getSize().getWidth() - InterfaceServeur.SCROLLPANE_WIDTH - 20);
				
				int labelWidth = (int) (width * 0.75);
				
				int defaultWidth = (InterfaceServeur.DEFAULT_WIDTH - InterfaceServeur.SCROLLPANE_WIDTH - 20);
				
				float labelSize;
				
				if(width > defaultWidth * 1.4f)
					labelSize = 1.15f;
				else if(width < (defaultWidth / 1.4f))
					labelSize = 0.55f;
				else
					labelSize =  ((float) width / defaultWidth) * 0.75f;
				
				if(getCurrentUser() != null) {
					String usernameText = "<html><h1 align='center' STYLE=\"color: rgb(127, 226, 196); font-size: " + labelSize + "em; width: " + labelWidth + "px;\">" + getCurrentUser() + "<br>";
					usernameText += getCurrentUser() instanceof AgentUser ? "<h2 align='center'>(Agent User)</h2></p></html>" : "<h2 align='center'>(Campus User)</h2></p></html>";
					
					setUsernameLabel(usernameText);
				}
				
				usersList.setCellRenderer(new CustomListRenderer<>(140, 15));
				groupsList.setCellRenderer(new CustomListRenderer<>(140, 15));
				
	            this.isTimerDone = false;
			}
        }
	}
}