/**
 * ESUP-Portail Blank Application - Copyright (c) 2006 ESUP-Portail consortium
 * http://sourcesup.cru.fr/projects/esup-transferts
 */
package org.esupportail.transferts.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.esupportail.transferts.domain.beans.User;
import org.esupportail.transferts.services.auth.Authenticator;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.commons.utils.Assert;
import org.esupportail.commons.utils.ContextUtils;
import org.esupportail.commons.utils.strings.StringUtils;
import org.esupportail.commons.web.controllers.ExceptionController;

import artois.domain.DomainService;

/**
 * A bean to memorize the context of the application.
 */
public class SessionController extends AbstractDomainAwareBean {

	/*
	 ******************* PROPERTIES ******************** */

	/**
	 * The serialization id.
	 */
	private static final long serialVersionUID = -5936434246704000653L;

	/**
	 * The exception controller (called when logging in/out).
	 */
	private ExceptionController exceptionController;

	/**
	 * The authenticator.
	 */
	private Authenticator authenticator;

	/**
	 * The CAS logout URL.
	 */
	private String casLogoutUrl;
	private boolean error = false;
	private String rne;
	private String numeroSerieImmatriculation;
	private Integer annee;
	private Integer currentAnnee;	
	private String htmlCssStyle;
	private boolean boutonDeconnexion;
	private boolean transfertsAccueil;
	private String superGestionnaire;
	private List<String> listSuperGestionnaire = new ArrayList<String>();	
	private String validationAutomatique;
	private Integer regleGestionTE02;
	private Logger logger = new LoggerImpl(getClass());
	
	/*
	 ******************* INIT ******************** */
	/**
	 * Constructor.
	 */
	public SessionController() {
		super();
	}

	/**
	 * @see org.esupportail.transferts.web.controllers.AbstractDomainAwareBean#afterPropertiesSetInternal()
	 */
	@Override
	public void afterPropertiesSetInternal() {
		Assert.notNull(this.exceptionController, "property exceptionController of class " 
				+ this.getClass().getName() + " can not be null");
		Assert.notNull(this.authenticator, "property authenticator of class " 
				+ this.getClass().getName() + " can not be null");
		Assert.hasText(rne, "property rne of class "
				+ this.getClass().getName() + " can not be null");		
		Assert.hasText(htmlCssStyle, "property htmlCssStyle of class "
				+ this.getClass().getName() + " can not be null");	
		Assert.notNull(this.boutonDeconnexion, "property boutonDeconnexion of class " 
				+ this.getClass().getName() + " can not be null");	
		Assert.notNull(this.transfertsAccueil, "property transfertsAccueil of class " 
				+ this.getClass().getName() + " can not be null");	
		
		if(this.superGestionnaire!=null && this.superGestionnaire!="" && ((this.superGestionnaire.split(",")).length>1))
		{
			String[] tokens = this.superGestionnaire.split(",");
			for(int i=0; i<tokens.length; i++)
				this.listSuperGestionnaire.add(tokens[i]);
		}
		else
			this.listSuperGestionnaire.add(this.superGestionnaire);				
	}


	/*
	 ******************* CALLBACK ******************** */


	/*
	 ******************* METHODS ******************** */

	/**
	 * @return the current user, or null if guest.
	 * @throws Exception 
	 */
	@Override
	public User getCurrentUser() throws Exception {
		User user = authenticator.getUser();
		// Verification du login authorise 
		if (listSuperGestionnaire.size() > 0) {
			if(user!=null)
			{
				user.setAdmin(false);
				for (String ident : listSuperGestionnaire) {
					if (ident.equals(user.getLogin()))
					{
						if (logger.isDebugEnabled()) {
							logger.debug("SuperGestionnaire --> "+ user.getLogin()+" est un super gestionnaire");
						}							
						user.setAdmin(true);
						break;
					}
					else
					{
						if (logger.isDebugEnabled()) {
							logger.debug("SuperGestionnaire --> "+ user.getLogin()+" n'est pas un super gestionnaire");
						}													
					}
				}
			}
		}
		return user;
	}
	
	/**
	 * JSF callback.
	 * @return a String.
	 * @throws IOException 
	 */
	public String logout() throws IOException {
		if (ContextUtils.isPortlet()) {
			throw new UnsupportedOperationException("logout() should not be called in portlet mode.");
		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String test = request.getRequestURL().toString();
		String returnUrl=null;
		if(test.contains("/stylesheets/gestionnaire/"))
			returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/.*", "/stylesheets/gestionnaire/welcome.xhtml");
		else if(test.contains("/stylesheets/depart/"))
			returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/.*", "/stylesheets/depart/welcome.xhtml");
		else if(test.contains("/stylesheets/arrivee/"))
			returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/.*", "/stylesheets/arrivee/welcome.xhtml");				
		String forwardUrl;
		Assert.hasText(
				casLogoutUrl, 
				"property casLogoutUrl of class " + getClass().getName() + " is null");
		forwardUrl = String.format(casLogoutUrl, StringUtils.utf8UrlEncode(returnUrl));
		// note: the session beans will be kept even when invalidating 
		// the session so they have to be reset (by the exception controller).
		// We invalidate the session however for the other attributes.
		request.getSession().invalidate();
		request.getSession(true);
		// calling this method will reset all the beans of the application
		exceptionController.restart();
		externalContext.redirect(forwardUrl);
		facesContext.responseComplete();
		return null;
	}

	@Override
	public String toString() {
		return "SessionController [exceptionController=" + exceptionController
				+ ", authenticator=" + authenticator + ", casLogoutUrl="
				+ casLogoutUrl + ", error=" + error + ", rne=" + rne
				+ ", numeroSerieImmatriculation=" + numeroSerieImmatriculation
				+ ", annee=" + annee + ", currentAnnee=" + currentAnnee
				+ ", htmlCssStyle=" + htmlCssStyle + ", boutonDeconnexion="
				+ boutonDeconnexion + ", opiReinscription=" + ", transfertsAccueil="
				+ transfertsAccueil + ", superGestionnaire="
				+ superGestionnaire + ", listSuperGestionnaire="
				+ listSuperGestionnaire + ", validationAutomatique="
				+ validationAutomatique + ", logger=" + logger + "]";
	}

	/**
	 * JSF callback.
	 * @return a String.
	 * @throws IOException 
	 */
	public String logout2() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String returnUrl = request.getRequestURL().toString().replaceFirst("/stylesheets/[^/]*$", "");
		String forwardUrl;
		forwardUrl = String.format(casLogoutUrl, StringUtils.utf8UrlEncode(returnUrl));
		// note: the session beans will be kept even when invalidating 
		// the session so they have to be reset (by the exception controller).
		// We invalidate the session however for the other attributes.
		request.getSession().invalidate();
		request.getSession(true);
		// calling this method will reset all the beans of the application
		exceptionController.restart();
		externalContext.redirect(forwardUrl);
		facesContext.responseComplete();
		return null;
	}	

	public void resetController()
	{
		// calling this method will reset all the beans of the application
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();		
		request.getSession().invalidate();
		request.getSession(true);		
		exceptionController.restart();	
	}





	/*
	 ******************* ACCESSORS ******************** */




	/**
	 * @param exceptionController the exceptionController to set
	 */
	public void setExceptionController(final ExceptionController exceptionController) {
		this.exceptionController = exceptionController;
	}

	/**
	 * @param authenticator the authenticator to set
	 */
	public void setAuthenticator(final Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	/**
	 * @return the casLogoutUrl
	 */
	protected String getCasLogoutUrl() {
		return casLogoutUrl;
	}

	/**
	 * @param casLogoutUrl the casLogoutUrl to set
	 */
	public void setCasLogoutUrl(final String casLogoutUrl) {
		this.casLogoutUrl = StringUtils.nullIfEmpty(casLogoutUrl);
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isError() {
		return error;
	}

	public String getRne() {
		return rne;
	}

	public void setRne(String rne) {
		this.rne = rne;
	}

	public void setNumeroSerieImmatriculation(String numeroSerieImmatriculation) {
		this.numeroSerieImmatriculation = numeroSerieImmatriculation;
	}

	public String getNumeroSerieImmatriculation() {
		return numeroSerieImmatriculation;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setCurrentAnnee(Integer currentAnnee) {
		this.currentAnnee = currentAnnee;
	}

	public Integer getCurrentAnnee() {
		return currentAnnee;
	}

	public String getHtmlCssStyle() {
		return htmlCssStyle;
	}

	public void setHtmlCssStyle(String htmlCssStyle) {
		this.htmlCssStyle = htmlCssStyle;
	}

	public void setBoutonDeconnexion(boolean boutonDeconnexion) {
		this.boutonDeconnexion = boutonDeconnexion;
	}

	public boolean isBoutonDeconnexion() {
		return boutonDeconnexion;
	}

	public String getSuperGestionnaire() {
		return superGestionnaire;
	}

	public void setSuperGestionnaire(String superGestionnaire) {
		this.superGestionnaire = superGestionnaire;
	}

	public List<String> getListSuperGestionnaire() {
		return listSuperGestionnaire;
	}

	public void setListSuperGestionnaire(List<String> listSuperGestionnaire) {
		this.listSuperGestionnaire = listSuperGestionnaire;
	}

	public boolean isTransfertsAccueil() {
		return transfertsAccueil;
	}

	public void setTransfertsAccueil(boolean transfertsAccueil) {
		this.transfertsAccueil = transfertsAccueil;
	}

	public String getValidationAutomatique() {
		return validationAutomatique;
	}

	public void setValidationAutomatique(String validationAutomatique) {
		this.validationAutomatique = validationAutomatique;
	}

	public Integer getRegleGestionTE02() {
		return regleGestionTE02;
	}

	public void setRegleGestionTE02(Integer regleGestionTE02) {
		this.regleGestionTE02 = regleGestionTE02;
	}
}
