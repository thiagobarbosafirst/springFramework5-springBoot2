package br.gov.go.sefaz.pat.procuracao;

import java.security.PublicKey;

import br.gov.go.sefaz.javaee.commons.support.Criptografia;
import br.gov.go.sefaz.javaee.security.jwt.support.RSALoaderSupport;

public class RSACryptographyTest {

public static void main(String[] args) throws Exception {
		
		System.setProperty("weblogic.Name", "RSACryptographyTest");
		

		PublicKey publicKey = RSALoaderSupport.loadPublicKey("br/gov/go/sefaz/pat/admin/portalsefaz-public-key-desenv.der");
		
//		PAT
		String password = "OYYECOLSKI4GI4KB";
		
		System.out.println("Text Plain Password: " + password);
		
		byte[] passwordAsByteArray = password.getBytes();
		
		String encriptedBase64Password = Criptografia.encriptarRSA(publicKey, passwordAsByteArray);
		
		System.out.println("Encripted Password: " + encriptedBase64Password);
		
	}
}
