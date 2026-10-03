package Exceptions;

/** Representa un DNI ausente o que no supera su validación. */
public class ValidarDniException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Crea la excepción con el detalle del fallo de validación. */
	public ValidarDniException(String mensaje) {
		super(mensaje);
	}
}
