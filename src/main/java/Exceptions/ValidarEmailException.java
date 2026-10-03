package Exceptions;

/** Representa una dirección de correo que no cumple el patrón esperado. */
public class ValidarEmailException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Crea la excepción con el correo que no superó la validación. */
	public ValidarEmailException(String mensaje) {
        super(mensaje);
    }
}
