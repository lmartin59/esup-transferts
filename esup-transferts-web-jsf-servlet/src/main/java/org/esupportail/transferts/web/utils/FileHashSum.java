/**
 *
 */
package org.esupportail.transferts.web.utils;

import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.transferts.web.controllers.UserController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Calcule et compare les empreintes MD5 et SHA-1 des fichiers.
 *
 * @author Mr-Hide (JB, http://www.mrhide.fr)
 * copyleft
 */
public class FileHashSum {
	/**
	 * A logger.
	 */
	private static final Logger logger = new LoggerImpl(FileHashSum.class);
	/**
	 * compare le <code>sha1sum</code> donné avec l'empreinte du fichier local <code>file</code>.
	 * @param file le fichier dont on doit calculer l'empreinte sha1
	 * @param sha1sum l'empreinte à comparer
	 * @return true si l'empreinte du fichier correspond à <code>sha1sum</code>
	 */
	public static boolean compareSha1sum(File file, String sha1sum) {
		boolean res = false;
		if (sha1sum.equals(sha1sum(file))) {
			res = true;
		}
		return res;
	}

	/**
	 * Retourne l'empreinte sha1 de fichier <code>file</code> ou null si le
	 * fichier n'a pas pu être lu.
	 * @param file le fichier dont on doit calculer l'empreinte sha1
	 * @return l'empreinte sha1
	 */
	public static String sha1sum(File file){
		String localSha1Sum = null;
		DigestInputStream dis=null;
		FileInputStream fis=null;
		if (file.exists() && file.isFile() && file.canRead()){
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-1");
				fis = new FileInputStream(file);
				dis = new DigestInputStream(fis, md);
				dis.on(true);
//				while (dis.read() != -1){
//					;
//				}
				byte[] b = md.digest();
				localSha1Sum = getHexString(b);
			} catch (Exception ex) {
				logger.error(ex);
			}finally {
				try {
					if(dis!=null)
						dis.close();
					if(fis!=null)
						fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return localSha1Sum;
	}

	/**
	 * Compare le <code>md5sum</code> donné avec l'empreinte du fichier local <code>file</code>.
	 * L'utilisation de MD5 est déconseillée ! Il existe en effet une faille de sécurité par collision sur MD5
	 * Utilisez plutôt SHA-1.
	 * @param file le fichier dont on doit calculer l'empreinte md5
	 * @param sha1sum l'empreinte à comparer
	 * @return true si l'empreinte du fichier correspond à <code>md5sum</code>
	 */
	public static boolean compareMd5sum(File file, String md5sum) {
		boolean res = false;
		if (md5sum.equals(md5sum(file))){
			res = true;
		}
		return res;
	}

	/**
	 * Retourne l'empreinte md5 de fichier <code>file</code> ou null si le
	 * fichier n'a pas pu être lu.
	 * @param file le fichier dont on doit calculer l'empreinte md5
	 * @return l'empreinte md5
	 */
	public static String md5sum(File file) {
		String localMd5Sum = null;
		FileInputStream fis=null;
		DigestInputStream dis=null;
		if (file.exists() && file.isFile() && file.canRead()){
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				fis = new FileInputStream(file);
				dis = new DigestInputStream(fis, md);
				dis.on(true);
//				while (dis.read() != -1){
//					;
//				}
				byte[] b = md.digest();
				localMd5Sum = getHexString(b);
			} catch (Exception ex) {
				logger.error(ex);
			}finally {
				try {
					if(dis!=null)
						dis.close();
					if(fis!=null)
						fis.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		return localMd5Sum;
	}

	private static String getHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length*2);
		for (byte b : bytes) {
			if (b <= 0x0F && b >= 0x00) { // On rajoute le 0 de poid fort ignoré à la conversion.
				sb.append('0');
			}
			sb.append( String.format("%x", b) );
		}
		return sb.toString();
	}
}
