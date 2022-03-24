package enums;

import java.io.Serializable;

public enum StatutEnum implements Serializable {
	
	// Pas recu par le serveur
	EN_ATTENTE,
	
	// Pas recu par les utilisateurs
	RECU_SERVEUR,
	
	// Pas lu par tous les utilisateurs
	NON_LU_PAR_TOUS,
	
	// Lu par tous les utilisateurs
	LU_PAR_TOUS;
	
	private static final long serialVersionUID = 42L;
}